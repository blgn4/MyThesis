package Latvia_Thesis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;

/*
 * Author: Niharika
 * Removes documents with less than 3 words
 * Stemming and stop-word removal from files
 */
public class TwitterProcess {
	private static String profileDirectory = "E:\\jars\\langdetect\\profiles";
	private static Set<String> filst = new HashSet<String>();

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

	public static void load_files() {
		filst.add("CR_edgarsrinkevics.txt");
		filst.add("CR_Kalniete.txt");
		filst.add("CR_pabriks.txt");
		filst.add("CR_vdombrovskis.txt");
		filst.add("CR_VeikoSpolitis.txt");
		filst.add("LW_PCTVL.txt");
		filst.add("RW_NA.txt");
	}

	public static void main(String[] arg) throws IOException {

		try {
			DetectorFactory.loadProfile(profileDirectory);
		} catch (LangDetectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Map<String, HashMap<String, Integer>> corpus = new TreeMap<String, HashMap<String, Integer>>();

		// String out_path = "E:\\NihaThesis\\Latvia\\Corpus.txt";
		String op1 = "E:\\NihaThesis\\Latvia\\Tweet_Dif_Corpus.txt";
		int left_count = 0;
		int right_count = 0;
		load_files();
		for (String fold_names : filst) {
			String p = "E:\\NihaThesis\\Latvia\\Twitter\\" + fold_names;
			File f = new File(p);
			System.out.println(f);

			BufferedReader br = new BufferedReader(new FileReader(f));
			String line;
			while ((line = br.readLine()) != null) {

				Pattern pattern = Pattern.compile("[a-zA-Z]");
				String lang = null;
				if (line.trim() != null && line.split(" ").length > 2) {
					Matcher m = pattern.matcher(line);
					if (m.find()) {
						//System.out.println(line);
						lang = detect(line);
						System.out.println(f.getName()+"------>"+f.getName().substring(0,2).contains("LW"));
					}
				}
				if (lang == "lv") {
					String[] tokenizedTerms = line.replaceAll("^[,\\s]+", "")
							.split("([,\\s])+|(\\s)");
					for (String t : tokenizedTerms) {

						if (corpus.containsKey(t)) {
							HashMap<String, Integer> doc_term = corpus.get(t);
							String key_val;
							if (f.getName().substring(0,2).contains("LW"))
								key_val = "Left";
							else
								key_val = "Right";
							if (doc_term.containsKey(key_val)) {
								int val = doc_term.get(key_val);
								doc_term.put(key_val, val + 1);
							} else {
								doc_term.put(key_val, 1);
							}
							corpus.put(t, doc_term);

						} else {
							String key_val;
							if (f.getName().substring(0,2).contains("LW"))
								key_val = "Left";
							else
								key_val = "Right";
							HashMap<String, Integer> doc_term = new HashMap<String, Integer>();
							doc_term.put(key_val, 1);
							corpus.put(t, doc_term);
						}
					}
				}
			}

		}
		System.out.println("Writing into the files");
		FileWriter fw1 = new FileWriter(new File(op1));
		for (String word : corpus.keySet()) {
			HashMap<String, Integer> doc_term = corpus.get(word);
			int tot = 0;
			int diff = 0;
			if (doc_term.containsKey("Left")) {
				tot += doc_term.get("Left");
				diff = doc_term.get("Left");
			}
			if (doc_term.containsKey("Right")) {
				tot += doc_term.get("Right");
				diff = diff - doc_term.get("Right");
			}
			double avg = (double) diff / (double) tot;
			fw1.append(diff + " , " + avg + " \n");
		}
		fw1.flush();
		fw1.close();

		/*
		 * FileWriter fw1 = new FileWriter(new File(op1)); FileWriter fw2 = new
		 * FileWriter(new File(op2)); int count =1; for(String
		 * word:corpus.keySet()) { HashMap<String,Integer> doc_term =
		 * corpus.get(word); int tot=0; if(doc_term.containsKey("Left")) tot +=
		 * doc_term.get("Left"); if(doc_term.containsKey("Right")) tot
		 * +=doc_term.get("Right");
		 * 
		 * if(doc_term.containsKey("Left"))
		 * fw1.append(count+" , "+word+" , "+(double
		 * )doc_term.get("Left")*10000/(left_count*tot)+"\n"); else
		 * fw1.append(count+" , "+word+" , 0\n");
		 * if(doc_term.containsKey("Right"))
		 * fw2.append(count+" , "+word+" , "+(double
		 * )doc_term.get("Right")*10000/(right_count*tot)+"\n"); else
		 * fw2.append(count+" , "+word+" , 0\n"); count++; } fw1.flush();
		 * fw1.close(); fw2.flush(); fw2.close();
		 */
		// System.out.println("Total left wing docs: "+left_count);
		// System.out.println("Total right wing docs: "+right_count);
	}

}
