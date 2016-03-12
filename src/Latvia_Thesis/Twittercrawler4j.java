package Latvia_Thesis;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeTraversor;
import org.xml.sax.InputSource;

import twitter4j.GeoLocation;
import twitter4j.HashtagEntity;
import twitter4j.Paging;
import twitter4j.Place;
import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.URLEntity;
import twitter4j.User;
import twitter4j.UserMentionEntity;
import twitter4j.auth.AccessToken;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;

import de.l3s.boilerpipe.BoilerpipeExtractor;
import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.extractors.CommonExtractors;
import de.l3s.boilerpipe.sax.BoilerpipeSAXInput;
import de.l3s.boilerpipe.sax.HTMLDocument;
import de.l3s.boilerpipe.sax.HTMLHighlighter;

public class Twittercrawler4j {

	static final Pattern PAT_CHARSET = Pattern.compile("charset=([^; ]+)$");
	private final static String CONSUMER_KEY = "CONSUMER_KEY";
	private final static String CONSUMER_KEY_SECRET = "CONSUMER_KEY_SECRET";
	private final static String ACCESS_TOKEN = "ACCESS_TOKEN";
	private final static String ACCESS_TOKEN_SECRET = "ACCESS_TOKEN_SECRET";
	private static String profileDirectory = "E:\\jars\\langdetect\\profiles";
	private static Detector detector = null;
	private static Jsouptest jsoupInstance = null;

	public static void loadConfiguraion() {
		jsoupInstance = new Jsouptest();
		try {
			DetectorFactory.loadProfile(new File(profileDirectory));
			detector = DetectorFactory.create();
		} catch (LangDetectException e) {
			e.printStackTrace();
		}

	}

	private static String detect(String text) {
		detector.append(text);
		String language = "";
		try {
			language = detector.detect();
		} catch (LangDetectException | NullPointerException e) {
			e.printStackTrace();
		}
		return language;
	}

	public static void main(String[] args)  {
		
		loadConfiguraion();
		// Set twitter OAUTH params
		Twitter twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_KEY_SECRET);
		AccessToken oathAccessToken = new AccessToken(ACCESS_TOKEN,
				ACCESS_TOKEN_SECRET);
		twitter.setOAuthAccessToken(oathAccessToken);
		RateLimitStatus rlstatus = null;
		try {
			rlstatus = twitter.getRateLimitStatus("users").get("/users/search");
		} catch (TwitterException e2) {
			e2.printStackTrace();
		}
		int limit = rlstatus.getLimit();
		System.out.println("Limit: "+ rlstatus.getLimit());
		List<Status> statuses = null;

		// Postgres connection
		
		 /* String url = "jdbc:postgresql://129.219.60.22:5432/latvia"; 
		  String user = "postgres"; 
		  String password = "password";
		  try {
				Class.forName("org.postgresql.Driver");
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			}
		  Connection con1 = null;
		  try {
				con1 = DriverManager.getConnection(url, user, password);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}*/
		 

