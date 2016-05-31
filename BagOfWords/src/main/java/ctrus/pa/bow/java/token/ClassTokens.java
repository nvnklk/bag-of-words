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

package ctrus.pa.bow.java.token;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

// ClassTokens - Hold tokens for each class
// A Java file can contain more than one class definition. Class tokens are
// separated for each defined class. Tokens from Anonymous Inner classes 
// will be part of the corresponding method tokens
public class ClassTokens extends IdentifierTokens {

	private Map<String, MethodTokens> _methodsTokens = new HashMap<String, MethodTokens>();
	
	private boolean _ignoreComments = true;
	private boolean _stateAnalysis = false;
	
	public ClassTokens(boolean ignoreComments, boolean stateAnalysis) {
		_ignoreComments = ignoreComments;
		_stateAnalysis = stateAnalysis;
	}
	
	public Iterable<String> getMethodIdentifiers() {
		return _methodsTokens.keySet();
	}
	
	public MethodTokens getMethodTokens(String methodIdentifier) {
		return _methodsTokens.get(methodIdentifier);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void addTokens(ASTNode node) {
		// Check if the node is a MethodDeclaration 
		if(!(node instanceof TypeDeclaration))
			throw new IllegalArgumentException("Expecting class type declaration but found " + node.getClass().getName());
		
		TypeDeclaration cls = (TypeDeclaration)node;

		// Add class and its super types
		String className = cls.getName().toString(); 
		
		// Set the identifier of this token-set
		setIdentifier(className);

		if(!_stateAnalysis) {
			
			// Add class name to token list
			addInterfaceToken(className);
			
			// Add interfaces and super class if any
			if(cls.getSuperclassType() != null)
				addInterfaceToken(cls.getSuperclassType().toString());
			List<Type> superInterfaces = (List<Type>)cls.superInterfaceTypes();
			for(Type superInterface : superInterfaces) {
				addInterfaceToken(superInterface.toString());
			}
			
			// Collect tokens from field declarations
			for(FieldDeclaration field : cls.getFields()) {
				addTokensFromFieldDeclaration(field);
			}			
		}
		
		// Collect tokens for each method		
		for(MethodDeclaration mth : cls.getMethods()) {
			boolean hasBody = !cls.isInterface();
			MethodTokens methodTokens = new MethodTokens(hasBody, _ignoreComments, _stateAnalysis);
			methodTokens.acceptVisitor(_positionVisitor);
			methodTokens.addTokens(mth);			
			_methodsTokens.put(methodTokens.getIdentifier(), methodTokens);
		}
		
		// Collect tokens from comments associated with the class definition
		if(!_ignoreComments && !_stateAnalysis){
			addTokensFromJavaDoc(cls.getJavadoc());
		}	
		
		// update class identifier position in the Java source text
		updateIdentifierPosition(cls);
		
	}

	@SuppressWarnings("unchecked")
	private void addTokensFromJavaDoc(Javadoc jd) {
		if(jd != null) {
			List<TagElement> jDocComments = (List<TagElement>) jd.tags();
	        for(TagElement jDocComment : jDocComments) {
				addCommentToken(jDocComment.toString());
	        } 
		}
	}
	
	@SuppressWarnings("unchecked")
	private void addTokensFromFieldDeclaration(FieldDeclaration fd) {
		// Add field type to tokens
		addToken(fd.getType().toString());
		
		// Add field variable declaration and its expression to tokens
		List<VariableDeclarationFragment> frags = (List<VariableDeclarationFragment>)fd.fragments();
		for(VariableDeclarationFragment f : frags) {
			addToken(f.getName().getFullyQualifiedName());			
			Expression ex = f.getInitializer();
			if(ex != null && !(ex instanceof NullLiteral)) {
				addToken(ex.toString());
			}									
		}
		
		// Add any javadocs associated with the field to tokens
		// Block and line comments are added later
		if(!_ignoreComments && !_stateAnalysis) {
			addTokensFromJavaDoc(fd.getJavadoc());
		}
	}

	
	
}
