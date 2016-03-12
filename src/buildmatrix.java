import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;


public class buildmatrix {
 public static void main(String[] args) throws IOException
 {
	 int k=0;
	 
	 File fout1 = new File("E:\\TurkeyDataset\\Test\\class1.txt");
	 if(fout1.exists())
	 {
		 fout1.delete();
	 }
	 FileWriter fw1 = new FileWriter(fout1);
	 File fout = new File("E:\\TurkeyDataset\\Test\\matrix1.txt");
	 if(fout.exists())
	 {
		 fout.delete();
	 }
	 FileWriter fw = new FileWriter(fout);
	 
	 File fileslist  = new File("E:\\TurkeyDataset\\Test\\INPUT");
	 File[] filedir = fileslist.listFiles();
	 for(File eachfile:filedir)
	 {
		System.out.println(eachfile.getName());
		 String[] arr = new String[524936];
		 Arrays.fill(arr, "0");
		 
		 if(eachfile.getName().endsWith(".txt"))
		 {
			 BufferedReader br = new BufferedReader(new FileReader(eachfile));
			 String line;
			 while((line=br.readLine())!=null)
			 {
				 String[] word = line.split("\t");
				 arr[Integer.parseInt(word[0])]=word[1]; 
			 }
			 
			 for(int i=0;i<524936;i++)
			 {
				if(arr.length-1!=i)
					 fw.append(arr[i]+",");
				else if(arr.length-1==i)
					fw.append(arr[i]);
			 }
			
			 fw.append(System.getProperty("line.separator"));
			 fw.flush();
			 fw1.append(eachfile.getName()+"\n");
			 br.close();
		 }
		
		 
	 }
 fw.close();
 fw1.close();
 }
}
