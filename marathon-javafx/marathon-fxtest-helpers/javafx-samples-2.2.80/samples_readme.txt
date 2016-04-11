JAVAFX SAMPLES README

Contents

What's in the samples zip file?
What do I need to set up my environment?
How do I run the prebuilt samples?
How do I run the sample projects in NetBeans IDE?
Sample Descriptions


===============================
What's in the samples zip file?
===============================

The samples zip file contains prebuilt samples that you can run, plus NetBeans 
project files for each sample.

Extracting the zip file produces the following directory structure:

--src  (Contains a NetBeans project for each sample)
    --<Sample1>
        --nbproject
        --src
        --build.xml
        --manifest.mf 
    --<Sample2>
        ...
<sample1>.jar   (Runs the sample as a standalone application)
<sample2>.jar
 ...
 
    
========================================
What do I need to set up my environment?
========================================

To run the samples, you need the following environment:

- A version of the JavaFX SDK or JavaFX Runtime that matches the sample zip 
  file version. The JavaFX SDK includes the JavaFX Runtime.
- A supported version** of the Java Development Kit (JDK) or Java Runtime 
  Environment (JRE). The JDK includes the JRE.


To open the samples projects in NetBeans IDE, you need the following 
environment:

- A version of the JavaFX SDK that matches the sample zip file version.
- A supported version** of the JDK.
- A supported version** of NetBeans IDE.

**To find the supported versions of operating system, browser, Java platform, 
and NetBeans IDE for a particular JavaFX release, see the release 
documentation page at
http://docs.oracle.com/javafx/release-documentation.html 

To get the latest release of JavaFX, go to 
http://www.oracle.com/technetwork/java/javafx/downloads/index.html


==================================
How do I run the prebuilt samples?
==================================

To run as a standalone application, double-click the JAR file.

You can also package any sample as an applet, JNLP, or native bundle that 
includes an installer and a copy of the JRE for execution in an environment 
that does not have JavaFX installed. See "Deploying JavaFX Applications" at
http://docs.oracle.com/javafx/2/deployment/jfxpub-deployment.htm for
information.

=================================================
How do I run the sample projects in NetBeans IDE?
=================================================

The following procedure assumes you have already extracted the samples zip 
file. The DataApp sample requires some additional setup, as described in the
DataApp readme in the src\DataApp directory.

To run the sample projects:

1. In NetBeans IDE, click Open Project in the toolbar, or on the File menu, 
   select Open Project.
2. Navigate to the location in which you unzipped the samples, and in the src 
   directory, select a project, then click Open.
3. For the SwingInterop sample only, add the JavaFX libraries as follows:
   a. Right-click the project icon in the Projects pane and choose Properties.
   b. In the Properties dialog box, select Libraries in the Categories pane.
   c. Delete the existing jfxrt.jar and ant-javafx.jar entries.
   d. Using the Add JAR/Folder button, add two entries, one for the jfxrt.jar
      library and one for the ant-javafx.jar library, using the appropriate 
      paths for your installation:
      - jfxrt.jar is in the lib subdirectory of the JRE installation when 
        running with JDK 7 and in the lib subdirectory of the JavaFX Runtime 
        installation when running with JDK 6.
      - ant-javafx.jar is in the lib subdirectory of the JDK installation when 
        running with JDK 7 and in the lib subdirectory of the JavaFX SDK
        installation when running with JDK 6.
4. To run the application in NetBeans IDE, in the Project pane, right-click 
   the project and choose Run.


===================
Sample Descriptions
===================

The following samples are included in the zip file.

--------
Ensemble

A gallery of examples, which demonstrates a large variety of JavaFX features, 
such as animation, charts, and controls. For each feature, you can view the 
running sample, read a description, copy the source code, and follow links to 
the relevant API documentation.


---------------------------------------
DataApp (Henley Auto Sales Application)

An end-to-end solution for a fictional global automobile company called Henley 
Automobiles. Automobile sales are simulated on an EJB server using JavaDB, and 
the data is available via Derby and a RESTful web service. The client 
demonstrates a variety of data presentations, using a mix of FXML and JavaFX.

The DataApp sample has multiple NetBeans projects and cannot be run without 
some additional setup. You can find the DataApp readme and the NetBeans 
project files in the src\DataApp directory.


--------------
FXML-LoginDemo

A UI that demonstrates a simple login system and user session, using FXML to 
set up the UI controls. Users can log in and edit their profile information.


------------
SwingInterop

A sample that demonstrates how you can add JavaFX to an existing Swing 
applciation. Note that the SwingInterop sample is set up in NetBeans IDE as 
a Java project that incorporates JavaFX components. In order to display and 
run the application in NetBeans IDE, you must add the jfxrt.jar and 
ant-javafx.jar libraries to the NetBeans project, as described in the 
instructions on how to run the samples in NetBeans IDE.


------------
BrickBreaker

A classic game that demonstrates the use of JavaFX animation, images, and 
effects. The object of the game is to use a bat to bounce a ball upwards to 
break bricks while. capturing various reward tokens.


-------------------------------------------------------------------- 
Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
