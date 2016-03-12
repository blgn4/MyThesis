package Latvia_Thesis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Generate_Bi_Grams {
	public void genStop_words()
	{
		
	}
	public static void main(String[] args) throws IOException {
		String out_path = "E:\\NihaThesis\\Latvia\\Bigrams\\Split_data\\Latvian";
		String[] fold_names = { "CenterRight", "Leftwing", "RightWing" };
		for (int i = 0; i < fold_names.length; i++) {
			String p = "E:\\NihaThesis\\Latvia\\Raw_Data\\Latvian"
					+ fold_names[i];
			File path = new File(p);
			File[] listfiles = path.listFiles();
			for (File f : listfiles) {
				BufferedReader br = new BufferedReader(new FileReader(f));
				StringBuilder sb = new StringBuilder();
				String line;
				while((line=br.readLine())!=null)
				{
					sb.append(line);
				}
				String[] tokenizedTerms = sb.toString().replaceAll("[,\\s]+", "").split("([,\\s])+|(\\s)");
				for(int j=0;j<tokenizedTerms.length;j++)
				{
					
				}
			}
		}
	}
}
