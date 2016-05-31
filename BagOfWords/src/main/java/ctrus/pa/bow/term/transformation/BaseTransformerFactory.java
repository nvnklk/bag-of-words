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

import java.io.File;

import org.apache.commons.cli.MissingOptionException;

import ctrus.pa.bow.DefaultOptions;
import ctrus.pa.bow.core.BOWOptions;
import ctrus.pa.bow.term.TermTransformer;
import ctrus.pa.bow.term.TransformerFactory;
import ctrus.pa.util.CtrusHelper;

public abstract class BaseTransformerFactory implements TransformerFactory {
	
	private BOWOptions _options = null;
		
	protected void setOptions(BOWOptions options) {
		_options = options;
	}
	
	public String getOption(String key) throws MissingOptionException {
		return _options.getOption(key);
	}
	
	public boolean hasOption(String key) {
		return _options.hasOption(key);
	}
		
	public TermTransformer createSanityTransformer() {
		SanityTransformer st = new SanityTransformer();
		if(_options.hasOption(DefaultOptions.IGNORE_SPECIAL_CHARS)) {
			String ignoreSplChars;
			try {
				ignoreSplChars = _options.getOption(DefaultOptions.IGNORE_SPECIAL_CHARS);
				CtrusHelper.printToConsole("Special characters retained - " + ignoreSplChars);
				for(char c : ignoreSplChars.toCharArray())
					st.escapeChar(c);			
			} catch (MissingOptionException e) {
				CtrusHelper.printToConsole("Warning: Could not read special characters to retain...");
			}
		}
		return st;
	}
	
	public TermTransformer createChunkTransformer() {
		ChunkTransformer chunckTransformer = new ChunkTransformer();
		try {
			String chunckChars = _options.getOption(DefaultOptions.TERM_CHUNK_CHARS);
			CtrusHelper.printToConsole("Additional chars for term chuncking - " + chunckChars);
			for(char c : chunckChars.toCharArray())
				chunckTransformer.addChunckChar(c);
		} catch (MissingOptionException e) {
			chunckTransformer.setEnabled(false);
		}			
		return chunckTransformer;
	}
	
	public TermTransformer createJargonTransformer() {
		JargonTransformer jargonTransformer = null;		
		try{
			File jargonsFile = new File(_options.getOption(DefaultOptions.REPLACE_JARGONS));
			jargonTransformer = JargonTransformer.Factory.newInstance(jargonsFile);
		} catch(MissingOptionException ex) {
			// Use default
			jargonTransformer = JargonTransformer.Factory.newInstance();
			// No jargon file provided, disable this transformer
			jargonTransformer.setEnabled(false);
		}
		return jargonTransformer;		
	}

	public TermTransformer createLowercaseTransformer() {
		LowercaseTransformer lowercaseTransformer = new LowercaseTransformer();
		if(_options.hasOption(DefaultOptions.CASE_SENSITIVE)) {
			lowercaseTransformer.setEnabled(false);
		}
		return lowercaseTransformer;
	}
	
	public TermTransformer createLengthTransformer() {
		LengthTransformer lengthTransformer = new LengthTransformer();
		if(_options.hasOption(DefaultOptions.HASH_TERMS)) {
			try {
				String length = _options.getOption(DefaultOptions.HASH_TERMS);
				lengthTransformer.setMaxTermLength(Integer.parseInt(length));
			} catch(MissingOptionException ex) {}
		} else {
			lengthTransformer.setEnabled(false);
		}
		return lengthTransformer;
	}
	
	public abstract TermTransformer createStemmingTransformer();
}
