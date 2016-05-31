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

package ctrus.pa.bow.java;

import java.io.File;

import org.apache.commons.cli.MissingOptionException;

import ctrus.pa.bow.DefaultOptions;
import ctrus.pa.bow.core.BOWOptions;
import ctrus.pa.bow.en.EnStopFilter;
import ctrus.pa.bow.en.EnStopFilter.StopWordsSize;
import ctrus.pa.bow.term.TermFilter;
import ctrus.pa.bow.term.filter.BaseFilterFactory;
import ctrus.pa.bow.term.filter.StopFilter;
import ctrus.pa.util.CtrusHelper;

public class JavaFilterFactory extends BaseFilterFactory {

	private static JavaFilterFactory _instance = null;
	
	private JavaFilterFactory() {}

	public static JavaFilterFactory newInstance(BOWOptions options) {
		if(_instance == null) {
			_instance = new JavaFilterFactory();
			_instance.setOptions(options);
		}
		return _instance;
	}
	
	public TermFilter createStopFilter() {
		String stopFileName;
		try {
			stopFileName = getOption(DefaultOptions.STOP_WORDS_FILE);
			CtrusHelper.printToConsole("Loading stop-words file - " + stopFileName);
			return new StopFilter(new File(stopFileName));			
		} catch (MissingOptionException ex) {
			String stopWordsFile = "/ctrus/pa/bow/java/sw-java.lst";
			CtrusHelper.printToConsole("Loading default Stop-word file - " + stopWordsFile);
			return new StopFilter(getClass().getResourceAsStream(stopWordsFile));
		}						
	}
	
	public TermFilter createStopFilterForComments() {
		CtrusHelper.printToConsole("Choosing stop-words for Java comments ...");
		return EnStopFilter.newInstance(StopWordsSize.SMALL);
	}
}
