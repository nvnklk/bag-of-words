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
