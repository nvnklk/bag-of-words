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
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import ctrus.pa.bow.java.token.ClassTokens;
import ctrus.pa.bow.java.token.IdentifierTokens;
import ctrus.pa.bow.java.token.IdentifiersPosition;
import ctrus.pa.bow.java.token.MethodTokens;

public class JavaFileTokenizer extends ASTVisitor {

	private boolean _init 			 	= false;
	private boolean _ignoreComments  	= false;
	private boolean _considerCopyright 	= false;
	private boolean _stateAnalysis		= false;
	
	private ASTParser 		_javaParser  = null;
	private CompilationUnit _cu 		 = null;
	
	private String 				_packageName 	= null;
	private List<ClassTokens> 	_classTokens 	= null;
	private IdentifiersPosition _positionObserver = null;
		
	public String getPackageName() {
		return _packageName;
	}
	
	public Iterable<ClassTokens> getClassTokens() {
		return _classTokens;
	}
	
	public void setIgnoreComments(boolean ignore) {
		_ignoreComments = ignore;
	}
	
	public void setConsiderCopyright(boolean consider) {
		_considerCopyright = consider;
	}
	
	public void setStateAnalysis(boolean stateAnalysis) {
		_stateAnalysis = stateAnalysis;
	}
	
	@SuppressWarnings("unchecked")
	public void tokenize(List<String> sourceTextLines) {
		// initialize if not done earlier
		if(!_init) _init();
		
		// Processing a new file, reset tokens
		_classTokens.clear();
		_positionObserver = new IdentifiersPosition();
		
		// Normalize single line comments to block comments
		StringBuffer sourceText = _preProcessSourceText(sourceTextLines);
		
		// Extract all identifiers from java source text
		_javaParser.setSource(sourceText.toString().toCharArray());				
		_cu = (CompilationUnit) _javaParser.createAST(null);
		_cu.accept(this);
		
		// Extract all comments from java source text
		// and merge them with their corresponding identifiers
		if(!_ignoreComments && !_stateAnalysis) {
			// Extract
			List<Comment> comments = (List<Comment>) _cu.getCommentList();		
			for (Comment comment : comments) {
				int start = comment.getStartPosition();
				int end = start + comment.getLength();
				String identifierOfComment = _positionObserver.getIdentifierForPosition(start, end); 				
				String commentText = sourceText.substring(start, end).replaceAll("[\\t\\n\\r]"," ");

				// Do filtering of comments
				if(!_considerCopyright) {
					if(commentText.toLowerCase().contains("copyright") || 
	        			commentText.toLowerCase().contains("license")) continue;
				}
	        	// More filtering rules...TBD
	        	
				IdentifierTokens it = getTokens(identifierOfComment);
				it.addCommentToken(commentText);
			}		
		}
		
		// Add package information
		if(!_stateAnalysis) {
			for(ClassTokens c : _classTokens) 
				c.addToken(_packageName);
		}
	}
	
	private void _init() {
		_javaParser = ASTParser.newParser(AST.JLS8);
		_javaParser.setKind(ASTParser.K_COMPILATION_UNIT);
		
		Map options = JavaCore.getOptions();
		JavaCore.setComplianceOptions(JavaCore.VERSION_1_8, options);
		_javaParser.setCompilerOptions(options);
		
		_classTokens = new ArrayList<ClassTokens>();
		
		_init = true;				
	}
	
	private StringBuffer _preProcessSourceText(List<String> sourceTextLines) {
		StringBuffer javaSourceTextBuffer = new StringBuffer();
		boolean commentMarked = false;
		for(String line : sourceTextLines) {
			String stripedLine = StringUtils.strip(line);
			if(stripedLine.startsWith("//")) {					
				javaSourceTextBuffer.append("/* ").append(stripedLine);
				commentMarked = true;
			}
			else {
				if(commentMarked) {
					javaSourceTextBuffer.append("*/ ").append(stripedLine);						
					commentMarked = false;
				} else {
					javaSourceTextBuffer.append(stripedLine);
				}
			}
			javaSourceTextBuffer.append("\n");
		}			
		return javaSourceTextBuffer;
	}	
	
	private IdentifierTokens getTokens(String identifier) {
		for(ClassTokens ct : _classTokens) {
			if(ct.getIdentifier().equals(identifier))
				return ct;
			for(String mId : ct.getMethodIdentifiers()) {
				MethodTokens mt = ct.getMethodTokens(mId);
				if(mt.getIdentifier().equals(identifier))
					return mt;
			}
		}
		throw new IllegalArgumentException("Tokens not found for identifier - " + identifier);
	}
	

	//-------------------------------------------------------------------------
	// Overridden ASTVisitor visit methods
	
	@Override
	public boolean visit(TypeDeclaration node) {		
		// Add class 
		ClassTokens eachClassTokens = new ClassTokens(_ignoreComments, _stateAnalysis);
		eachClassTokens.acceptVisitor(_positionObserver);
		eachClassTokens.addTokens(node);
		_classTokens.add(eachClassTokens);
		return true;	// continue visiting other class definitions if found 
	}
	
	@Override
	public boolean visit(PackageDeclaration node) {
		_packageName = node.getName().getFullyQualifiedName();
		return true;
	}
	
	@Override
	public boolean visit(AnnotationTypeDeclaration node) {
		System.out.println("Found custom annotation type - " + node.getName().getFullyQualifiedName());
		return true;
	}
	
	@Override
	public boolean visit(EnumDeclaration node) {
		System.out.println("Found custom Enum type - " + node.getName().getFullyQualifiedName());
		return true;
	}
	
	@Override
	public boolean visit(NormalAnnotation node) {
		System.out.println("Found Annotation - " + node.getTypeName());
		return true;
	}
}

