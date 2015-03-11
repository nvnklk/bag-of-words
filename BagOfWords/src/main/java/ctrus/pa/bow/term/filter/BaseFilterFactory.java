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

package ctrus.pa.bow.term.filter;

import org.apache.commons.cli.MissingOptionException;

import ctrus.pa.bow.core.BOWOptions;
import ctrus.pa.bow.core.DefaultOptions;
import ctrus.pa.bow.term.FilterFactory;
import ctrus.pa.bow.term.TermFilter;

public abstract class BaseFilterFactory implements FilterFactory {

	private BOWOptions _options = null;

	protected void setOptions(BOWOptions options) {
		_options = options;
	}
	
	protected String getOption(String key) throws MissingOptionException {
		return _options.getOption(key);
	}
	
	protected boolean hasOption(String key) {
		return _options.hasOption(key);
	}
	
	public abstract TermFilter createStopFilter();
	
	public TermFilter createLengthFilter() {
		LengthFilter lengthFilter = new LengthFilter();
		// Check if we want a different term length, else use default value
		if(_options.hasOption(DefaultOptions.MIN_WORD_LENGTH)) {
			try {
				String minLengthString = _options.getOption(DefaultOptions.MIN_WORD_LENGTH);
				int minLength = Integer.parseInt(minLengthString);
				lengthFilter.setMinLength(minLength);
			} catch (MissingOptionException e) {}			
		}
		
		return lengthFilter;	
	}
	
	public TermFilter createNumericFilter() {
		NumericFilter numericFilter = new NumericFilter();
		// check if vocabulary should contain numeric terms, 
		// default is to skip numeric terms
		if(_options.hasOption(DefaultOptions.RETAIN_NUMERAL)) {
			numericFilter.setEnabled(false);	
		}
		return numericFilter;
	}
}
