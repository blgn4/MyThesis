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

/*
 * Author: Niharika
 * Removes documents with less than 3 words
 * Stemming and stop-word removal from files
 */
public class Generate_Corpus {
	
	
	public static void main(String[] arg) throws IOException {
		Map<String,HashMap<String,Integer>> corpus = new TreeMap<String,HashMap<String,Integer>>();
		
		String out_path = "E:\\NihaThesis\\Latvia\\Tests\\Test1_Latvia\\Corpus.txt";
		String op1 = "E:\\NihaThesis\\Latvia\\Tests\\Test1_Latvia\\Dif_Corpus_newdiff.txt";
		//String op2 = "E:\\NihaThesis\\Latvia\\Dif_Lat_Right.txt";
		String[] fold_names = { "Leftwing","CenterRight", "RightWing" };
		int left_count = 0;
		int right_count = 0;
		for (int i = 0; i < fold_names.length; i++) {
			String p = "E:\\NihaThesis\\Latvia\\Raw_Data\\Latvian\\"
					+ fold_names[i];
			File path = new File(p);
			System.out.println(path);
			File[] listfiles = path.listFiles();
			
			for (File f : listfiles) {
				if(i==0)
				{
					left_count++;
				}
				else
				{
					right_count++;
				}
				BufferedReader br = new BufferedReader(new FileReader(f));
				String line;
				while ((line = br.readLine()) != null) {
					String[] tokenizedTerms = line.replaceAll("^[,\\s]+", "")
							.split("([,\\s\\.])+|(\\s)");
					for (String t_sub : tokenizedTerms) {
						
						String t = t_sub.replaceAll("!", "");
						if(t.contains("http")) continue;
						t=t.replaceAll("<(\"[^\"]*\"|'[^']*'|[^'\">])*>","");
						t=t.replaceAll("[0-9]", "");
						t=t.replaceAll("\\(", "");
						t=t.replaceAll("\\)", "");
						t=t.replace("\"", "");
						t=t.replaceAll("#","");
						t=t.replaceAll("[@$%'*&+-?/<>„”“]","");
						t=t.replace("\\","");
						t=t.replaceAll(";","");
						t=t.replaceAll("\\s+","");
						t=t.trim();
						if(t_sub.contains("”")) System.out.println(t_sub +" ---> "+t);
						if(t!=""){
						if(corpus.containsKey(t))
						{
							HashMap<String,Integer> doc_term = corpus.get(t);
							String key_val;
							if(i==0)
								key_val= "Left";
							else 
								key_val = "Right";
							if(doc_term.containsKey(key_val))
							{
								int val = doc_term.get(key_val);
								doc_term.put(key_val, val+1);
							}
							else
							{
								doc_term.put(key_val, 1);
							}
							corpus.put(t, doc_term);	
								
						}
						else
						{
							String key_val;
							if(i==0)
								key_val= "Left";
							else 
								key_val = "Right";
							HashMap<String,Integer> doc_term = new HashMap<String, Integer>();
							doc_term.put(key_val,1);
							corpus.put(t, doc_term);
						}
					}
					}
				}
			}
		}
		System.out.println("Writing into the files");
		FileWriter fw1 = new FileWriter(new File(op1));
		FileWriter fw2 = new FileWriter(new File(out_path));
		for(String word:corpus.keySet())
		{			
			HashMap<String,Integer> doc_term = corpus.get(word);
			int tot=0;
			int diff=0;
			int l_count = 0;
			int r_count = 0;
			if(doc_term.containsKey("Left"))
			{
				tot += doc_term.get("Left");
				l_count = doc_term.get("Left");
				diff=doc_term.get("Left");
			}
			if(doc_term.containsKey("Right"))
			{
				tot +=doc_term.get("Right");
				r_count = doc_term.get("Right");
				diff = diff-doc_term.get("Right");
			}
			double avg = (double)diff/(double)tot;
			double left_avg=(double)l_count/(double)left_count;
			double right_avg = (double)r_count/(double)right_count;
			fw1.append(word+" , "+l_count+" , "+left_avg+" , "+r_count+" , "+right_avg+" , "+(l_count+r_count)+" , "+(left_avg-right_avg)+" , "+avg+" \n");
			fw2.append(word+" \n");
		}
		fw1.flush();
		fw1.close();
		fw2.flush();
		fw2.close();
		
		
		/*FileWriter fw1 = new FileWriter(new File(op1));
		FileWriter fw2 = new FileWriter(new File(op2));
		int count =1;
		for(String word:corpus.keySet())
		{
			HashMap<String,Integer> doc_term = corpus.get(word);
			int tot=0;
			if(doc_term.containsKey("Left"))
				tot += doc_term.get("Left");
			if(doc_term.containsKey("Right"))
				tot +=doc_term.get("Right");
			
			if(doc_term.containsKey("Left"))
				fw1.append(count+" , "+word+" , "+(double)doc_term.get("Left")*10000/(left_count*tot)+"\n");
			else
				fw1.append(count+" , "+word+" , 0\n");
			if(doc_term.containsKey("Right"))
				fw2.append(count+" , "+word+" , "+(double)doc_term.get("Right")*10000/(right_count*tot)+"\n");
			else
				fw2.append(count+" , "+word+" , 0\n");
			count++;
		}
		fw1.flush();
		fw1.close();
		fw2.flush();
		fw2.close();*/
		//System.out.println("Total left wing docs: "+left_count);
		//System.out.println("Total right wing docs: "+right_count);
	}

}
