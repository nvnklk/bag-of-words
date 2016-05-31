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
