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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.jdt.core.dom.ASTNode;

public abstract class IdentifierTokens implements ASTTokens {

	private static final String CLEAN_PUNC_REGEX  	= "(?![_])\\p{Punct}";
	private static final String SPACE_REGX   		= "\\p{Space}";
	
	private String		 _identifier 	 = null;
	private List<String> _interfaceTokens= new ArrayList<String>();
	private List<String> _tokens 	 	 = new ArrayList<String>();
	private List<String> _commentTokens  = new ArrayList<String>();
	
	protected IdentifiersPosition _positionVisitor = null;
	
	@Override
	public String getIdentifier() {
		return _identifier;
	}
	
	protected void setIdentifier(String id) {
		_identifier = id;
	}
	
	public void acceptVisitor(IdentifiersPosition visitor) {
		_positionVisitor = visitor;
	}
	
	public void merge(ASTTokens tokens) {
		for(String t : tokens.getTokens())
			_tokens.add(t);
	}
	
	protected void updateIdentifierPosition(ASTNode node) {
		if(_positionVisitor == null)
			throw new IllegalArgumentException("Identifier Position not set");
		
		if(getIdentifier() == null)
			throw new IllegalArgumentException("Identifier name not set");
		
		_positionVisitor.visit(getIdentifier(), node);
	}
	
	@Override
	public String[] getTokens() {		
		return ArrayUtils.addAll(_interfaceTokens.toArray(new String[0]), 
								 _tokens.toArray(new String[0]));		
	}
	
	@Override
	public String[] getCommentTokens() {
		return _commentTokens.toArray(new String[0]);
	}	
	
	@Override
	public String[] getInterfaceTokens() {
		return _interfaceTokens.toArray(new String[0]);
	}

	@Override
	public void addToken(String token) {
		_addToken(token, _tokens);
	}
	
	@Override
	public void addInterfaceToken(String token) {
		_addToken(token, _interfaceTokens);
	}
		
	@Override
	public void addCommentToken(String token) {
		_addToken(token, _commentTokens);
	}	
	
	private void _addToken(String tokens, List<String> container) {
		// Clean tokens of any punctuation and trim the ends
		tokens = tokens.replaceAll(CLEAN_PUNC_REGEX, " ").trim(); 
		
		// Split the space delimited tokens to add to the list 
		for (String token : tokens.split(SPACE_REGX)) { 
    		if (token.equals("")) continue;
    		else container.add(token);		// multiplicity of a token to be maintained
		}
		
	}

	public abstract void addTokens(ASTNode node);
}
