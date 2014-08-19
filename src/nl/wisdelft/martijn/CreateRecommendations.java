package nl.wisdelft.martijn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import nl.wisdelft.martijn.Movie.Domain;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.nl.DutchAnalyzer;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.xml.sax.SAXException;

public class CreateRecommendations {
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/oip";
	static final String USER = "mmbase";
	static final String PASS = "admin2k";
	private static Map<String, Float> recommendedMovies;
	public static MovieDB movies;

	private static Directory indexDir;
	private static Analyzer analyzer;
	private static IndexWriterConfig config;
	private static final int NUMBER_OF_RECOMMENDATIONS = 5;

	private static final int NUMBER_OF_SEARCH_RESULTS = 1;
	private static final int NUMBER_OF_RELATED_VIDEOS = 50;
	private static final String INPUT = "Title"; // Title + date, Title + domain
	private static final int NUMBER_OF_SIMILAR_MOVIES_YT_TO_OI = 1;
	private static int SCORING_FUNCTION = 2;

	static Movie movie_to_recommend;
	
	public static void main(String[] args) throws IOException,
			XPathExpressionException, SAXException,
			ParserConfigurationException, TransformerException,
			ClassNotFoundException, SQLException {
		CreateRecommendations recSys = new CreateRecommendations();
		recSys.init();

		IndexCreator creator = new IndexCreator(indexDir, config, "domains.csv");
		creator.createIndex();
		SearchIndex search = new SearchIndex(analyzer, indexDir);
		createAllRecommendationsForGroundTruthExperiment(recSys, search);
	}
	
	private static void createAllRecommendationsForGroundTruthExperiment(CreateRecommendations recSys, SearchIndex search) throws IOException, XPathExpressionException, SAXException, ParserConfigurationException, TransformerException {
		YouTubeMovie movie = new YouTubeMovie();
		Dataset d = new Dataset();
		
		ArrayList<String> videos = new ArrayList<String>();
		
//		videos.add("1493"); //news
//		videos.add("151225"); //news
//		videos.add("612014"); //news
//		videos.add("614834"); //news
//		videos.add("155988"); //news
//		videos.add("617569"); //event
//		videos.add("630457"); //event
//		videos.add("155841"); //event
//		videos.add("613631"); //event
//		videos.add("631648"); //event
//		videos.add("1885"); //docu
//		videos.add("15581"); //docu
//		videos.add("28984"); //docu
//		videos.add("639267"); //docu
		videos.add("611962"); //docu		
		
		for (String video : videos) {
			movie_to_recommend = CreateRecommendations.movies.getMovie(Integer
					.parseInt(video));
			
			String date = d.getDate(Integer.toString(movie_to_recommend.getId()));
			Map<String, ArrayList<String>> relatedResults_Title = movie
					.retrieveRelatedVideos(INPUT,
							movie_to_recommend.getTitle(),
							NUMBER_OF_SEARCH_RESULTS, NUMBER_OF_RELATED_VIDEOS);

			ArrayList<String> counts = relatedResults_Title.get("counts");
			ArrayList<String> titles = relatedResults_Title.get("titles");
			ArrayList<String> descriptions = relatedResults_Title
					.get("descriptions");

			int search_results = Integer.parseInt(counts.get(0));
			int read_so_far = 0;
			for (int i = 0; i < search_results; i++) {
				int related_videos = Integer.parseInt(counts.get(i + 1));
				for (int j = 0; j < related_videos; j++) {
					String title = titles.get(j + read_so_far);
					System.out.println("---------------------------------------");
					System.out.println("Title related video: " + title);
					String description = descriptions.get(j + read_so_far);
					System.out.println("Description related video: " + description);
					Map<Movie, Float> similar = search.findSimilar(title,
							description, NUMBER_OF_SIMILAR_MOVIES_YT_TO_OI);
					recSys.score(similar, (float) (i + 1), (float) (j + 1));
				}
				read_so_far = read_so_far + related_videos;
			}
			ValueComparator vc = new ValueComparator(recommendedMovies);
			TreeMap<String, Float> sorted_map = new TreeMap<String, Float>(vc);
			sorted_map.putAll(recommendedMovies);
			recSys.printResults(sorted_map);
//			String directory = "D:/Dropbox/Public/Master Thesis/Dataset/Ground_truth/";
//			String path = directory + "video" + movie_to_recommend.getId() + "_" + INPUT + "_" + NUMBER_OF_SEARCH_RESULTS + "_" + NUMBER_OF_RELATED_VIDEOS + "_" + NUMBER_OF_SIMILAR_MOVIES_YT_TO_OI + "_" + SCORING_FUNCTION + ".csv";
//			if (movie_to_recommend.getDomain().equals(Movie.Domain.NEWS)) {
//				recSys.writeResultsToFile(sorted_map, path, Movie.Domain.DOCUMENTARY);
//			}
//			else if (movie_to_recommend.getDomain().equals(Movie.Domain.DOCUMENTARY)) {
//				recSys.writeResultsToFile(sorted_map, path, Movie.Domain.NEWS);
//			}
//			else if (movie_to_recommend.getDomain().equals(Movie.Domain.EVENT_COVERAGE)) {
//				recSys.writeResultsToFile(sorted_map, path, Movie.Domain.DOCUMENTARY);
//			}
		}
	}
		
