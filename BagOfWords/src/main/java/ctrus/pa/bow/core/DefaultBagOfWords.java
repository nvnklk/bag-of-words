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

import ctrus.pa.util.CtrusHelper;

public abstract class DefaultBagOfWords extends UnWeightedBagOfWords {
		
	protected static final String DEFAULT_OUTPUT_DIR 		= "output";
	protected static final String DEFAULT_OUTPUT_FILE 		= "bow.txt";
	protected static final String DEFAULT_TERM_VOCAB_FILE 	= "term_vocab.txt";
	protected static final String DEFAULT_TERM_FREQ_FILE 	= "term_freq.txt";
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
		String outputDirString = null;
		try{
			outputDirString = getOption(DefaultOptions.OUTPUT_DIR);
		} catch (MissingOptionException ex) {
			outputDirString = DEFAULT_OUTPUT_DIR; 
		}
		_outputDir = new File(outputDirString);
		if(!_outputDir.exists()) _outputDir.mkdirs();
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
	protected void writeToOutput(String doc) throws IOException {				
		if(_singleFileOut){
			IOUtils.write(doc + "=", _out);
			writeTo(_out);
		} else {			
			_out = new FileOutputStream(new File(_outputDir, doc));
			writeTo(_out);					
			_out.close();
			_out = null;
		} 
		
	}	
	
	protected Collection<File> getSourceDocuments(String wildCard) throws MissingOptionException {		
		File sourceDir = new File(_options.getOption(DefaultOptions.SOURCE_DIR));
		if(sourceDir.exists()){
			return FileUtils.listFiles(sourceDir, new WildcardFileFilter(wildCard), DirectoryFileFilter.DIRECTORY);
		} else {
			throw new MissingOptionException("Unable to find source directory!");
		}
	}
	
	protected void printVocabulary() throws IOException {
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
			Vocabulary.getInstance().writeTermVocabularyTo(fos1);
			fos1.close();
			
			// Write frequency
			FileOutputStream fos2 = FileUtils.openOutputStream(new File(outputFileString2));
			Vocabulary.getInstance().writeTermFrequencyTo(fos2);
			fos2.close();
			
			// Write doc
			FileOutputStream fos3 = FileUtils.openOutputStream(new File(outputFileString3));
			Vocabulary.getInstance().writeDocVocabularyTo(fos3);
			fos3.close();
		}
	}
}