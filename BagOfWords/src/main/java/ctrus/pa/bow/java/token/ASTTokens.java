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

package ctrus.pa.bow.java.token;

import org.eclipse.jdt.core.dom.ASTNode;

public interface ASTTokens {

	public String getIdentifier();
	
	public String[] getTokens();
	
	public String[] getInterfaceTokens();
	
	public String[] getCommentTokens();
	
	public void addToken(String token);
	
	public void addInterfaceToken(String token);
	
	public void addCommentToken(String token);
	
	public void addTokens(ASTNode node);
}
