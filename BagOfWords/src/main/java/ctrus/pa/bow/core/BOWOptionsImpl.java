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

import java.io.PrintWriter;
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

public abstract class BOWOptionsImpl implements BOWOptions {

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
			printHelp(new PrintWriter(System.out));
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
	
	public void printHelp(PrintWriter pw) {
		int width = 100;
		String cliHeader = toolDescription();
		String cliFooter = "";
		String cliSyntax = usageDescription();

		for(int i=0; i<width; i++) {
			cliHeader = cliHeader + "-";
			cliFooter = cliFooter + "_";
		}

		HelpFormatter hf = new HelpFormatter();
		//hf.printHelp(usageDescription(), _cliOptions);
		hf.printHelp(pw, width, cliSyntax, cliHeader, _cliOptions, 1, 2, cliFooter);
	}
	
}
