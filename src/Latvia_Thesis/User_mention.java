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
import java.util.ArrayList;
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
import twitter4j.api.TweetsResources;
import twitter4j.auth.AccessToken;

public class User_mention {

	static final Pattern PAT_CHARSET = Pattern.compile("charset=([^; ]+)$");
	private final static String CONSUMER_KEY = "CONSUMER_KEY";
	private final static String CONSUMER_KEY_SECRET = "CONSUMER_KEY_SECRET";
	private final static String ACCESS_TOKEN = "ACCESS_TOKEN";
	private final static String ACCESS_TOKEN_SECRET = "ACCESS_TOKEN_SECRET";
	private final static String url = "jdbc:postgresql://localhost:5432/latvia";
	private final static String user = "postgres";
	private final static String password = "root";
	private static Connection con = null;
	
	
	private final static String tweetsQuery = "select tid , tweet  from tweet where tid in(select tid from result) and tid not in (select tid from missing_user_mention)";
	private final static String q4 = "INSERT INTO missing_user_mention(tid, screen_name)VALUES (?, ? );";

	/*
	 * private static PreparedStatement ps1 = null; private static
	 * PreparedStatement ps2 = null; private static PreparedStatement ps3 =
	 * null;
	 */
	private static PreparedStatement ps4 = null;
	/*
	 * private static PreparedStatement ps5 = null; private static
	 * PreparedStatement ps6 = null;
	 */

	

	public static Connection getPostgresConnection() {

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



	public static void setTweetidlist() {
		Statement st = null;
		try {
			st = con.createStatement();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		ResultSet rs = null;
		try {
			rs = st.executeQuery(tweetsQuery);

			while (rs.next()) {
				Long tweet_id =  rs.getLong(1);
				String tweet = rs.getString(2);
				String[] terms = tweet.split(" ");
				int i=0;
				if (i < (terms.length - 1)) {
					String regex_ettag = "[@@]+([A-Za-z0-9-_]+)";
					Pattern p_ettag = Pattern.compile(regex_ettag);
					Matcher m_ettag = p_ettag.matcher(terms[i + 1]);
					
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	public static void createPreparedStatements() {
		try {

			ps4 = con.prepareStatement(q4);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void checkAPILimits() {
		if (++twitterCallsCounter > 170) {

			try {
				final long endTime = System.currentTimeMillis();
				if (endTime - startTime <= 900000) {
					Thread.sleep(960000 - (endTime - startTime));
				}
				twitterCallsCounter = 0;
				startTime = System.currentTimeMillis();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void getUsermention_targetQueries(Status status) {
		UserMentionEntity[] usermentions = status.getUserMentionEntities();
		if (usermentions.length != 0) {
			for (UserMentionEntity user_mention : usermentions) {
				try {
					ps4.setLong(1, tweet_id);
					ps4.setLong(2, user_mention.getId());
					ps4.setString(3, user_mention.getScreenName());
					ps4.setString(4, user_mention.getName());
					ps4.setLong(5, user_id);
					ps4.addBatch();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void psBatchExecute() {
		try {
			ps4.executeBatch();
			ps4.close();
			/*
			 * PreparedStatement ps7 = con.prepareStatement(q7);
			 * ps7.setString(1, user_name); ps7.executeUpdate();
			 */
		} catch (BatchUpdateException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		getTwitterCredentials();
		List<Status> statuses = null;
		getPostgresConnection();
		setTweetidlist();
		startTime = System.currentTimeMillis();
		createPreparedStatements();
		int counter = 1;
		List<Long> tweets100 = new ArrayList<Long>();
		for (Long tweet_id : tweetlist) {

			tweets100.add(tweet_id);
			counter++;
			if (counter > 100) {
				counter = 1;
				getStatuses(tweets100);
				tweets100.clear();
			}
		}

	}

	public static void getStatuses(List<Long> tweetlist) {
		for (Long tweet_id : tweetlist) {

			System.out.println(tweet_id);
			try {
				Status status = twitter.;
				checkAPILimits();

				getUsermention_targetQueries(status);

				psBatchExecute();
				createPreparedStatements();

			} catch (TwitterException e) {
				e.printStackTrace();
				System.out.println("Failed to get timeline: " + e.getMessage());
			}
		}
	}
}
