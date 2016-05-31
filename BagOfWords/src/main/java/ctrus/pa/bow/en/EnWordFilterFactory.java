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
