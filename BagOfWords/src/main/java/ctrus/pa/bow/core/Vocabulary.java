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

package ctrus.pa.bow.core;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ConcurrentNavigableMap;

import org.apache.commons.io.IOUtils;
import org.mapdb.DB;
import org.mapdb.DBMaker;

public class Vocabulary {
	
	private static Vocabulary _instance = null;
	private ConcurrentNavigableMap<String, Integer> _vocabulary = null;

	private Vocabulary() {}
	
	private void init() {
		// a disk based strategy to handle large numbers of vocabulary terms
		File vocabularyFile = new File("bow.db");
		DB vocabularyDB = DBMaker.newFileDB(vocabularyFile)
								 .compressionEnable()
								 .deleteFilesAfterClose()
								 .closeOnJvmShutdown()
								 .make();
		_vocabulary = vocabularyDB.getTreeMap("Vocabulary");
	}
	
	public void add(String term) {
		// Add to vocabulary
		if(_vocabulary.containsKey(term)) {				
			Integer count = _vocabulary.get(term);								
			_vocabulary.put(term, ++count);					
		} else {
			_vocabulary.put(term, 1);
		}
	}
	
	public final void writeTo(OutputStream out) throws IOException {
		
		for(String term : _vocabulary.keySet()) {
			IOUtils.write(term + "," + _vocabulary.get(term) + "\n", out);
		}
	}
	
	public static Vocabulary getInstance() {
		if(_instance == null) {
			// Thread safety
			synchronized(Vocabulary.class) {
				if(_instance == null) {
					_instance = new Vocabulary();
					_instance.init();
				}
			}
		}
		return _instance;
	}
}
