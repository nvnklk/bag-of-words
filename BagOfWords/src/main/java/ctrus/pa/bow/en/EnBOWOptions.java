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

import ctrus.pa.bow.core.BOWOptions;
import ctrus.pa.bow.core.DefaultOptions;

public class EnBOWOptions extends DefaultOptions {

	private static BOWOptions _instance = null;
			
	public static BOWOptions getInstance() {			
		// Lazy singleton
		if(_instance == null) {
			_instance = new EnBOWOptions();
		}		
		return _instance;
	}
	
	@Override
	@SuppressWarnings("static-access")
	public void defineOptions() {

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
