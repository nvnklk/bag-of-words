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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;

import ctrus.pa.util.CtrusHelper;

public class StopFilter extends BaseFilter {
	
	private HashSet<String> _stopWords = new HashSet<String>();
	
	protected StopFilter() {}
	
	public StopFilter(File stopFile) {
		fillStopWordsFromFile(stopFile);
	}
	
	public StopFilter(InputStream stopFileStream) {
		fillStopWordsFromFile(stopFileStream);
	}
	
	protected void fillStopWordsFromFile(File stopFile) {
		try {
			FileInputStream fis = new FileInputStream(stopFile);
			fillStopWordsFromFile(fis);
		} catch (IOException ex) {
			CtrusHelper.printToConsole("Warning! Could not find stop words file...");
		}		
	}
	
	protected void fillStopWordsFromFile(InputStream fis) {		
		try {
			// Read stop word file and prepare stop word list
			
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		
			String line = null;
			int count = 0;
			while ((line = br.readLine()) != null) {
				if(!line.startsWith("#") && !line.isEmpty()) {
					add(line);
					count++;
				}
			}
			CtrusHelper.printToConsole("Number of stop words loaded - " + count);
			br.close();
			fis.close();
		} catch(IOException ex) {
			CtrusHelper.printToConsole("Warning! error in reading stop words file...");
		}
	}
	
	protected void add(String term) {
		_stopWords.add(term.toLowerCase());
	}
	
	public boolean filter(String term) {
		return _stopWords.contains(term.toLowerCase());
	}
	
}
