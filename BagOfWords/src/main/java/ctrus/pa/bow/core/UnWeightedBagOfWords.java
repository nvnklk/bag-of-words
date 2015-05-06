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
		
	public abstract void create();
	
	protected final void setTransformations(TermTransformation transformations) {
		_transformations = transformations;
	}
	
	protected final void setFilterations(TermFilteration filterations) {
		_filterations = filterations;
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
	
	public final void addTerms(String[] terms, String doc) {
		for(String term : terms)
			addTerm(term, doc);
	}
		
	public final void addTerm(String term, String doc) {
		// Check if term is null or empty
		if(term == null || term.length() == 0) return;
		
		// Is it required to be added?
		if(_filterations.filter(term)) return;
		
		// Transform the term
		String transformedTerm = _transformations.transform(term);
		
		// Add transformed term(s) to the list
		if(transformedTerm != null && transformedTerm.length() != 0) {			
			if(transformedTerm.indexOf(" ") == -1) {	
				// Not a compound term
				_terms.add(transformedTerm);
				Vocabulary.getInstance().addTerm(transformedTerm, doc);   // Add to vocabulary
			} else {
				// compound term, split and add
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
