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

package ctrus.pa.bow.term;

import java.util.ArrayList;
import java.util.List;

public class TermTransformation {
	
	private List<TermTransformer> _transfomers = null;
	
	public TermTransformation() {
		_transfomers = new ArrayList<TermTransformer>();
	}
	
	public void addTransfomer(TermTransformer transformer) {
		_transfomers.add(transformer);
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
