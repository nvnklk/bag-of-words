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

import java.io.File;

import org.apache.commons.cli.MissingOptionException;

import ctrus.pa.bow.core.BOWOptions;
import ctrus.pa.bow.core.DefaultOptions;
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
