package nl.wisdelft.martijn;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;

public class IndexCreator {
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private static final String DB_URL = "jdbc:mysql://localhost/oip";
	private static final String USER = "mmbase";
	private static final String PASS = "admin2k";
	
	private Directory indexDir;
	private IndexWriterConfig config;
    
	public IndexCreator(Directory indexDir, IndexWriterConfig config) {
		this.indexDir = indexDir;
		this.config = config;
	}
	
	public void createIndex() throws IOException{
		IndexWriter indexWriter = new IndexWriter(indexDir, config);
		indexWriter.commit();
		
		Connection conn = null;
		Statement stmt = null;

		try {
			Class.forName(JDBC_DRIVER);

			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			System.out.println("Creating statement...");
			stmt = conn.createStatement();
			String sql;
			sql = "SELECT number, title, body, keywords, date FROM oi_mediafragments_table";
			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				FieldType type = new FieldType();
				type.setIndexed(true);
				type.setStored(true);
				type.setStoreTermVectors(true); //TermVectors are needed for MoreLikeThis
				
				Document doc = new Document();
				doc.add(new StringField("id", Integer.toString(rs.getInt("number")), Store.YES));
				doc.add(new Field("title", rs.getString("title"), type));
				doc.add(new Field("content", rs.getString("body"), type));
				//doc.add(new LongField("date", Long.parseLong(rs.getString("date")), Field.Store.YES));
				//TODO: make this smarter
				Movie movie = new Movie(rs.getInt("number"), rs.getString("title"), rs.getString("body"), Movie.Domain.DOCUMENATARY);
				CreateRecommendations.movies.addMovie(movie);
				indexWriter.addDocument(doc);
			}
						
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
			}
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		indexWriter.commit();
		indexWriter.forceMerge(100, true);
		indexWriter.close();
	}		
}
