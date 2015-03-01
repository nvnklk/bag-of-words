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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;

import ctrus.pa.util.CtrusHelper;

public class StopFilter extends BaseFilter {
	
	private HashSet<String> _stopWords = new HashSet<String>();
	
	protected StopFilter() {}
	
	public StopFilter(File stopFile) {
		fillStopWordsFromFile(stopFile);
	}
		
	protected void fillStopWordsFromFile(File stopFile) {		
		try {
			// Read stop word file and prepare stop word list
			FileInputStream fis = new FileInputStream(stopFile);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		
			String line = null;
			while ((line = br.readLine()) != null) {
				if(!line.startsWith("#") && !line.isEmpty())
					add(line);
			}
			br.close();
			fis.close();
		} catch(IOException ex) {
			CtrusHelper.printToConsole("Warning! Could not read stop words file...");
		}
	}
	
	protected void add(String term) {
		_stopWords.add(term.toLowerCase());
	}
	
	public boolean filter(String term) {
		return _stopWords.contains(term.toLowerCase());
	}
	
}
