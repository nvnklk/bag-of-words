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

package ctrus.pa.bow.java;

import java.io.File;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;

import ctrus.pa.bow.DefaultBagOfWords;
import ctrus.pa.bow.DefaultMultiBagOfWords;
import ctrus.pa.bow.DefaultOptions;
import ctrus.pa.bow.core.Vocabulary;
import ctrus.pa.bow.java.token.ClassTokens;
import ctrus.pa.bow.java.token.MethodTokens;
import ctrus.pa.bow.term.TermFilter;
import ctrus.pa.bow.term.TermFilteration;
import ctrus.pa.bow.term.TermTransformation;
import ctrus.pa.util.CtrusHelper;

public class JavaBagOfWords {

	protected static final String 	DEFAULT_OUTPUT_MCHUNK_DIR 	= "method_chunk";
	private static final String 	FILE_SELECT_WILDCARD 		= "*.java";
	
	private TermFilter 			_stopWordFilterForComments 		= null;	
	private TermFilteration 	_filterations 					= null;
	private TermTransformation 	_termTransformation 			= null;
	private JavaBOWOptions		_opts							= null;
	
	public JavaBagOfWords() {}
	
	protected void init(JavaBOWOptions opts) {
		this._opts = opts;
		
		// Create filter
		_filterations = new TermFilteration();
		JavaFilterFactory filterFactory = JavaFilterFactory.newInstance(opts);
				
		// create a comment stop word filter and get its reference. This filter is
		// disabled by default and it enabled as required.
		_stopWordFilterForComments = filterFactory.createStopFilterForComments();
		_stopWordFilterForComments.setEnabled(false);
		
		// Add all required filters		
		_filterations.addFilter(filterFactory.createStopFilter())
					 .addFilter(_stopWordFilterForComments)
					 .addFilter(filterFactory.createNumericFilter())
					 .addFilter(filterFactory.createLengthFilter());		
				
		// Create transformer
		_termTransformation = new TermTransformation();
		JavaTransformerFactory transformerFactory = JavaTransformerFactoryImpl.newInstance(opts);
						
		// Add all required transformers			
		_termTransformation.addTransfomer(transformerFactory.createCamelcaseTransformer())
						  .addTransfomer(transformerFactory.createChunkTransformer())	
						  .addTransfomer(transformerFactory.createJargonTransformer())
						  .addTransfomer(transformerFactory.createSanityTransformer())				
						  .addTransfomer(transformerFactory.createLowercaseTransformer())
						  .addTransfomer(transformerFactory.createStemmingTransformer())
						  .addTransfomer(transformerFactory.createLengthTransformer());
	}
	
	private JavaFileTokenizer getTokenizer() {
		
		// Set up Java source text file tokenizer			
		JavaFileTokenizer jTokenizer = new JavaFileTokenizer();
		jTokenizer.setIgnoreComments(_opts.hasOption(JavaBOWOptions.IGNORE_COMMENTS));
		jTokenizer.setConsiderCopyright(_opts.hasOption(JavaBOWOptions.CONSIDER_COPYRIGHT));
		jTokenizer.setStateAnalysis(_opts.hasOption(JavaBOWOptions.STATE_ANALYSIS));
		return jTokenizer;
	}
	
	public void process() throws Exception {
		// Select the type of BOW
		if(_opts.hasOption(JavaBOWOptions.STRUCTURE_MULTI_BOW)) {
			JavaDefaultMultiBagOfWords jMultiBow = new JavaDefaultMultiBagOfWords();
			jMultiBow.setup(_opts);
			jMultiBow.setBowIdentifiers(JavaStructureIdentifiers.class);
			jMultiBow.create();			
		} else {
			JavaDefaultBagOfWords jBow = new JavaDefaultBagOfWords();
			jBow.setup(_opts);
			jBow.create();
			jBow.printVocabulary();
		}
		
	}
		
