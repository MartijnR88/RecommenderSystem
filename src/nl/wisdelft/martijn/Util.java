package nl.wisdelft.martijn;

import java.util.ArrayList;
import java.util.List;

import nl.wisdelft.martijn.Movie.Domain;

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
	
	public static String formatDate(String date) {
		String[] parts = date.split("-");
		String year = parts[0];
		String month = parts[1];
		String day = parts[2].split(" ")[0];

		if (day.startsWith("0")) {
			day = day.substring(1);
		}

		int month_int = Integer.parseInt(month);
		String monthString;
		switch (month_int) {
			case 1:
				monthString = "Januari";
				break;
			case 2:
				monthString = "Februari";
				break;
			case 3:
				monthString = "Maart";
				break;
			case 4:
				monthString = "April";
				break;
			case 5:
				monthString = "Mei";
				break;
			case 6:
				monthString = "Juni";
				break;
			case 7:
				monthString = "Juli";
				break;
			case 8:
				monthString = "Augustus";
				break;
			case 9:
				monthString = "September";
				break;
			case 10:
				monthString = "Oktober";
				break;
			case 11:
				monthString = "November";
				break;
			case 12:
				monthString = "December";
				break;
			default:
				monthString = "Invalid month";
				break;
			}

			String result = day + " " + monthString + " " + year;

		return result;
	}
	
	public static String translateDomain(Domain domain) {
		String result = "";
		if (domain.equals(Movie.Domain.DOCUMENTARY))
			result = "documentaire";
		else if (domain.equals(Movie.Domain.EVENT_COVERAGE))
			result = "evenement";
		else if (domain.equals(Movie.Domain.FILM))
			result = "film";
		else if (domain.equals(Movie.Domain.NEWS))
			result = "nieuws";
		else if (domain.equals(Movie.Domain.SERIES))
			result = "serie";
		else if (domain.equals(Movie.Domain.VIDEO_BLOG))
			result = "video blog";
		return result;
	}
	
	public static String translateDomain2(Domain domain) {
		String result = "";
		if (domain.equals(Movie.Domain.DOCUMENTARY))
			result = "Documentaire/reportage";
		else if (domain.equals(Movie.Domain.EVENT_COVERAGE))
			result = "Evenement-verslag";
		else if (domain.equals(Movie.Domain.FILM))
			result = "Film";
		else if (domain.equals(Movie.Domain.NEWS))
			result = "Nieuws item";
		else if (domain.equals(Movie.Domain.SERIES))
			result = "Serie";
		else if (domain.equals(Movie.Domain.VIDEO_BLOG))
			result = "Video blog";
		return result;
	}
}