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

import java.io.PrintWriter;

import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

public interface BOWOptions {

	public void defineOptions();
	
	public String usageDescription();
	
	public String toolDescription();
	
	public void addOption(Option opt);

	public String getOption(String option) throws MissingOptionException;
	
	public boolean hasOption(String option); 
	
	public void parseCLI(String[] args) throws ParseException;
	
	public void printHelp(String message, PrintWriter pw);
}
