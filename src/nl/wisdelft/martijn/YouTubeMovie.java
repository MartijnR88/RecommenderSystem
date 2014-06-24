package nl.wisdelft.martijn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * In this class a YouTubeMovie is reconstructed from a text file
 * @author af13020
 *
 */

public class YouTubeMovie {	
	private static final String PATH = "D:/workspace/YouTubeAPI/Dataset/";
	
	public YouTubeMovie() throws IOException {
	}
			
	public Map<String, ArrayList<String>> retrieveSearchResults(String type, String title, int numberOfSearchResults) throws IOException {
		ArrayList<String> relatedTitles = new ArrayList<String>();
		ArrayList<String> relatedDescriptions = new ArrayList<String>();

		FileReader fr = new FileReader(new File(PATH + type + "/" + Util.correctPath(title) + ".csv"));		
		BufferedReader br = new BufferedReader(fr);
		
		String line = br.readLine();
		int index = 0;

		while ((line = br.readLine()) != null  && index < numberOfSearchResults) {
			String[] split = line.split(",");
			
			if (split.length == 4) {
				relatedTitles.add(split[2]);
				relatedDescriptions.add(split[3]);
			}
			else if (split.length == 3) {
				relatedTitles.add(split[2]);
				relatedDescriptions.add("");
			}
			else {
				relatedTitles.add("");
				relatedDescriptions.add("");
			}
			
			index++;				
		}
		
		br.close();
		Map<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		map.put("titles", relatedTitles);
		map.put("descriptions", relatedDescriptions);

		return map;
	}
	
	public Map<String, ArrayList<String>> retrieveRelatedVideos(String type, String title, int numberOfSearchResults, int numberOfRelatedVideos) throws IOException {
		ArrayList<String> relatedTitles = new ArrayList<String>();
		ArrayList<String> relatedDescriptions = new ArrayList<String>();
		ArrayList<String> videoIds = getVideoIds(PATH + type + "/" + Util.correctPath(title) + ".csv", numberOfSearchResults);
				
		//First the number of search results and then the number of related videos per search result.
		ArrayList<String> counts = new ArrayList<String>();
		counts.add(Integer.toString(videoIds.size()));
		
		for (String id : videoIds) {
			FileReader fr = new FileReader(new File(PATH + type + "/" + Util.correctPath(title) + "/" + Util.correctPath(id) + ".csv"));
			BufferedReader br = new BufferedReader(fr);
			
			String line = br.readLine();
			int index = 0;
			//Read another line to skip the header
			line = br.readLine();
			while (line != null && index < numberOfRelatedVideos) {
				String[] split = line.split(",");
				
				if (split.length == 4) {
					relatedTitles.add(split[2]);
					relatedDescriptions.add(split[3]);
				}
				else if (split.length == 3) {
					relatedTitles.add(split[2]);
					relatedDescriptions.add("");
				}
				else {
					relatedTitles.add("");
					relatedDescriptions.add("");
				}
				
				index++;				
				line = br.readLine();
			}
			
			counts.add(Integer.toString(index));
			br.close();
		}		
		
		Map<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		map.put("counts", counts);
		map.put("titles", relatedTitles);
		map.put("descriptions", relatedDescriptions);
		return map;		
	}
	
//	public ArrayList<String> retrieveRelatedRelatedVideos(String type, String title, int numberOfSearchResults, int numberOfRelatedVideos) throws IOException {
//		ArrayList<String> relatedVideos = new ArrayList<String>();
//		ArrayList<String> temp = getVideoIds(PATH + type + "/" + Util.correctPath(title) + ".txt", numberOfSearchResults);
//		//TODO: possibly improve this, when for every related video the related videos are downloaded, will be very slow
//		String videoId = temp.get(0);
//		ArrayList<String> videoIds = getVideoIds(PATH + type + "/" + Util.correctPath(title) + "/" + Util.correctPath(videoId) + ".txt", numberOfSearchResults);
//		
//		for (String id : videoIds) {
//			FileReader fr = new FileReader(new File(PATH + type + "/" + Util.correctPath(title) + "/" + Util.correctPath(videoId) + "/" + id + ".txt"));		
//			BufferedReader br = new BufferedReader(fr);
//			
//			String line = br.readLine();
//			while (line != null) {
//				//TODO: include descriptions
//				if (line.contains("Title")) {
//					relatedVideos.add(line.replace("Title: ", ""));
//				}
//				line = br.readLine();
//			}
//			
//			br.close();
//		}
//		
//		return relatedVideos;		
//	}

	private ArrayList<String> getVideoIds(String path, int numberOfSearchResults) throws IOException {
		FileReader fr = new FileReader(new File(path));		
		BufferedReader br = new BufferedReader(fr);
		ArrayList<String> ids = new ArrayList<String>();
		
		String line = br.readLine();
		int index = 0;
		while (line != null && index < numberOfSearchResults) {
			String[] res = line.split(",");
			//Don't add the headers
			if (!(res[1].contains("Video Id"))) {
				ids.add(res[1]);
				index++;
			}
			line = br.readLine();
		}
		
		br.close();
		return ids;
	}
}
