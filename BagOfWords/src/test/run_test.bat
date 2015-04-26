@ECHO OFF

SET m2repo=c:\Users\%USERNAME%\.m2\repository

SET bow=../../target/BagOfWords-0.1b.jar
SET commons-cli=%m2repo%\commons-cli\commons-cli\1.2\commons-cli-1.2.jar
SET commons-io=%m2repo%\commons-io\commons-io\2.4\commons-io-2.4.jar
SET commons-lang=%m2repo%\org\apache\commons\commons-lang3\3.3.2\commons-lang3-3.3.2.jar
SET jdt-core=%m2repo%\org/eclipse/jdt/core/3.3.0-v_771/core-3.3.0-v_771.jar
SET core-contenttype=%m2repo%\org/eclipse/core/contenttype/3.4.200-v20140207-1251/contenttype-3.4.200-v20140207-1251.jar
SET core-jobs=%m2repo%\org/eclipse/core/jobs/3.6.0-v20140424-0053/jobs-3.6.0-v20140424-0053.jar
SET core-res=%m2repo%\org/eclipse/core/resources/3.3.0-v20070604/resources-3.3.0-v20070604.jar
SET core-runtime=%m2repo%\org/eclipse/core/runtime/3.10.0-v20140318-2214/runtime-3.10.0-v20140318-2214.jar
SET equinox-common=%m2repo%\org/eclipse/equinox/common/3.6.200-v20130402-1505/common-3.6.200-v20130402-1505.jar
SET equinox-pref=%m2repo%\org/eclipse/equinox/preferences/3.5.200-v20140224-1527/preferences-3.5.200-v20140224-1527.jar
SET osgi=%m2repo%\org/eclipse/osgi/org.eclipse.osgi/3.7.1/org.eclipse.osgi-3.7.1.jar
SET mapdb=%m2repo%\org/mapdb/mapdb/1.0.6/mapdb-1.0.6.jar

SET en_classpath=%bow%;%commons-cli%;%commons-io%;%commons-lang%;%mapdb%
SET ja_classpath=%en_classpath%;%jdt-core%;%core-contenttype%;%core-jobs%;%core-res%;%core-runtime%;%equinox-common%;%equinox-pref%;%osgi%

SET option1=-minWordLength 3 -useStemming -printVocabulary
SET option2=%option1% -retainNum -methodChunking -caseSensitive
SET option3=-sourceDir ./data/en -outputDir ./output -outputSingleFile en_bow.txt
SET option4=-sourceDir ./data/java -outputDir ./output -outputSingleFile java_bow.txt

IF [%1] == [help]	GOTO param_help
IF [%1] == [en] 	GOTO param_english
IF [%1] == [java] 	GOTO param_java
GOTO param_not_understood		:: For any illegal parameter

:param_help	
	java -cp %en_classpath% ctrus.pa.bow.en.EnBagOfWords -help				
	GOTO end_program

:param_english
	ECHO =========== Test creation of bag-of-words for English text documents ===========	
	java -cp %en_classpath% ctrus.pa.bow.en.EnBagOfWords %option1% %option3%				
	GOTO end_program

:param_java
	ECHO =========== Test creation of bag-of-words for Java programs =========== 
	java -cp %ja_classpath% ctrus.pa.bow.java.JavaBagOfWords %option1% %option2% %option4%		
	GOTO end_program

:param_not_understood
	ECHO No parameter passed choose either 'en' or 'java'
	GOTO end_program

:end_program
