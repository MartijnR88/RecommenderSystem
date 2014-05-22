package nl.wisdelft.martijn;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

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
	
	public Map<Movie, Float> findSimilar(String searchForSimilar, int totalHits) throws IOException {
		Map<Movie, Float> similar = new HashMap<Movie, Float>();
		IndexReader reader = DirectoryReader.open(indexDir);
		IndexSearcher indexSearcher = new IndexSearcher(reader);
		
		MoreLikeThis mlt = new MoreLikeThis(reader);
		mlt.setMinTermFreq(0);
		mlt.setMinDocFreq(0);
		mlt.setFieldNames(new String[]{"title", "content"});
		mlt.setAnalyzer(analyzer);
	
		Reader sReader = new StringReader(searchForSimilar);
		Query query = mlt.like(sReader, null);
		
		TopDocs topDocs = indexSearcher.search(query, totalHits);
		for ( ScoreDoc scoreDoc : topDocs.scoreDocs ) {
			Document aSimilar = indexSearcher.doc( scoreDoc.doc );
			String id = aSimilar.get("id");
			String similarTitle = aSimilar.get("title");
			String similarContent = aSimilar.get("content");
			float score = scoreDoc.score;
						
			Movie movie = new Movie(Integer.parseInt(id), similarTitle, similarContent);
			similar.put(movie, score);
		}
		
		return similar;
	}
}
