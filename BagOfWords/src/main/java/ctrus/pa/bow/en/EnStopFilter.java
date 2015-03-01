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

package ctrus.pa.bow.en;

import java.io.File;

import ctrus.pa.bow.term.filter.StopFilter;
import ctrus.pa.util.CtrusHelper;

public class EnStopFilter extends StopFilter {
	
	public enum StopWordsSize {
		SMALL, MEDIUM, LARGE1, LARGE2
	}
	
	private EnStopFilter(File stopWordsFile) {
		fillStopWordsFromFile(stopWordsFile);		
	}
	
	public static StopFilter newInstance(File stopWordsFile) {
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
		
		String stopWordFile = EnStopFilter.class.getResource(stopWordFileString).getFile();
		CtrusHelper.printToConsole("Stop-word file - " + stopWordFile);
		return new EnStopFilter(new File(stopWordFile));
	}

	
}
