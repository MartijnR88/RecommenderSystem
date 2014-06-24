package nl.wisdelft.martijn;

public class Movie {
	private int id;
	private String title;
	private String description;
	public enum Domain {
		NEWS, FILM, DOCUMENTARY, VIDEO_BLOG, EVENT_COVERAGE, SERIES
	}
	private Domain domain;
	
	public Movie() {
		this.setId(0);
		this.setTitle("");
		this.setDescription("");
		this.setDomain(null);
	}
	
	public Movie(int id, String title, String description, Domain domain) {
		this.setId(id);
		this.setTitle(title);
		this.setDescription(description);
		this.setDomain(domain);
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public Domain getDomain() {
		return domain;
	}

	public void setDomain(Domain domain) {
		this.domain = domain;
	}	
}
