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

package ctrus.pa.bow.java.token;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;

public abstract class IdentifierTokens implements ASTTokens {

	private static final String CLEAN_PUNC_REGEX  	= "(?![_])\\p{Punct}";
	private static final String SPACE_REGX   		= "\\p{Space}";
	
	private String		 _identifier 	 = null;
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
		return _tokens.toArray(new String[0]);
	}
	
	@Override
	public String[] getCommentTokens() {
		return _commentTokens.toArray(new String[0]);
	}	

	@Override
	public void addToken(String token) {
		_addToken(token, _tokens);
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
