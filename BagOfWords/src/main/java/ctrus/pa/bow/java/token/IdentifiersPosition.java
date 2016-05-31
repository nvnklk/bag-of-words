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

import java.util.Deque;
import java.util.LinkedList;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class IdentifiersPosition {
	
	public Deque<Position> _methodsPosition = new LinkedList<Position>();
	public Deque<Position> _classesPosition = new LinkedList<Position>();
		
	public String getIdentifierForPosition(int start, int end) {
		// First check in methods and then in classes, if not matched
		// then return the root class identifier of the Java source file
		for(Position p : _methodsPosition) {
			if(p.inRange(start, end))
				return p.getName();
		}  // Position provided not within the range of any method definitions		
		
		for(Position p : _classesPosition) {
			if(p.inRange(start, end))
				return p.getName();
		}  // Position provided not within the range of any class definitions
		
		return _classesPosition.getLast().getName();
	}

	public void visit(String identifier, ASTNode node) {		
		Position p = new Position(identifier, node);
		
		if(node instanceof MethodDeclaration)
			_methodsPosition.push(p);
		else if(node instanceof TypeDeclaration)
			_classesPosition.push(p);
		else	// A different ASTNode encountered		 	
			throw new IllegalArgumentException("Unexpected ASTNode " + node.getClass().getName() + " found...");
	}
		
	// Position - To hold the position of an identifier in
	// the Java source text
	private class Position {
		private int _begin, _end = 0;
		private String _name = null;
		
		public Position(String name, ASTNode node) {
			_begin = node.getStartPosition();
			_end = _begin + node.getLength();
			_name = name;
		}
		
		public String getName() { 
			return _name; 
		}
		
		public boolean inRange(int b, int e) {
			if(b > _begin && e < _end ) {
				return true;				// Is within the range of this position
			}
			else if((_begin - e) > 0 && (_begin - e) < 30) {
											// TBD: Revisit the proximity assumption
				return true;				// Is close proximity to this position
			}
			return false;
		}
	}
	

}
