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

package ctrus.pa.bow.en;

import java.io.File;

import org.apache.commons.cli.MissingOptionException;

import ctrus.pa.bow.DefaultOptions;
import ctrus.pa.bow.core.BOWOptions;
import ctrus.pa.bow.term.FilterFactory;
import ctrus.pa.bow.term.TermFilter;
import ctrus.pa.bow.term.filter.BaseFilterFactory;
import ctrus.pa.bow.term.filter.StopFilter;

public class EnWordFilterFactory extends BaseFilterFactory {
	
	private static EnWordFilterFactory _instance = null;
	
	private EnWordFilterFactory() {}

	public static FilterFactory newInstance(BOWOptions options) {
		if(_instance == null) {
			_instance = new EnWordFilterFactory();
			_instance.setOptions(options);
		}
		return _instance;
	}

	public TermFilter createStopFilter() {
		StopFilter stopFilter = null; 
		if(hasOption(DefaultOptions.STOP_WORDS_FILE)) {
			File stopFile;
			try {
				stopFile = new File(getOption(DefaultOptions.STOP_WORDS_FILE));
				stopFilter = EnStopFilter.newInstance(stopFile);
			} catch (MissingOptionException e) {
				stopFilter = EnStopFilter.newInstance(EnStopFilter.StopWordsSize.SMALL);
			}
			
		} else {
			// Default stop filter
			stopFilter = EnStopFilter.newInstance(EnStopFilter.StopWordsSize.SMALL);
		}
		return stopFilter;
	}

}
