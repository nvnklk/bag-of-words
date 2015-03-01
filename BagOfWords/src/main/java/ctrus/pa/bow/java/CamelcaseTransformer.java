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

import org.apache.commons.lang3.StringUtils;

import ctrus.pa.bow.term.transformation.BaseTransformer;

//Break camel cased words and return them as space delimited words
public class CamelcaseTransformer extends BaseTransformer {

	public String transform(String term) {
		String transformedTerm = "";
		String[] split = StringUtils.splitByCharacterTypeCamelCase(term);
		for(String eachTerm : split) 
			transformedTerm = transformedTerm + " " + eachTerm;
		return StringUtils.strip(transformedTerm);		
	}
}
