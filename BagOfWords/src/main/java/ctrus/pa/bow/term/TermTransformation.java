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

public class TermTransformation {
	
	private List<TermTransformer> _transfomers = null;
	
	public TermTransformation() {
		_transfomers = new ArrayList<TermTransformer>();
	}
	
	public TermTransformation addTransfomer(TermTransformer transformer) {
		_transfomers.add(transformer);
		return this;
	}
	
	private String transform(String[] terms) {
		String multiTransformedTerm = "";
		for(String eachTerm : terms) {
			multiTransformedTerm = multiTransformedTerm + " " + transform(eachTerm); 
		}
		return multiTransformedTerm;
	}
	
	public String transform(String term) {
		if(term == null || term.length() == 0) return term;
		
		String transformedTerm = term;
		// Check if the term is a multi term
		if(isMultiTerm(transformedTerm)) {
			transformedTerm = transform(transformedTerm.split("\\p{Space}"));
		} else {
			// pass through all the transformations
			for(TermTransformer transformer : _transfomers) {
				if(transformer.isEnabled() && transformedTerm != null) {
					transformedTerm = transformer.transform(transformedTerm);
				}
			}
		}
		return transformedTerm;
	}
	
	private boolean isMultiTerm(String term) {
		return term.split("\\p{Space}").length > 1;
	}
	
}
