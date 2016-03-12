import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


/**
 * @author Niharika
 *
 */
public class Hello {
public static void main(String[] args) throws IOException
{
	File f = new File("E:\\hello.txt");
	FileWriter fw = new FileWriter(f);
	Hello h = new Hello();
	for(int i=0;i<1000000;i++)
	{
	fw.append("HEllowWord!!!! \n");
    System.out.println("Hello!!!"); 
	}
	fw.flush();
	fw.close();
}
}
