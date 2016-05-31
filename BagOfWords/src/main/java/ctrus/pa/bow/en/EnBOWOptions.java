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

package ctrus.pa.bow.en;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

import ctrus.pa.bow.DefaultOptions;
import ctrus.pa.bow.core.BOWOptions;

public class EnBOWOptions extends DefaultOptions {

	private static final long serialVersionUID = 5228784712049378746L;

	private static BOWOptions _instance = null;

	public final static String SPLIT_CAMELCASE			= "splitCamelCase";

	public static BOWOptions getInstance() {
		// Lazy singleton
		if(_instance == null) {
			_instance = new EnBOWOptions();
			_instance.defineOptions();
		}
		return _instance;
	}

	@Override
	@SuppressWarnings("static-access")
	public void defineOptions() {
		super.defineDefaultOptions();
		
		Option o1 =  OptionBuilder.hasArg(false)
				.withDescription("Split camel cased terms")
				.create(SPLIT_CAMELCASE);
		addOption(o1);
	}


	@Override
	public String usageDescription() {
		return "java EnBagOfWords [options] -sourceDir <docs dir> -ouputDir <dir>";
	}

	@Override
	public String toolDescription() {
		return "Bag Of Words for English text documents";
	}
}
