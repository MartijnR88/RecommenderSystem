package nl.wisdelft.martijn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Subsetdataset {
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private static final String DB_URL = "jdbc:mysql://localhost/oip";
	private static final String USER = "mmbase";
	private static final String PASS = "admin2k";

	public static void main(String[] args) throws ClassNotFoundException,
			SQLException, IOException {
		Connection conn = null;
		Statement stmt = null;

		Class.forName(JDBC_DRIVER);

		System.out.println("Connecting to database...");
		conn = DriverManager.getConnection(DB_URL, USER, PASS);

		System.out.println("Creating statement...");
		stmt = conn.createStatement();
		String sql;

		FileReader fr = new FileReader(new File("domains.csv"));
		BufferedReader br = new BufferedReader(fr);
		FileWriter fw = new FileWriter("subdataset.csv");

		// Read header
		String line = br.readLine();

		while ((line = br.readLine()) != null) {
			String[] split = line.split(",");
			String id = split[0];
			sql = "SELECT title, body, source FROM oi_mediafragments_table WHERE source = 'oaiopenimages.eu"
					+ id + "'";
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String title = rs.getString("title").replace(",", ";");
				String description = rs.getString("body").replace(",", ";");
				fw.append(id + "," + title + "," + description);
				fw.append('\n');
			}
		}

		fw.flush();
		fw.close();
		br.close();
		stmt.close();
		conn.close();
	}
	
	public static String getDate(String sqlquery) throws ClassNotFoundException, SQLException {
		Connection conn = null;
		Statement stmt = null;

		Class.forName(JDBC_DRIVER);

		System.out.println("Connecting to database...");
		conn = DriverManager.getConnection(DB_URL, USER, PASS);

		System.out.println("Creating statement...");
		stmt = conn.createStatement();

		ResultSet rs = stmt.executeQuery(sqlquery);
		String result = "";
		while (rs.next()) {
			result = rs.getString("date");
		}

		stmt.close();
		conn.close();

		return result;
	}
	
	public String getDescription(String sqlquery) throws ClassNotFoundException, SQLException {
		Connection conn = null;
		Statement stmt = null;

		Class.forName(JDBC_DRIVER);

		System.out.println("Connecting to database...");
		conn = DriverManager.getConnection(DB_URL, USER, PASS);

		System.out.println("Creating statement...");
		stmt = conn.createStatement();

		ResultSet rs = stmt.executeQuery(sqlquery);
		String result = "";
		while (rs.next()) {
			result = rs.getString("body");
		}

		stmt.close();
		conn.close();

		return result;
	}	
}
