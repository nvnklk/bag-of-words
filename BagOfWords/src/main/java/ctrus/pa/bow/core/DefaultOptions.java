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

package ctrus.pa.bow.core;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

public class DefaultOptions extends BOWOptionsImpl {
	
	public final static String USE_WEIGHT 			= "useWeight";
	public final static String USE_STEMMING 		= "useStemming";
	public final static String REPLACE_JARGONS		= "replaceJargons";
	public final static String CASE_SENSITIVE 		= "caseSensitive";
	public final static String RETAIN_NUMERAL 		= "retainNum";
	public final static String MIN_WORD_LENGTH 		= "minWordLength";	
	public final static String STEMMING_ALGO 		= "stemAlgo";	
	public final static String SOURCE_DIR 			= "sourceDir";
	public final static String OUTPUT_SINGLE_FILE 	= "outputSingleFile";
	public final static String STOP_WORDS_FILE		= "stopWordsFile";
	public final static String JARGONS_FILE			= "jargonsFile";
	public final static String OUTPUT_DIR			= "outputDir";
	public final static String TERM_CHUNK_CHARS		= "termChunkChars";
	public final static String PRINT_VOCABULARY		= "printVocabulary";
	public final static String HASH_TERMS			= "hashTerms";				
	
	public final static String DEBUG_LOG			= "debugLog";			
	public final static String PRINT_HELP			= "help";
	
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
		
		Option o11 =  OptionBuilder.hasArg(false)
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
		
	}

	@Override
	public void defineOptions() {
	
	}

	@Override
	public String usageDescription() {
		return "java DefaultBagOfWords [options] -ouputDir <dir> -docsDir <docs dir>";
	}

	@Override
	public String toolDescription() {
		return "Default";
	}


}
