//Uses the VirusTotal.com API to get MD5 format hashes from SHA format hashes
package extractor.address.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

public class Lookup{

	private static CloseableHttpClient httpclient;
	private static HttpPost httppost;

	//Call the methods to post an http request to the API for a hash and extract the MD5 from the response. Takes a VirusTotal API key and a hash to lookup
	public static String getmd5(String search, String userKey) throws UnknownHostException, UnsupportedEncodingException, ClientProtocolException, IOException{
		return extractmd5(lookup(search, userKey));

	}

	//Acquires the full http response from the API call
	public static String lookup(String search, String userKey) throws UnknownHostException, UnsupportedEncodingException, ClientProtocolException, IOException {

		httpclient = HttpClients.createDefault();
		httppost = new HttpPost("https://www.virustotal.com/vtapi/v2/file/report");
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>(2);
		StringBuilder next = new StringBuilder();

		params.add(new BasicNameValuePair("resource", search));
		params.add(new BasicNameValuePair("apikey", userKey));

		httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		HttpResponse response = httpclient.execute(httppost);
		HttpEntity entity = response.getEntity();

		int i;
		if (entity != null) {
		    InputStream instream = entity.getContent();
		    while((i=instream.read())!=-1) next.append((char)i);

		    instream.close();

		    System.out.println("next: " + next.toString());
		    return next.toString();
		}else {
			return null;
		}

	}

	//Exctracts the MD5 hash from an API response
	public static String extractmd5(String next){

		String md5;

		if(next == null) return null;

		if (next.toString().contains("md5")){
			int index = next.indexOf("md5");
			md5 = next.substring(index);

			md5 = md5.replaceAll("[^a-zA-Z\\d]|(md5)", "");
			System.out.println(md5);
		}else {
			return "";
		}

		return md5;

	}

}
