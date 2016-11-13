//This class is the object that will extract IOCs from files and pasted text
package extractor.address.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class IOCObject {

	private List<File> files;
	private String str;
	private HashSet<String> md5s;
	private HashSet<String> sha1s;
	private HashSet<String> sha256s;
	private HashSet<String> ipv4s;
	private HashSet<String> domains;
	//Regular expressions for extracting the various IOC types
	private final String md5Reg = "\\b[a-fA-F0-9]{32}\\b";
	private final String sha1Reg = "\\b[a-fA-F0-9]{40}\\b";
	private final String sha256Reg = "\\b[a-fA-F0-9]{64}\\b";
	private final String ipv4Reg = "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
	private final String domainReg = "\\b([a-z0-9\\[\\]]+(-[a-z0-9\\[\\]]+)*\\.)+[\\[\\]a-z]{2,}\\b";
	private final String urlReg = "(h[a-z][a-z]p(s)?://.)?(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)";

	//For IOCs from a file
	public IOCObject() throws IOException{

		str = "";
		open();
		extract();

	}

	//For IOCs from pasted text
	public IOCObject(String input) throws IOException{

		str = input;
		extract();
		System.out.println(sha1s);

	}

	private void open() throws IOException{

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open IOC File");
		files = fileChooser.showOpenMultipleDialog(new Stage());

		for (File file : files) str = str.concat("\n" + new String(Files.readAllBytes(file.toPath())));

		System.out.println(str);

	}

	private void extract() throws IOException{

		md5Extract();
		ipv4Extract();
		sha1Extract();
		sha256Extract();
		domainExtract();
		urlExtract();

	}

	//These methods use the regular expressions defined in the above fields to exctract iocs (MD5 hashes, SHA hashes, IPv4 addresses, and possible domain names and urls)
	//from the selected files or pasted text

	private void md5Extract() throws IOException{

		Pattern r = Pattern.compile(md5Reg);
		Matcher matcher = r.matcher(str);
		md5s = new HashSet<String>();

		while(matcher.find()) md5s.add(matcher.group());

	}

	private void ipv4Extract() throws IOException{

		Pattern r = Pattern.compile(ipv4Reg);
		Matcher matcher = r.matcher(str);
		ipv4s = new HashSet<String>();

		while(matcher.find()) ipv4s.add(matcher.group());

	}

	private void sha1Extract() throws IOException{

		Pattern r = Pattern.compile(sha1Reg);
		Matcher matcher = r.matcher(str);
		sha1s = new HashSet<String>();

		while (matcher.find()) sha1s.add(matcher.group());

	}

	private void sha256Extract() throws IOException{

		Pattern r = Pattern.compile(sha256Reg);
		Matcher matcher = r.matcher(str);
		sha256s = new HashSet<String>();

		while (matcher.find()) sha256s.add(matcher.group());

	}

	private void domainExtract() throws IOException{

		Pattern r = Pattern.compile(domainReg);
		Matcher matcher = r.matcher(str);
		domains = new HashSet<String>();

		while (matcher.find()) domains.add(matcher.group());

	}

	private void urlExtract() throws IOException{

		Pattern r = Pattern.compile(urlReg);
		Matcher matcher = r.matcher(str);

		while (matcher.find()) domains.add(matcher.group());

	}

	public List<File> getFiles() {

		return files;

	}

	public HashSet<String> getMd5s() {

		return md5s;

	}

	public HashSet<String> getSha1s() {

		return sha1s;

	}

	public HashSet<String> getSha256s() {

		return sha256s;

	}

	public HashSet<String> getIpv4s() {

		return ipv4s;

	}


	public HashSet<String> getDomains() {

		return domains;

	}

}