	private static ArrayList<String> createTagBasedRecommendations(int id, Movie.Domain domain) throws IOException {
		ArrayList<String> result = new ArrayList<String>();
		Map<String, Float> result_temp = new HashMap<String, Float>();
		
		FileReader fr = new FileReader("videotag.csv");
		BufferedReader br = new BufferedReader(fr);
		String line = "";
		String tags = "";
		while((line = br.readLine()) != null) {
			String[] split = line.split(",");
			String videoId = split[1];
			if (videoId.equals(Integer.toString(id))) {
				tags = split[3];
			}
		}
		br.close();
		
		FileReader fr2 = new FileReader("tagvideo.csv");
		BufferedReader br2 = new BufferedReader(fr2);
		String[] tag = tags.split(";");
		ArrayList<String> tag_list = new ArrayList<String>();
		for (int i = 0; i < tag.length; i++) {
			tag_list.add(tag[i]);
		}
		
		while((line = br2.readLine()) != null) {
			String[] split = line.split(",");
			String tag_temp = split[0];
			if (tag_list.contains(tag_temp)) {
				if (!result_temp.containsKey(split[2]))
					result_temp.put(split[2], (float) 1);
				else
					result_temp.put(split[2], result_temp.get(split[2]) + 1);
			}
		}
		br2.close();
		
		ValueComparator vc = new ValueComparator(result_temp);
		TreeMap<String, Float> sorted_map = new TreeMap<String, Float>(vc);
		sorted_map.putAll(result_temp);
		
		Set<String> ids = sorted_map.keySet();
		Iterator<String> it = ids.iterator();
		int index = 0;
		while(it.hasNext() && index < 5) {
			String id_domain = it.next();
			Movie m = CreateRecommendations.movies.getMovie(Long.parseLong(id_domain));
			if (m != null) {
				System.out.println(id + ": " + m.getDomain());
				if(m.getDomain().equals(domain)) {
					result.add(id_domain);
					index++;
				}
			}
		}
				
		return result;
	}
	
	private static void writeToFile(Movie movie_to_recommend,
			ArrayList<String> recommendations) throws IOException {
		FileWriter fw = new FileWriter("video" + movie_to_recommend.getId() + "_strategy5.csv");
		for (String recommendation : recommendations) {
			fw.append(movie_to_recommend.getId() + "," + recommendation);
			fw.append('\n');
		}
		fw.flush();
		fw.close();
	}

	public void init() throws IOException {
		analyzer = new DutchAnalyzer(Version.LUCENE_47);
		config = new IndexWriterConfig(Version.LUCENE_47, analyzer);
		config.setOpenMode(OpenMode.CREATE_OR_APPEND);

		indexDir = new RAMDirectory(); // don't write on disk
		// indexDir = FSDirectory.open(new File("/Path/to/luceneIndex/"));
		// //write on disk

		recommendedMovies = new HashMap<String, Float>();
		movies = new MovieDB();
	}

