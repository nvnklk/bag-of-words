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

package ctrus.pa.bow.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class Tokenizer extends ASTVisitor {
	
	
	private String  _packageName = null;
	private List<String>  _classDeclarations = null;
		
	private List<String> _classTerms = null;
	private Map<String, List<String>> _terms = null;
	private Map<String, String> _methodPosition = null;
	
	private CompilationUnit _cu = null;
	private String _sourceText = null;
	
	protected Tokenizer(CompilationUnit cu, String sourceText) {
		_cu = cu;
		_sourceText = sourceText;
		_classTerms = new ArrayList<String>();
		_classDeclarations = new ArrayList<String>();
		_terms = new HashMap<String, List<String>>();
		_methodPosition = new HashMap<String, String>();
		
	}
	
	public String getPackageName() {
		return _packageName;
	}
	
	public List<String> getClassDeclartions() {
		return _classDeclarations;
	}
	
	@SuppressWarnings("unchecked")
	public boolean visit(TypeDeclaration node) {
		// Add class and its super types
		_classDeclarations.add(node.getName().toString());
		if(node.getSuperclassType() != null)
			_classDeclarations.add(node.getSuperclassType().toString());
		List<Type> superInterfaces = (List<Type>)node.superInterfaceTypes();
		for(Type superInterface : superInterfaces) {
			_classDeclarations.add(superInterface.toString());
		}
		
		// Add field declarations
		for(FieldDeclaration field : node.getFields()) {
			addString(field.getType().toString(), _classTerms);					
			List<VariableDeclarationFragment> frags = (List<VariableDeclarationFragment>)field.fragments();
			for(VariableDeclarationFragment f : frags) {
				addString(f.getName().getFullyQualifiedName(), _classTerms);			
				Expression ex = f.getInitializer();
				if(ex != null && !(ex instanceof NullLiteral)) {
					addString(ex.toString(), _classTerms);
				}									
			}
			
			// Add any java docs associated with the field
			if(field.getJavadoc() != null) {
				List<TagElement> jDocComments = (List<TagElement>) field.getJavadoc().tags();
		        for(TagElement jDocComment : jDocComments) {
		        	addString(jDocComment.toString(), _classTerms);
		        } 
			}
		}
		return true;
	}
	
	public boolean visit(PackageDeclaration node) {
		_packageName = node.getName().getFullyQualifiedName();
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public boolean visit(MethodDeclaration node) {
		
		// Visit the method body to collect terms
		Block block = node.getBody();    		 
		final List<String> methodTokens = new ArrayList<String>();
		
        block.accept(new ASTVisitor() {
        	public boolean visit(StringLiteral node) {
        		methodTokens.add(node.toString());
        		return true;
        	}
        	    
        	public boolean  visit(SimpleName node) {
        		methodTokens.add(node.toString());
        		return true;
        	}
        });
         
        // Add java doc comment found for the method
        if(node.getJavadoc() != null) {
	        List<TagElement> jDocComments = (List<TagElement>) node.getJavadoc().tags();
	        for(TagElement jDocComment : jDocComments) {
	        	addString(jDocComment.toString(), methodTokens);
	        }
        }
                                   
        // Add return type
        if (!node.isConstructor())
        	methodTokens.add(0, node.getReturnType2().toString());
         
        // Add exceptions thrown by method
        for(Name exception: (List<Name>) node.thrownExceptions()) {
        	methodTokens.add(0, exception.getFullyQualifiedName());
        }
         
        String uniqueMethodName = "";
        String methodName = node.getName().getFullyQualifiedName();
        uniqueMethodName = methodName;

        // Add parameters of the method
        for(SingleVariableDeclaration param : (List<SingleVariableDeclaration>) node.parameters()) {
        	methodTokens.add(0, param.getName().getIdentifier());
        	methodTokens.add(0, param.getType().toString());
        	
        	uniqueMethodName = uniqueMethodName  + " " + param.getType().toString();  
        }	
        	 
        // Add method Name identifier
        methodTokens.add(0, methodName);
		
		// Get the method position
		int begin = node.getStartPosition();
		int end = begin + node.getLength();
        _methodPosition.put(uniqueMethodName, begin + "," + end);
        
        // Store all the terms for the method
        _terms.put(uniqueMethodName, methodTokens);
         
        return true;	// Continue visiting next method
	}
	
	private void addString(String line, List<String> tokens) {
		line = line.replaceAll("(?![_])\\p{Punct}", " "); 	
		for (String term : line.split("\\p{Space}")) { 
    		if (term.equals("")) continue;
    		else tokens.add(term);
		}
	}
	
	private String getMethodForPosition(int begin, int end) {
		for(String methodName : _methodPosition.keySet()) {						
			String[] s = _methodPosition.get(methodName).split(",");						
			int methodBegin = Integer.parseInt(s[0]);						
			int methodEnd = Integer.parseInt(s[1]);
			if(begin > methodBegin && begin < methodEnd &&
			   end > methodBegin && end < methodEnd	)
				return methodName;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public void processComments() {
		List<Comment> comments = (List<Comment>) _cu.getCommentList();
		for (Comment comment : comments) {
			
			comment.accept(new ASTVisitor() {
				
				private void getComment(Comment node) {
					int start = node.getStartPosition();
					int end = start + node.getLength();
					String comment = _sourceText.substring(start, end);
					String methodName = getMethodForPosition(start, end);
					if(methodName != null) {
						addString(comment.toString(), _terms.get(methodName));
					} else {
						addString(comment.toString(), _classTerms);
					}
				}
				
				public boolean visit(LineComment node) {
					getComment(node);					
					return true;
				}
				
				public boolean visit(BlockComment node) {					
					getComment(node);
					return true;
				}				
			});
		}
	}
	
	public Map<String, List<String>> getTokens() {
		return _terms;
	}
	
	public List<String> getClassTokens() {
		return _classTerms;
	}
}
