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

import org.apache.commons.lang3.NotImplementedException;

import ctrus.pa.bow.term.TermFilteration;
import ctrus.pa.bow.term.TermTransformation;

public abstract class WeightedBagOfWords implements BagOfWords {
	
	protected Double DEFAULT_WEIGHT = new Double(1.0);
			
	private TermTransformation 	_transfomrations 	= null;
	private TermFilteration 	_filterations 		= null;
		
	public abstract void create();
	
	protected final void setTransformations(TermTransformation transformations) {
		_transfomrations = transformations;
	}
	
	protected final void setFilterations(TermFilteration filterations) {
		_filterations = filterations;
	}
				
	public final void writeTo(OutputStream out) throws IOException {		
		throw new NotImplementedException("TBD");		
	}
		
	public final void addTerm(String word, double weight) {
		throw new NotImplementedException("TBD");	
	}
		
	public final void addTerm(String term) {
		addTerm(term, DEFAULT_WEIGHT);
	}
	
	public final void reset() {
		throw new NotImplementedException("TBD");
	}

}
