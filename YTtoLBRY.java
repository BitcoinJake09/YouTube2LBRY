import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import javax.net.ssl.HttpsURLConnection;
import java.net.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.io.*;
import java.util.*;

public class YTtoLBRY {
	public static void main(String[] args) throws ParseException {
	String stringToSplit = "";
        String[] tempArray;
        String delimiter = " ";
	String YTtoConvert = "";
	int counts=1;
		if (args.length==0) {
			try {
				File file = new File("subscription_manager"); 
				BufferedReader br = new BufferedReader(new FileReader(file)); 
				String st; 
				while ((st = br.readLine()) != null) {
					st = st.replaceAll(" /><outline text", ""); 
					st = st.replaceAll(" /></outline></body></opml>", ""); 
					st = st.replaceAll("title", ""); 
					st = st.replaceAll("type", ""); 
					st = st.replaceAll("https://www.youtube.com/feeds/videos.xml", "");
					st = st.replaceAll("channel_id", "");
					st = st.replaceAll("xmlUrl", ""); 
					st = st.replaceAll("rss", ""); 
					//System.out.println(st);		
					//System.out.println(st);
					stringToSplit = stringToSplit + st;
				}
        			tempArray = stringToSplit.split(delimiter);
        			for (int i = 13; i < tempArray.length; i++) {
					if (tempArray[i].indexOf('?') >= 1) {
						tempArray[i] = tempArray[i].replaceFirst("=","");
						tempArray[i] = tempArray[i].replaceFirst("=","");
						tempArray[i] = tempArray[i].split("=")[0];
						tempArray[i] = tempArray[i].substring(2, tempArray[i].length()-1);
            					System.out.println(counts+") "+ tempArray[i] +"\n");
						System.out.println(counts+") "+ tempArray[i] +"\n");
						if (i <= tempArray.length-2) {
							YTtoConvert = YTtoConvert +tempArray[i]+",";
						} else {
							YTtoConvert = YTtoConvert +tempArray[i];
						}
						counts++;
					}
				}
				System.out.println(YTtoConvert);
				String temp = getLBRYchannel(YTtoConvert);
				System.out.println(temp);
			} catch(Exception excpets) {
				System.out.println("File not found");
			}
  	/*
		}
		String s = "{\"name\":\"aaa\",\"title\":\"bbb\"}";
	        JSONObject json = (JSONObject) new JSONParser().parse(s);
	        String vout = (String) json.get("title");
		System.out.println(vout);*/
		}
	}
	public static String getLBRYchannel(String YTchannels) throws ParseException {
		try {
        //JSONParser parser = new JSONParser();
      final JSONObject jsonObject = new JSONObject();
      //System.out.println("Checking blockchain info...");
      URL url = new URL("https://api.lbry.com/yt/resolve?channel_ids="+YTchannels+"");
      //System.out.println(url.toString());
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      //con.setRequestProperty("Authorization", "Basic " + encoding);

      con.setRequestMethod("POST");
      con.setRequestProperty("User-Agent", "YTtoLBRY");
      con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
      con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
      con.setDoOutput(true);
      OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
      out.write(jsonObject.toString());
      out.close();

    if(con.getResponseCode()==200) {

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        //System.out.println(response.toString());
        JSONObject response_object = (JSONObject) new JSONParser().parse(response.toString());
        //System.out.println(response_object);
      return ((JSONObject) new JSONParser().parse(response.toString())).toString();
    } else {
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        //System.out.println(response.toString());
        JSONObject response_object = (JSONObject) new JSONParser().parse(response.toString());
	
        return new String();
    }
    } catch (Exception e) {
      System.out.println("problem connecting with LBRY API");
      System.out.println(e);
      // Unable to call API?
    }

    return new String(); // just give them an empty object
  }

}
