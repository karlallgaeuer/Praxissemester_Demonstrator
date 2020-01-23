1.1 Install Java (1.8 or more) JDK 
1.2 (optional) Install an IDE (e.g. Eclipse)
2.  Install Git
3.  Install Node.js (https://nodejs.org/en/download/) - do then need nvm?
4.  Install Maven (https://maven.apache.org/install.html) (not required if using IDE such as Eclipse)
5.1 Setup SSH key for the git repository
5.2 Clone project from Git (https://git.science.uu.nl/v.kasalica/biotoolcomposedemo)
5.3 (optional) Import the project from the file system through your IDE (e.g. in Eclipse as "existing maven project")
6.  Fetch the dependencies with Maven (e.g. in Eclipse Maven -> Update Project)
7.  Run Application.java (Starts the backend with Spring Boot (for the REST-API))
8.  In the command line, go into the directory "~/git/biotoolcompesedemo/src/main/webapp/js" by typing "cd [directory]"
9.  To start the Node.js server type "node server.js" (Port changable by editing server.js (Change it in line 14 after "listen")) (Default port 8080)
10. Open Chrome with disabled web security (Linux: Run "google-chrome --disable-web-security")(Windows: Go to the folder where Chrome.exe is and run this command: "chrome.exe --disable-web-security")
11. Open "http://localhost:8080/" in your browser 
