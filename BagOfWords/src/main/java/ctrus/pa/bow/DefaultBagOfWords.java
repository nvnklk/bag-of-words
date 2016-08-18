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

package ctrus.pa.bow;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import ctrus.pa.bow.core.BOWOptions;
import ctrus.pa.bow.core.UnWeightedBagOfWords;
import ctrus.pa.bow.core.Vocabulary;
import ctrus.pa.util.CtrusHelper;

public abstract class DefaultBagOfWords extends UnWeightedBagOfWords {
		
	protected static final String DEFAULT_OUTPUT_DIR 		= "output";	
	protected static final String DEFAULT_OUTPUT_FILE 		= "bow.txt";
	protected static final String DEFAULT_TERM_VOCAB_FILE 	= "term_vocab.txt";
	protected static final String DEFAULT_TERM_FREQ_FILE 	= "vocab_freq.txt";
	protected static final String DEFAULT_DOC_VOCAB_FILE 	= "doc.txt";
	
	protected BOWOptions 		_options 		= null;
	protected File				_outputDir		= null;
	
	private OutputStream 		_out 			= null;
	private boolean				_singleFileOut  = false;
	
	protected DefaultBagOfWords() {}
		
	protected abstract void setup();
	
	public void setup(BOWOptions options) {
		// Setup options
		this._options = options;
		
		// Prepare the output directory
		setupOutputDir();
		CtrusHelper.printToConsole("Output folder - " + _outputDir.getAbsolutePath());
		
		// Prepare the single output file
		_singleFileOut = hasOption(DefaultOptions.OUTPUT_SINGLE_FILE); 
		if(_singleFileOut) {
			String singleOutputFileName;
			try {
				singleOutputFileName = getOption(DefaultOptions.OUTPUT_SINGLE_FILE);
			} catch (MissingOptionException e) {
				singleOutputFileName = DEFAULT_OUTPUT_FILE;
			}			
			try {
				_out =  new FileOutputStream(new File(_outputDir, singleOutputFileName));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		// Setup vocabulary
		Vocabulary voc = Vocabulary.getInstance(_options);		
		
		// perform additional setup
		setup();
	}
	
	protected void setupOutputDir() {
		String outputDirString = null;
		try{
			outputDirString = getOption(DefaultOptions.OUTPUT_DIR);
		} catch (MissingOptionException ex) {
			outputDirString = DEFAULT_OUTPUT_DIR; 
		}
		_outputDir = new File(outputDirString);
		if(!_outputDir.exists()) _outputDir.mkdirs();		
	}
	
	protected String getOption(String key) throws MissingOptionException {
		return _options.getOption(key);
	}
	
	protected boolean hasOption(String key) {
		return _options.hasOption(key);
	}

	// Write the terms collected to output file
	protected void writeToOutput(String doc) throws IOException {				
		if(_singleFileOut){
			IOUtils.write(doc + "=", _out);
			writeTo(_out);
		} else {
			// replace special characters in the file name 
			doc = doc.replace('<','[');
			doc = doc.replace('>',']');
			
			_out = new FileOutputStream(new File(_outputDir, doc));
			writeTo(_out);					
			_out.close();
			_out = null;
		} 		
	}	
	
	protected String getDocumentId(String name) {

		Vocabulary voc = Vocabulary.getInstance(_options);
		String did = (_options.hasOption(DefaultOptions.PRESERVE_DOC_ID)) 
					? name : CtrusHelper.uniqueId(name).toString();
		
		// Below lines of code was to ensure that there are no duplicate file names as 
		// getSourceDocuments(String) was returning all files under directory along with 
		// its sub-directory. 

		/*
		// Handle duplicate document names
		// Duplicate names are appended with a count		
		int count = 1;
		String newid = null; 

		while(voc.hasDocument(did)) {
			newid = name + "_" + count;
			did = (_options.hasOption(DefaultOptions.PRESERVE_DOC_ID)) 
					? newid : CtrusHelper.uniqueId(newid).toString();
			count++;
		}
		*/
		
		return did;
	}
	
	protected Collection<File> getSourceDocuments(String wildCard) throws MissingOptionException {			
		File sourceDir = new File(_options.getOption(DefaultOptions.SOURCE_DIR));
		CtrusHelper.printToConsole("Choosen source folder - " + sourceDir.getAbsolutePath());		
		if(sourceDir.exists()){
			// Below lines of code was to provide all the files under a directory along with its sub-directories
			/*
			return FileUtils.listFiles(sourceDir, new WildcardFileFilter(wildCard), DirectoryFileFilter.DIRECTORY);			
			*/

			// Provide only the files under the current directory (file under sub directories are not included)
			// Handle the wildcards
			if (wildCard.equals("*")) {
				return FileUtils.listFiles(sourceDir, null, false);
			} else if (wildCard.startsWith("*.")) {				
				String newWildCard = wildCard.substring(2);
				String[] exts = {newWildCard};
				return FileUtils.listFiles(sourceDir, exts, false);
			} else {
				String[] exts = {wildCard};
				return FileUtils.listFiles(sourceDir, exts, false);
			}
			
		} else {
			throw new MissingOptionException("Unable to find source directory!");
		}
	}
	
	public void printVocabulary(boolean clearVocabulary) throws IOException {
		if(_options.hasOption(DefaultOptions.PRINT_VOCABULARY)) {
			String outputFileString;
			try {
				outputFileString = _options.getOption(DefaultOptions.OUTPUT_DIR);
				if(!outputFileString.endsWith(File.separator))
					outputFileString = outputFileString + File.separator;
			} catch(MissingOptionException ex) {
				outputFileString = "." + File.separator;
			}
			String outputFileString1 = outputFileString + File.separator + DEFAULT_TERM_VOCAB_FILE;
			String outputFileString2 = outputFileString + File.separator + DEFAULT_TERM_FREQ_FILE;
			String outputFileString3 = outputFileString + File.separator + DEFAULT_DOC_VOCAB_FILE;
			
			CtrusHelper.printToConsole("Writing vocabulary to " + outputFileString + " ...");
			
			// Write terms
			FileOutputStream fos1 = FileUtils.openOutputStream(new File(outputFileString1));
			Vocabulary.getInstance(_options).writeTermVocabularyTo(fos1);
			fos1.close();
			
			// Write frequency
			FileOutputStream fos2 = FileUtils.openOutputStream(new File(outputFileString2));
			Vocabulary.getInstance(_options).writeTermFrequencyTo(fos2);
			fos2.close();
			
			// Write doc
			FileOutputStream fos3 = FileUtils.openOutputStream(new File(outputFileString3));
			Vocabulary.getInstance(_options).writeDocVocabularyTo(fos3);
			fos3.close();			
		}

		if(clearVocabulary) {
			Vocabulary.getInstance().reset();
		}
	}
}
