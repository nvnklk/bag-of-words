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
import java.io.Serializable;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.io.IOUtils;
import org.mapdb.DB;
import org.mapdb.DBMaker;

public class Vocabulary implements Serializable {
	
	private static final long serialVersionUID = -668965040667128267L;
	private static AtomicLong term_counter = new AtomicLong(0);
	private static AtomicLong doc_counter = new AtomicLong(0);
	
	private static Vocabulary _instance = null;
	private ConcurrentNavigableMap<String, TermMeta> _termVocabulary = null;
	private ConcurrentNavigableMap<Long, TermFreq> _termFrequency = null;
	private ConcurrentNavigableMap<String, DocMeta> _docVocabulary = null;
		
	private class DocMeta implements Serializable {
		
		private static final long serialVersionUID = 3217146017692636352L;
		public long doc_id = 0;
		public String file_name = "";		
	}
	
	private class TermFreq implements Serializable {
	
		private static final long serialVersionUID = 7583158016445398900L;
		public long bucket = 0;
		public long freq = 1;
	}
	
	private class TermMeta implements Serializable {
		
		private static final long serialVersionUID = 5448836097415306367L;
		public long term_id = 0;
		public long doc_id = 0;
		public long  freq = 1;		
	}

	private Vocabulary() {}
	
	private void init() {
		// a disk based strategy to handle large numbers of vocabulary terms
		File vocabularyFile = new File("bow.db");
		DB vocabularyDB = DBMaker.newFileDB(vocabularyFile)
								 .compressionEnable()
								 .deleteFilesAfterClose()
								 .closeOnJvmShutdown()
								 .make();
		_termVocabulary = vocabularyDB.getTreeMap("TERM_VOCABULARY");
		_docVocabulary = vocabularyDB.getTreeMap("DOC_VOCABULARY");
		_termFrequency = vocabularyDB.getTreeMap("TERM_FREQUENCY");
	}
	
	public void addTerm(String term, String doc) {
		// Add to vocabulary
		if(_termVocabulary.containsKey(term)) {				
			TermMeta tm = _termVocabulary.get(term);								
			tm.freq++;					
		} else {
			if(!_docVocabulary.containsKey(doc))
				throw new IllegalArgumentException("Document " + doc + " not found in the vocabulary");
			
			TermMeta tm = new TermMeta();
			tm.term_id = term_counter.incrementAndGet();
			tm.doc_id = _docVocabulary.get(doc).doc_id;			
			_termVocabulary.put(term, tm);
		}
	}
	
	public void addDocument(String docref, String file) {
		// Add to vocabulary
		if(!_docVocabulary.containsKey(docref)) {
			DocMeta dm = new DocMeta();
			dm.doc_id = doc_counter.incrementAndGet();
			dm.file_name = file;
			_docVocabulary.put(docref, dm);
		}			
	}
	
	public final void writeDocVocabularyTo(OutputStream out) throws IOException {
		IOUtils.write("DOC_REF,DOC_NAME\n", out);
		for(String docref : _docVocabulary.keySet()) {
			DocMeta d = _docVocabulary.get(docref); 
			IOUtils.write(docref + "," + d.file_name + "\n", out);			
		}
	}
	
	public final void writeTermVocabularyTo(OutputStream out) throws IOException {
		IOUtils.write("TERM,FREQ \n", out);		
		for(String term : _termVocabulary.keySet()) {
			TermMeta t = _termVocabulary.get(term);
			// Add the term frequency
			if(_termFrequency.containsKey(t.freq)) {
				TermFreq tfreq = _termFrequency.get(t.freq);	
				tfreq.freq++;							
			} else {
				TermFreq tfq = new TermFreq();
				tfq.bucket = t.freq;				
				_termFrequency.put(t.freq, tfq);
			}
			IOUtils.write(term + "," + t.freq + "\n", out);
		}			
	}
	
	public final void writeTermFrequencyTo(OutputStream out) throws IOException {
		IOUtils.write("BUCKET,FREQ\n", out);		
		for(Long bucket : _termFrequency.keySet()) {
			TermFreq tfq = _termFrequency.get(bucket);
			IOUtils.write(tfq.bucket + "," + tfq.freq + "\n", out);
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