	private void score(Map<Movie, Float> similar, float position1,
			float position2) {
		if (SCORING_FUNCTION == 1) {
			for (Map.Entry<Movie, Float> entry : similar.entrySet()) {
				String id = Integer.toString(entry.getKey().getId());
				float search_result_position = 1 / position1;
				float related_video_position = 1 / position2;
				Float similarity_score = entry.getValue();
				float final_score = similarity_score * search_result_position
						* related_video_position;
				if (!recommendedMovies.containsKey(id)) {
					recommendedMovies.put(id, final_score);
				} else {
					recommendedMovies.put(id,
							(recommendedMovies.get(id) + final_score) );
				}
			}
		} else if (SCORING_FUNCTION == 2) {
			for (Map.Entry<Movie, Float> entry : similar.entrySet()) {
				String id = Integer.toString(entry.getKey().getId());
				System.out.println("Title OI video: " + entry.getKey().getTitle());
				System.out.println("Description OI video: " + entry.getKey().getDescription());
				float search_result_position = 1 / (float) Math.log(position1+1);
				float related_video_position = 1 / (float) Math.log(position2+1);				
				Float similarity_score = entry.getValue();
				System.out.println("Similarity score: " + similarity_score);
				System.out.println("Search_result_position: " + position1);
				System.out.println("Search_result_score: " + search_result_position);
				System.out.println("Related_video_position: " + position2);
				System.out.println("Related_video_score" + related_video_position);
				float final_score = similarity_score * Math.abs(search_result_position)
						* Math.abs(related_video_position);
				System.out.println("Final score: " + final_score);
				if (!recommendedMovies.containsKey(id)) {
					recommendedMovies.put(id, final_score);
				} else
					recommendedMovies.put(id,
							(recommendedMovies.get(id) + final_score) );
			}
		}
	}

	private void writeResultsToFile(TreeMap<String, Float> sorted_map,
			String fileName, Movie.Domain domain) throws IOException, XPathExpressionException,
			SAXException, ParserConfigurationException, TransformerException {
		File file = new File(fileName);
		file.getParentFile().mkdirs();
		file.createNewFile();
		boolean exists = false;
		if (file.exists()) {
			exists = true;
		}

		FileWriter fw = new FileWriter(file, true);
		if (!exists) {
			fw.append("Video1, Video2, Simulated_Score");
			fw.append('\n');
		}

		int index = 0;
		for (Map.Entry<String, Float> entry : sorted_map.entrySet()) {
			String id = entry.getKey();
			Float score = entry.getValue();
			if (index < NUMBER_OF_RECOMMENDATIONS) {
				Movie m = movies.getMovie(Long.parseLong(id));
				if (!m.getTitle().equals(movie_to_recommend.getTitle())
						&& m.getDomain()
								.equals(domain)) {
					fw.append(movie_to_recommend.getId() + "," + m.getId()
							+ "," + score);
					fw.append('\n');
					index++;
				}
			}
		}

		fw.flush();
		fw.close();
	}

	private void printResults(TreeMap<String, Float> sorted_map) {
		Set<String> ids = sorted_map.keySet();
		Iterator<String> iterator = ids.iterator();		
		if (iterator.hasNext()) {
			String id = iterator.next();
			int index = 0;
			while (iterator.hasNext() && index < NUMBER_OF_RECOMMENDATIONS) {
				Movie m = movies.getMovie(Long.parseLong(id));
				if (!m.getTitle().equals(movie_to_recommend.getTitle())
						&& m.getDomain()
								.equals(Domain.NEWS)) {
					System.out
							.println("------------------------------------------------");
					System.out.println("Id: " + id);
					System.out.println("Title: " + m.getTitle());
					System.out.println("Description: " + m.getDescription());
					System.out.println("Domain: " + m.getDomain());
					id = iterator.next();
					index++;
				} else {
					id = iterator.next();
				}
			}
		}
	}
}

class ValueComparator implements Comparator<String> {

	Map<String, Float> base;

	public ValueComparator(Map<String, Float> base) {
		this.base = base;
	}

	// Note: this comparator imposes orderings that are inconsistent with
	// equals.
	public int compare(String a, String b) {
		if (base.get(a) >= base.get(b)) {
			return -1;
		} else {
			return 1;
		} // returning 0 would merge keys
	}
}