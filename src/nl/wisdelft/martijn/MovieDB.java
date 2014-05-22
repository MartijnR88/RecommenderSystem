package nl.wisdelft.martijn;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import java.util.Iterator;

/**
 * @author
 */
public class MovieDB implements Iterable<Movie> {
    private Long2ObjectMap<Movie> movies = new Long2ObjectOpenHashMap<Movie>();

    public void addMovie(Movie m) {
        movies.put(m.getId(), m);
    }

    public Movie getMovie(long id) {
        return movies.get(id);
    }

    public Iterator<Movie> iterator() {
        return movies.values().iterator();
    }
}
