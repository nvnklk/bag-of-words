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

package ctrus.pa.bow.term;

import java.util.ArrayList;
import java.util.List;

public class TermFilteration {

	private List<TermFilter> _filters = null;
	
	public TermFilteration() {
		_filters = new ArrayList<TermFilter>();
	}
	
	public TermFilteration addFilter(TermFilter filter) {
		_filters.add(filter);
		return this;
	}
	
	// Filter true means remove the term
	public boolean filter(String term) {
		// Pass through filter
		for(TermFilter filter : _filters) { 
			if(filter.isEnabled()) {
				if(filter.filter(term)) return true;	// Remove the term
			} else {
				continue;
			}
		}
		
		return false;
	}
	
}
