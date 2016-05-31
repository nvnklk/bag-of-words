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

package ctrus.pa.bow.term.transformation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import ctrus.pa.util.CtrusHelper;


public class JargonTransformer extends BaseTransformer {

	private Map<String, String> _jargonsMap = null;
	
	// Closed Constructor
	private JargonTransformer() {
		_jargonsMap = new HashMap<String, String>();
	}
	
	private void add(String jargon, String term) {
		_jargonsMap.put(jargon, term);
	}
	
	public boolean isJargon(String jargon) {
		return _jargonsMap.containsKey(jargon);
	}
	
	public String transform(String term) {
		// Possibility of having a compound word
		StringBuffer sb = new StringBuffer();
		for(String eachTerm : getTerms(term)) {
			if(isJargon(eachTerm))
				sb.append(_jargonsMap.get(eachTerm));
			else
				sb.append(eachTerm);
			sb.append(" ");
		}	
		
		return sb.toString().trim();
	}
		
	public static class Factory {
		
		public static JargonTransformer newInstance(File jargonsFile) {
			JargonTransformer _instance = new JargonTransformer();
			
			try {
				// Read stop word file and prepare stop word list
				FileInputStream fis = new FileInputStream(jargonsFile);
				BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			
				String line = null;
				int countJargons = 0;
				while ((line = br.readLine()) != null) {
					line = line.trim(); // Remove white spaces
					if(!line.startsWith("#") && line.length() > 0) {
						StringTokenizer st = new StringTokenizer(line, " ");
						_instance.add(st.nextToken(), st.nextToken());
						st = null;
						countJargons++;
					}
				}
				CtrusHelper.printToConsole("Number of jargons loaded - " + countJargons);
				br.close();
			} catch(IOException ex) {
				CtrusHelper.printToConsole("Warning! could not read jargon file, disabling jargon transformer...");
				_instance = null;
				_instance = newInstance();
				_instance.setEnabled(false);
			}
			
			return _instance;
		}
		
		public static JargonTransformer newInstance() {
			JargonTransformer _instance = new JargonTransformer();
			
			return _instance;
		}
	}
}
