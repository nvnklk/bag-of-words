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

package ctrus.pa.bow.term.filter;

import org.apache.commons.lang3.math.NumberUtils;

public class NumericFilter extends BaseFilter {

	public boolean filter(String term) {
		boolean filterTerm = false;
		if(NumberUtils.isDigits(term) || Character.isDigit(term.charAt(0)))
			filterTerm = true; 
		return filterTerm;
	}

}
