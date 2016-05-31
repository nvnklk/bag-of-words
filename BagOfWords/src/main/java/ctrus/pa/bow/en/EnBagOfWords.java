/*******************************************************************************
 * Copyright (c) 2015, 2016 
 *
 * This file is part of Bag of Words program. 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Naveen Kulkarni (naveen.kulkarni@research.iiit.ac.in)
 *     
 *******************************************************************************/

package ctrus.pa.bow.en;

import java.io.File;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;

import ctrus.pa.bow.DefaultBagOfWords;
import ctrus.pa.bow.DefaultOptions;
import ctrus.pa.bow.core.BOWOptions;
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
		ctrus.pa.bow.java.JavaTransformerFactory transformerFactory1 = ctrus.pa.bow.java.JavaTransformerFactoryImpl.newInstance(_options);
		termTransformation.addTransfomer(transformerFactory1.createCamelcaseTransformer());
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
						String delimiter = " ";
						if(_options.hasOption(DefaultOptions.DOCUMENT_ID_DELIMITER))
							delimiter = _options.getOption(DefaultOptions.DOCUMENT_ID_DELIMITER);
						String[] docAndContent = line.split(delimiter);
						if(docAndContent.length > 1 && !docAndContent[1].isEmpty()){

							String docref = getDocumentId(docAndContent[0]);

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
					//fileName = fileName.substring(0, fileName.lastIndexOf("."));

					String docref = getDocumentId(fileName);

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
