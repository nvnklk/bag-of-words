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

package ctrus.pa.bow.java;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

import ctrus.pa.bow.DefaultOptions;

public class JavaBOWOptions extends DefaultOptions {

	
	private static final long serialVersionUID = -2557201193069657135L;
	
	public final static String METHOD_CHUNKING			= "methodChunking";	
	public final static String SPLIT_CAMELCASE			= "splitCamelCase";		
	public final static String CONSIDER_COPYRIGHT		= "considerCopyright";
//	public final static String RETAIN_COMPOUND_WORDS	= "retainCompoundWords";	
	public final static String IGNORE_COMMENTS			= "ignoreComments";
	public final static String NO_STEMMING				= "noStemming";
	public final static String STATE_ANALYSIS			= "stateAnalysis";
	public final static String STRUCTURE_MULTI_BOW		= "structureBasedMultiBOW";
		
	// Closed constructor
	private JavaBOWOptions() {
		
	}

	@SuppressWarnings("static-access")
	@Override
	public void defineOptions() {
		
		super.defineDefaultOptions();
		
		Option o1 =  OptionBuilder.hasArg(false)
				.withDescription("Split camel cased terms")
				.create(SPLIT_CAMELCASE);
		addOption(o1);					
		
		Option o3 =  OptionBuilder.hasArg(false)
				.withDescription("Consider terms from copyright notice in source files")
				.create(CONSIDER_COPYRIGHT);
		addOption(o3);	
				
		Option o4 =  OptionBuilder.hasArg(false)
				.withDescription("Ignore comments in source files")
				.create(IGNORE_COMMENTS);
		addOption(o4);	
				
		Option o6 =  OptionBuilder.hasArg(false)
				.withDescription("Create BOW model per method, default is per class")
				.create(METHOD_CHUNKING);
		addOption(o6);		
/*		
		Option o7 =  OptionBuilder.hasArg(false)
				.withDescription("Retain compound terms (eg.camel cased) in the model")
				.create(RETAIN_COMPOUND_WORDS);
		addOption(o7);		
*/
		
		Option o8 =  OptionBuilder.hasArg(false)
				.withDescription("Create BOW model for state analysis")
				.create(STATE_ANALYSIS);
		addOption(o8);	
		
		Option o9 =  OptionBuilder.hasArg(false)
				.withDescription("Create multiple Bag of Words based on Java code structure")
				.create(STRUCTURE_MULTI_BOW);
		addOption(o9);			
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
				_instance.defineOptions();
			}
			return _instance;
		}
	}

}
