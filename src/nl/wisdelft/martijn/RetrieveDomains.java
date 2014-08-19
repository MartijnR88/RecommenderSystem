package nl.wisdelft.martijn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class RetrieveDomains {
	private static Map<String, Movie.Domain> domains;
	private static ArrayList<String> news;
	private static ArrayList<String> eventcoverage;
	private static ArrayList<String> documentary;
	private static ArrayList<String> films;
	private static ArrayList<String> series;
	private static ArrayList<String> videoblog;
	private static String FILE;

	public RetrieveDomains() throws IOException {
		news = new ArrayList<String>();
		eventcoverage = new ArrayList<String>();
		documentary = new ArrayList<String>();
		films = new ArrayList<String>();
		series = new ArrayList<String>();
		videoblog = new ArrayList<String>();
		FILE = "domains.csv";
		domains = importDomains();
	}
	
	public RetrieveDomains(String filename) throws IOException {
		news = new ArrayList<String>();
		eventcoverage = new ArrayList<String>();
		documentary = new ArrayList<String>();
		films = new ArrayList<String>();
		series = new ArrayList<String>();
		videoblog = new ArrayList<String>();
		FILE = filename;
		domains = importDomains();		
	}

	public Movie.Domain getDomain(String id) {
		return domains.get(id);
	}

	private Map<String, Movie.Domain> importDomains() throws IOException {
		Map<String, Movie.Domain> result = new HashMap<String, Movie.Domain>();
		BufferedReader in = new BufferedReader(new FileReader(FILE));
		String line;
		while ((line = in.readLine()) != null) {
			String[] info = line.split(",");
			Movie.Domain domain = null;
			String id = info[0];
			info[1] = info[1].replace("-", "");
			if (info[1].contains("newsitem")) {
				domain = Movie.Domain.NEWS;
				news.add(id);
			} else if (info[1].contains("eventcoverage")) {
				domain = Movie.Domain.EVENT_COVERAGE;
				eventcoverage.add(id);
			} else if (info[1].contains("documentaryreport")) {
				domain = Movie.Domain.DOCUMENTARY;
				documentary.add(id);
			} else if (info[1].contains("films")) {
				domain = Movie.Domain.FILM;
				films.add(id);
			} else if (info[1].contains("series")) {
				domain = Movie.Domain.SERIES;
				series.add(id);
			} else if (info[1].contains("videoblog")) {
				domain = Movie.Domain.VIDEO_BLOG;
				videoblog.add(id);
			}
			if(result.containsKey(id))
				System.out.println(id);
			
			if (!(domain == null)) {
				result.put(id, domain);
			}
		}
		in.close();
		return result;
	}

	public ArrayList<String> randomlySelectVideos(int total, ArrayList<String> domain) throws IOException {
		ArrayList<String> result = new ArrayList<String>();
		ArrayList<String> ids = readVideoIds();
		
		Random randomGenerator = new Random();
		if (!domain.isEmpty()) {
			for (int i = 0; i < total; i++) {
				int randomInt = randomGenerator.nextInt(domain.size());
				if (ids.contains(domain.get(randomInt)) && !result.contains(domain.get(randomInt)))
					result.add(domain.get(randomInt));
				else
					i--;
			}
		}
		
		return result;
	}
	
	private static ArrayList<String> readVideoIds() throws IOException {
		ArrayList<String> result = new ArrayList<String>();

		BufferedReader in = new BufferedReader(new FileReader(
				"data_title_date_filtered.txt"));
		String line;
		while ((line = in.readLine()) != null) {
			//result.add(line);
			String[] ids = line.split(" ");
			for (int i = 1; i < ids.length; i++) {
				if (!ids[i].isEmpty()) {
					if (!ids[i].contains("["))
						result.add(ids[i]);
				}
			}
		}
		in.close();

		return result;
	}
	
	public ArrayList<String> randomlySelectVideos() {
		ArrayList<String> result = new ArrayList<String>();
		int numberOfVideos = 5;
		Random randomGenerator = new Random();

		if (!news.isEmpty()) {
			for (int i = 0; i < numberOfVideos; i++) {
				int randomInt = randomGenerator.nextInt(news.size());
				if (!result.contains(news.get(randomInt)))
					result.add(news.get(randomInt));
				else {
					i--;
				}
			}
		}

		if (!eventcoverage.isEmpty()) {
			for (int i = 0; i < numberOfVideos; i++) {
				int randomInt = randomGenerator.nextInt(eventcoverage.size());
				if (!result.contains(eventcoverage.get(randomInt)))
					result.add(eventcoverage.get(randomInt));
				else
					i--;
			}
		}
		if (!documentary.isEmpty()) {
			for (int i = 0; i < numberOfVideos; i++) {
				int randomInt = randomGenerator.nextInt(documentary.size());
				if(!result.contains(documentary.get(randomInt)))
						result.add(documentary.get(randomInt));
				else
					i--;
			}
		}
		if (!films.isEmpty()) {
			for (int i = 0; i < numberOfVideos; i++) {
				int randomInt = randomGenerator.nextInt(films.size());
				if (!result.contains(films.get(randomInt)))
					result.add(films.get(randomInt));
				else
					i--;
			}
		}
		if (!series.isEmpty()) {
			for (int i = 0; i < numberOfVideos; i++) {
				int randomInt = randomGenerator.nextInt(series.size());
				if (!result.contains(series.get(randomInt)))
					result.add(series.get(randomInt));
				else
					i--;
			}
		}
		//TODO: SET VIDEO_BLOG BACK
//		if (!videoblog.isEmpty()) {
//			for (int i = 0; i < numberOfVideos; i++) {
//				int randomInt = randomGenerator.nextInt(videoblog.size());
//				if (!result.contains(videoblog.get(randomInt)))
//					result.add(videoblog.get(randomInt));
//				else
//					i--;
//			}
//		}

		return result;
	}
	
	public Set<String> getAllVideos() {
		return domains.keySet();
	}
	
	private static void createCombinations(ArrayList<String> news_selected, ArrayList<String> docu_relevant, String filename) throws XPathExpressionException, SAXException, IOException, ParserConfigurationException, TransformerException {
		List<String> list = new ArrayList<String>();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Map<String, String> myJSON = new LinkedHashMap<String, String>();
		Dataset d = new Dataset();
		List<String> ids = d.getIdentifiers();
		List<String> mediums = d.getUniqueMediums();

		for (String news : news_selected) {
			for (String docu : docu_relevant) {
				String id1 = "oai:openimages.eu:" + news;
				String id2 = "oai:openimages.eu:" + docu;
				
				int index1 = ids.indexOf(id1);
				String medium1 = mediums.get(index1);
				myJSON.put("videoId1", id1);
				myJSON.put("webm1", medium1);

				int index2 = ids.indexOf(id2);
				String medium2 = mediums.get(index2);
				myJSON.put("videoId2", id2);
				myJSON.put("webm2", medium2);
				
				//Don't add double pairs
				if (!list.contains(gson.toJson(myJSON)))
					list.add(gson.toJson(myJSON));
			}
		}

		try {
			FileWriter file = new FileWriter(filename);
			System.out.println(list.toString());
			file.write(list.toString());
			file.flush();
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException, XPathExpressionException, SAXException, ParserConfigurationException, TransformerException {
		RetrieveDomains domain = new RetrieveDomains();
//		ArrayList<String> news_selected = domain.randomlySelectVideos(3, news);
//		ArrayList<String> docu_selected = domain.randomlySelectVideos(3, documentary);
//		ArrayList<String> event_selected = domain.randomlySelectVideos(3, eventcoverage);

		ArrayList<String> news_selected = new ArrayList<String>();
		ArrayList<String> docu_selected = new ArrayList<String>();
		ArrayList<String> event_selected = new ArrayList<String>();
		news_selected.add("1493"); //news
		news_selected.add("151225"); //news
		news_selected.add("612014"); //news
		news_selected.add("614834"); //news
		news_selected.add("155988"); //news
		event_selected.add("617569"); //event
		event_selected.add("630457"); //event
		event_selected.add("155841"); //event
		event_selected.add("613631"); //event
		event_selected.add("631648"); //event
		docu_selected.add("1885"); //docu
		docu_selected.add("15581"); //docu
		docu_selected.add("28984"); //docu
		docu_selected.add("639267"); //docu
		docu_selected.add("611962"); //docu		
						
		createCombinations(news_selected, documentary, "ground_truth_test1.json");
		createCombinations(docu_selected, news, "ground_truth_test2.json");
		createCombinations(event_selected, documentary, "ground_truth_test3.json");
		
//		System.out.println(domain.getDomain("624385"));
//		System.out.println(news.size());
//		System.out.println(documentary.size());
//		System.out.println(eventcoverage.size());
//		System.out.println(films.size());
//		System.out.println(series.size());
//		System.out.println(videoblog.size());
	}

	public ArrayList<String> randomlySelectVideos(int total, String domain) throws IOException {
		ArrayList<String> result = new ArrayList<String>();
		if (domain.equals("news")) {
			result = randomlySelectVideos(total, news);
		}
		else if (domain.equals("event")) {
			result = randomlySelectVideos(total, eventcoverage);
		}
		else if (domain.equals("documentary"))
			result = randomlySelectVideos(total, documentary);
		
		return result;
	}
}
