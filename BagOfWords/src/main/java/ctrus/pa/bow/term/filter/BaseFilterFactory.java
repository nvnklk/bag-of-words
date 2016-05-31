/*******************************************************************************
 * Copyright (c) 2015, 2016 
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

package ctrus.pa.bow.term.filter;

import org.apache.commons.cli.MissingOptionException;

import ctrus.pa.bow.DefaultOptions;
import ctrus.pa.bow.core.BOWOptions;
import ctrus.pa.bow.term.FilterFactory;
import ctrus.pa.bow.term.TermFilter;
import ctrus.pa.util.CtrusHelper;

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
			} catch (MissingOptionException e) {
				lengthFilter.setEnabled(false);
			} catch (NumberFormatException e) {
				CtrusHelper.printToConsole("Warning! Term length filter disabled - unable to read input value");
				lengthFilter.setEnabled(false);
			}			
		} else {
			// No length option specified
			lengthFilter.setEnabled(false);
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
