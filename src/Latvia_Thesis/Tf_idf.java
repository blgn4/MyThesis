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



public class Tf_idf {
	
	private Map<String,Integer> corpus = new TreeMap<String,Integer>();
	private Map<Integer,String> corpus_line = new TreeMap<Integer,String>();
	private Map<String,Double> idf_vals = new HashMap<String,Double>();
	private int doc_count =0;
	private List<HashSet<String>> doc_terms = new ArrayList<HashSet<String>>();
	private Map<String,Double> tf_idf_tot = new HashMap<String,Double>();
	
	public void generate_Corpus() throws IOException
	{
		FileWriter fw = new FileWriter("E:\\NihaThesis\\Latvia\\Results_observations\\Tests\\Test1_Latvia_tf_idfs\\Feature_selection\\5Mutual_info\\features30k\\Corpus_lines.txt");
		String corpus_file = "E:\\NihaThesis\\Latvia\\Results_observations\\Tests\\Test1_Latvia_tf_idfs\\Feature_selection\\5Mutual_info\\features30k\\Corpus30k.txt";
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
			fw.append(corpus.get(term)+","+term+"\n");
		}
		fw.flush();
		fw.close();
		System.out.println("Corpus Lines are generated");
		System.out.println("Corpus is generated");
	}
	
	public void idf_generate() throws IOException
	{
		generate_Corpus();
		//File f1 = new File("E:\\NihaThesis\\Latvia\\Results_observations\\Tests\\Test1_Latvia_tf_idfs\\Feature_selection\\Basic_tf_idf\\idf_vals.txt");
		//FileWriter fw = new FileWriter(f1);
		
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
							clean_String(t);
					
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
					double val = idf_vals.get(s); 
					idf_vals.put(s, val+1);
				}
				else idf_vals.put(s, 1.0);
			}
		}
		
		for(String s: idf_vals.keySet())
		{
			double idf_val = Math.log((double)doc_count/(double)idf_vals.get(s));
			idf_vals.put(s, idf_val);
			//fw.append(s+" , "+idf_val+"\n");
		}
		//fw.flush();
		//fw.close();
		System.out.println("Generated IDF values");
	}
	
	public void gen_avg_tf_idf() throws IOException
	{
		idf_generate();
		String[] fold_names = { "Leftwing","CenterRight", "RightWing" };
		String op_path = "E:\\NihaThesis\\Latvia\\Results_observations\\Tests\\Test1_Latvia_tf_idfs\\Feature_selection\\tf_idf\\tf_idf.txt";
		FileWriter fw = new FileWriter(new File(op_path));
		
		for (int i = 0; i < fold_names.length; i++) {
			
			
			String p = "E:\\NihaThesis\\Latvia\\Raw_Data\\Latvian\\"+ fold_names[i];			
			File path = new File(p);
			System.out.println(path);
			File[] listfiles = path.listFiles();
			for (File f : listfiles) {
					
				    System.out.println(f.getName());				
					BufferedReader br = new BufferedReader(new FileReader(f));
					String line;
					while ((line = br.readLine()) != null) {
						String[] tokenizedTerms = line.replaceAll("^[,\\s]+", "").split("([,\\s\\.])+|(\\s)");
						for (String t_sub : tokenizedTerms) {
								String t = t_sub.replaceAll("!", "");
								if(t.contains("http")) continue;								
								clean_String(t);
								
								if(t!=""){
									if(corpus.containsKey(t))
									{
										if(tf_idf_tot.containsKey(t))
										{
											double val = tf_idf_tot.get(t);
											tf_idf_tot.put(t, val+1.0);
										}
										else
											tf_idf_tot.put(t,1.0);
																					
									}
						
								}
						}
					}
				}
			}
		
		for(String s:tf_idf_tot.keySet())
		{
			double count= 0;
			for(HashSet<String> hs:doc_terms)
			{
				if(hs.contains(s))count++;
			}
			fw.append(s+","+idf_vals.get(s)*tf_idf_tot.get(s)/count+"\n");
		}
		fw.flush();
		fw.close();
		System.out.println("tf-idf for feature selection is generated");
		
	}


	public void generate_tf_idf_docs() throws IOException {		
		idf_generate();
		String[] fold_names = { "Leftwing","CenterRight", "RightWing" };
		
		
		for (int i = 0; i < fold_names.length; i++) {
			
			String op_path = "E:\\NihaThesis\\Latvia\\Results_observations\\Tests\\Test1_Latvia_tf_idfs\\Unigrams_Termcount\\"+fold_names[i]+"\\";
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
								clean_String(t);						
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
					FileWriter fw = new FileWriter(new File(op_path+f.getName()));
					for(String term: doc_term.keySet())
					{
						double idf=(double)idf_vals.get(term);
						fw.append(term+" , "+(double)doc_term.get(term)*idf+"\n");
					}
					fw.flush();
					fw.close();
					}
				}
			}
	    } 
	
	
	public void clean_String(String s)
	{
		
		s=s.replaceAll("<(\"[^\"]*\"|'[^']*'|[^'\">])*>","");
		s=s.replaceAll("[0-9]", "");
		s=s.replaceAll("\\(", "");
		s=s.replaceAll("\\)", "");
		s=s.replace("\"", "");
		s=s.replaceAll("#","");
		s=s.replaceAll("[@$%'*&+-?/<>„”“]","");
		s=s.replace("\\","");
		s=s.replaceAll(";","");
		s=s.replaceAll("\\s+","");
		s=s.trim();
		s=s.replaceAll("^\\s+", "");
	}
	
	public void sparseMatrixGenerator() throws IOException
	{
		idf_generate();
		String[] fold_names = { "Leftwing","CenterRight", "RightWing" };
		FileWriter fw = new FileWriter(new File("E:\\NihaThesis\\Latvia\\Results_observations\\Tests\\Test1_Latvia_tf_idfs\\Feature_selection\\5Mutual_info\\features30k\\Sparse.txt"));
		FileWriter fw2 = new FileWriter(new File("E:\\NihaThesis\\Latvia\\Results_observations\\Tests\\Test1_Latvia_tf_idfs\\Feature_selection\\5Mutual_info\\features30k\\Class_vals.txt"));
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
				
				    //System.out.println(f.getName());
				    HashMap<String,Integer> doc_term = new HashMap<String,Integer>();					
					BufferedReader br = new BufferedReader(new FileReader(f));
					String line;
					while ((line = br.readLine()) != null) {
						String[] tokenizedTerms = line.replaceAll("^[,\\s]+", "").split("([,\\s\\.])+|(\\s)");
						for (String t_sub : tokenizedTerms) {
						
								String t = t_sub.replaceAll("!", "");
								if(t.contains("http")) continue;
								clean_String(t);
						
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
						double idf=(double)idf_vals.get(term);
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
		System.out.println("Sparse Matrix is Generated");
	}
	
	public static void main(String args[]) throws IOException
	{
		Tf_idf ti = new Tf_idf();
		ti.sparseMatrixGenerator();
	}
}
