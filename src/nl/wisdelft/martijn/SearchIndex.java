package nl.wisdelft.martijn;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queries.mlt.MoreLikeThis;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;

public class SearchIndex {
	private Directory indexDir;
	private Analyzer analyzer;

	public SearchIndex(Analyzer analyzer, Directory indexDir) {
		this.analyzer = analyzer;
		this.indexDir = indexDir;
	}

	public Map<Movie, Float> findSimilar(String title, String description,
			int totalHits) throws IOException {
		Map<Movie, Float> similar = new HashMap<Movie, Float>();
		IndexReader reader = DirectoryReader.open(indexDir);
		IndexSearcher indexSearcher = new IndexSearcher(reader);

		MoreLikeThis mlt = new MoreLikeThis(reader);
		mlt.setMinTermFreq(0);
		mlt.setMinDocFreq(0);
		mlt.setFieldNames(new String[] { "title", "content" });
		mlt.setAnalyzer(analyzer);

		Reader titles = new StringReader(title);
		Query query_titles = mlt.like(titles, "title");
		TopDocs topDocs = indexSearcher.search(query_titles, totalHits);
		Reader descriptions = new StringReader(description);
		Query query_descriptions = mlt.like(descriptions, "content");
		TopDocs topDocs2 = indexSearcher.search(query_descriptions, totalHits);

		for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
			Document aSimilar = indexSearcher.doc(scoreDoc.doc);
			String id = aSimilar.get("id");
			//System.out.println(indexSearcher.explain(query_titles, scoreDoc.doc));
			// String similarTitle = aSimilar.get("title");
			// String similarContent = aSimilar.get("content");
			float score = scoreDoc.score;

			Movie movie = CreateRecommendations.movies.getMovie(Integer
					.parseInt(id));
			similar.put(movie, score);
		}

		for (ScoreDoc scoreDoc : topDocs2.scoreDocs) {
			Document aSimilar = indexSearcher.doc(scoreDoc.doc);
			String id = aSimilar.get("id");
			// String similarTitle = aSimilar.get("title");
			// String similarContent = aSimilar.get("content");
			float score = scoreDoc.score;

			Movie movie = CreateRecommendations.movies.getMovie(Integer
					.parseInt(id));
			if (!similar.containsKey(movie))
				similar.put(movie, score);
			else {
				float temp = similar.get(movie);
				if (score > temp) {
					similar.remove(movie);
					similar.put(movie, score);
				}
			}
		}

		MovieValueComparator vc = new MovieValueComparator(similar);
		TreeMap<Movie, Float> sorted_map = new TreeMap<Movie, Float>(vc);
		sorted_map.putAll(similar);

		Map<Movie, Float> new_similar = new HashMap<Movie, Float>();
		int index = 0;
		for (Map.Entry<Movie, Float> entry : sorted_map.entrySet()) {
			if (index < totalHits) {
				Movie key = entry.getKey();
				Float value = entry.getValue();
				new_similar.put(key, value);
				index++;
			}
		}
		return new_similar;
	}
}

class MovieValueComparator implements Comparator<Movie> {

	Map<Movie, Float> base;

	public MovieValueComparator(Map<Movie, Float> base) {
		this.base = base;
	}

	// Note: this comparator imposes orderings that are inconsistent with
	// equals.
	public int compare(Movie a, Movie b) {
		if (base.get(a) >= base.get(b)) {
			return -1;
		} else {
			return 1;
		} // returning 0 would merge keys
	}
}
