package Latvia_Thesis;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class CrawlTwitterProfiles {
	//private static String profileDirectory = "E:\\jars\\langdetect\\profiles";
	public static final String PATH = "E:\\Latvia\\Latviya_files\\twitter_users";
	public static List<String> userList = new ArrayList<String>();
	public static final int MAX_PAGE_COUNT = 100;
	private final static String CONSUMER_KEY = "P2Lj6RKqso0HZs8cXV1t42szc";
	private final static String CONSUMER_KEY_SECRET = "YtKkUK2idaXF38a2VOiRD2uipkUt7GDgwV5jlJcTnjcULqvNFO";
	private final static String ACCESS_TOKEN = "2362166354-cdW4LBUEzeCQveXsmmZQHg04lKc1OZOQnc8VUv3";
	private final static String ACCESS_TOKEN_SECRET = "Ri44RfzNkL40N9YipxQqoeJsBLupNkNBwR84zJaYFESUm";
	
	private static String detect(String text) {
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

	public static void getuserList() {
		userList.add("aivarslembergs");
		userList.add("AlexanderMirsky");
		userList.add("ayobenes");
		userList.add("Bartasevics");
		userList.add("edgarsrinkevics");
		userList.add("EinarsGraudins");
		userList.add("ekrivcova");
		userList.add("EllaAleksejeva");
		userList.add("faithhealer_ru");
		userList.add("Gaponenko_54");
		userList.add("illarion_girs");
		userList.add("imhoclub");
		userList.add("Kalniete");
		userList.add("Kuzins");
		userList.add("Latgola_Latgale");
		userList.add("lato_lv");
		userList.add("linderman2013");
		userList.add("Lvnoncitizens");
		userList.add("Maris_Zanders");
		userList.add("olegsridzenieks");
		userList.add("pabriks");
		userList.add("PCTVL");
		userList.add("RihardCeglowski");
		userList.add("Straujuma");
		userList.add("timur709");
		userList.add("VairaVF");
		userList.add("vdombrovskis");
		userList.add("VeikoSpolitis");
		userList.add("Vejonis");
		userList.add("vl_tblnnk");
		userList.add("vofnlatvia");
		userList.add("Tatjana_Zdanoka");
		userList.add("raudseps");
		userList.add("informzarya");
		
		/*
		 * int pageCount = 0; BufferedReader in = null; try { in = new
		 * BufferedReader(new FileReader(PATH + "Lenka_Twitter.xlsx")); String
		 * next = null; while ((next = in.readLine()) != null) { if (++pageCount
		 * > MAX_PAGE_COUNT) break; next = next.trim(); if (next == "")
		 * continue; String parts[] = next.split(","); if (parts.length > 2)
		 * userList.add(parts[2].trim()); } } catch (Exception e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */
	}

	public static void main(String[] args) {
		// gets Twitter instance with default credentials
		try {
		getuserList();
		int usercount =0;
		int twitterCallsCounter = 0;
		long startTime = System.currentTimeMillis();
		Twitter twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_KEY_SECRET);
		AccessToken oathAccessToken = new AccessToken(ACCESS_TOKEN,ACCESS_TOKEN_SECRET);
		twitter.setOAuthAccessToken(oathAccessToken);
		
		String url = "jdbc:postgresql://129.219.60.22:5432/latvia";
		String user = "postgres";
		String password = "password";
		Class.forName("org.postgresql.Driver");
		Connection con = DriverManager.getConnection(url,user,password);
       
		Map<String , String> hm = new HashMap<String , String>();
		File f1 = new File("E:\\Latvia\\final.txt");
		BufferedReader in  = new BufferedReader ( new FileReader(f1));
		String line;
		while((line = in.readLine())!= null)
		{
			System.out.println(line);
			String[] cols = line.split("\t");
			hm.put(cols[0].trim(), cols[2]+","+ cols[1]); // position = cols[1] org_type and party/individuals = cols[2] org_name			
		}
		
		String query = "INSERT INTO documents( party_individuals, \"position\", language, content) VALUES (?, ?, ?, ?);";
		//FileWriter fr = new FileWriter(new File("E://Twitterusers.txt"));
		
			Iterator<String> userListIterator = userList.iterator();
			while (userListIterator.hasNext() && usercount < 1) {
				PreparedStatement ps = con.prepareStatement(query);
				String nextEntry = userListIterator.next();
				System.out.println("______________________" + nextEntry	+ "______________________");
				// String fileName = PATH + nextEntry+ ".txt";
				List<Status> statuses = null;
				String filename = nextEntry;
				int previousCount = Integer.MAX_VALUE;
				Paging paging = null;
				int page = 1;
				while (previousCount > 0) {
					paging = new Paging(page, 200);
					statuses = twitter.getUserTimeline(filename, paging);
				if (++twitterCallsCounter > 170){
						try{	
								final long endTime = System.currentTimeMillis();
							if (endTime - startTime <= 900000) {
								Thread.sleep(960000 - (endTime - startTime));
							}
							twitterCallsCounter = 0;
							startTime = System.currentTimeMillis();
						}
							catch (InterruptedException e) {
								e.printStackTrace();
							}
					}
					previousCount = statuses.size();
					System.out.println("Page Number: " + page + " of @" + filename
							+ "'s user timeline with " + statuses.size()
							+ " and call number is " + twitterCallsCounter);
					for (Status status : statuses) {
						String ind_party = hm.get(filename.trim()).split(",")[0]; 
						String position = hm.get(filename.trim()).split(",")[1];
						String lang = status.getLang();
						String post = status.getText();
						ps.setString(1, ind_party);
						ps.setString(2,position);
						ps.setString(3,lang);
						ps.setString(4,post);
						ps.addBatch();
						
					}
					page++;
				}
				
				//fr.flush();
				//fr.close();
				//System.out.println("number of tweets for "+filename+" are "+tweets.size());
				usercount++;
				ps.executeBatch();
				ps.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Failed to get timeline: " + e.getMessage());
		}
	}
}
