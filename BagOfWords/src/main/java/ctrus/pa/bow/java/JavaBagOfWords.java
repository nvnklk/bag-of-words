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

import org.eclipse.jdt.core.dom.CompilationUnit;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;

import ctrus.pa.bow.core.DefaultBagOfWords;
import ctrus.pa.bow.core.DefaultOptions;
import ctrus.pa.bow.term.FilterFactory;
import ctrus.pa.bow.term.TermFilteration;
import ctrus.pa.bow.term.TermTransformation;
import ctrus.pa.util.CtrusHelper;

public class JavaBagOfWords extends DefaultBagOfWords {

	public JavaBagOfWords() {}
	
	protected void setup() {
		// Create filter
		TermFilteration filterations = new TermFilteration();
		setFilterations(filterations);
				
		// Add all required filters
		FilterFactory filterFactory = JavaFilterFactory.newInstance(_options); 
		filterations.addFilter(filterFactory.createStopFilter());
		filterations.addFilter(filterFactory.createNumbericFilter());
		filterations.addFilter(filterFactory.createLengthFilter());
		
				
		// Create transformer
		TermTransformation termTransformation = new TermTransformation();
		setTransformations(termTransformation);
				
		// Add all required transformers
		JavaTransformerFactory transformerFactory = JavaTransformerFactoryImpl.newInstance(_options);
		termTransformation.addTransfomer(transformerFactory.createChunkTransformer());
		termTransformation.addTransfomer(transformerFactory.createCamelcaseTransformer());
		termTransformation.addTransfomer(transformerFactory.createSanityTransformer());		
		termTransformation.addTransfomer(transformerFactory.createJargonTransformer());
		termTransformation.addTransfomer(transformerFactory.createLowercaseTransformer());
		termTransformation.addTransfomer(transformerFactory.createLengthTransformer());
	}
	
	private void wrapUpTermCollection(Tokenizer cv, String fileName) throws IOException {
		// Add package name and class name to the above
		// tokens collected from all methods
		addTerm(cv.getPackageName());
		for(String t : cv.getClassDeclartions())
			addTerm(t);
				
		// Add tokens found outside method such as from comments and class variables
		for(String cTokens : cv.getClassTokens()) {
			addTerms(cTokens.split("\\p{Space}"));
		}
	
		writeToOutput(fileName);
		reset();		
	}

	@Override
	public void create() {
		try {
		
			boolean methodChunk = _options.hasOption(JavaBOWOptions.METHOD_CHUNKING);
			
			Collection<File> srcfiles = getSourceDocuments("*.java");			
			ASTParser javaParser = ASTParser.newParser(AST.JLS3);
			javaParser.setKind(ASTParser.K_COMPILATION_UNIT);
			
			int totalFiles = srcfiles.size();
			int currentFile = 0;
			CtrusHelper.printToConsole("Total files - " + srcfiles.size());			

			for(File srcFile : srcfiles) {
								
				// Parse the Java source file using a Off-the-shelf Java Parser
				String javaSourceText = FileUtils.readFileToString(srcFile); 
				javaParser.setSource(javaSourceText.toCharArray());				
				final CompilationUnit cu = (CompilationUnit) javaParser.createAST(null);
				Tokenizer cv = new Tokenizer(cu, javaSourceText); 
				cu.accept(cv);
				cv.processComments();	// This has to be called after visiting all the methods for tokens
				
				Map<String, List<String>> terms = cv.getTokens();
				
				for(String method : terms.keySet()) {
					List<String> methodTerms = terms.get(method);
											
					for(String term : methodTerms) {
						addTerms(term.split("\\p{Space}"));
					}
					
					if(methodChunk) {
						wrapUpTermCollection(cv, method + "_" + srcFile.getName());						
					}
				}
				
				if(!methodChunk) {
					wrapUpTermCollection(cv, srcFile.getName());
				}
				
				currentFile++;						// Update the counter
				// print progress
				CtrusHelper.progressMonitor("Progress - ", currentFile, totalFiles);
			}
			
		} catch (Exception e) {			
			e.printStackTrace();
		}
		
	}
		
	public static void main(String[] args) throws Exception {
		// Parse the command line input
		JavaBOWOptions opts = JavaBOWOptions.Factory.getInstance();	
		opts.parseCLI(args);
		if(opts.hasOption(DefaultOptions.PRINT_HELP)) {
			opts.printHelp(new PrintWriter(System.out));
		} else {					
			JavaBagOfWords jBow = new JavaBagOfWords();
			jBow.setup(opts);
			jBow.create();
			jBow.printVocabulary();
		}
	}

	
}
