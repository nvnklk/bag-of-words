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
