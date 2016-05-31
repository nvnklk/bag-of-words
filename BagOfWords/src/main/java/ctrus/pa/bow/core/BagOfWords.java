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

import java.io.IOException;
import java.io.OutputStream;

public interface BagOfWords {
	
	public void create();
	
	public double getTermCount();
	
	public void writeTo(OutputStream out) throws IOException;
	
	public <E extends Enum<E>> void writeTo(OutputStream out,  E identifier) throws IOException;
	
	public void addTerm(String word, double weight);
		
	public void addTerm(String term, String doc);
	
	public <E extends Enum<E>> void addTerm(String term, String doc, E identifier);
			
	public void reset();
		
}
