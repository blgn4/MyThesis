package Latvia_Thesis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class InsertintoLatvia_articles {
	public static boolean toboolean(String s)
	{
		if (s.equals("0"))
			return false;
		else 
			return true;
		
		
	}
	
	public static void main(String[] arg) throws ClassNotFoundException, SQLException, IOException
	{
		
		String url = "jdbc:postgresql://url/latvia";
		String user = "postgres";
		String password = "password";
		Class.forName("org.postgresql.Driver");
		Connection con = DriverManager.getConnection(url,user,password);
		//String query = "UPDATE  latvia_articles set org_name =? ,org_type = ? ,\"EU\"   =?, \"NATO\" =?,\"RUSSIA\" =? where ind_name = ?";
		
		//File f1 = new File("E:\\Latvia\\Book1.csv");
		File f1 = new File("E:\\Latvia\\final.txt");
		//FileWriter fw = new FileWriter(f2);
		BufferedReader in  = new BufferedReader ( new FileReader(f1));
		String line;
		while((line = in.readLine())!= null)
		{
			byte bytes[] = line.getBytes("ISO-8859-1"); 
			String value = new String(bytes, "UTF-8");
			String query="";
			//query = "UPDATE  latvia_articles set org_name =? ,org_type = ? ,\"EU\"   =?, \"NATO\" =?,\"RUSSIA\" =? where ind_name = ?";

			String[] cols = line.split("\t");
			//System.out.println(line);
			//fw.append(line+"\n");
			//System.out.println(cols.length);
			query = "UPDATE  latvia_articles set org_name =? ,org_type = ?  where ind_name = ?";
			String fname = cols[0].split(".txt")[0];
			String org_name = cols[2];
			String org_type = cols[1];
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, org_name);
			ps.setString(2,org_type);
			ps.setString(3,fname);
			ps.executeUpdate();			
			System.out.println(ps);
			ps.close();
			/*if(!cols[3].isEmpty()){
				query="UPDATE  latvia_articles set \"EU\"   =? where ind_name = ?";
				
				//query="UPDATE  latvia_articles set \"NATO\" =? where ind_name = ?";
				
				//query="UPDATE  latvia_articles set \"RUSSIA\" =? where ind_name = ?";
				PreparedStatement ps = con.prepareStatement(query);
				boolean eu = toboolean(cols[3]);
				String fname = cols[0].split(".txt")[0];
				//boolean nato = toboolean(cols[4]);
				//boolean russia = toboolean(cols[5]);			
				ps.setBoolean(1,eu);
				//ps.setBoolean(1,nato);
				//ps.setBoolean(1,russia);
				ps.setString(2,fname);
				ps.executeUpdate();
				ps.close();
				System.out.println(ps);
			}*/
			
			
		}
		
		//fw.flush();
		//fw.close();
	}

}