	public static void main(String[] args) throws Exception {
		// Parse the command line input
		JavaBOWOptions opts = JavaBOWOptions.Factory.getInstance();	
		opts.parseCLI(args);
		if(opts.hasOption(DefaultOptions.PRINT_HELP)) {
			opts.printHelp("HELP", new PrintWriter(System.out));
		} else {					
			JavaBagOfWords jBow = new JavaBagOfWords();
			jBow.init(opts);
			jBow.process();			
		}
	}
	
	// Private instantiation for DefaultBagOfWords
	private class JavaDefaultBagOfWords extends DefaultBagOfWords {
		
		protected void setup() {
			setFilterations(_filterations);
			setTransformations(_termTransformation);
		}
		
		@Override
		public void create() {
			String currentFileInProcess = null;
			try {
			
				boolean methodChunk = hasOption(JavaBOWOptions.METHOD_CHUNKING);
				boolean ignoreComments = hasOption(JavaBOWOptions.IGNORE_COMMENTS);
				
				JavaFileTokenizer jTokenizer = getTokenizer();				
				
				Collection<File> srcfiles = getSourceDocuments(FILE_SELECT_WILDCARD);
				int totalFiles = srcfiles.size();
				int currentFile = 0;
				CtrusHelper.printToConsole("Total files (" +  FILE_SELECT_WILDCARD + ") - " + srcfiles.size());			

				for(File srcFile : srcfiles) {
					// to debug in case of failure
					currentFileInProcess = srcFile.getName();
					
					// Parse the Java source file using a Off-the-shelf Java Parser
					List<String> javaSourceTextLines = FileUtils.readLines(srcFile);
					jTokenizer.tokenize(javaSourceTextLines);
					
					for(ClassTokens c : jTokenizer.getClassTokens()) {
						
						String[] commentTokens = c.getCommentTokens();
						String[] classTokens = ArrayUtils.addAll(c.getInterfaceTokens(), c.getTokens());
						for(String mId : c.getMethodIdentifiers()) {
							MethodTokens m = c.getMethodTokens(mId);	
							
							if(methodChunk) {
								// Add document to the vocabulary first before adding terms
								String folderName = c.getIdentifier();
								String fileName = m.getIdentifier().replaceAll(" ", "_");							
								String docName =  folderName + ":" + fileName;
															
								String docref = getDocumentId(docName);		
								Vocabulary.getInstance().addDocument(docref, docName);
								
								addTerms(ArrayUtils.addAll(classTokens, m.getTokens()), docref);
								
								if(!ignoreComments) {
									// Enable stop words filtering for comments
									_stopWordFilterForComments.setEnabled(true);
									addTerms(ArrayUtils.addAll(m.getCommentTokens(), commentTokens), docref);
									_stopWordFilterForComments.setEnabled(false);
								}
								
								// Check if method_chunk folder exists and Create folder with name of the class, create if not exists
								File methodChunkFolder = new File(_outputDir, DEFAULT_OUTPUT_MCHUNK_DIR);
								
								_outputDir = new File(methodChunkFolder, folderName);
								if(!_outputDir.exists()) 
									_outputDir.mkdirs();
								
								String outputFileName = hasOption(DefaultOptions.OUTPUT_SINGLE_FILE) ? docref : fileName;							System.out.println("-->" + outputFileName);	
								// Write bow to the file
								writeToOutput(outputFileName);
								setupOutputDir(); // Set output dir back to the root to avoid recursive folder creation
								reset();  // Reset so that next method tokens can 
										  // be added as new document 
							} else {
								String[] methodTokens = ArrayUtils.addAll(m.getInterfaceTokens(), m.getTokens());
								classTokens = ArrayUtils.addAll(classTokens, methodTokens);
								commentTokens = ArrayUtils.addAll(m.getCommentTokens(), commentTokens);
							}							
						}
						
						if(!methodChunk) {
							// Add document to file name mapping						
							String docref = getDocumentId(c.getIdentifier());

							// Add document to the vocabulary first before adding terms
							Vocabulary.getInstance().addDocument(docref, srcFile.getName());
							
							// Add all collected tokens as terms
							addTerms(classTokens, docref);
							
							if(!ignoreComments) {
								// Enable stop words filtering for comments
								_stopWordFilterForComments.setEnabled(true);
								addTerms(commentTokens, docref);
							}
							
							writeToOutput(docref);
							reset(); // Reset so that next class tokens can 
							  		 // be added as new document
						}
							
					}
					
					currentFile++;	// Update the counter
					// print progress
					CtrusHelper.progressMonitor("Progress -", currentFile, totalFiles);
				}
				
			} catch (Exception e) {		
				CtrusHelper.printToConsole("Current file in process - " + currentFileInProcess);
				e.printStackTrace();
			}
			
		}		
	}


