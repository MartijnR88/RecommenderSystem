package nl.wisdelft.martijn;

public class Util {
	public static String correctPath(String path) {
		path = path.replace('#', ' ');
		path = path.replace('%', ' ');
		path = path.replace('&', ' ');
		path = path.replace('{', ' ');
		path = path.replace('}', ' ');
		path = path.replace('\"', ' ');
		path = path.replace('<', ' ');
		path = path.replace('>', ' ');
		path = path.replace('*', ' ');
		path = path.replace('?', ' ');
		path = path.replace('/', ' ');
		path = path.replace('$', ' ');
		path = path.replace('!', ' ');
		path = path.replace('\'', ' ');
		path = path.replace('\"', ' ');
		path = path.replace(':', ' ');
		path = path.replace('@', ' ');
		path = path.replace('+', ' ');
		path = path.replace('`', ' ');
		path = path.replace('|', ' ');
		path = path.replace('=', ' ');
		// Removes all spaces
		path = path.replaceAll("\\s+", "");

		return path;
	}
}