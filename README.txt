This is the CBUS I/O Java program - part of the CCMR suite.

Author: Ian Hogg

I have supplied:
* This README text file
* Pre-compiled executable as a jar file
* Sources in a tar file

You will need a native Linux library for the serial driver to implement the jssc.SerialPort API. I believe these are available from Oracle for Linux. This and the JSSC Java library will need to be on the CLASSPATH when run.

To execute the jar file:

$ cd 'to directory where jar file is'
$ java -cp . CBUSIO

To compile the Java sources
expand the tar file into a directory
$ javac *.java
You will also need to ensure that the jssc library is on the classpath:
$ javac -cp 'path to jssc' *.java

