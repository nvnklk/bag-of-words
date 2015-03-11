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

import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.Type;

public class MethodTokens extends IdentifierTokens {
	
	private boolean _hasBody = true;
	private boolean _ignoreComments = true;
	
	public MethodTokens(boolean hasBody, boolean ignoreComments) {
		_hasBody = hasBody;
		_ignoreComments = ignoreComments;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void addTokens(ASTNode node) {		
		// Check if the node is a MethodDeclaration 
		if(!(node instanceof MethodDeclaration))
			throw new IllegalArgumentException("Expecting method declaration but found " + node.getClass().getName());
		
		MethodDeclaration mth = (MethodDeclaration)node;
		
        // Add method name
        String methodName = mth.getName().getFullyQualifiedName();
        addToken(methodName);
        
        // Add method declaration parameters and create unique method name
        String uniqueMethodName = "";
        uniqueMethodName = methodName;

        for(SingleVariableDeclaration param : (List<SingleVariableDeclaration>) mth.parameters()) {
        	addToken(param.getName().getIdentifier());
        	addToken(param.getType().toString());	        	
        	uniqueMethodName = uniqueMethodName  + " " + param.getType().toString();  
        }	
        
        // Set the identifier of this token-set
        setIdentifier(uniqueMethodName);
        
		// update method identifier position in the Java source text
        updateIdentifierPosition(mth);
		
        // Add return type
        if (!mth.isConstructor()) {
        	boolean skipReturn = false;
        	Type mthReturn = mth.getReturnType2();
        	if(mthReturn.isPrimitiveType()) {
        		if (PrimitiveType.VOID.equals(((PrimitiveType)mthReturn).getPrimitiveTypeCode()))
        			skipReturn = true;
        	}
        	if(!skipReturn)
        		addToken(mthReturn.toString());
        }
         
        // Add exceptions thrown by method
        for(Name exception: (List<Name>) mth.thrownExceptions()) {
        	addToken(exception.getFullyQualifiedName());
        }
		                                  			
		// Visit the method body to collect terms	        
		Block block = mth.getBody(); 
		if(_hasBody && block != null) {			// Found method body
			block.accept(new ASTVisitor() {
		        	public boolean visit(StringLiteral node) {
		        		addToken(node.toString());
		        		return true;
		        	}
		        	    
		        	public boolean  visit(SimpleName node) {
		        		addToken(node.toString());
		        		return true;
		        	}
		    });
		}
		
        // Add java doc comment found for the method
        if(!_ignoreComments && mth.getJavadoc() != null) {
	        List<TagElement> jDocComments = (List<TagElement>) mth.getJavadoc().tags();
	        for(TagElement jDocComment : jDocComments) {
	        	addCommentToken(jDocComment.toString());
	        }
        }			
	}

}
