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

import ctrus.pa.bow.core.BOWOptions;
import ctrus.pa.bow.term.TermTransformer;
import ctrus.pa.bow.term.transformation.BaseTransformerFactory;
import ctrus.pa.bow.term.transformation.DummyStemmer;

public class JavaTransformerFactoryImpl extends BaseTransformerFactory implements JavaTransformerFactory { 

	private static JavaTransformerFactoryImpl _instance = null;
	
	private JavaTransformerFactoryImpl() {}
	
	public static JavaTransformerFactory newInstance(BOWOptions options) {
		if(_instance == null) {
			_instance = new JavaTransformerFactoryImpl();
			_instance.setOptions(options);
		}
		return _instance;
	}
	
	public TermTransformer createStemmingTransformer() {
		return new DummyStemmer();
	}

	public TermTransformer createCamelcaseTransformer() {
		CamelcaseTransformer ct = new CamelcaseTransformer();
		if(!hasOption(JavaBOWOptions.SPLIT_CAMELCASE))
			ct.setEnabled(false);
		return ct;
	}
}
