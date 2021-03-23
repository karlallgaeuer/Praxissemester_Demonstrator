# BioToolsCompose Demonstrator
Researchers in the life sciences – just as in other domains – regularly face 
the need of composing several individual software tools into pipelines or
workflows that perform the specific data analysis processes that they need in
their research. The idea of automated workflow composition is to simplify the
work with scientific workflows and free life science researchers from having to
deal with the technicalities of software composition. This would not only save
valuable research time, but also reduce errors, and enable new scientific
findings by discovering workflows that researchers would not have thought of
themselves.

On one important aspect towards this vision, namely the semantic annotation of 
tools on a large scale, the life science community has made significant progress
in the last years: The [EDAM ontology](http://edamontology.org/page) 
provides a controlled vocabulary of bioinformatics operations, data types and
formats, and the [bio.tools registry](https://bio.tools/) has become a large collection of 
bioinformatics tools that are semantically annotated with terms from the EDAM ontology. 

As demonstrated in a recent Bioinformatics publication titled [Automated Workflow
Composition in Mass Spectrometry Based Proteomics](https://academic.oup.com/bioinformatics/article/35/4/656/5060940), 
this forms a solid basis for performing automated workflow composition in the
life sciences domain. The BioToolsCompose Demonstrator brings the use cases
described in the paper to life, enabling researchers to explore workflows
composed from bio.tools through an easy-to-use web interface. 

## Installation
1. Install Java (1.8 or more) JDK

2. (optional) Install an IDE (e.g. Eclipse)

3.  Install Git

4.  Install Node.js (https://nodejs.org/en/download/)

5.  Install Maven (https://maven.apache.org/install.html) (not required if using IDE such as Eclipse)

6. Clone project from Git (https://github.com/karlallgaeuer/Praxissemester_Demonstrator)

7. (optional) Import the project from the file system through your IDE (e.g. in Eclipse as "existing maven project")

8.  Fetch the dependencies with Maven (e.g. in Eclipse Maven -> Update Project)

9. Add the APE-library. Location: "~/lib/APE-0.9.1.jar"

9.  Run Application.java (Starts the backend with Spring Boot (for the REST-API))

10.  In the command line, go into the directory "~/src/main/webapp/js" by typing "cd [directory]"

11.  To start the Node.js server type "node server.js" (Port changable by editing server.js (Change it in line 14 after "listen")) (Default port 8080)

12. Open Chrome with disabled web security (Linux: Run "google-chrome --disable-web-security")(Windows: Go to the folder where Chrome.exe is and run this command: "chrome.exe --disable-web-security")

13. Open "http://localhost:8080/" in your browser 

## Support
You can contact Vedran Kasalica (v.kasalica@uu.nl) or Anna-Lena Lamprecht 
(a.l.lamprecht@uu.nl) for questions on this project.

## Authors and Acknowledgments
The BioToolsCompose Demonstrator has been developed by Karl Allgäuer, 
Vedran Kasalica, Anna-Lena Lamprecht.

## License
This project is licensed under the Apache 2.0 license.

