package de.pictureedit.httprequest;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public abstract class HTTPRequest {

	public static void sendHTTPRequest(String serverFolder, String photograph,
			String name, String date) throws Exception {
		HttpClient httpclient = new DefaultHttpClient();

		List<NameValuePair> attributes = new ArrayList<NameValuePair>();
		attributes.add(new BasicNameValuePair("serverFolder", serverFolder));
		attributes.add(new BasicNameValuePair("photograph", photograph));
		attributes.add(new BasicNameValuePair("name", name));
		attributes.add(new BasicNameValuePair("date", date));
		attributes.add(new BasicNameValuePair("token", "Jennyfer"));

		URI uri = URIUtils.createURI("http", "h1875803.stratoserver.net", -1,
				"/picture/addNewGallery.php",
				URLEncodedUtils.format(attributes, "UTF-8"), null);

		HttpGet httpget = new HttpGet(uri);
		HttpResponse response = httpclient.execute(httpget);
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			if (EntityUtils.toString(entity).equals("Access denied!")) {
				throw new Exception("Webserver response: \"Access denied\".");
			}
		}
	}

}
