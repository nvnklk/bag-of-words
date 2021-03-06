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

package ctrus.pa.bow.core;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.NotImplementedException;

import ctrus.pa.bow.term.TermFilteration;
import ctrus.pa.bow.term.TermTransformation;

public abstract class UnWeightedBagOfWords implements BagOfWords {

	private List<String> 		_terms 				= new ArrayList<String>();	
	private TermTransformation 	_transformations 	= null;
	private TermFilteration 	_filterations 		= null;
		
	protected final UnWeightedBagOfWords setTransformations(TermTransformation transformations) {
		_transformations = transformations;
		return this;
	}
	
	public double getTermCount() {
		return _terms.size();
	}
	
	protected final UnWeightedBagOfWords setFilterations(TermFilteration filterations) {
		_filterations = filterations;
		return this;
	}
	
	
	public final <E extends Enum<E>> void writeTo(OutputStream out, E identifier) throws IOException {
		throw new NotImplementedException("WriteTo with identifier is not supported in UnWeightedBagOfWords");
	}
				
	public final void writeTo(OutputStream out) throws IOException {
		
		Iterator<String> terms = _terms.iterator();
		while(terms.hasNext())
			IOUtils.write(terms.next() + " ", out);
		IOUtils.write("\n", out);
		out.flush();
	}
		
	public final void addTerm(String word, double weight) {
		throw new NotImplementedException("Term weight is not supported in UnWeightedBagOfWords");	
	}
	
	public final <E extends Enum<E>> void addTerm(String term, String doc, E identifier) {
		throw new NotImplementedException("Term with identifier is not supported in UnWeightedBagOfWords");	
	}		
	
	public final void addTerms(String[] terms, String doc) {
		for(String term : terms)
			addTerm(term, doc);
	}
		
	public final void addTerm(String term, String doc) {
		// Check if term is null or empty
		if(term == null || term.length() == 0) return;
		
		// Is it required to be added?	// First filtration
		if(_filterations.filter(term)) return;
		
		// Transform the term
		String transformedTerm = _transformations.transform(term);
		
		// Add transformed term(s) to the list
		if(transformedTerm != null && transformedTerm.length() != 0) {			
			if(transformedTerm.indexOf(" ") == -1) {	// Not a compound term				
				if(!_filterations.filter(transformedTerm)) {	// Second filtration					 
					_terms.add(transformedTerm);					
					// Add to vocabulary
					Vocabulary.getInstance().addTerm(transformedTerm, doc);
				}
			} else {	// compound term, split and add				
				String[] terms = transformedTerm.split(" "); 
				for(String eachTerm : terms) {
					addTerm(eachTerm, doc);
				}
			}
		}
		
	}
	
	public final void reset() {
		// Clean up all terms for recycling bag of words
		_terms.clear();
	}
}
