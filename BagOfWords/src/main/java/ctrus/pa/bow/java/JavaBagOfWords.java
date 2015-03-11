/*
 * Copyright (C) 2015 Naveen Kulkarni.
 *
 * This file is part of Bag of Words program.
 *
 * Bag of Words is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by the 
 * Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version.
 *
 * Bag of Words is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License 
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along 
 * with Bag of Words program. If not, see <http://www.gnu.org/licenses/>.
 */

package ctrus.pa.bow.java;

import java.io.File;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;

import ctrus.pa.bow.core.DefaultBagOfWords;
import ctrus.pa.bow.core.DefaultOptions;
import ctrus.pa.bow.core.Vocabulary;
import ctrus.pa.bow.java.token.ClassTokens;
import ctrus.pa.bow.java.token.MethodTokens;
import ctrus.pa.bow.term.TermFilter;
import ctrus.pa.bow.term.TermFilteration;
import ctrus.pa.bow.term.TermTransformation;
import ctrus.pa.util.CtrusHelper;

public class JavaBagOfWords extends DefaultBagOfWords {

	TermFilter _stopWordFilterForComments = null;
	
	public JavaBagOfWords() {}
	
	protected void setup() {
		// Create filter
		TermFilteration filterations = new TermFilteration();
		setFilterations(filterations);
				
		// Add all required filters
		JavaFilterFactory filterFactory = JavaFilterFactory.newInstance(_options); 
		filterations.addFilter(filterFactory.createStopFilter());
		// create a comment stop word filter and get its reference. This filter is
		// disabled by default and it enabled as required.
		_stopWordFilterForComments = filterFactory.createStopFilterForComments();
		_stopWordFilterForComments.setEnabled(false);
		filterations.addFilter(_stopWordFilterForComments);
		filterations.addFilter(filterFactory.createNumericFilter());
		filterations.addFilter(filterFactory.createLengthFilter());
		
				
		// Create transformer
		TermTransformation termTransformation = new TermTransformation();
		setTransformations(termTransformation);
				
		// Add all required transformers
		JavaTransformerFactory transformerFactory = JavaTransformerFactoryImpl.newInstance(_options);
		termTransformation.addTransfomer(transformerFactory.createChunkTransformer());
		termTransformation.addTransfomer(transformerFactory.createCamelcaseTransformer());
		termTransformation.addTransfomer(transformerFactory.createSanityTransformer());		
		termTransformation.addTransfomer(transformerFactory.createJargonTransformer());
		termTransformation.addTransfomer(transformerFactory.createLowercaseTransformer());
		termTransformation.addTransfomer(transformerFactory.createLengthTransformer());
	}
	
	@Override
	public void create() {
		try {
		
			boolean methodChunk = _options.hasOption(JavaBOWOptions.METHOD_CHUNKING);
			boolean ignoreComments = _options.hasOption(JavaBOWOptions.IGNORE_COMMENTS);
			
			// Set up Java source text file tokenizer			
			JavaFileTokenizer jTokenizer = new JavaFileTokenizer();
			jTokenizer.setIgnoreComments(ignoreComments);
			
			Collection<File> srcfiles = getSourceDocuments("*.java");
			int totalFiles = srcfiles.size();
			int currentFile = 0;
			CtrusHelper.printToConsole("Total files - " + srcfiles.size());			

			for(File srcFile : srcfiles) {								
				// Parse the Java source file using a Off-the-shelf Java Parser
				List<String> javaSourceTextLines = FileUtils.readLines(srcFile);
				jTokenizer.tokenize(javaSourceTextLines);
				for(ClassTokens c : jTokenizer.getClassTokens()) {
					String[] commentTokens = c.getCommentTokens();
					String[] classTokens = c.getTokens();
					for(String mId : c.getMethodIdentifiers()) {
						MethodTokens m = c.getMethodTokens(mId);						
						if(methodChunk) {
							String docref = CtrusHelper.uniqueId(m.getIdentifier()).toString();
							
							// Add document to the vocabulary first before adding terms							
							Vocabulary.getInstance().addDocument(docref, srcFile.getName());
							
							addTerms(ArrayUtils.addAll(m.getTokens(), classTokens), docref);
							
							if(!ignoreComments) {
								// Enable stop words filtering for comments
								_stopWordFilterForComments.setEnabled(true);
								addTerms(ArrayUtils.addAll(m.getCommentTokens(), commentTokens), docref);
								_stopWordFilterForComments.setEnabled(false);
							}
							
							// Write bow to the file
							writeToOutput(docref);
							reset();  // Reset so that next method tokens can 
									  // be added as new document 
						} else {
							classTokens = ArrayUtils.addAll(m.getTokens(), classTokens);
							commentTokens = ArrayUtils.addAll(m.getCommentTokens(), commentTokens);
						}							
					}
					
					if(!methodChunk) {
						// Add document to file name mapping
						String docref = CtrusHelper.uniqueId(c.getIdentifier()).toString();

						// Add document to the vocabulary first before adding terms
						Vocabulary.getInstance().addDocument(docref, srcFile.getName());
						
						// Add all collected tokens as terms
						addTerms(classTokens, docref);
						
						if(!ignoreComments) {
							// Enable stop words filtering for comments
							_stopWordFilterForComments.setEnabled(true);
							addTerms(commentTokens, docref);
						}
						
						writeToOutput(docref);
						reset(); // Reset so that next class tokens can 
						  		 // be added as new document
					}
						
				}
				
				currentFile++;	// Update the counter
				// print progress
				CtrusHelper.progressMonitor("Progress - ", currentFile, totalFiles);
			}
			
		} catch (Exception e) {			
			e.printStackTrace();
		}
		
	}
		
	public static void main(String[] args) throws Exception {
		// Parse the command line input
		JavaBOWOptions opts = JavaBOWOptions.Factory.getInstance();	
		opts.parseCLI(args);
		if(opts.hasOption(DefaultOptions.PRINT_HELP)) {
			opts.printHelp("HELP", new PrintWriter(System.out));
		} else {					
			JavaBagOfWords jBow = new JavaBagOfWords();
			jBow.setup(opts);
			jBow.create();
			jBow.printVocabulary();
		}
	}

	
}
