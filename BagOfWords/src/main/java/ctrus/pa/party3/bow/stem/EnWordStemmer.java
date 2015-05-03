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
	private PaiceStemmer 		paice 		= null;
	private LovinsStemmer		lovins 		= null;

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
			case PAICE:
							String rules = this.getClass().getResource("paice.rules").getFile();
							paice = new PaiceStemmer(rules, "/p");
			case LOVINS:	
							lovins = new LovinsStemmer();
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
			case PAICE:
							stemmedWord = paice.stripAffixes(word);
							break;
			case LOVINS:	
							stemmedWord = lovins.stem(word);
							break;
			case MSTEM:													
			case KSTEM:
			default:
							throw new java.lang.UnsupportedOperationException("Not implemented yet!");
		}
		
		return stemmedWord;		
	}
	
}
