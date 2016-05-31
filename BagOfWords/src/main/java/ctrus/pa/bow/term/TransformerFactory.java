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

package ctrus.pa.bow.term;

public interface TransformerFactory {

	public TermTransformer createChunkTransformer();
	
	public TermTransformer createSanityTransformer();
	
	public TermTransformer createJargonTransformer();
	
	public TermTransformer createLowercaseTransformer();
	
	public TermTransformer createStemmingTransformer();
	
	public TermTransformer createLengthTransformer();
}
