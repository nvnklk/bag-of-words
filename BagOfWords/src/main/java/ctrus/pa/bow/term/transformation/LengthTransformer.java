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

package ctrus.pa.bow.term.transformation;

import ctrus.pa.util.CtrusHelper;

public class LengthTransformer extends BaseTransformer {

	public static final int DEFAULT_MAX_TERM_LENGTH = 5;
	
	private int _maxTermLength = DEFAULT_MAX_TERM_LENGTH;
	
	public void setMaxTermLength(int length) {
		_maxTermLength = length;
	} 
	
	public String transform(String term) {
		String trnasformedTerm = term;
		if(term.length() > _maxTermLength)
			trnasformedTerm = CtrusHelper.uniqueId(trnasformedTerm).toString();
		return trnasformedTerm;
	}
}
