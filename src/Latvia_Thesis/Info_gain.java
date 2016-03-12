package Latvia_Thesis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

class T_cpclass
{
	public double class_counts[]={1.0,1.0};
	
	public T_cpclass inc_val(int ind)
	{
		this.class_counts[ind] += 1;
		return this;
	}
	
	public double get_val(int ind)
	{
		return this.class_counts[ind];
	}
}

public class Info_gain {
	
	public static HashMap<String,Double> terms_tot_occ = new HashMap<String,Double>();
	public static HashMap<String ,T_cpclass> term_class_prob= new HashMap<String , T_cpclass>();
	public static double[] docs_class_counts = new double[2];
	public static double total_docs=0;
	public static double[] terms_class=new double[2];
	public static void generateTerms() throws IOException
	{
		String corpus_file = "E:\\NihaThesis\\Latvia\\Results_observations\\Tests\\Test1_Latvia_tf_idfs\\Feature_selection\\RefinedCorpus.txt";
		FileWriter fw = new FileWriter("E:\\NihaThesis\\Latvia\\Results_observations\\Tests\\Test1_Latvia_tf_idfs\\Feature_selection\\Corpus_lines.txt");
		BufferedReader br  = new BufferedReader(new FileReader(corpus_file));
		String line;
		
		
		while((line=br.readLine())!=null)
		{
			T_cpclass hm= new T_cpclass();
			terms_tot_occ.put(line.trim(), 0.0);
			term_class_prob.put(line.trim(), hm);
		}
		System.out.println("Terms are generated");
	}
	
	public static void gen_term_prob() throws IOException
	{
		docs_class_counts[0] = docs_class_counts[1]=0;
		terms_class[0] = terms_class[1]=0;
		generateTerms();
		List<HashSet<String>> doc_terms = new ArrayList<HashSet<String>>();
		String[] fold_names = { "Leftwing","CenterRight", "RightWing" };
		for (int i = 0; i < fold_names.length; i++) {
			String p = "E:\\NihaThesis\\Latvia\\Raw_Data\\Latvian\\"+ fold_names[i];			
			File path = new File(p);
			System.out.println(path);
			File[] listfiles = path.listFiles();
			int class_val;
			for (File f : listfiles) {
				System.out.println(f.getName());
				total_docs++;
				if(i==0) class_val =0;
				else class_val =1;
				
				docs_class_counts[class_val] ++;
				
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
					
							if(t!="" && terms_tot_occ.containsKey(t)){
								terms_class[class_val]+=1.0;
									double val = terms_tot_occ.get(t);
									terms_tot_occ.put(t, val+1.0);
									T_cpclass hm= term_class_prob.get(t);
									 //System.out.println(class_val+hm.get(0));
									T_cpclass hm_new= hm.inc_val(class_val); 								 
									 term_class_prob.put(t, hm_new);
							}
						}
				   }
			 }
		}
		System.out.println("Probabilities are generated");
		
   }
	
	public static void main(String[] args) throws IOException
	{
		gen_term_prob();
		System.out.println("writing features to file");
		File f = new File("E:\\NihaThesis\\Latvia\\Results_observations\\Tests\\Test1_Latvia_tf_idfs\\Information_Gain\\FS\\IG_Corpus.txt");
		FileWriter fw = new FileWriter(f);
		File f2 = new File("E:\\NihaThesis\\Latvia\\Results_observations\\Tests\\Test1_Latvia_tf_idfs\\Information_Gain\\FS\\values.txt");
		FileWriter fw2 = new FileWriter(f2);
		double pc1 = docs_class_counts[0]/total_docs;
		double pc2 = docs_class_counts[1]/total_docs;
		//System.out.println(pc1+"---"+pc2);
		double part1 =  -pc1*Math.log(pc1) - pc2*Math.log(pc2);
		for(String t : terms_tot_occ.keySet())
		{
			T_cpclass hm = term_class_prob.get(t);
			double t1 = (hm.get_val(0));
			double t2 = (hm.get_val(1));
			System.out.println(t2);
			double pt = terms_tot_occ.get(t)/(terms_class[0]+terms_class[1]);
			//System.out.println(pt);
			double ptc1=(t1/terms_class[0]);
			double ptc2=(t2/terms_class[1]);
			//System.out.println(ptc1+"--------------"+ptc2);
			double pc1t = (pc1*ptc1)/pt;
			double pc2t = (pc2*ptc2)/pt;
			double part2 = pt*(pc1t*Math.log(pc1t)+pc2t*Math.log(pc2t));
			double part3 = (1.0-pt)*(Math.log(1.0-pc1t)*(1.0-pc1t) + (1.0-pc2t)*Math.log(1.0-pc2t));
			fw2.append(t1+","+t2+","+pt+","+pc1+","+pc2+","+ptc1+","+ptc2+","+pc1t+","+pc2t+","+part2+","+part3+"\n");
			fw.append(t+","+(part1+part2+part3)+"\n");
		}
		fw2.flush();
		fw2.close();
		fw.flush();
		fw.close();
	}
}
