/*******************************************************************************
 * Copyright (c) 2015, 2016  Naveen Kulkarni
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

package ctrus.pa.party3.bow.stem;

import ctrus.pa.bow.term.transformation.StemmingTransformer;


public class EnWordStemmer extends StemmingTransformer {
	
	public enum EnWordStemmerAlgo {
		PORTER,		// A gentle algorithmic stemmer
		SNOWBALL, 	// A variant of PORTER stemmer
		KSTEM,		// A gentle morphological stemmer			 
		LOVINS, 	// A medium heavy algorithmic stemmer		 
		PAICE,		// A heavy algorithmic stemmer 
		MSTEM		// A heavy morphological stemmer
	}
	
	private EnWordStemmerAlgo 	stemmerAlgo = null;
	private PorterStemmer 		porter 		= null;
	private SnowballStemmer 	snowball 	= null;
	//private PaiceStemmer 		paice 		= null;
	//private LovinsStemmer		lovins 		= null;

	// Closed Constructor
	private EnWordStemmer(EnWordStemmerAlgo stemmerAlgo) {
		this.stemmerAlgo = stemmerAlgo;
		initStemmer();
	}
	
	private void initStemmer() {
		switch(stemmerAlgo) {
			case PORTER:
							porter = new PorterStemmer();
							break;
			case SNOWBALL:
							snowball = new SnowballStemmer();
							break;
			/*case PAICE:
							String rules = this.getClass().getResource("paice.rules").getFile();
							paice = new PaiceStemmer(rules, "/p");*/
			/*case LOVINS:	
							lovins = new LovinsStemmer();*/
			case MSTEM:
			case KSTEM:										
		}
		
	}
	
	public static EnWordStemmer getStemmer(EnWordStemmerAlgo stemmerAlgo) {
		return new EnWordStemmer(stemmerAlgo);
	}
	
	public String stem(String word) {
		// Possibility of having a compound word
		StringBuffer sb = new StringBuffer();
		for(String eachTerm : getTerms(word)) {
			sb.append(_steamEachTerm(eachTerm)).append(" ");
		}
		return sb.toString().trim();
	}
	
	private String _steamEachTerm(String word) {
		String stemmedWord = null;
		
		switch(stemmerAlgo) {
			case PORTER:
							porter.add(word.toCharArray(), word.length());
							porter.stem();
							stemmedWord = porter.toString();
							break;
			case SNOWBALL:
							snowball.setCurrent(word);
							snowball.stem();
							stemmedWord = snowball.getCurrent();
							break;
			/*case PAICE:
							stemmedWord = paice.stripAffixes(word);
							break;*/
			/*case LOVINS:	
							stemmedWord = lovins.stem(word);
							break;*/
			case MSTEM:													
			case KSTEM:
			default:
							throw new java.lang.UnsupportedOperationException("Not implemented yet!");
		}
		
		return stemmedWord;		
	}
	
}
