#**IOC Wizard**
## A GUI-based Utility for extracting and processing Indicators of Compromise(IOCs)
###Functionality:
*	Extract IOCs in the form of MD5 hashes, SHA hashes, domain names, and IPv4 addresses from text files or pasted text, and save these in separate text files.
*	Lookup the equivalent MD5s hashes for particular SHA hashes with the VirusTotal API
*	Create IOC feeds in both STIX and JSON formats, compatible with Carbon Black and McAfee monitoring services

###Build:
requires Java 8
	git clone https://github.com/arjchenn/ioc-wizard.git
	mvn clean install
	**Or, with Eclipse:**
		Clone the repository in the git view: https://github.com/arjchenn/ioc-wizard.git
		Import it as an existing maven project from the local repository

![ioctest](https://cloud.githubusercontent.com/assets/23390502/20253102/40c69558-a9f6-11e6-8262-42beb4200b6f.JPG)
