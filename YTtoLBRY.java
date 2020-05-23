/* YTtoLBRY.java @BitcoinJake09 05/17/2020
This is an opensource GUI to help convert YT accounts you follow to LBRY if they have a LBRY account

if you are compiling and running yourself you may need to change your java.policy to allow it to run to be able to midify files to save things. http://mindprod.com/jgloss/signedapplets.html#PROCESS
*/
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
        String[] tempArray, finalArray, tempSplit, finalLBRYchannels;
        String delimiter = " ";
	String YTtoConvert = "";
	String tempLBRYchannels = "";
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
            					//System.out.println(counts+") "+ tempArray[i] +"\n");
						//System.out.println(counts+") "+ tempArray[i] +"\n");
						if (i <= tempArray.length-2) {
							YTtoConvert = YTtoConvert +tempArray[i]+",";
						} else {
							YTtoConvert = YTtoConvert +tempArray[i];
						}
						counts++;
					}
				}
				//System.out.println(YTtoConvert);
				String temp = getLBRYchannel(YTtoConvert);
			        JSONParser parser = new JSONParser();
			        JSONObject response_object = (JSONObject) parser.parse(temp);
				//System.out.println(response_object.toString());
				JSONObject temp_object = (JSONObject) response_object.get("data");
				String lbryChannels = temp_object.get("channels").toString(); 
				delimiter = ",";
        			finalArray = lbryChannels.split(delimiter);
				finalArray[0] = finalArray[0].substring(1, finalArray[0].length());
				finalArray[finalArray.length-1] = finalArray[finalArray.length-1].substring(0, finalArray[finalArray.length-1].length()-1);
				//System.out.println(temp);
				for (int z = 0; z < finalArray.length; z++) {
				delimiter = ":";
        			tempSplit = finalArray[z].split(delimiter);
				//System.out.println("tempSplit[0]) "+ tempSplit[0]);
				//System.out.println("tempSplit[1]) "+ tempSplit[1]);
				if (tempSplit[1].length() >= 5) {
				tempLBRYchannels = tempLBRYchannels + tempSplit[1] + ","; 		
				}
				//System.out.println("Channel " +z+") "+ finalArray[z]);
				
				}
tempLBRYchannels  = tempLBRYchannels.substring(0, tempLBRYchannels.length()-1);
		tempLBRYchannels = tempLBRYchannels.replaceAll("\"", "");
		delimiter = ",";
        			finalLBRYchannels = tempLBRYchannels.split(delimiter);
      File myObj = new File("LBRY-Subscriptions.txt");
    if (myObj.createNewFile()) {
        System.out.println("File created: " + myObj.getName());
      } else {
        System.out.println("File already exists.");
      }
      FileWriter myWriter = new FileWriter("LBRY-Subscriptions.txt");

		for (int a = 0; a < finalLBRYchannels.length; a++) {
		System.out.println("lbry://" + finalLBRYchannels[a]);
		myWriter.write("lbry://" + finalLBRYchannels[a]+"\n");
	}
         myWriter.close();
      System.out.println("Successfully wrote to the file.");
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
