package Latvia_Thesis;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import com.cybozu.labs.langdetect.Language;

public class FacebookUserProfiles
{
	
	private static String profileDirectory = "E:\\jars\\langdetect\\profiles";
	private StringBuilder englishPosts = new StringBuilder();
	private StringBuilder latvianPosts = new StringBuilder();
	private StringBuilder russianPosts = new StringBuilder();
	
	
	public void Twitter() throws SQLException, IOException 
	{	    		
		AtomicInteger ct1=new AtomicInteger(0);
		AtomicInteger ct2=new AtomicInteger(0);
		AtomicInteger ct3=new AtomicInteger(0);
	    	try{
				FileWriter f2 = new FileWriter("D:/Thesis/Latvia/Twitter.csv");
	    		Files.walk(Paths.get("D:/Thesis/Latvia/Latvia_files/Twitter")).forEach(filePath -> {
		    		String filename = filePath.getFileName().toString();
		    		System.out.println(filename);
	    			if (Files.isRegularFile(filePath)&&filename.endsWith(".txt")) {
			    	try{
			    		englishPosts.setLength(0);
						latvianPosts.setLength(0);
						russianPosts.setLength(0);
						ct1.set(0);
						ct2.set(0);
						ct3.set(0);
			    		List<String> lines = Files.readAllLines(filePath);
			    		for (String post: lines) {
			    		  Pattern pattern = Pattern
									.compile("[a-zA-Z]");

							if (post.trim() != null
									&& post.split(" ").length > 2) {
								Matcher m = pattern.matcher(post);
								if (m.find()) {
									//System.out.println(post);
									String lang = detect(post);
									if (lang.equals("en"))
									{
										englishPosts.append(post);
							    		ct1.incrementAndGet();
									}
									else if (lang.equals("lv"))
									{
										latvianPosts.append(post);
										ct2.incrementAndGet();
									}
									else if (lang.equals("ru"))
									{
										russianPosts.append(post);
										ct3.incrementAndGet();
									}
								}
							}
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.exit(0);
					}
			
				try {
					f2.write(filename+"*"+ct1+"*"+ct2+"*"+ct3+"\n");
					if (englishPosts.length() > 0)
						f2.write(filename+"*"+"english"+"*"+englishPosts.toString()+"\n");
					if (latvianPosts.length() > 0)
						f2.write(filename+"*"+"latvian"+"*"+latvianPosts.toString()+"\n");
					if (russianPosts.length() > 0)
						f2.write(filename+"*"+"russian"+"*"+russianPosts.toString()+"\n");
					 
				
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    			}
			});
	    	f2.close();	
} catch (Exception e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}

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

	    	public ArrayList<Language> detectLangs(String text)
	    			throws LangDetectException {
	    		Detector detector = DetectorFactory.create();
	    		detector.append(text);
	    		return detector.getProbabilities();
	    	}
	    
	public static void main(String args[]) throws SQLException, IOException, ClassNotFoundException
	{
		try {
			
			DetectorFactory.loadProfile(profileDirectory);
		} catch (LangDetectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String url = "jdbc:postgresql://129.219.60.22:5432/latvia";
		String user = "postgres";
		String password = "password";
		Class.forName("org.postgresql.Driver");
		Connection con = DriverManager.getConnection(url,user,password);
       
		String query = "INSERT INTO latvia_articles (content_language , file_content ,  ind_name, post_num) values(?,?,?,?)";
		Files.walk(Paths.get("E:\\Latvia\\Latviya_files\\Facebook_HTML")).forEach(filePath -> {
    		String filename = filePath.getFileName().toString();
    		System.out.println(filename);
			if (Files.isRegularFile(filePath)&&filename.endsWith(".html")) {
	    	try{
				
				byte[] encoded = Files.readAllBytes(filePath);
	    		String html=  new String(encoded, "UTF-8");
	    		Document doc = Jsoup.parse(html);
	    		Elements p= doc.getElementsByTag("p");
	    		int counter =0;
	    		for (Element x: p) {
	    			String post="";
	    		  post = x.text();
	    		  Pattern pattern = Pattern.compile("[a-zA-Z]");

					if (post.trim() != null && post.split(" ").length > 2) {
						counter++;
						Matcher m = pattern.matcher(post);
						if (m.find()) {
				            PreparedStatement ps = con.prepareStatement(query);
							System.out.println("Post Number " + counter);
							System.out.println(post);
							String lang = detect(post);
							ps.setString(1, lang);
							ps.setString(2,post);
							ps.setString(3,filename);
							ps.setInt(4,counter);
							ps.executeUpdate();
							ps.close();
						}
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(0);
			}
	}
	});
		
} 

	}
