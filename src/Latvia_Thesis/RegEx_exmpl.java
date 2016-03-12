package Latvia_Thesis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import com.cybozu.labs.langdetect.Language;



public class RegEx_exmpl {
	private static String profileDirectory = "E:\\jars\\langdetect\\profiles";
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
	public static void main(String[] args) throws IOException {
		
		try {
			
			DetectorFactory.loadProfile(profileDirectory);
			} catch (LangDetectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}

		//String s="Saskaņā<h1> ar grozījumiem,<p> turpmāk piketos būs aizliegts ne tikai teikt runas,<href> bet arī \"mutvārdos izteikt atsevišķus saukļus, lozungus vai uzrunas\". Pret šo grozījumu iebilda Valsts cilvēktiesību birojs . Arī Ārlietu ministrija aicināja precizēt, ka saukļi nav uzskatāmi par runām. Saeima atbalstīja deputāta Mihaila Pietkeviča (Tautas partija) priekšlikumu izslēgt no likuma normu par to, ka sapulču, gājienu un piketu sarīkošanai, ja tie atbilst likuma prasībām, nav nepieciešama valsts vai pašvaldību iestāžu atļauja.   Valsts cilvēktiesību birojs, pētījuma “Demokrātijas audits” autori, kā arī daudzi citi juristi vairākkārt norādīja, ka ir nepieciešams novērst likuma pretrunu ar Latvijas Republikas Satversmi un noteikt, ka par pulcēšanos ir tikai jāinformē pašvaldība, nevis jāsaņem pašvaldības izziņa.";
		String s = "–";
		
		Pattern pattern = Pattern.compile("[a-zA-Z]");
		Matcher m = pattern.matcher(s);
		String lang = detect(s);
		  /*String regex="<(\"[^\"]*\"|'[^']*'|[^'\">])*>";
		  String[] tokens = s.replaceAll(regex,"").split(" "); 
		  StringBuilder sb = new StringBuilder(); 
		  for(int i=0;i<tokens.length;i++) {
			  sb.append(tokens[i]+" "); }*/
		 
		  System.out.println(lang);
		  
		  Sparse_generation sg = new Sparse_generation();
		  sg.generate_Corpus();
	}
}
