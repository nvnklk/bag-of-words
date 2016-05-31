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

package ctrus.pa.bow.en;

import java.io.File;
import java.io.InputStream;

import ctrus.pa.bow.term.filter.StopFilter;
import ctrus.pa.util.CtrusHelper;

public class EnStopFilter extends StopFilter {
	
	public enum StopWordsSize {
		SMALL, MEDIUM, LARGE1, LARGE2
	}
	
	private EnStopFilter(File stopWordsFile) {
		fillStopWordsFromFile(stopWordsFile);		
	}
	
	private EnStopFilter(InputStream stopFileStream) {
		fillStopWordsFromFile(stopFileStream);		
	}	
	
	public static StopFilter newInstance(File stopWordsFile) {
		CtrusHelper.printToConsole("Loading default Stop-word file - " + stopWordsFile);
		return new EnStopFilter(stopWordsFile); 
	}
	
	public static StopFilter newInstance(StopWordsSize size) {
		String stopWordFileString;
		
		switch (size) {
			case LARGE1: stopWordFileString = "sw-en-large1.lst"; break;
			case LARGE2: stopWordFileString = "sw-en-large2.lst"; break;
			case MEDIUM: stopWordFileString = "sw-en-medium.lst"; break;
			case SMALL : 
			default : stopWordFileString = "sw-en-small.lst"; break;
		}
		stopWordFileString = "/ctrus/pa/bow/en/" + stopWordFileString;
		CtrusHelper.printToConsole("Loading default Stop-word file - " + stopWordFileString);
		return new EnStopFilter(EnStopFilter.class.getResourceAsStream(stopWordFileString));			
	}

	
}
