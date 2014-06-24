package nl.wisdelft.martijn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import nl.wisdelft.martijn.Movie.Domain;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class FinalTestToJSON {
	private static String PATH;
	private static Dataset dataset;
	private static RetrieveDomains domains;

	public static void main(String[] args) throws IOException, XPathExpressionException, SAXException, ParserConfigurationException, TransformerException, JSONException, ClassNotFoundException, SQLException {
		//PATH = "C:/Users/Martijn/Dropbox/Public/Master Thesis/Results/Recommendations/";
		PATH="";
		dataset = new Dataset();
		domains = new RetrieveDomains();

		ArrayList<String> videos = new ArrayList<String>();
		videos.add("1493");
		videos.add("1885");
		videos.add("15581");
		videos.add("28984");
		videos.add("151225");
		videos.add("155841");
		videos.add("612014");
		videos.add("617569");
		videos.add("630457");
		videos.add("639267");
		videos.add("611962");
		videos.add("613631");
		videos.add("614834");
		videos.add("631648");
		videos.add("155988");
		videosToJSON(videos);
	}

	private static void videosToJSON(ArrayList<String> videos) throws IOException, XPathExpressionException, SAXException, ParserConfigurationException, TransformerException, JSONException, ClassNotFoundException, SQLException {		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JSONObject myJSON = new JSONObject();
		List<String> list = new ArrayList<String>();

		for (String video : videos) {
			ArrayList<String> files = new ArrayList<String>();
			files.add(PATH + "video" + video + "_strategy1.csv");
			files.add(PATH + "video" + video + "_strategy2.csv");
			files.add(PATH + "video" + video + "_strategy3.csv");
			files.add(PATH + "video" + video + "_strategy4.csv");
			files.add(PATH + "video" + video + "_strategy5.csv");
			File file = null;
			JSONObject myJSON_list = new JSONObject();
			myJSON_list.put("id", video);
			for (int i = 0; i < files.size(); i++) {
				file = new File(files.get(i));

				FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);
				//Don't read headers
				String line = br.readLine();

				JSONArray array = new JSONArray();
				while ((line = br.readLine()) != null) {
					String[] split = line.split(",");
					String videoId = split[1];
					String title = getTitle(videoId);
					byte[] bytes = title.getBytes("UTF-8");
					title = new String(bytes, "UTF-8");
					String thumbnail = getThumbnail(videoId);
					JSONObject obj = new JSONObject();
					obj.put("title", title.replace("\"", "").replace("\'", ""));
//					obj.put("description", getDescription(videoId).replace("\"","").replace("\'", ""));
					obj.put("domain", getDomain(videoId));
					obj.put("image", thumbnail);
					array.put(obj);
				}
				for (int j = array.length(); j < 5; j++) {
					JSONObject obj = new JSONObject();
					obj.put("title", "-");
					obj.put("domain", "-");
					obj.put("image", getThumbnail(""));
					array.put(obj);
				}
				myJSON_list.put("list" + (i+1), array);
				br.close();
			}
			myJSON.put("id", video);
			myJSON.put("title", getTitle(video));
			myJSON.put("domain", getDomain(video));
			myJSON.put("webm", getWebm(video));
			myJSON.put("ogv", getOgv(video));
			myJSON.put("mp4", getMp4(video));
			myJSON.put("list", myJSON_list);
			
			if (!list.contains(gson.toJson(myJSON)))
				list.add(myJSON.toString());
		}
		
		System.out.println(list);
		
		try {
			FileWriter fw = new FileWriter("strategies.json");
			fw.write(list.toString());
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
//	private static String getDescription(String id) throws XPathExpressionException, SAXException, IOException, ParserConfigurationException, TransformerException, ClassNotFoundException, SQLException {
//		Subsetdataset d = new Subsetdataset();
//		String result = d.getDescription("SELECT body,source FROM oi_mediafragments_table WHERE source = 'oaiopenimages.eu" + id + "'");
//		return result;
//	}

	private static String getDomain(String video) {
		return Util.translateDomain2(domains.getDomain(video));
	}
	
	private static String getMp4(String video) {
		return "";
	}

	private static String getOgv(String video) {
		return "";
	}

	private static String getWebm(String id) throws XPathExpressionException {
		return dataset.getMedium(id);
	}
	
	private static String getTitle(String id) throws XPathExpressionException, SAXException, IOException, ParserConfigurationException, TransformerException {
		return dataset.getTitle(id);
	}
	
	private static String getThumbnail(String id) {
		String path = "https://dl.dropboxusercontent.com/u/16985363/Master%20Thesis/Results/workdir_thumbnails/";
		String result = "";
		if (id == "") {
			result = path + "novideo.png";
		}
		else {
			result = path + "oaiopenimages.eu" + id + ".mp4.png";
		}
		return result;
	}
}
