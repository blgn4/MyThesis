import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class Bello {
	public static void main(String[] args) throws IOException
	{
		File f = new File("E:\\hello1.txt");
		FileWriter fw = new FileWriter(f);
		for(int i=0;i<1000000;i++)
		{
		fw.append("Bubbyee!!!! \n");
		System.out.println("Bubbyee!!");
	}
		fw.flush();
		fw.close();
	}
}
