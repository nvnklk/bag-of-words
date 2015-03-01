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
