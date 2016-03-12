package Latvia_Thesis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Sparse_generation {
	public static Map<String,Integer> corpus = new TreeMap<String,Integer>();
	public static Map<Integer,String> corpus_line = new TreeMap<Integer,String>();
	public static Map<String,Integer> idf_vals = new HashMap<String,Integer>();
	public static int doc_count =0;
	public static void generate_Corpus() throws IOException
	{
		String corpus_file = "E:\\NihaThesis\\Latvia\\Results_observations\\Tests\\Test1_Latvia_tf_idfs\\Feature_selection\\RefinedCorpus.txt";
		FileWriter fw = new FileWriter("E:\\NihaThesis\\Latvia\\Results_observations\\Tests\\Test1_Latvia_tf_idfs\\Feature_selection\\Corpus_lines.txt");
		BufferedReader br  = new BufferedReader(new FileReader(corpus_file));
		String line;
		int count=1;
		while((line=br.readLine())!=null)
		{
			corpus.put(line,count);
			count++;
		}
		for(String term:corpus.keySet())
		{
			corpus_line.put(corpus.get(term), term);
		}
		for(int i=1;i<=corpus_line.size();i++)
		{
			fw.append(i+","+corpus_line.get(i)+"\n");
		}
		fw.flush();
		fw.close();
		System.out.println("Corpus is generated");
	}
	
	public static void idf_generate() throws IOException
	{
		generate_Corpus();
		List<HashSet<String>> doc_terms = new ArrayList<HashSet<String>>();
		String[] fold_names = { "Leftwing","CenterRight", "RightWing" };
		for (int i = 0; i < fold_names.length; i++) {
			String p = "E:\\NihaThesis\\Latvia\\Raw_Data\\Latvian\\"+ fold_names[i];			
			File path = new File(p);
			System.out.println(path);
			File[] listfiles = path.listFiles();
			
			for (File f : listfiles) {
				doc_count++;
				Set<String> dt = new HashSet<String>();
				BufferedReader br = new BufferedReader(new FileReader(f));
				String line;
				while ((line = br.readLine()) != null) {
					String[] tokenizedTerms = line.replaceAll("^[,\\s]+", "").split("([,\\s\\.])+|(\\s)");
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
							t=t.replaceAll("^\\s+", "");
					
							if(t!=""){
								if(corpus.containsKey(t))
								{
									dt.add(t);
								}
							}
					}
				}
				doc_terms.add((HashSet<String>) dt);
			}
			
		}
		
		for(HashSet<String> dt :doc_terms )
		{
			for(String s:dt)
			{
				if(idf_vals.containsKey(s)) 
				{
					int val = idf_vals.get(s); 
					idf_vals.put(s, val+1);
				}
				else idf_vals.put(s, 1);
			}
		}
	}
	 
	
	public static void main(String[] arg) throws IOException {		
		idf_generate();
		String[] fold_names = { "Leftwing","CenterRight", "RightWing" };
		FileWriter fw = new FileWriter(new File("E:\\NihaThesis\\Latvia\\Results_observations\\Tests\\Test1_Latvia_tf_idfs\\Unigrams_Termcount\\Sparse.txt"));
		FileWriter fw2 = new FileWriter(new File("E:\\NihaThesis\\Latvia\\Results_observations\\Tests\\Test1_Latvia_tf_idfs\\Unigrams_Termcount\\Class_vals.txt"));
		int count=0;
		for (int i = 0; i < fold_names.length; i++) {
			int class_val;

			if(i==0) class_val=1;
			else class_val=-1;
			String p = "E:\\NihaThesis\\Latvia\\Raw_Data\\Latvian\\"+ fold_names[i];			
			File path = new File(p);
			System.out.println(path);
			File[] listfiles = path.listFiles();			
			for (File f : listfiles) {
				
				    System.out.println(f.getName());
				    HashMap<String,Integer> doc_term = new HashMap<String,Integer>();					
					BufferedReader br = new BufferedReader(new FileReader(f));
					String line;
					while ((line = br.readLine()) != null) {
						String[] tokenizedTerms = line.replaceAll("^[,\\s]+", "").split("([,\\s\\.])+|(\\s)");
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
								t=t.replaceAll("^\\s+", "");
						
								if(t!=""){
									if(corpus.containsKey(t))
									{
										
										if(doc_term.containsKey(t))
										{
											int val = doc_term.get(t);
											doc_term.put(t, val+1);
										}
										else
										{
											doc_term.put(t, 1);
										}
																					
									}
						
								}
						}
					}
					if(doc_term.size()!=0)
					{

						count++;
					for(String term: doc_term.keySet())
					{
						double idf=(double)idf_vals.get(term)/(double)doc_count;
						fw.append(count+","+corpus.get(term)+","+(double)doc_term.get(term)*idf+"\n");
					}
					fw2.append(class_val+"\n");
					
					}
				}
			}
		fw.flush();
		fw.close();
		fw2.flush();
		fw2.close();
	    }
}
