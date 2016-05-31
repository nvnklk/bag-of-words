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

package ctrus.pa.bow.term.transformation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;


public class ChunkTransformer extends BaseTransformer {

	private List<Character> _chunkChars = new ArrayList<Character>();
	
	public void addChunckChar(char c) {		
		_chunkChars.add(c);
	}
	
	@Override
	public String transform(String term) {
		String chunckTerms = term;
		for(Character c: _chunkChars) {
			chunckTerms = StringUtils.replaceChars(chunckTerms, c, ' ');			
		}
		// Remove multiple spaces with single space before returning
		return chunckTerms.replaceAll("\\s+", " ");
	}

}
