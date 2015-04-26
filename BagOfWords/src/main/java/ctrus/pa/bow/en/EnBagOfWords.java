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

package ctrus.pa.bow.en;

import java.io.File;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;

import ctrus.pa.bow.core.BOWOptions;
import ctrus.pa.bow.core.DefaultBagOfWords;
import ctrus.pa.bow.core.DefaultOptions;
import ctrus.pa.bow.core.Vocabulary;
import ctrus.pa.bow.term.FilterFactory;
import ctrus.pa.bow.term.TermFilteration;
import ctrus.pa.bow.term.TermTransformation;
import ctrus.pa.bow.term.TransformerFactory;
import ctrus.pa.util.CtrusHelper;

public class EnBagOfWords extends DefaultBagOfWords {
	
	public EnBagOfWords() {}
	
	protected void setup() {
		// Create filter
		TermFilteration filterations = new TermFilteration();
		setFilterations(filterations);
		
		// Add all required filters
		FilterFactory filterFactory = EnWordFilterFactory.newInstance(_options); 
		filterations.addFilter(filterFactory.createStopFilter());
		filterations.addFilter(filterFactory.createNumericFilter());
		filterations.addFilter(filterFactory.createLengthFilter());
		
		// Create transformer
		TermTransformation termTransformation = new TermTransformation();
		setTransformations(termTransformation);
		
		// Add all required transformers
		TransformerFactory transformerFactory = EnWordTransformerFactory.newInstance(_options);
		termTransformation.addTransfomer(transformerFactory.createChunkTransformer());
		termTransformation.addTransfomer(transformerFactory.createSanityTransformer());
		termTransformation.addTransfomer(transformerFactory.createJargonTransformer());
		termTransformation.addTransfomer(transformerFactory.createLowercaseTransformer());
		termTransformation.addTransfomer(transformerFactory.createStemmingTransformer());
		termTransformation.addTransfomer(transformerFactory.createLengthTransformer());
	}
	
	@Override
	public void create() {
		try {				
			Collection<File> srcfiles = getSourceDocuments("*");
			int totalFiles = srcfiles.size();
			int currentFile = 0;
			CtrusHelper.printToConsole("Number of files to process - " + srcfiles.size());
			
			for(File srcFile : srcfiles) {
				
				// Read each line
				Iterator<String> lines = FileUtils.lineIterator(srcFile);
				
				if(_options.hasOption(DefaultOptions.DOCUMENT_PER_LINE)) {
					while(lines.hasNext()) {					
						String line = lines.next();
						String[] docAndContent = line.split("=");
						if(docAndContent.length > 1 && !docAndContent[1].isEmpty()){
							String docref = CtrusHelper.uniqueId(docAndContent[0]).toString();
							// Add document to the vocabulary first before adding terms
							Vocabulary.getInstance().addDocument(docref, docAndContent[0]);
						
							addTerms(docAndContent[1].split("\\p{Space}"), docref);
							writeToOutput(docref);
							reset();
						}
					}
				} else {
					// Add document to file name mapping
					String fileName = srcFile.getName();
					String docref = CtrusHelper.uniqueId(fileName).toString();

					// Add document to the vocabulary first before adding terms
					Vocabulary.getInstance().addDocument(docref, fileName);
					
					while(lines.hasNext()) {					
						String line = lines.next();
						addTerms(line.split("\\p{Space}"), docref);					
					}
					
					writeToOutput(docref);	// Write BOW to output file							
					reset();				// Reusing BOW, make sure to reset

				}
				
				// Update the counter and print progress
				currentFile++;
				CtrusHelper.progressMonitor("Progress - ", currentFile, totalFiles);
			}
		} catch (Exception e) {			
			e.printStackTrace();
		}				
	}
	
	public static void main(String[] args) throws Exception {
		// Parse the command line input
		BOWOptions opts = EnBOWOptions.getInstance();	
		opts.parseCLI(args);		
		if(opts.hasOption(DefaultOptions.PRINT_HELP)) {
			opts.printHelp("HELP", new PrintWriter(System.out));
		} else {		
			EnBagOfWords eBow = new EnBagOfWords();
			eBow.setup(opts);
			eBow.create();
			eBow.printVocabulary();
		}
	}	
	
	
}
