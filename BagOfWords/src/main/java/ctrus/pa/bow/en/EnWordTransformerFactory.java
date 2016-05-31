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

import org.apache.commons.cli.MissingOptionException;

import ctrus.pa.bow.DefaultOptions;
import ctrus.pa.bow.core.BOWOptions;
import ctrus.pa.bow.term.TermTransformer;
import ctrus.pa.bow.term.TransformerFactory;
import ctrus.pa.bow.term.transformation.BaseTransformerFactory;
import ctrus.pa.party3.bow.stem.EnWordStemmer;
import ctrus.pa.party3.bow.stem.EnWordStemmer.EnWordStemmerAlgo;
import ctrus.pa.util.CtrusHelper;

public class EnWordTransformerFactory extends BaseTransformerFactory {
	
	private static EnWordTransformerFactory _instance = null;
	
	private EnWordTransformerFactory() {}
	
	public static TransformerFactory newInstance(BOWOptions options) {
		if(_instance == null) {
			_instance = new EnWordTransformerFactory();
			_instance.setOptions(options);
		}
		return _instance;
	}
	
	public TermTransformer createStemmingTransformer() {
		EnWordStemmerAlgo stemAlgo = null;
		try{
			String algo = getOption(DefaultOptions.STEMMING_ALGO);				
			switch(algo) {
				case "porter"   : stemAlgo = EnWordStemmerAlgo.PORTER; break;									 
				case "snowball" : stemAlgo = EnWordStemmerAlgo.SNOWBALL; break;
				case "paice" 	: stemAlgo = EnWordStemmerAlgo.PAICE; break;
				case "lovins" 	: stemAlgo = EnWordStemmerAlgo.LOVINS; break;
				case "kstem" 	: stemAlgo = EnWordStemmerAlgo.KSTEM; break;
				case "mstem" 	: stemAlgo = EnWordStemmerAlgo.MSTEM; break;
				default         : stemAlgo = EnWordStemmerAlgo.PORTER;
			}			
		} catch(MissingOptionException ex) {
			// Default stemming is 
			stemAlgo = EnWordStemmerAlgo.PORTER;
		}
		
		TermTransformer stemmingTransformer = EnWordStemmer.getStemmer(stemAlgo);
		if(hasOption(DefaultOptions.NO_STEMMING)) {
			stemmingTransformer.setEnabled(false);
			CtrusHelper.printToConsole("No stemming of terms...");
		} else {
			CtrusHelper.printToConsole("Stemming rules used - " + stemAlgo);
		}
		return stemmingTransformer;
	}
	
}
