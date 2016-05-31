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
