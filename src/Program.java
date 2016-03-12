
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Program {

	static String filePath = "C:/Users/Sultan/PycharmProjects/FirstOne/weka/local_and_global_updated.json";


	public static ArrayList<MeasuresColl> ls = new ArrayList<MeasuresColl>();
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Hellow");
		//parseTheFile0();

		parseTheJsonFileAndFillTheList();
		populateTheListToDataBase();

	}

	static Connection connection = null;

	public static void populateTheListToDataBase()
	{
		System.out.println("-------- PostgreSQL "
				+ "JDBC Connection Testing ------------");

		try {

			Class.forName("org.postgresql.Driver");

		} catch (ClassNotFoundException e) {

			System.out.println("Where is your PostgreSQL JDBC Driver? "
					+ "Include in your library path!");
			e.printStackTrace();
			return;
		}

		System.out.println("PostgreSQL JDBC Driver Registered!");

		connection = null;;

		try {

			connection = DriverManager.getConnection(
					"jdbc:postgresql://127.0.0.1:5432/UKNew", "postgres",
					"password");

		} catch (SQLException e) {

			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return;

		}

		if (connection != null) {
			System.out.println("You made it, take control your database now!");
			populate_BrNBrNetworkMeasures();
		} else {
			System.out.println("Failed to make connection!");
		}
		// Populate tables

	}
	public static void populate_BrNBrNetworkMeasures()
	{
		Statement stmt = null;
		try{
			Class.forName("org.postgresql.Driver");

			connection.setAutoCommit(false);
			System.out.println("Opened database successfully");

			stmt = connection.createStatement();
			stmt.executeUpdate("DELETE FROM BrNBrNetworkMeasures;");
			for (MeasuresColl m : ls)
			{
				/*String sql = "INSERT INTO BrNBrNetworkMeasures (fileName,brkNbrk,eigenvectorL,avgNeiborL,pageRankL,closenessL,numOfEdgesL,numOfNodesL,eigenvectorG,closenessG,pageRankG,indegreeG,outdegreeG,degreeG)"
						+ "VALUES ("
						+"'"+m.fileName+"',"
					//	+"'"+m.BrkNBrk+"',"
						+m.ev_local+","
						+m.avgN_local+","
						+m.pr_local+","
						+m.cls_local+","
						//+m.numOfEdges_local+","
						//+m.numOfNodes_local+","
						+m.ev_global+","
						+m.cls_global+","
						+m.pr_global+","
						+m.ind_global+","
					//	+m.outd_gloval+","
					//	+m.deg_gloval+
					
						String fileName;

	double ev_local;
	double pr_local;
	double cls_local;
	double btweeness_local;
	double avgN_local;
	double degree_local;
	double uninfectN;
	long numberOfNodes;
	long numberOfEdgesUnique;
	double numberOfEdgesWeighted;

	double ev_global;
	double pr_global;
	double cls_global;
	double btweeness_global;
	double avgN_global;
	double ind_global;
	double outd_global;
	double deg_global;
	double link_rate;
	double purelinkratio;
	
	String originalClass;
	String predictedClass;+");";  */

				String sql = "INSERT INTO BrNBrNetworkMeasures (fileName,ev_local,pr_local,"
						   + "cls_local,btweeness_local,avgN_local,degree_local,uninfectN,"
						   + "numberOfNodes,numberOfEdgesUnique,numberOfEdgesWeighted,"
						  
						   + "ev_global,pr_global,cls_global,btweeness_global,avgN_global,"
						   + "ind_global,outd_global,deg_global,link_rate,purelinkratio,"
						   
						   + "originalClass,predictedClass)"
						+ "VALUES ("
						+"'"+m.fileName+"',"
						+m.ev_local+","
						+m.pr_local+","
						+m.cls_local+","
						+m.btweeness_local+","
						+m.avgN_local+","
						+m.degree_local+","
						+m.uninfectN+","
						+m.numberOfNodes+","
						+m.numberOfEdgesUnique+","
						+m.numberOfEdgesWeighted+","
						
						+m.ev_global+","
						+m.pr_global+","
						+m.cls_global+","
						+m.btweeness_global+","
						+m.avgN_global+","
						+m.ind_global+","
						+m.outd_global+","
						+m.deg_global+","
						+m.link_rate+","
						+m.purelinkratio+","
						
						+"'"+m.originalClass+"',"
						+"'"+m.predictedClass+"'"
						
						+");";  

				System.out.println(sql);
				stmt.executeUpdate(sql);
			}

			stmt.close();
			connection.commit();
			//connection.close();
		} catch (Exception e) {
			System.err.println( e.getClass().getName()+": "+ e.getMessage() );
			System.exit(0);
		}
		System.out.println("Records created successfully");



	}


	public static MeasuresColl ParseTheMesObj(JSONObject j)
	{
		MeasuresColl m = new MeasuresColl();

		String fileName;

		double ev_local;
		double pr_local;
		double cls_local;
		double btweeness_local;
		double avgN_local;
		double degree_local;
		double uninfectN;
		int numberOfNodes;
		int numberOfEdgesUnique;
		int numberOfEdgesWeighted;

		double ev_global;
		double pr_global;
		double cls_global;
		double btweeness_global;
		double avgN_global;
		double ind_global;
		double outd_global;
		double deg_global;
		double link_rate;
		double purelinkratio;
		String originalClass;
		String predictedClass;

		m.ev_local = (long)j.get("ev_local");
		m.pr_local = (double)j.get("pr_local");
		m.cls_local = (double)j.get("cls_local");
		m.btweeness_local = (double)j.get("btweeness_local");
		m.avgN_local = (double)j.get("avgN_local");
		m.degree_local = (double)j.get("degree_local");
		m.uninfectN = (double)j.get("uninfectN");
		m.numberOfNodes = (long) j.get("numberOfNodes");
		m.numberOfEdgesUnique = (long) j.get("numberOfEdgesUnique");
		m.numberOfEdgesWeighted = (double) j.get("numberOfEdgesWeighted");

		m.ev_global = (double)j.get("ev_global");
		m.pr_global = (double)j.get("pr_global");
		m.cls_global = (double)j.get("cls_global");
		m.btweeness_global = (double)j.get("btweeness_global");
		m.avgN_global = (double)j.get("avgN_global");
		m.ind_global = (double)j.get("ind_global");
		m.outd_global = (double)j.get("outd_global");
		m.deg_global = (double)j.get("deg_gloval");
		m.link_rate = (double)j.get("link_rate");
		m.purelinkratio = (double)j.get("purelinkratio");

		m.originalClass = (String)j.get("originalClass");
		m.predictedClass = (String)j.get("predictedClass");

		return m;
	}
	public static void objectJsonFIle(JSONObject brnbr,String strBrkNBrk) {
		Iterator<String> keys = brnbr.keySet().iterator();

		while( keys.hasNext() ){
			String key = (String)keys.next();
			if( brnbr.get(key) instanceof JSONObject ){

				System.out.println(brnbr.get(key));
				JSONObject val = (JSONObject) brnbr.get(key);
				MeasuresColl  m = ParseTheMesObj(val);
				m.fileName = new String(key);
				//m.BrkNBrk = new String(strBrkNBrk);


				ls.add(m);

			}
		}
	}
	public static void parseTheJsonFileAndFillTheList()
	{
		try {
			FileReader reader = new FileReader(filePath);

			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);

			JSONObject BreakingObj = (JSONObject) jsonObject.get("Breaking");
			System.out.println(BreakingObj);

			JSONObject NonBreakingObj = (JSONObject) jsonObject.get("Nonbreaking");
			System.out.println(NonBreakingObj);


			System.out.println(BreakingObj.size());
			System.out.println(NonBreakingObj.size());

			objectJsonFIle(BreakingObj,"Breaking");
			objectJsonFIle(NonBreakingObj,"Nonbreaking");


		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

}

class MeasuresColl{

	String fileName;

	double ev_local;
	double pr_local;
	double cls_local;
	double btweeness_local;
	double avgN_local;
	double degree_local;
	double uninfectN;
	long numberOfNodes;
	long numberOfEdgesUnique;
	double numberOfEdgesWeighted;

	double ev_global;
	double pr_global;
	double cls_global;
	double btweeness_global;
	double avgN_global;
	double ind_global;
	double outd_global;
	double deg_global;
	double link_rate;
	double purelinkratio;
	
	String originalClass;
	String predictedClass;

}