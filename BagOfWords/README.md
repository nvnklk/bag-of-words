Bag of Words
============
Create a multiset (bag) of terms for a corpus where multiplicity of terms are 
preserved but not the word order.
 
Bag-Of-Words (BOW) is a popular data representation model for many Information 
Retrieval (IR) and Text processing methods. A term is an unit in this model and a 
term is defined as per the problem context. In this model each term is considered 
independent disregarding the word ordering. The sentence - "An alternative view", has 
same probability as "view alternative an". Due to this assumption, Bag-Of-Word model 
looses the semantic and contextual meaning/usage of a term. In general, during text 
processing a word is considered as a term. For example, in the sentence 
"Life is beautiful" has three terms. Similarly, in case of a Java program, a term 
can be the name of a variable, type, method or a class. It is customary to use 
camel-cased words in a program. In such cases, a term can be a compound word such 
as "LifeIsMagic" or a part of a compound word (split at every capitalized character).

The Bag-Of-Words module provides a basic scaffolding to easily construct a bag-of-words
model from a corpus of documents. This can be extended to provide different 
implementations based on the definition of a term. At present, the module includes 
default implementations for creating bag-of-words from English text documents and 
Java programs.

Build and Try
-------------

Building of module is based on Maven 3.x, at the root of the project execute the 
following commands

	mvn clean package

This will compile the source and create a jar file under "<Proj_Root>/target" folder.
English and a Java document samples are available in "<Proj_Root>/src/test" folder.
A pre-configured windows batch file is available to try the sample and see the help.

	cd src/test
	run_test.bat help

To generate BOW for sample English documents in "<Proj_Root>/src/test/data/en" folder
	
	runtest.bat en

You will find BOW in en_bow.txt and vocabulary in voc.txt in "<Proj_Root>/src/test" folder.
To generate BOW for sample Java documents in "<Proj_Root>/src/test/data/java" folder.

	run_test.bat java

You will find BOW in java_bow.txt and vocabulary in voc.txt in "<Proj_Root>/src/test" folder

Usage
-----

Bag-of-Words (BOW) model for two types of documents - English text and Java Programs 
are provided. The BOW model for English text documents can be created as below -

	java EnBagOfWords [options] -sourceDir <docs dir> -ouputDir <dir>

***Example***: For the test documents packaged with this module
	
	java EnBagOfWords -minWordLength 3 -useStemming -outputSingleFile outbow.txt 
		-sourceDir ./test/en -ouputDir ./test
		
### Default List of options

The following are default options available. However, this may vary depending on
specific extensions. 		   

 	-caseSensitive            	Retain the capital characters in the term
	-debugLog                 	Output debug log
 	-help                     	Print this help
 	-minWordLength <arg>      	Minimum word length to consider, default is 3
 	-ouputDir <arg>           	Directory to write output
 	-outputSingleFile <arg>   	Output single file with each line corresponding
    	                       		to an input source file
 	-replaceJargons <arg>      	Replace jargon with full word provided in a file
 	-retainNum                	Retain the numerals, default ignored
 	-sourceDir <arg>          	Directory containing input documents
 	-documentsPerLine         	Each line is a document in the input file(s)
 	-documentIdDelimiter <arg>	Term left to delimiter is considered document Id, 
 									default is space	
 	-stemAlgo <arg>           	Stemming algorithm to use, default is PORTER
 	-stopWordsFile <arg>      	Stop words file
 	-termChunkChars <arg>     	Chunk characters eg. ";,_:"
 	-ignoreSpecialChars <arg> 	Special characters to ignore eg. "_$" 
 	-noStemming              	Do not stem the terms, default is to stem
 	-printVocabulary		  	Print the vocabulary to a file 'voc.txt'	
 	-hashTerms <arg>		  	Hash the term if its length exceeds <arg>
	-preserveDocId 		  	  	Retain the document ID or name from input file
	
Additional options for EnBagOfWords

	--NILL--

Additional options for JavaBagOfWords
	
	-splitCamelCase 		  	Split camel cased words
	-retainCompoundWords	  	Retain compound terms (eg.camel cased) along with 
								  split words
	-methodChunking 		  	Create BOW per method, default is per class
	-ignoreComments		  	  	Ignore comments in source files
	-considerCopyright		  	Consider terms from copyright notice in source files,
								  ignored by default

Extending
---------

To construct bag-of-words for a new document(s), extend the implementation for 
ctrus.pa.bow.core.DefaultBagOfWords and implement methods setup() and create(). To 
provide more options (passed as command line arguments) extend the implementation for
ctrus.pa.bow.core.DefaultOptions and implement method defineOptions().

To yield good results from a bag-of-words model, terms have to be pre-processed. This 
module provides an extensible approach to add a chain of filters and transformers. A
filter will decide if a term should be added to the model. For example, very high 
frequency words in an English text are filtered. Similarly, length of a term in the 
model can be controlled through a filter. A custom filter can be added by extending
ctrus.pa.bow.term.filter.BaseFilter and implementing filter(term) method. For easy 
access to the filters a ctrus.pa.bow.term.FilterFactory class is provided. A custom 
filter can be made available by extending ctrus.pa.bow.term.filter.BaseFilterFactory. 

A transformer will facilitate modification of a term. For example, a camel-cased word 
is transformed into multiple terms split at every capital case character. In case a 
term is transformed to multiple terms, the transformer should return multiple terms 
as a single string with each term separated by a single space. So a term - 
"LifeIsMagic" is transformed by CamelCaseTransformer as - "Life Is Magic". A custom 
transformer can be added by extending ctrus.pa.bow.term.transformation.BaseTransformer 
and implementing transform(term) method. For easy access to the transformers a 
ctrus.pa.bow.term.TransformerFactory class is provided. A custom transformer can be 
made available by extending ctrus.pa.bow.term.transformation.BaseTrasformerFactory.


General Information
-------------------

### Contact
Please direct your questions to [Naveen Kulkarni](naveen{dot}kulkarni{at}research{dot}iiit{dot}ac{dot}in)
with email subject - "GitHub BOW"

### Dependency

1. Apache Commons CLI 1.2 - Parsing command line arguments passed to the program	
	http://commons.apache.org/proper/commons-cli/ 	

2. Apache Commons Lang 3.3.2 - Helper utilities for String manipulation
	http://commons.apache.org/proper/commons-lang/

3. Apache Commons IO 2.4 - Helper utilities for IO operations
	http://commons.apache.org/proper/commons-io/

4. Eclipse JDT - A Java Parser with AST generation
	https://eclipse.org/jdt/, specifically, following jars are referenced
	
		org.eclipse.jdt.core
		org.eclipse.core.contenttype
		org.eclipse.core.jobs
		org.eclipse.core.resources
		org.eclipse.core.runtime
		org.eclipse.equinox.common
		org.eclipse.equinox.preferences
		org.eclipse.osgi
	
5. MapDB 1.0.6 - A Java collection backed by disk or memory store
	http://mapdb.org/
	
5. English Stop words 
	https://code.google.com/p/stop-words/
	
### License

Bag Of Words is licensed under the GPLv3. Please see the LICENSE file for 
the full license.

Bag Of Words
Copyright (C) 2015 Naveen Kulkarni

This program is free software: you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by the 
Free Software Foundation, either version 3 of the License, or (at your 
option) any later version.

This program is distributed in the hope that it will be useful, but 
WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License 
for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
