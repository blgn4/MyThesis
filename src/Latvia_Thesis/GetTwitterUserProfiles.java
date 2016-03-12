package Latvia_Thesis;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet; 
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.GeoLocation;
import twitter4j.HashtagEntity;
import twitter4j.Paging;
import twitter4j.Place;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.URLEntity;
import twitter4j.User;
import twitter4j.UserMentionEntity;
import twitter4j.auth.AccessToken;

public class GetTwitterUserProfiles {
	
	static final Pattern PAT_CHARSET = Pattern.compile("charset=([^; ]+)$");
	private final static String CONSUMER_KEY = "CONSUMER_KEY";
	private final static String CONSUMER_KEY_SECRET = "CONSUMER_KEY_SECRET";
	private final static String ACCESS_TOKEN = "ACCESS_TOKEN";
	private final static String ACCESS_TOKEN_SECRET = "ACCESS_TOKEN_SECRET";
	private final static String url = "jdbc:postgresql://url:5432/latvia";
	private final static String user = "postgres";
	private final static String password = "password";
	private static Connection con = null;
	private static Twitter twitter = null;
	private  static Set<String> userlist = new HashSet<String>();
	//private final static String query = "SELECT screen_name_follower from follower where crawled is null and screen_name_user in ('illarion_girs', 'EinarsGraudins')";
	private final static String query = "SELECT distinct(screen_name_user) from follower ";
	
	private final static String q1 = "INSERT INTO tweet_target(tid, tweet, geo, place, uid, \"timestamp\",lang) VALUES (?, ?, ?, ?, ?, ?, ?);";
	private final static String q2 = "INSERT INTO hashtag_target(tid, hashtag) VALUES (?, ?);";
	private final static String q3 = "INSERT INTO url_target(tid, url, expanded_url)VALUES (?, ?, ?);";
	private final static String q4 = "INSERT INTO user_mention_target(tid, uid, screen_name, name, tweet_uid)VALUES (?, ?, ?, ?, ?);";
	private final static String q5 = "INSERT INTO retweet_target(tid, screen_name_from, screen_name_to)VALUES (?, ?, ?);";
	private final static String q6 = "INSERT INTO userinfo_target(uid, name, screen_name, user_url, location, lang, favourites_count, followers_count, friends_count, statuses_count, listed_count, created_at, time_zone, description)VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	//private final static String q7 = "UPDATE follower set crawled =1 where screen_name_follower= ?";
	
	private  static PreparedStatement ps1 = null;
	private  static PreparedStatement ps2 = null;
	private  static PreparedStatement ps3 = null;
	private  static PreparedStatement ps4 = null;
	private  static PreparedStatement ps5 = null;
	private  static PreparedStatement ps6 = null;
	
	private  static User tweet_user = null;
	private  static String user_location = null;
	private  static String user_place = null;
	private  static long user_id = 0;
	private  static String user_name = null;
	private  static long tweet_id =0;
	private  static String tweet =null;
	
	private static int twitterCallsCounter = 0;
	
	private static long startTime =0;
	private static long endTime = 0;
	
