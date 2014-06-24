package nl.wisdelft.martijn;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.lang3.StringEscapeUtils;
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
	private String filename;
    
	public IndexCreator(Directory indexDir, IndexWriterConfig config, String filename) {
		this.indexDir = indexDir;
		this.config = config;
		this.filename = filename;
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
			sql = "SELECT title, body, source FROM oi_mediafragments_table";
			ResultSet rs = stmt.executeQuery(sql);
			RetrieveDomains domains = new RetrieveDomains(filename);
			
			while (rs.next()) {
				FieldType type = new FieldType();
				type.setIndexed(true);
				type.setStored(true);
				type.setStoreTermVectors(true); //TermVectors are needed for MoreLikeThis
				
				Document doc = new Document();
				//doc.add(new StringField("id", Integer.toString(rs.getInt("number")), Store.YES));
				doc.add(new StringField("id", StringEscapeUtils.unescapeHtml4(rs.getString("source")).replace("oaiopenimages.eu", ""), Store.YES));
				doc.add(new Field("title", StringEscapeUtils.unescapeHtml4(rs.getString("title")), type));
				doc.add(new Field("content", StringEscapeUtils.unescapeHtml4(rs.getString("body")), type));				
				//doc.add(new LongField("date", Long.parseLong(rs.getString("date")), Field.Store.YES));
				Movie.Domain domain = domains.getDomain(rs.getString("source").replace("oaiopenimages.eu", ""));
				if (domain != null) {
					Movie movie = new Movie(Integer.parseInt(rs.getString("source").replace("oaiopenimages.eu", "")), rs.getString("title"), rs.getString("body"), domain);
					CreateRecommendations.movies.addMovie(movie);
					indexWriter.addDocument(doc);
				}
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
