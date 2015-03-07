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