	public static void getTwitterCredentials()
	{
		twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_KEY_SECRET);
		AccessToken oathAccessToken = new AccessToken(ACCESS_TOKEN,
				ACCESS_TOKEN_SECRET);
		twitter.setOAuthAccessToken(oathAccessToken);
	}
	
	public static Connection getPostgresConnection()
	{
		
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		
		try {
			con = DriverManager.getConnection(url, user, password);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return con;
	}
	
	public static void setUserlist()
	{
		Statement st = null;
		try {
			st = con.createStatement();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		ResultSet rs = null;
		try {
			rs = st.executeQuery(query);

			while (rs.next()) {
				userlist.add(rs.getString(1));
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
	public static void createPreparedStatements()
	{
		try {
			 ps1 = con.prepareStatement(q1);
			 ps2 = con.prepareStatement(q2);
			 ps3 = con.prepareStatement(q3);
			 ps4 = con.prepareStatement(q4);
			 ps5 = con.prepareStatement(q5);
			 ps6 = con.prepareStatement(q6);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void generateUser_infoQuery()
	{
		user_location = tweet_user.getLocation();
		user_place = user_location.split(",|-|\t ")[0];
		user_id = tweet_user.getId();
		user_name = tweet_user.getScreenName();
		String uname = tweet_user.getName();
		String user_url = tweet_user.getURL();
		String user_lang = tweet_user.getLang();
		int favourites_count = tweet_user.getFavouritesCount();
		int followers_count = tweet_user.getFollowersCount();
		int friends_count = tweet_user.getFriendsCount();
		int status_count = tweet_user.getStatusesCount();
		int listed_count = tweet_user.getListedCount();
		Date user_created_at = tweet_user.getCreatedAt(); 
		String time_zone = tweet_user.getTimeZone();
		String description = tweet_user.getDescription();
		
		try {
			ps6.setLong(1, user_id);
			ps6.setString(2,uname );
			ps6.setString(3, user_name);
			ps6.setString(4, user_url);
			ps6.setString(5,user_place);
			ps6.setString(6,user_lang);
			ps6.setInt(7, favourites_count);
			ps6.setInt(8, followers_count);
			ps6.setInt(9, friends_count);
			ps6.setInt(10, status_count);
			ps6.setInt(11, listed_count);
			DateFormat df1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss z YYYY");
			String usrcreated_date = df1.format(user_created_at);
			ps6.setString(12, usrcreated_date);
			ps6.setString(13, time_zone);
			ps6.setString(14, description);
			ps6.addBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}	
	public static void checkAPILimits()
	{
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
	}

	
	public static void getTweet_targetQueries(Status status)
	{
		tweet_id = status.getId();
		tweet = status.getText();
		String language = status.getLang();// Note to be filled in lang column of the database
		GeoLocation location = status.getGeoLocation();
		Place place = status.getPlace();
		Date create_date = status.getCreatedAt();// Mon Jan 07 09:05:17 MST 2013
		DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss z YYYY");
		String created_date = df.format(create_date);
		String geo = null;
		String plc = null;
		if (place != null && location != null) {
			geo = location.getLatitude() + ","
					+ location.getLongitude();
			plc = place.getName();
		} else if (place != null && location == null) {
			plc = place.getName();
		} else if (place == null && location != null) {
			geo = location.getLatitude() + ","
					+ location.getLongitude();
		} else if (user_place != null) {
			plc = user_place;
		}
		SimpleDateFormat timeStampFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z YYYY");
		Date dateParse = null;
		try {
			dateParse = timeStampFormat.parse(created_date);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		Timestamp dateTimeValue = new Timestamp(
				dateParse.getTime());
		// End of setting up data
		try {
			ps1.setLong(1, tweet_id);
			ps1.setString(2, tweet);
			ps1.setString(3, geo);
			ps1.setString(4, plc);
			ps1.setLong(5, user_id);
			ps1.setTimestamp(6, dateTimeValue);
			ps1.setString(7, language);
			// System.out.println(ps1);
			ps1.addBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void getHashtag_targetQueries(Status status)
	{
		HashtagEntity[] hashtags = status.getHashtagEntities();
		if (hashtags.length != 0) {
			for (HashtagEntity hashtag : hashtags) {

				try {
					ps2.setLong(1, tweet_id);
					ps2.setString(2, hashtag.getText());
					// System.out.println(ps2);
					ps2.addBatch();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void getURL_targetQueries(Status status)
	{
		URLEntity[] urls = status.getURLEntities();
		if (urls.length != 0) {
		for (URLEntity each_url : urls) {
			// String url_ = each_url.getURL();
			try {
				ps3.setLong(1, tweet_id);
				ps3.setString(2, each_url.getURL());
				ps3.setString(3, each_url.getExpandedURL());
				ps3.addBatch();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		}
	}
	
	public static void getUsermention_targetQueries(Status status){
		UserMentionEntity[] usermentions = status.getUserMentionEntities();
		if (usermentions.length != 0) {
			for (UserMentionEntity user_mention : usermentions) {
				try{
				ps4.setLong(1, tweet_id);
				ps4.setLong(2, user_mention.getId());
				ps4.setString(3, user_mention.getScreenName());
				ps4.setString(4, user_mention.getName());
				ps4.setLong(5, user_id);
				ps4.addBatch();
				} catch(SQLException e){
					e.printStackTrace();
				}
			}
		}
	}
	
	
	public static void getretweet_targetQueries(Status status){
		String[] terms = tweet.split(" ");
		int i = 0;
		if (i < (terms.length - 1)) {
			String regex_ettag = "[@@]+([A-Za-z0-9-_]+)";
			Pattern p_ettag = Pattern.compile(regex_ettag);
			Matcher m_ettag = p_ettag.matcher(terms[i + 1]);
			if (terms[i].equalsIgnoreCase("rt")
					&& m_ettag.find()) {
				String retweetId = terms[i + 1].replaceAll(
						"[^A-Za-z0-9-_]", "");
				if (retweetId != user_name) {
					try {
					ps5.setLong(1, tweet_id);
					ps5.setString(2, retweetId);
					ps5.setString(3, user_name);
					ps5.addBatch();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					
				}
			}
		}
	}
	
	public static void psBatchExecute()
	{
		try {
			ps1.executeBatch();		
			ps2.executeBatch();
			ps3.executeBatch();
			ps4.executeBatch();
			ps5.executeBatch();
			ps6.executeBatch();
			ps1.close();
			ps2.close();
			ps3.close();
			ps4.close();
			ps5.close();
			ps6.close();
			/*PreparedStatement ps7 = con.prepareStatement(q7);
			ps7.setString(1, user_name);
			ps7.executeUpdate();*/
		} 
		catch (BatchUpdateException  e) {
			e.printStackTrace();
		}catch (SQLException  e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) 
	{
		getTwitterCredentials();
		List<Status> statuses = null;
		getPostgresConnection();
		setUserlist();
		startTime = System.currentTimeMillis();
		createPreparedStatements();
		for (String each_user : userlist) {
			System.out.println(each_user);
			try{
			tweet_user = twitter.showUser(each_user);
			if(!tweet_user.isProtected()){
				generateUser_infoQuery();
				int previousCount = Integer.MAX_VALUE;
				Paging paging = null;
				int page = 1;
				while (previousCount > 0) {
					paging = new Paging(page, 200);
					statuses = twitter.getUserTimeline(each_user, paging);
					checkAPILimits();
					previousCount = statuses.size();
					System.out.println("Page Number: " + page + " of @"
							+ each_user + "'s user timeline with "
							+ statuses.size() + " and call number is "
							+ twitterCallsCounter);
					for (Status status : statuses) {
						getTweet_targetQueries(status);
						getHashtag_targetQueries(status);
						getURL_targetQueries(status);
						getUsermention_targetQueries(status);
						getretweet_targetQueries(status);
					}
					page++;
				}
				psBatchExecute();
				createPreparedStatements();
			}
			}
			catch(TwitterException e)
			{
				e.printStackTrace();
				System.out.println("Failed to get timeline: " + e.getMessage());
			}
		}
	}
}