	// Private instantiation for DefaultMultiBagOfWords
	private class JavaDefaultMultiBagOfWords extends DefaultMultiBagOfWords {
		
		protected void setup() {
			setFilterations(_filterations);
			setTransformations(_termTransformation);
		}
		
		@Override
		public void create() {
			String currentFileInProcess = null;
			try {
			
				if(hasOption(JavaBOWOptions.METHOD_CHUNKING))
					CtrusHelper.printToConsole("[Warning] Option " + JavaBOWOptions.METHOD_CHUNKING + " not used!");
				
				if(hasOption(JavaBOWOptions.IGNORE_COMMENTS))
					CtrusHelper.printToConsole("[Warning] Option " + JavaBOWOptions.IGNORE_COMMENTS + " not used!");
				
				JavaFileTokenizer jTokenizer = getTokenizer();				
				
				Collection<File> srcfiles = getSourceDocuments(FILE_SELECT_WILDCARD);
				int totalFiles = srcfiles.size();
				int currentFile = 0;
				CtrusHelper.printToConsole("Total files (" +  FILE_SELECT_WILDCARD + ") - " + srcfiles.size());			

				for(File srcFile : srcfiles) {
					// to debug in case of failure
					currentFileInProcess = srcFile.getName();
					
					// Parse the Java source file using a Off-the-shelf Java Parser
					List<String> javaSourceTextLines = FileUtils.readLines(srcFile);
					jTokenizer.tokenize(javaSourceTextLines);
										
					String[] interfaceTokens = null;
					String[] commentTokens 	 = null;
					String[] methodTokens 	 = null;
					
					for(ClassTokens c : jTokenizer.getClassTokens()) {
						
						interfaceTokens = c.getInterfaceTokens();
						commentTokens 	= c.getCommentTokens();
						
						for(String mId : c.getMethodIdentifiers()) {
							MethodTokens m = c.getMethodTokens(mId);	
							if(methodTokens == null)
								methodTokens = m.getTokens();
							else
								methodTokens = ArrayUtils.addAll(methodTokens,m.getTokens());
							
							interfaceTokens = ArrayUtils.addAll(interfaceTokens, m.getInterfaceTokens());							
							commentTokens = ArrayUtils.addAll(commentTokens, m.getCommentTokens());
						}
						
						
						// Add document to file name mapping						
						String docref = getDocumentId(c.getIdentifier());

						// Add document to the vocabulary first before adding terms
						Vocabulary.getInstance().addDocument(docref, srcFile.getName());
							
						// Add all collected tokens as terms
						addTerms(interfaceTokens, docref, JavaStructureIdentifiers.CLASS_INTERFACE);													
						
						// Add all collected tokens as terms
						if(methodTokens != null && methodTokens.length > 0)
							addTerms(methodTokens, docref, JavaStructureIdentifiers.METHOD_CONTENT);						
							
						_stopWordFilterForComments.setEnabled(true);
						if(commentTokens != null && commentTokens.length > 0)
							addTerms(commentTokens, docref, JavaStructureIdentifiers.COMMENTS);
						
						writeToOutput(docref, JavaStructureIdentifiers.class);
							
						
						reset(); // Reset so that next class tokens can 
							  		 // be added as new document
							
					}
					
					currentFile++;	// Update the counter
					// print progress
					CtrusHelper.progressMonitor("Progress -", currentFile, totalFiles);
				}
				
			} catch (Exception e) {		
				CtrusHelper.printToConsole("Current file in process - " + currentFileInProcess);
				e.printStackTrace();
			}
			
		}		
	}	
}
