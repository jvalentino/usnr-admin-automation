package com.blogspot.jvalentino.usnrauto.util

class TextBeltUtil {

	private static final String USER_AGENT = "Mozilla/5.0";
	
	public static String sendSmsThroughTextBelt(String number, String message) throws Exception {
		
	   String url = "http://textbelt.com/text";
	   URL obj = new URL(url);
	   HttpURLConnection con = (HttpURLConnection) obj.openConnection();

	   //add reuqest header
	   con.setRequestMethod("POST");
	   con.setRequestProperty("User-Agent", USER_AGENT);
	   con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

	   String encodedMessage = URLEncoder.encode(message, "UTF-8");
	   String urlParameters = "number=" + number+ "&message=" + encodedMessage;

	   // Send post request
	   con.setDoOutput(true);
	   DataOutputStream wr = new DataOutputStream(con.getOutputStream());
	   wr.writeBytes(urlParameters);
	   wr.flush();
	   wr.close();

	   int responseCode = con.getResponseCode();
	   System.out.println("\nSending 'POST' request to URL : " + url);
	   System.out.println("Post parameters : " + urlParameters);
	   System.out.println("Response Code : " + responseCode);

	   BufferedReader ins = new BufferedReader(
			   new InputStreamReader(con.getInputStream()));
	   String inputLine;
	   StringBuffer response = new StringBuffer();

	   while ((inputLine = ins.readLine()) != null) {
		   response.append(inputLine);
	   }
	   ins.close();

	   //print result
	   return (response.toString());

   }
}
