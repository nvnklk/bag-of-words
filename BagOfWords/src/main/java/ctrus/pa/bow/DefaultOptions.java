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

package ctrus.pa.bow;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

import ctrus.pa.bow.core.BOWOptionsImpl;

public abstract class DefaultOptions extends BOWOptionsImpl {
	
	private static final long serialVersionUID = 1L;
	
	public final static String USE_WEIGHT 			 = "useWeight";
	public final static String USE_STEMMING 		 = "useStemming";
	public final static String REPLACE_JARGONS		 = "replaceJargons";
	public final static String CASE_SENSITIVE 		 = "caseSensitive";
	public final static String RETAIN_NUMERAL 		 = "retainNum";
	public final static String MIN_WORD_LENGTH 		 = "minWordLength";	
	public final static String STEMMING_ALGO 		 = "stemAlgo";	
	public final static String SOURCE_DIR 			 = "sourceDir";
	public final static String DOCUMENT_PER_LINE 	 = "documentsPerLine";
	public final static String OUTPUT_SINGLE_FILE 	 = "outputSingleFile";
	public final static String STOP_WORDS_FILE		 = "stopWordsFile";
	public final static String OUTPUT_DIR			 = "outputDir";
	public final static String TERM_CHUNK_CHARS		 = "termChunkChars";
	public final static String PRINT_VOCABULARY		 = "printVocabulary";
	public final static String HASH_TERMS			 = "hashTerms";
	public final static String IGNORE_SPECIAL_CHARS	 = "ignoreSpecialChars";
	public final static String NO_STEMMING			 = "noStemming";
	public final static String DOCUMENT_ID_DELIMITER = "documentIdDelimiter";
	public final static String PRESERVE_DOC_ID		 = "preserveDocId";
	
	public final static String DEBUG_LOG			 = "debugLog";			
	public final static String PRINT_HELP			 = "help";
	
	public final static String INTERNAL_REMOVE_STOP_WORDS = "removeStopWords";
	
	// Restricted use
	protected DefaultOptions() {
		
	}
	
	@SuppressWarnings("static-access")
	public void defineDefaultOptions() {
		Option o1 =  OptionBuilder.hasArg(false)
					  		.withDescription("Use weights for words")
					  		.create(USE_WEIGHT);
		addOption(o1);
		
		Option o2 =  OptionBuilder.hasArg(false)
			  			.withDescription("Stem the words extracted")
			  			.create(USE_STEMMING);
		addOption(o2);
		
		Option o3 =  OptionBuilder.hasArg(false)
						.withDescription("Retain the capital characters in the term")
						.create(CASE_SENSITIVE);
		addOption(o3);
				
		Option o4 =  OptionBuilder.hasArg(false)
						.withDescription("Retain the numerals")
						.create(RETAIN_NUMERAL);
		addOption(o4);
		
		Option o5 =  OptionBuilder.hasArg(true)
						.withDescription("Minimum word length to consider, default is 3")
						.create(MIN_WORD_LENGTH);
		addOption(o5);	
		
		Option o6 =  OptionBuilder.hasArg(true)
				.withDescription("Stemming algorithm to use")
				.create(STEMMING_ALGO);
		addOption(o6);
		
		Option o7 =  OptionBuilder.hasArg(true)
				.withDescription("Directory containing input documents")
				.isRequired()
				.create(SOURCE_DIR);
		addOption(o7);
		
		Option o8 =  OptionBuilder.hasArg(true)
				.withDescription("Output single file with each line corresponding to an input source file")
				.create(OUTPUT_SINGLE_FILE);
		addOption(o8);	
				
		Option o9 =  OptionBuilder.hasArg(true)
				.withDescription("Stop words file")
				.create(STOP_WORDS_FILE);
		addOption(o9);

		Option o10 =  OptionBuilder.hasArg(true)
				.withDescription("Directory to write output")
				.isRequired()
				.create(OUTPUT_DIR);
		addOption(o10);
		
		Option o11 =  OptionBuilder.hasArg(true)
				.withDescription("Replace jargon words with full words")
				.create(REPLACE_JARGONS);
		addOption(o11);
		
		Option o12 =  OptionBuilder.hasArg(true)
				.withDescription("Chunk characters eg. ;,_:")
				.create(TERM_CHUNK_CHARS);
		addOption(o12);
		
		Option o13 =  OptionBuilder.hasArg(false)
				.withDescription("Output debug log")
				.create(DEBUG_LOG);
		addOption(o13);		
		
		Option o14 =  OptionBuilder.hasArg(false)
				.withDescription("Print this help")
				.create(PRINT_HELP);
		addOption(o14);
		
		Option o15 = OptionBuilder.hasArg(false)
				.withDescription("Print the vocabulary to a file 'voc.txt'")
				.create(PRINT_VOCABULARY);
		addOption(o15);
		
		Option o16 = OptionBuilder.hasArg(true)
				.withDescription("Hash the term if its length exceeds <arg>")
				.create(HASH_TERMS);
		addOption(o16);
		
		Option o17 = OptionBuilder.hasArg(true)
				.withDescription("Special characters to ignore eg. _$")
				.create(IGNORE_SPECIAL_CHARS);
		addOption(o17);
		
		Option o18 = OptionBuilder.hasArg(false)
				.withDescription("Each line is a document in the input file(s)")
				.create(DOCUMENT_PER_LINE);
		addOption(o18);

		Option o19 = OptionBuilder.hasArg(false)
				.withDescription("Do not stem the terms")
				.create(NO_STEMMING);
		addOption(o19);
							  
		Option o20 = OptionBuilder.hasArg(true)
				.withDescription("Term left to delimiter is considered document Id, default is space")
				.create(DOCUMENT_ID_DELIMITER);
		addOption(o20);
		
		Option o21 = OptionBuilder.hasArg(false)
				.withDescription("Retain the document ids from input")
				.create(PRESERVE_DOC_ID);
		addOption(o21);
		
	}
	
	public abstract void defineOptions() ;

	@Override
	public String usageDescription() {
		return "java DefaultBagOfWords [options] -ouputDir <dir> -docsDir <docs dir>";
	}

	@Override
	public String toolDescription() {
		return "Default";
	}


}
