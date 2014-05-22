package nl.wisdelft.martijn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * In this class a YouTubeMovie is reconstructed from a text file
 * @author af13020
 *
 */

public class YouTubeMovie {	
	private static final String PATH = "D:/workspace/YouTubeAPI/Dataset/";
	
	public YouTubeMovie() throws IOException {
	}
			
	public ArrayList<String> retrieveSearchResults(String type, String title) throws IOException {
		FileReader fr = new FileReader(new File(PATH + type + "/" + Util.correctPath(title) + ".txt"));		
		BufferedReader br = new BufferedReader(fr);
		ArrayList<String> searchResults = new ArrayList<String>();
		
		String line = br.readLine();
		while (line != null) {
			if (line.contains("Title")) {
				searchResults.add(line.replace("Title: ", ""));
			}
			line = br.readLine();
		}
		
		br.close();
		return searchResults;
	}
	
	public ArrayList<String> retrieveRelatedVideos(String type, String title) throws IOException {
		ArrayList<String> relatedVideos = new ArrayList<String>();
		ArrayList<String> videoIds = getVideoIds(PATH + type + "/" + Util.correctPath(title) + ".txt");
		
		for (String id : videoIds) {
			FileReader fr = new FileReader(new File(PATH + type + "/" + Util.correctPath(title) + "/" + Util.correctPath(id) + ".txt"));		
			BufferedReader br = new BufferedReader(fr);
			
			String line = br.readLine();
			while (line != null) {
				if (line.contains("Title")) {
					relatedVideos.add(line.replace("Title: ", ""));
				}
				line = br.readLine();
			}
			
			br.close();
		}
		
		return relatedVideos;		
	}
	
	public ArrayList<String> retrieveRelatedRelatedVideos(String type, String title) throws IOException {
		ArrayList<String> relatedVideos = new ArrayList<String>();
		ArrayList<String> temp = getVideoIds(PATH + type + "/" + Util.correctPath(title) + ".txt");
		//TODO: possibly improve this, when for every related video the related videos are downloaded, will be very slow
		String videoId = temp.get(0);
		ArrayList<String> videoIds = getVideoIds(PATH + type + "/" + Util.correctPath(title) + "/" + Util.correctPath(videoId) + ".txt");
		
		for (String id : videoIds) {
			FileReader fr = new FileReader(new File(PATH + type + "/" + Util.correctPath(title) + "/" + Util.correctPath(videoId) + "/" + id + ".txt"));		
			BufferedReader br = new BufferedReader(fr);
			
			String line = br.readLine();
			while (line != null) {
				if (line.contains("Title")) {
					relatedVideos.add(line.replace("Title: ", ""));
				}
				line = br.readLine();
			}
			
			br.close();
		}
		
		return relatedVideos;		
	}

	private ArrayList<String> getVideoIds(String path) throws IOException {
		FileReader fr = new FileReader(new File(path));		
		BufferedReader br = new BufferedReader(fr);
		ArrayList<String> ids = new ArrayList<String>();
		
		String line = br.readLine();
		while (line != null) {
			if (line.contains("Video Id")) {
				ids.add(line.replace("Video Id: ", ""));
			}
			line = br.readLine();
		}
		
		br.close();
		return ids;
	}
}
