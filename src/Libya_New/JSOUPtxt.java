package Libya_New;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.jsoup.HttpStatusException;
	import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

	public class JSOUPtxt {
		public static void main(String[] args) throws IOException
		{
			int count = 0;
			File f1 = new File("E:\\NihaThesis\\Libya\\Links\\PFG.txt");
			BufferedReader br = new BufferedReader(new FileReader(f1));
			String line;
			while((line=br.readLine())!=null)
			{
				System.out.println(line);
				line = line.trim();
				String url = line;//java.net.URLEncoder.encode(line, "UTF-8");
				String fname = "PFG";
				
				
				File fout1 = new File("E:\\NihaThesis\\Libya\\Coalition1\\PFG\\"+fname+count+".txt");
				if(fout1.exists())
				 {
					 fout1.delete();
				 }
				
				System.out.println(count);
				StringBuffer text1 = new StringBuffer();
				text1.append("");
				Document doc = null;
			    try {
			        doc = Jsoup.connect(url)
			               .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
			               .referrer("http://www.google.com")              
			               .get();
			        text1.append(doc.text());
			    } catch (NullPointerException e) {
			        // TODO Auto-generated catch block
			        e.printStackTrace();
			    } catch (HttpStatusException e) {
			        e.printStackTrace();
			    } catch (IOException e) {
			        // TODO Auto-generated catch block
			        e.printStackTrace();
			    }
			    FileWriter fw = new FileWriter(fout1);
				count++;
				fw.append(text1);
				fw.flush();
				fw.close();	
			}
		
		}
		
	}


