package Latvia_Thesis;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Raw_Data_Collect {
	
	public Set<String> Leftwing = new HashSet<String>();
	public Set<String> CenterRight = new HashSet<String>();
	public Set<String> RightWing = new HashSet<String>();
	public Set<String> Socialist = new HashSet<String>();
	public Set<String> ProRussian = new HashSet<String>();
	public Set<String> Separatist = new HashSet<String>();
	public  void set_Orgs_List()
	{
		//Leftwing.add("PCTVL");
		/*Leftwing.add("Yuriy Zaytsev");
		Leftwing.add("Miroslav Mitrofanov");
		Leftwing.add("Tatyana Zhdanok");
		CenterRight.add("Edgars RinkÄ“viÄs");
		CenterRight.add("Sandra Kalniete");
		CenterRight.add("Veiko Spolitis");
		CenterRight.add("Valdis Dombrovskis");
		CenterRight.add("Artis Pabriks");
		CenterRight.add("Laimdota Straujuma");
		CenterRight.add("Unity");
		RightWing.add("NA");
		RightWing.add("TB/LNNK");*/
		Socialist.add("PCTVL");
		/*Socialist.add("Tatyana Zhdanok");
		Socialist.add("Miroslav Mitrofanov");
		ProRussian.add("IMHO");
		ProRussian.add("Russkaya Zarya");
		ProRussian.add("RusskayaZarya");
		ProRussian.add("Janis Kuzins");
		ProRussian.add("Yuriy Alekseyev");
		ProRussian.add("Iosif Koren");
		ProRussian.add("Viktor Gushchin");
		ProRussian.add("Yelena Bachinskaya");
		ProRussian.add("Aleksandr Gaponenko");
		ProRussian.add("Yelizaveta Krivtsova");
		ProRussian.add("Einars Graudins");
		ProRussian.add("Vladimir Linderman");
		ProRussian.add("Illarion Girs");
		ProRussian.add("Yevgeniy Osipov");
		Separatist.add("LPR");
		Separatist.add("Anatoliy Matyukovskiy");
		Separatist.add("Aijo Beness");
		Separatist.add("Stanislavs Bukains");
		Separatist.add("Grigoriy Kosnikovskiy");
		Separatist.add("Valentin Milyutin");
		Separatist.add("Dmitriy Prokopenko");
		Separatist.add("Vyacheslav Vysotskiy");*/
	}
	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException
	{
		
		Raw_Data_Collect rc = new Raw_Data_Collect();
		rc.set_Orgs_List();
		String url = "jdbc:postgresql://localhost:5432/latvia_thesis";
		String user = "postgres";
		String password = "root";
		Class.forName("org.postgresql.Driver");
		Connection con = DriverManager.getConnection(url,user,password);
		
		String query = "Select distinct content from documents where party_individuals = ? and \"position\" = ? and language = ?; ";
		for(String s: rc.Socialist)
		{
			System.out.print(s+"'s documents are obtained:  ");
			int count = 1;
			String path = "E:\\NihaThesis\\Latvia\\Raw_Data\\Socialist\\";
			PreparedStatement ps=con.prepareStatement(query);
			
			ps.setString(1, s);
			ps.setString(2, "Left-Wing");
			ps.setString(3, "ru");
			System.out.println(ps);
			ResultSet rs = ps.executeQuery();
			
			while(rs.next())
			{
				String sb = rs.getString(1);
				File f  = new File(path+s+count+".txt");
				FileWriter fw = new FileWriter(f);
				fw.append(sb);
				fw.flush();
				fw.close();
				count++;
			}
			System.out.println("Total docs---"+ count);
		}
		
	}
}
