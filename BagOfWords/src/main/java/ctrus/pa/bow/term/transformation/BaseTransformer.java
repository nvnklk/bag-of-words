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

package ctrus.pa.bow.term.transformation;

import ctrus.pa.bow.term.TermTransformer;

public abstract class BaseTransformer implements TermTransformer {

	private boolean _enabled = true;	// Default, transformer enabled
	
	public boolean isEnabled() {
		return _enabled;
	}
	
	public void setEnabled(boolean enabled) {
		_enabled = enabled;
	}
	
	protected String[] getTerms(String term) {
		return term.split(" ");
	}
	
}
