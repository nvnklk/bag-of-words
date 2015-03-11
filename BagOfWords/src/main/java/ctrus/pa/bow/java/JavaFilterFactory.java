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

package ctrus.pa.bow.java;

import java.io.File;

import org.apache.commons.cli.MissingOptionException;

import ctrus.pa.bow.core.BOWOptions;
import ctrus.pa.bow.core.DefaultOptions;
import ctrus.pa.bow.en.EnStopFilter;
import ctrus.pa.bow.term.FilterFactory;
import ctrus.pa.bow.term.TermFilter;
import ctrus.pa.bow.term.filter.BaseFilterFactory;
import ctrus.pa.bow.term.filter.StopFilter;
import ctrus.pa.util.CtrusHelper;

public class JavaFilterFactory extends BaseFilterFactory {

	private static JavaFilterFactory _instance = null;
	
	private JavaFilterFactory() {}

	public static FilterFactory newInstance(BOWOptions options) {
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
			CtrusHelper.printToConsole("Stop-word file - " + stopFileName);
			return new StopFilter(new File(stopFileName));			
		} catch (MissingOptionException ex) {
			String stopWordsFile = "/ctrus/pa/bow/java/sw-java.lst";
			CtrusHelper.printToConsole("Loading default Stop-word file - " + stopWordsFile);
			return new StopFilter(getClass().getResourceAsStream(stopWordsFile));
		}						
	}
}
