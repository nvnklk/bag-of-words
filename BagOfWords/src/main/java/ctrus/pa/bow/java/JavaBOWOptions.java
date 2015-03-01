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

package ctrus.pa.bow.java;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

import ctrus.pa.bow.core.DefaultOptions;

public class JavaBOWOptions extends DefaultOptions {
	
	public final static String METHOD_CHUNKING			= "methodChunking";	
	public final static String SPLIT_CAMELCASE			= "splitCamelCase";		
	public final static String CONSIDER_COPYRIGHT		= "considerCopyright";
	public final static String RETAIN_COMPOUND_WORDS	= "retainCompoundWords";	
	public final static String IGNORE_API_COMMENTS		= "ignoreAPIComments";
	public final static String IGNORE_INLINE_COMMENTS	= "ignoreInlineComments";
		
	// Closed constructor
	private JavaBOWOptions() {
		
	}

	@SuppressWarnings("static-access")
	@Override
	public void defineOptions() {
		
		Option o1 =  OptionBuilder.hasArg(false)
				.withDescription("Split camel cased terms")
				.create(SPLIT_CAMELCASE);
		addOption(o1);					
		
		Option o3 =  OptionBuilder.hasArg(false)
				.withDescription("Consider terms from copyright notice in source files")
				.create(CONSIDER_COPYRIGHT);
		addOption(o3);	
				
		Option o4 =  OptionBuilder.hasArg(false)
				.withDescription("Ignore API comments in source files")
				.create(IGNORE_API_COMMENTS);
		addOption(o4);	
				
		Option o5 =  OptionBuilder.hasArg(false)
				.withDescription("Ignore inline comments in source files")
				.create(IGNORE_INLINE_COMMENTS);
		addOption(o5);

		Option o6 =  OptionBuilder.hasArg(false)
				.withDescription("Create BOW model per method, default is per class")
				.create(METHOD_CHUNKING);
		addOption(o6);		
		
		Option o7 =  OptionBuilder.hasArg(false)
				.withDescription("Retain compound terms (eg.camel cased) in the model")
				.create(RETAIN_COMPOUND_WORDS);
		addOption(o7);		

	}

	@Override
	public String usageDescription() {
		return "java JavaBagOfWords [options] -ouputDir <dir> -docsDir <docs dir>";
	}

	@Override
	public String toolDescription() {
		return "Bag Of Words for Java Program";
	}
	
	public static class Factory {
		private static JavaBOWOptions _instance = null;
		
		public static JavaBOWOptions getInstance() {
			
			// Lazy singleton
			if(_instance == null) {
				_instance = new JavaBOWOptions();
			}
			return _instance;
		}
	}

}
