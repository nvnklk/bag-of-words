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

package ctrus.pa.bow.core;

import java.io.PrintWriter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public abstract class BOWOptionsImpl implements BOWOptions, Serializable {
	
	private static final long	serialVersionUID	= 1232656881902982991L;
	private Options 			_cliOptions 	 = new Options();
	private Map<String, String> _internalOptions = new HashMap<String, String>();
	private CommandLine			_parsedOptions 	 = null;
	
	public abstract void defineOptions();
	
	public abstract void defineDefaultOptions();
		
	public abstract String usageDescription();
	
	public abstract String toolDescription();
	
	public final void addOption(Option opt) {
		_cliOptions.addOption(opt);
	}
		
	public final void parseCLI(String[] args) throws ParseException {
		// Define all the options expected to be passed
		defineDefaultOptions();
		defineOptions();
		
		// Parse the command line input
		CommandLineParser p = new GnuParser(); 
		try {
			_parsedOptions = p.parse(_cliOptions, args, true);			
		} catch(MissingOptionException ex) {			
			printHelp(ex.getMessage(), new PrintWriter(System.out));
			System.exit(1);
		}
		
	}
	
	public final void addOptionInternal(String option, String value) {
		_internalOptions.put(option, value);			
	}
	
	public final String getOptionInternal(String option, boolean isInternal) throws MissingOptionException {
		
		if(hasOptionInternal(option, isInternal))
			if(isInternal)
				return _internalOptions.get(option);
			else
				return _parsedOptions.getOptionValue(option);
		else
			throw new MissingOptionException(option + " - Option not provided");		
	}
	
	public final boolean hasOptionInternal(String option, boolean isInternal) {
		
		if(isInternal) {
			return _internalOptions.containsKey(option); 
		} else {
			if(_parsedOptions == null)
				throw new UnknownError("CLI not parsed yet!");
			return _parsedOptions.hasOption(option);		
		}
		
	}
	
	public final String getOption(String option) throws MissingOptionException {
		return getOptionInternal(option, false);
	}
	
	public final boolean hasOption(String option) {
		return hasOptionInternal(option, false);
	}
	
	public void printHelp(String message, PrintWriter pw) {
		int width = 100;
		String cliHeader = toolDescription();
		String cliFooter = "";
		//String cliSyntax = usageDescription();

		for(int i=0; i<width; i++) {
			cliHeader = cliHeader + "-";
			cliFooter = cliFooter + "_";
		}

		HelpFormatter hf = new HelpFormatter();
		System.out.println("Message - " + message);
		hf.printHelp(usageDescription(), _cliOptions);
		//hf.printHelp(pw, width, cliSyntax, cliHeader, _cliOptions, 1, 2, cliFooter);
	}
	
}
