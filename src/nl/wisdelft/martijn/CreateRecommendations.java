package nl.wisdelft.martijn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.nl.DutchAnalyzer;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

public class CreateRecommendations {
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/oip";
	static final String USER = "mmbase";
	static final String PASS = "admin2k";
	private static Map<String, Integer> recommendedMovies;
	public static MovieDB movies;
	
	private static Directory indexDir;
	private static Analyzer analyzer;
	private static IndexWriterConfig config;
	private static final int NUMBER_OF_RECOMMENDATIONS = 5;
	static String videoTitle;

	public static void main(String[] args) throws IOException {
		CreateRecommendations recSys = new CreateRecommendations();
		recSys.init();
		
		IndexCreator creator = new IndexCreator(indexDir, config);
		creator.createIndex();
		
		SearchIndex search = new SearchIndex(analyzer, indexDir);
		YouTubeMovie movie = new YouTubeMovie();
		videoTitle = "1 km trappen, 4 km vrijwielen";
		ArrayList<String> relatedVideos = movie.retrieveRelatedVideos("Title", videoTitle);
		System.out.println(relatedVideos.size());
		for (int i = 0; i < relatedVideos.size(); i++) {
			Map<Movie, Float> similar = search.findSimilar(relatedVideos.get(i), 1);
			recSys.score(similar);
		}
		ValueComparator vc = new ValueComparator(recommendedMovies);
		TreeMap<String, Integer> sorted_map = new TreeMap<String, Integer>(vc);
		sorted_map.putAll(recommendedMovies);
		recSys.printResults(sorted_map);
	}

	public void init() throws IOException{
		analyzer = new DutchAnalyzer(Version.LUCENE_47);
		config = new IndexWriterConfig(Version.LUCENE_47, analyzer);
		config.setOpenMode(OpenMode.CREATE_OR_APPEND);
		
		indexDir = new RAMDirectory(); //don't write on disk
		//indexDir = FSDirectory.open(new File("/Path/to/luceneIndex/")); //write on disk
		
		recommendedMovies = new HashMap<String, Integer>();
		movies = new MovieDB();
	}

	private void score(Map<Movie, Float> similar) {
		//TODO: incorporate scores
		for (Map.Entry<Movie, Float> entry : similar.entrySet())
		{
			String id = Integer.toString(entry.getKey().getId());
			if (!recommendedMovies.containsKey(id))
				recommendedMovies.put(id, 1);
			else 
				recommendedMovies.put(id, recommendedMovies.get(id) + 1);
		}
	}
	
	private void printResults(TreeMap<String, Integer> sorted_map) {
		Set<String> ids = sorted_map.keySet();
		Iterator<String> iterator = ids.iterator();
		String id = iterator.next();
		int index = 0;
		while (iterator.hasNext() && index < NUMBER_OF_RECOMMENDATIONS) {
			Movie m = movies.getMovie(Long.parseLong(id));
			//TODO: change this to something more smarter
			if (!m.getTitle().equals(videoTitle) && m.getDomain().equals(Movie.Domain.DOCUMENATARY)) {
				System.out.println("------------------------------------------------");
				System.out.println("Id: " + id);
				System.out.println("Title: " + m.getTitle());
				System.out.println("Description: " + m.getDescription());
				id = iterator.next();
				index++;	
			}
			else {
				id = iterator.next();
			}
		}
	}
}

class ValueComparator implements Comparator<String> {

    Map<String, Integer> base;
    public ValueComparator(Map<String, Integer> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.    
    public int compare(String a, String b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}