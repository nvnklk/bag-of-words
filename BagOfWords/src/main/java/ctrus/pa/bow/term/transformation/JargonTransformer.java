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
		if(isJargon(term))
			return _jargonsMap.get(term);
		else 
			return term;
	}
		
	public static class Factory {
		
		public static JargonTransformer newInstance(File jargonsFile) {
			JargonTransformer _instance = new JargonTransformer();
			
			try {
				// Read stop word file and prepare stop word list
				FileInputStream fis = new FileInputStream(jargonsFile);
				BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			
				String line = null;
				while ((line = br.readLine()) != null) {
					if(!line.startsWith("#")) {
						StringTokenizer st = new StringTokenizer(line, " ");
						_instance.add(st.nextToken(), st.nextToken());
						st = null;
					}
				}
				br.close();
			} catch(IOException ex) {
				CtrusHelper.printToConsole("Warning! could not read stop word file, using default");
				_instance = null;
				return newInstance();
			}
			
			return _instance;
		}
		
		public static JargonTransformer newInstance() {
			JargonTransformer _instance = new JargonTransformer();
			
			return _instance;
		}
	}
}
