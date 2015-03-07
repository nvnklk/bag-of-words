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
	
	public ClassTokens(boolean ignoreComments) {
		_ignoreComments = ignoreComments;
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
		addToken(className);
		
		// Set the identifier of this token-set
		setIdentifier(className);
		
		// Add interfaces and super class if any
		if(cls.getSuperclassType() != null)
			addToken(cls.getSuperclassType().toString());
		List<Type> superInterfaces = (List<Type>)cls.superInterfaceTypes();
		for(Type superInterface : superInterfaces) {
			addToken(superInterface.toString());
		}
		
		// Collect tokens from field declarations
		for(FieldDeclaration field : cls.getFields()) {
			addTokensFromFieldDeclaration(field);
		}			
		
		// Collect tokens for each method
		
		for(MethodDeclaration mth : cls.getMethods()) {
			boolean hasBody = !cls.isInterface();
			MethodTokens methodTokens = new MethodTokens(hasBody, _ignoreComments);
			methodTokens.acceptVisitor(_positionVisitor);
			methodTokens.addTokens(mth);
			_methodsTokens.put(methodTokens.getIdentifier(), methodTokens);
		}
		
		// Collect tokens from comments associated with the class definition
		if(!_ignoreComments){
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
				addToken(jDocComment.toString());
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
		if(!_ignoreComments ) {
			addTokensFromJavaDoc(fd.getJavadoc());
		}
	}

	
	
}
