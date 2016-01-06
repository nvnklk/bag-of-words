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
import org.apache.commons.io.FilenameUtils;

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
	protected static final String DEFAULT_OUTPUT_MCHUNK_DIR = "method_chunk";
	
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
		termTransformation.addTransfomer(transformerFactory.createCamelcaseTransformer());
		termTransformation.addTransfomer(transformerFactory.createChunkTransformer());	
		termTransformation.addTransfomer(transformerFactory.createJargonTransformer());
		termTransformation.addTransfomer(transformerFactory.createSanityTransformer());				
		termTransformation.addTransfomer(transformerFactory.createLowercaseTransformer());
		termTransformation.addTransfomer(transformerFactory.createStemmingTransformer());
		termTransformation.addTransfomer(transformerFactory.createLengthTransformer());
	}
	
	@Override
	public void create() {
		String currentFileInProcess = null;
		try {
		
			boolean methodChunk = _options.hasOption(JavaBOWOptions.METHOD_CHUNKING);
			boolean ignoreComments = _options.hasOption(JavaBOWOptions.IGNORE_COMMENTS);
			boolean considerCopyright =  _options.hasOption(JavaBOWOptions.CONSIDER_COPYRIGHT);
			boolean stateAnalysis = _options.hasOption(JavaBOWOptions.STATE_ANALYSIS);
			
			// Set up Java source text file tokenizer			
			JavaFileTokenizer jTokenizer = new JavaFileTokenizer();
			jTokenizer.setIgnoreComments(ignoreComments);
			jTokenizer.setConsiderCopyright(considerCopyright);
			jTokenizer.setStateAnalysis(stateAnalysis);
			
			String fileSelectWildCard = "*.java";
			Collection<File> srcfiles = getSourceDocuments(fileSelectWildCard);
			int totalFiles = srcfiles.size();
			int currentFile = 0;
			CtrusHelper.printToConsole("Total files (" +  fileSelectWildCard + ") - " + srcfiles.size());			

			for(File srcFile : srcfiles) {
				// to debug in case of failure
				currentFileInProcess = srcFile.getName();
				
				// Parse the Java source file using a Off-the-shelf Java Parser
				List<String> javaSourceTextLines = FileUtils.readLines(srcFile);
				jTokenizer.tokenize(javaSourceTextLines);
				
				for(ClassTokens c : jTokenizer.getClassTokens()) {
					
					String[] commentTokens = c.getCommentTokens();
					String[] classTokens = c.getTokens();
					for(String mId : c.getMethodIdentifiers()) {
						MethodTokens m = c.getMethodTokens(mId);	
						
						if(methodChunk) {
							// Add document to the vocabulary first before adding terms
							String folderName = c.getIdentifier();
							String fileName = m.getIdentifier().replaceAll(" ", "_");							
							String docName =  folderName + ":" + fileName;
														
							String docref = getDocumentId(docName);		
							Vocabulary.getInstance().addDocument(docref, docName);
							
							addTerms(ArrayUtils.addAll(m.getTokens(), classTokens), docref);
							
							if(!ignoreComments) {
								// Enable stop words filtering for comments
								_stopWordFilterForComments.setEnabled(true);
								addTerms(ArrayUtils.addAll(m.getCommentTokens(), commentTokens), docref);
								_stopWordFilterForComments.setEnabled(false);
							}
							
							// Check if method_chunk folder exists and Create folder with name of the class, create if not exists
							File methodChunkFolder = new File(_outputDir, DEFAULT_OUTPUT_MCHUNK_DIR);
							
							_outputDir = new File(methodChunkFolder, folderName);
							if(!_outputDir.exists()) 
								_outputDir.mkdirs();
							
							String outputFileName = hasOption(DefaultOptions.OUTPUT_SINGLE_FILE) ? docref : fileName;							System.out.println("-->" + outputFileName);	
							// Write bow to the file
							writeToOutput(outputFileName);
							setupOutputDir(); // Set output dir back to the root to avoid recursive folder creation
							reset();  // Reset so that next method tokens can 
									  // be added as new document 
						} else {
							classTokens = ArrayUtils.addAll(m.getTokens(), classTokens);
							commentTokens = ArrayUtils.addAll(m.getCommentTokens(), commentTokens);
						}							
					}
					
					if(!methodChunk) {
						// Add document to file name mapping						
						String docref = getDocumentId(c.getIdentifier());

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
				CtrusHelper.progressMonitor("Progress -", currentFile, totalFiles);
			}
			
		} catch (Exception e) {		
			CtrusHelper.printToConsole("Current file in process - " + currentFileInProcess);
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
