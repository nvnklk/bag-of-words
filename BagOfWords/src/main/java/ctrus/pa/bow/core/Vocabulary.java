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

package ctrus.pa.bow.core;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.io.IOUtils;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import ctrus.pa.util.CtrusHelper;
import ctrus.pa.util.FNVHash;

public class Vocabulary implements Serializable {
	
	private static final long serialVersionUID = -668965040667128267L;
	private static AtomicLong term_counter = new AtomicLong(0);
	private static AtomicLong doc_counter = new AtomicLong(0);
	
	private static Vocabulary 	_instance = null;
	private BOWOptions 			_options = null;
	private ConcurrentNavigableMap<String, TermMeta> _termVocabulary = null;
	private ConcurrentNavigableMap<Long, TermFreq> _termFrequency = null;
	private ConcurrentNavigableMap<String, DocMeta> _docVocabulary = null;
	
	private static final List<FNVHash> hashFunctions = FNVHash.newHashFunctions(10);
		
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
		public List<String> minHash = null;
	}

	private Vocabulary(BOWOptions options) {
		_options = options;
	}
	
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
			tm.minHash = CtrusHelper.minHash(term, 3, hashFunctions);
			_termVocabulary.put(term, tm);
		}
	}
	
	public boolean hasDocument(String docref) {
		return _docVocabulary.containsKey(docref);
	}
	
	public void addDocument(String docref, String file) {
		// Add to vocabulary
		if(!hasDocument(docref)) {
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
		IOUtils.write("TERM,FREQ,HASH \n", out);		
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
			StringBuffer sb = new StringBuffer();
			if(t.minHash != null) {
				for(String s : t.minHash)
					sb.append(s).append(" ");
				IOUtils.write(term + "," + t.freq + "," + sb.toString() + "\n", out);
			} else {
				IOUtils.write(term + "," + t.freq + "\n", out);
			}
		}			
	}
	
	public final void writeTermFrequencyTo(OutputStream out) throws IOException {
		IOUtils.write("BUCKET,FREQ\n", out);		
		for(Long bucket : _termFrequency.keySet()) {
			TermFreq tfq = _termFrequency.get(bucket);
			IOUtils.write(tfq.bucket + "," + tfq.freq + "\n", out);
		}			
	}

	public final void reset() {
		_termVocabulary.clear();
		_termFrequency.clear();
		_docVocabulary.clear();
	}
	
	public static Vocabulary getInstance() {
		// Quick fix, need to look at a good strategy to create and access instance
		assert _instance != null : "Vocabulary not initialized with options";		
		return _instance;
	}
	
	public static Vocabulary getInstance(BOWOptions options) {
		if(_instance == null) {
			// Thread safety
			synchronized(Vocabulary.class) {
				if(_instance == null) {
					_instance = new Vocabulary(options);
					_instance.init();
				}
			}
		}
		return _instance;
	}
}
