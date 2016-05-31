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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.NotImplementedException;

import ctrus.pa.bow.term.TermFilteration;
import ctrus.pa.bow.term.TermTransformation;

public abstract class UnWeightedMultiBagOfWords implements BagOfWords {

	private Map<String,BagOfWords> 	_terms 				= new HashMap<String, BagOfWords>();
	private TermTransformation 		_transformations 	= null;
	private TermFilteration 		_filterations 		= null;	
		
	public abstract void create();
	
	public double getTermCount() {
		double count = 0;
		Iterator<Entry<String, BagOfWords>> entries = _terms.entrySet().iterator();
		while(entries.hasNext()) {
			Entry<String, BagOfWords> e = entries.next();
			count += e.getValue().getTermCount();
		}
		return count;
	}
	
	public final <E extends Enum<E>> double getTermCount(E identifier) {
		return _terms.get(identifier.toString()).getTermCount();
	}
	
	protected final void setTransformations(TermTransformation transformations) {
		_transformations = transformations;
	}
	
	protected final void setFilterations(TermFilteration filterations) {
		_filterations = filterations;
	}
	
	public final <E extends Enum<E>> void setBowIdentifiers(final Class<E> bowIdentifiers){				
		// Initialize all terms map with list of identifiers
		E[] values = bowIdentifiers.getEnumConstants();
		for (E value : values) {
	        _terms.put(value.toString(), new UnWeightedBagOfWords() {
	    		@Override
	    		public void create() {}
	    	}.setFilterations(_filterations).setTransformations(_transformations));
		}
	}
		
	public Set<String> getBowIdentifiers() {
		return _terms.keySet();
	}	

	public final void addTerm(String word, double weight) {
		throw new NotImplementedException("Term weight is not supported in UnWeightedMultiBagOfWords");	

	}

	public final void addTerm(String term, String doc) {
		throw new NotImplementedException("Term without identifier is not supported in UnWeightedMultiBagOfWords");	
	}
	
	public final <E extends Enum<E>> void addTerms(String[] terms, String doc, E identifier) {
		for(String term : terms)
			addTerm(term, doc, identifier);
	}
	
	public final <E extends Enum<E>> void addTerm(String term, String doc, E identifier) {
		_terms.get(identifier.toString()).addTerm(term, doc);
	}

	public <E extends Enum<E>> void writeTo(OutputStream out, E identifier) throws IOException {		
		BagOfWords bowToWrite = _terms.get(identifier.toString());
		if(bowToWrite.getTermCount() > 0)
			bowToWrite.writeTo(out);
	}
	
	public void writeTo(OutputStream out) throws IOException {
		throw new NotImplementedException("WriteTo without identifier is not supported in UnWeightedMultiBagOfWords");
	}
	
	public void reset() {
		// Clean up all terms for recycling bag of words
		Iterator<Entry<String, BagOfWords>> entries = _terms.entrySet().iterator();
		while(entries.hasNext()) {
			Entry<String, BagOfWords> e = entries.next();
			e.getValue().reset();
		}
	}

}
