package Latvia_Thesis;

import java.io.IOException;
import java.sql.SQLException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Jsouptest {
	
	public Document getURLPage(String url){
		try {
			return Jsoup.connect(url).userAgent("Mozilla").timeout(999999)
					.get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	public static void main(String[] args) throws IOException, SQLException,
			ClassNotFoundException {

		String url = "https://t.co/LqeFNmy69x";

		

		Document doc1 = Jsoup.connect(url).userAgent("Mozilla").timeout(999999)
				.get();

		System.out.println(doc1.text());

	}
}
