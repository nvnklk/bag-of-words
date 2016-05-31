/*******************************************************************************
 * Copyright (c) 2015, 2016 
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
package ctrus.pa.bow;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import ctrus.pa.bow.core.BOWOptions;
import ctrus.pa.bow.core.UnWeightedMultiBagOfWords;
import ctrus.pa.bow.core.Vocabulary;
import ctrus.pa.util.CtrusHelper;

public abstract class DefaultMultiBagOfWords extends UnWeightedMultiBagOfWords {

	protected static final String DEFAULT_OUTPUT_DIR 		= "output";	
	protected static final String DEFAULT_OUTPUT_FILE 		= "bow.txt";
	protected static final String DEFAULT_TERM_VOCAB_FILE 	= "term_vocab.txt";
	protected static final String DEFAULT_TERM_FREQ_FILE 	= "vocab_freq.txt";
	protected static final String DEFAULT_DOC_VOCAB_FILE 	= "doc.txt";
	
	protected BOWOptions 		_options 		= null;	
	private boolean				_singleFileOut  = false;
	private String 		_singleOutputFileName	= null;
	private File				_outParentDir   = null;
	
	private Map<String,File>		  _outDir	= new HashMap<String, File>();
	private Map<String, OutputStream> _outs 	= new HashMap<String, OutputStream>();	
	
	protected abstract void setup();
	
	public void setup(BOWOptions options) {
		
		this._options = options;
		
		String outParentDirString = null;
		try{
			outParentDirString  = getOption(DefaultOptions.OUTPUT_DIR);
		} catch (MissingOptionException ex) {
			outParentDirString = DEFAULT_OUTPUT_DIR; 
		}
		_outParentDir = new File(outParentDirString);
		if(!_outParentDir.exists()) _outParentDir.mkdirs();	
		
		CtrusHelper.printToConsole("Output folder - " + _outParentDir.getAbsolutePath());
		
		// Prepare the single output file
		_singleFileOut = hasOption(DefaultOptions.OUTPUT_SINGLE_FILE); 
		if(_singleFileOut) {			
			try {
				_singleOutputFileName = getOption(DefaultOptions.OUTPUT_SINGLE_FILE);
			} catch (MissingOptionException e) {
				_singleOutputFileName = DEFAULT_OUTPUT_FILE;
			}			
		}
		
		// perform additional setup
		setup();		
	}
	
	protected String getOption(String key) throws MissingOptionException {
		return _options.getOption(key);
	}
	
	protected boolean hasOption(String key) {
		return _options.hasOption(key);
	}
	
	// Write the terms collected to output file
	protected <E extends Enum<E>> void writeToOutput(String doc, Class<E> identifier) throws IOException {
		E[] values = identifier.getEnumConstants();
		for (E value : values) {
			String key = value.toString();
			
			// Check if the identifier directories are created
			if(!_outDir.containsKey(key)) {
				File f = new File(_outParentDir, key);
				if(!f.exists()) f.mkdirs();
				_outDir.put(key, f);
			}
			
			if(getTermCount(value) > 0) {
				if(_singleFileOut){
					// Check if we have a output stream available
					if(!_outs.containsKey(key)) {					
						_outs.put(key, new FileOutputStream(new File(_outDir.get(key), _singleOutputFileName)));
					}			
					IOUtils.write(doc + "=", _outs.get(key));
					writeTo(_outs.get(key), value);
				} else {
					// replace special characters in the file name 
					doc = doc.replace('<','[');
					doc = doc.replace('>',']');
					
					FileOutputStream out = new FileOutputStream(new File(_outDir.get(key), doc));
					writeTo(out, value);					
					out.close();
					out = null;				
				}						
			}
		}
	}	
	
	protected String getDocumentId(String name) {
		String did = (_options.hasOption(DefaultOptions.PRESERVE_DOC_ID)) 
					? name : CtrusHelper.uniqueId(name).toString();
		// Handle duplicate document names
		// Duplicate names are appended with a count
		Vocabulary voc = Vocabulary.getInstance(_options);
		int count = 1;
		String newid = null; 
		while(voc.hasDocument(did)) {
			newid = name + "_" + count;
			did = (_options.hasOption(DefaultOptions.PRESERVE_DOC_ID)) 
					? newid : CtrusHelper.uniqueId(newid).toString();
			count++;
		}
		return did;
	}
	
	protected Collection<File> getSourceDocuments(String wildCard) throws MissingOptionException {			
		File sourceDir = new File(_options.getOption(DefaultOptions.SOURCE_DIR));
		CtrusHelper.printToConsole("Choosen source folder - " + sourceDir.getAbsolutePath());		
		if(sourceDir.exists()){
			return FileUtils.listFiles(sourceDir, new WildcardFileFilter(wildCard), DirectoryFileFilter.DIRECTORY);
		} else {
			throw new MissingOptionException("Unable to find source directory!");
		}
	}	

}
