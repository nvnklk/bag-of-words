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

package ctrus.pa.bow.term.transformation;

import java.util.ArrayList;
import java.util.List;

//Clean up punctuation and special characters except @, ', &
// [!"#$%&'()*+,\-./:;<=>?@[\\\]^_`{|}~] 
// http://www.regular-expressions.info/posixbrackets.html
public class SanityTransformer extends BaseTransformer {
	
	private List<Character> _escapes = new ArrayList<Character>();
	private String SANITY_REGEX_1  = "(?![";
	private String SANITY_REGEX_2  = "])\\p{Punct}";
	private String SANITY_REGEX  = "\\p{Punct}";
	
	public void escapeChar(Character c) {
		_escapes.add(c);
	}

	public String transform(String term) {
		String regex, cleanedTerm;		
		if(!_escapes.isEmpty()) {
			regex = SANITY_REGEX_1;
			for(Character c: _escapes)
				regex = regex + c;
			regex = regex + SANITY_REGEX_2;
		} else {
			regex = SANITY_REGEX;
		}
		cleanedTerm = term.replaceAll(regex, " ");
		
		// Remove multiple spaces with single space before returning
		return cleanedTerm.replaceAll("\\s+", " ");
	}

}
