package Latvia_Thesis;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import com.cybozu.labs.langdetect.Language;

public class FacebookCrawler
{
	private static String profileDirectory = "E:\\jars\\langdetect\\profiles";
  static String convertStreamToString(InputStream is)
  {
    Scanner s = new Scanner(is).useDelimiter("\\A");
    return s.hasNext() ? s.next() : "";
  }
  
  public static String detect(String text) {
		Detector detector = null;
		try {
			detector = DetectorFactory.create();
		} catch (LangDetectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		detector.append(text);
		try {
			return detector.detect();
		} catch (LangDetectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
  
  public static void main(String[] args)
  {
    try
    {
    	DetectorFactory.loadProfile(profileDirectory);
        String fileName =  "PCTVL";
        String Facebook_page_id = "PCTVL";
       
        
        String token = "CAALkjXDf7JUBAMtjcH2E9sZCPg8PXelkmYcf3iHqsgpPM466AGHZAsLovj5Ik5IEdZBEK1SRt0L0qeWKQ2Nz1G4MzuOx6MIoeBCbXZBaKmjItqfgfEcHBFI3djP2v7pZCTmcZCO43o2YnK7ilZCw7RmGIKeKrFuP7hX4MbUbZBXzOctKkfceEsB3";
        
        String nextURL = "https://graph.facebook.com/" + Facebook_page_id + "/feed?access_token=" + token;
        
        String url = "jdbc:postgresql://url:5432/latvia";
		String user = "postgres";
		String password = "password";
		Class.forName("org.postgresql.Driver");
		Connection con = DriverManager.getConnection(url,user,password);
       
		String query = "INSERT INTO latvia_articles (content_language , file_content ,  ind_name, post_num) values(?,?,?,?)";
      
      String previousURL = null;
      int counter = 0;
      while ((nextURL != null) && (nextURL != previousURL))
      {
        JSONObject jsonObject = ExtractContentFromPage(nextURL);
        
        previousURL = nextURL;
        
        JSONArray documentSet = (JSONArray)jsonObject.get("data");
        
        Iterator documentItr = documentSet.iterator();
        
        String output = "";
        while (documentItr.hasNext())
        {
          JSONObject innerObj = (JSONObject)documentItr.next();
          if (((innerObj != null ? 1 : 0) & (innerObj.get("message") != null ? 1 : 0)) != 0)
          {      
            
            output = innerObj.get("message").toString();
           
            PreparedStatement ps = con.prepareStatement(query);
            String lang="";
            Pattern pattern = Pattern.compile("[a-zA-Z]");
            if (output.trim() != null && output.split(" ").length > 2) {
            	counter++;	
				Matcher m = pattern.matcher(output);
				if (m.find()) {
					System.out.println("Post Number " + counter);
					System.out.println(output);
					lang = detect(output);
					ps.setString(1, lang);
					ps.setString(2,output);
					ps.setString(3,fileName);
					ps.setInt(4,counter);
					ps.executeUpdate();
					ps.close();
				}
			}
			
			
          
        }
        }
       
        
        JSONObject pagingTags = (JSONObject)jsonObject.get("paging");
        if (pagingTags != null) {
          nextURL = (String)pagingTags.get("next");
        } else {
          nextURL = null;
        }
      }
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
  
  private static JSONObject ExtractContentFromPage(String strurl)
    throws MalformedURLException, IOException, java.text.ParseException, org.json.simple.parser.ParseException
  {
    URL url = new URL(strurl);
    InputStream is = url.openStream();
    
    String data = convertStreamToString(is);
    
    JSONParser jsonParser = new JSONParser();
    JSONObject jsonObject = (JSONObject)jsonParser.parse(data);
    return jsonObject;
  }
}

