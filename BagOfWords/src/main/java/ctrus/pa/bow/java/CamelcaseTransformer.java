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
