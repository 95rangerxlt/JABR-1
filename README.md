# JABS
JABS stands for JABS: Automated Booking System.

JABS is appointment booking software written in Java.

# Installation

## Prerequisites
To compile JABS, you will need have the Java Development Kit installed. It does not need to be on your system path.

Compilation requires Windows Command Prompt.

## How to Build
Run build.cmd to compile. The program will be compiled and placed in the *deployment* folder.

Copy the contents of the deployment folder wherever you want to install JABS. Note that the files JABS uses are all in the same directory, with no registry entries or files hidden away. Be sure to copy all files in the JABS directory when moving the program if you want to keep your data.

## How to Run
You need to call JABS from the command line. Start a command shell in the JABS *deployment* directory and type
`java -jar jabs.jar`
to run jabs