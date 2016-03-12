package Latvia_Thesis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import com.cybozu.labs.langdetect.Language;

public class Corpus_Refine {
	public static Set<String> stopwords = new HashSet<String>();

	public static void set_StopWords() throws IOException {
		File f1 = new File(
				"E:\\NihaThesis\\Latvia\\Tests\\Test1_Latvia\\Stopwords.txt");
		BufferedReader br = new BufferedReader(new FileReader(f1));
		String line;

		while ((line = br.readLine()) != null) {
			String word = line.trim();
			if (!stopwords.contains(word))
				stopwords.add(word);
		}
	}

	private static String profileDirectory = "E:\\jars\\langdetect\\profiles";

	public static String detect(String text) {
		Detector detector = null;
		try {
			detector = DetectorFactory.create();
		} catch (LangDetectException e) {
			e.printStackTrace();
		}
		detector.append(text);
		try {
			return detector.detect();
		} catch (LangDetectException e) {
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
		PrintStream err_out = new PrintStream(new FileOutputStream(
				"E:\\NihaThesis\\Latvia\\Tests\\Test1_Latvia\\Error_log.txt"));
		Set<String> refined_corpus = new HashSet<String>();
		set_StopWords();
		try {

			DetectorFactory.loadProfile(profileDirectory);
		} catch (LangDetectException e) {
			e.printStackTrace();
		}
		File f_out = new File(
				"E:\\NihaThesis\\Latvia\\Tests\\Test1_Latvia\\RefinedCorpus.txt");
		FileWriter fw = new FileWriter(f_out);
		int count = 0;
		File f = new File(
				"E:\\NihaThesis\\Latvia\\Tests\\Test1_Latvia\\Reduced_Corpus.txt");
		BufferedReader br = new BufferedReader(new FileReader(f));
		String line;
		while ((line = br.readLine()) != null) {
			line = line.trim();
			line = line.replaceAll("^\\s+", "");
			count++;
			System.out.println(count+"----->"+line);	
				
			if ( line.length() >= 2 && !stopwords.contains(line)) {
				char[] c = line.toCharArray();
				if(line.length() == 2 && Character.isAlphabetic(c[0]) && Character.isAlphabetic(c[1])){refined_corpus.add(line);}
				else if (line.length() > 4) {
					Pattern pattern = Pattern.compile("[a-zA-Z]");
					Matcher m = pattern.matcher(line);
					String lang = detect(line);
					if (lang.equals("lv")) {
						
						refined_corpus.add(line);
					}
				} else {
					refined_corpus.add(line);
				}

			}
		}
	

		for (String word : refined_corpus) {
			fw.append(word + "\n");
		}

		fw.flush();
		fw.close();
		System.setErr(err_out);
		err_out.close();
	}
}
