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

package ctrus.pa.bow.java;

public enum JavaStructureIdentifiers {
	
	CLASS_INTERFACE		("class_interface"), 
	COMMENTS			("comments"), 
	METHOD_CONTENT		("method_content");
	
	private String value;
	
	// Intialize enum with a closed constructor
	JavaStructureIdentifiers(String s) {
		value = s;
	}
	
	public String toString() {
		return value;
	}
}