		// Local postgres connection
		String url1 = "jdbc:postgresql://localhost:5432/latvia";
		String user1 = "postgres";
		String password1 = "root";
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		Connection con1 = null;
		try {
			con1 = DriverManager.getConnection(url1, user1, password1);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		Set<String> userlist = new HashSet<String>();
		String query = "SELECT screen_name_user , screen_name_follower from follower where screen_name_user in ('illarion_girs', 'EinarsGraudins')";  //where screen_name_user = 'illarion_girs' OR screen_name_user = 'EinarsGraudins'
		Statement st = null;
		try {
			st = con1.createStatement();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		ResultSet rs = null;
		try {
			rs = st.executeQuery(query);
		
			while (rs.next()) {
				userlist.add(rs.getString(1));
				userlist.add(rs.getString(2));
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		int twitterCallsCounter = 0;
		PreparedStatement ps1 = null;
		PreparedStatement ps2 = null;
		PreparedStatement ps3 = null;
		PreparedStatement ps4 = null;
		PreparedStatement ps5 = null;
		try{
		String q1 = "INSERT INTO tweet_target(tid, tweet, geo, place, uid, \"timestamp\",lang) VALUES (?, ?, ?, ?, ?, ?, ?);";
		ps1 = con1.prepareStatement(q1);

		String q2 = "INSERT INTO hashtag_target(tid, hashtag) VALUES (?, ?);";
		ps2 = con1.prepareStatement(q2);

		String q3 = "INSERT INTO url_target(tid, url, expanded_url)VALUES (?, ?, ?);";
		ps3 = con1.prepareStatement(q3);

		String q4 = "INSERT INTO user_mention_target(tid, uid, screen_name, name, tweet_uid)VALUES (?, ?, ?, ?, ?);";
		ps4 = con1.prepareStatement(q4);

		String q5 = "INSERT INTO retweet_target(tid, screen_name_from, screen_name_to)VALUES (?, ?, ?);";
		ps5 = con1.prepareStatement(q5);
		
			for (String each_user : userlist) {
				
				// User Details
				User tweet_user = twitter.showUser(each_user);
				String user_location = tweet_user.getLocation();
				String user_place = user_location.split(",|-|\t ")[0];
				long user_id = tweet_user.getId();
				String user_name = tweet_user.getScreenName();
				// End of user details
				if(!tweet_user.isProtected()){
				int previousCount = Integer.MAX_VALUE;
				Paging paging = null;
				int page = 1;
				long startTime = System.currentTimeMillis();
				long endTime = 0;
				while (previousCount > 0) {
					//System.out.println(rlstatus.getRemaining());
					// Queries
					paging = new Paging(page, 200);
					statuses = twitter.getUserTimeline(each_user, paging);
					
					if (++twitterCallsCounter == limit -2)
						try {
							ps1.executeBatch();
							ps2.executeBatch();
							ps3.executeBatch();
							ps4.executeBatch();
							ps5.executeBatch();
							ps1.close();
							ps2.close();
							ps3.close();
							ps4.close();
							ps5.close();
							ps1 = con1.prepareStatement(q1);
							ps2 = con1.prepareStatement(q2);
							ps3 = con1.prepareStatement(q3);
							ps4 = con1.prepareStatement(q4);
							ps5 = con1.prepareStatement(q5);
							endTime = System.currentTimeMillis();
							if (endTime - startTime <= 900000) {
								Thread.sleep(960000 - (endTime - startTime));
							}
							twitterCallsCounter = 0;
							startTime = System.currentTimeMillis();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					previousCount = statuses.size();
					//System.out.println("Previouscount "+ previousCount);
					//Calendar cal = Calendar.getInstance();
					System.out.println("Page Number: " + page + " of @"
							+ each_user + "'s user timeline with "
							+ statuses.size() + " and call number is "
							+ twitterCallsCounter );
					/*cal.setTimeInMillis(System.currentTimeMillis());
					System.out.println(cal.get(Calendar.SECOND));*/
					for (Status status : statuses) {
						
						
						
						long tweet_id = status.getId();
						String tweet = status.getText();
						String language = status.getLang();
						GeoLocation location = status.getGeoLocation();
						Place place = status.getPlace();
						Date create_date = status.getCreatedAt();																	
						DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss z YYYY");
						String created_date = df.format(create_date);
						HashtagEntity[] hashtags = status.getHashtagEntities();
						URLEntity[] urls = status.getURLEntities();
						UserMentionEntity[] usermentions = status
								.getUserMentionEntities();

						// Setting up data for ps1
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
						SimpleDateFormat timeStampFormat = new SimpleDateFormat(
								"EEE MMM dd HH:mm:ss z YYYY");
						Date dateParse = timeStampFormat.parse(created_date);
						Timestamp dateTimeValue = new Timestamp(
								dateParse.getTime());
						// End of setting up data
						ps1.setLong(1, tweet_id);
						ps1.setString(2, tweet);
						ps1.setString(3, geo);
						ps1.setString(4, plc);
						ps1.setLong(5, user_id);
						ps1.setTimestamp(6, dateTimeValue);
						ps1.setString(7, language);
						// System.out.println(ps1);
						ps1.addBatch();
						// End of inserting into tweet table
						if (hashtags.length != 0) {
							for (HashtagEntity hashtag : hashtags) {

								ps2.setLong(1, tweet_id);
								ps2.setString(2, hashtag.getText());
								// System.out.println(ps2);
								ps2.addBatch();
							}
						}
						// System.out.print(" URL: ");
						for (URLEntity each_url : urls) {
							String url_ = each_url.getURL();
							/*//String long_url = GetLongURL(url_);
							//if (long_url == null)
								//continue;
							//String title = GetURLTitle(long_url);
							//String domain = getDomainName(long_url);
							//String content = GetURLContent(long_url);
							//String lang = detect(content);
							//System.out.println(content);*/
							ps3.setLong(1, tweet_id);
							ps3.setString(2, each_url.getURL());
							ps3.setString(3, each_url.getExpandedURL());
							/*//ps3.setString(4, long_url);
							//ps3.setString(5, title);
							//ps3.setString(6, domain);
							//ps3.setString(7, content);
							// ps3.setString(8,lang);
							// System.out.println(ps3);*/
							ps3.addBatch();
						}
						Set<String> mentioned_screen_names = new HashSet<String>();
						if (usermentions.length != 0) {
							for (UserMentionEntity user_mention : usermentions) {
								mentioned_screen_names.add(user_mention
										.getScreenName());

								ps4.setLong(1, tweet_id);
								ps4.setLong(2, user_mention.getId());
								ps4.setString(3, user_mention.getScreenName());
								ps4.setString(4, user_mention.getName());
								ps4.setLong(5, user_id);
								// System.out.println(ps4);
								ps4.addBatch();
							}
						}
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

									ps5.setLong(1, tweet_id);
									ps5.setString(2, retweetId);
									ps5.setString(3, user_name);
									// System.out.println(ps5);
									ps5.addBatch();
								}
							}
						}
					}
					page++;
				}
			}

			}
			// con.close();
			con1.close();

		} catch (TwitterException | SQLException | ParseException te) {
			te.printStackTrace();
			System.out.println("Failed to get timeline: " + te.getMessage());
			System.exit(-1);
		}

	}

	public static String GetLongURL(String URL)  {
		String long_url = null;
		try {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(URL);
			HttpContext context = new BasicHttpContext();
			HttpResponse response = httpclient.execute(httpget, context);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
				throw new IOException(response.getStatusLine().toString());
			HttpUriRequest currentReq = (HttpUriRequest) context
					.getAttribute(ExecutionContext.HTTP_REQUEST);
			HttpHost currentHost = (HttpHost) context
					.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
			long_url = (currentReq.getURI().isAbsolute()) ? currentReq.getURI()
					.toString() : (currentHost.toURI() + currentReq.getURI());
			String pattern = "https://www.google.com/url?rct=j&sa=t&url=";
			long_url = long_url.replace(pattern, "");
		} catch (Exception e) {

		}
		return long_url;
	}

	public static String GetURLTitle(String URL) {
		String title = null;
		try {
			Document doc = Jsoup
					.connect(URL)
					.userAgent(
							"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
					.timeout(50000).get();
			Elements metaOgTitle = doc.select("meta[property=og:title]");
			if (metaOgTitle != null) {
				title = metaOgTitle.attr("content");
			} else {
				title = doc.title();
			}
		} catch (Exception e) {
		}
		if (title == null) {
			try {
				HtmlCleaner cleaner = new HtmlCleaner();
				CleanerProperties props = cleaner.getProperties();
				props.setTranslateSpecialEntities(true);
				props.setTransResCharsToNCR(true);
				props.setOmitComments(true);
				TagNode node = cleaner.clean(new URL(URL));
				TagNode titleNode = node.findElementByName("title", true);
				title = titleNode.getText().toString();
				title = title.replace("\n", "");
			} catch (Exception e) {
			}
		}
		if (title == null || title == "" || title.isEmpty()) {
			title = "N/A";
		}
		return title;
	}

	public static String GetURLContent(String URL) {
		String content = null;
		try {
			URL link = new URL(URL);

			// URL link = new
			// URL("http://ria.ru/syria_mission/20151119/1324341559.html?utm_source=twitterfeed&utm_medium=twitter?utm_source=twitterfeed&utm_medium=twitter");
			BoilerpipeExtractor extractor = CommonExtractors.DEFAULT_EXTRACTOR;
			HTMLHighlighter hh = HTMLHighlighter.newExtractingInstance();
			HTMLDocument hDoc = fetch(link);
			TextDocument tdoc = new BoilerpipeSAXInput(hDoc.toInputSource())
					.getTextDocument();
			// System.out.println(tdoc.toString());
			extractor.process(tdoc);
			InputSource is = hDoc.toInputSource();
			String extractedHtml = hh.process(tdoc, is);
			Document doc = Jsoup.parse(extractedHtml);

			// Document doc = jsoupInstance.getURLPage(URL);
			doc.outputSettings().escapeMode(Entities.EscapeMode.xhtml);
			doc.select("*[style*=display:none]").remove();
			doc.select("a").unwrap();
			Element element = doc.body();
			HTMLTraversor formatter = new HTMLTraversor();
			NodeTraversor traversor = new NodeTraversor(formatter);
			traversor.traverse(element); // walk the DOM, and call .head() and
											// .tail() for each node
			content = formatter.toString().trim();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;
	}

	public static String getDomainName(String URL)  {
		URI uri = null;
		try {
			uri = new URI(URL);
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		String domain = null;
		try {
			String temp = uri.getHost();
			if (temp.startsWith("www.")) {
				domain = temp.substring(4);
			} else {
				domain = temp;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return domain;
	}

	public static HTMLDocument fetch(final URL url)  {
		System.out.println(url);
		HttpURLConnection httpcon;
		try {
			httpcon = (HttpURLConnection) url
					.openConnection();
		
		httpcon.setDoOutput(true);
		httpcon.setRequestProperty(
				"User-Agent",
				"Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.0.5) Gecko/2008120122 Firefox/3.0.5");
		httpcon.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		httpcon.setRequestProperty("Accept", "*/*");

		final String ct = httpcon.getContentType();

		Charset cs = Charset.forName("Cp1252");
		if (ct != null) {
			Matcher m = PAT_CHARSET.matcher(ct);
			if (m.find()) {
				final String charset = m.group(1);
				try {
					cs = Charset.forName(charset);
				} catch (UnsupportedCharsetException e) {
				}
			}
		}
		java.io.InputStream in = null;
		try{
		in = httpcon.getInputStream();
		}
		catch(ConnectException e){
		   e.printStackTrace();
		}
		final String encoding = httpcon.getContentEncoding();
		if (encoding != null) {
			if ("gzip".equalsIgnoreCase(encoding)) {
				in = new GZIPInputStream(in);
			} else {
				System.err.println("WARN: unsupported Content-Encoding: "
						+ encoding);
			}
		}

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buf = new byte[4096];
		int r;
		while ((r = in.read(buf)) != -1) {
			bos.write(buf, 0, r);
		}
		in.close();

		final byte[] data = bos.toByteArray();

		return new HTMLDocument(data, cs);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return null;
	}

}
