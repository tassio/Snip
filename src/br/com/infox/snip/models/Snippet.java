package br.com.infox.snip.models;

public class Snippet {
	
	private Long idSnippet;
	private String name;
	private String snippet;
	private Category category;
	
	public Long getIdSnippet() {
		return idSnippet;
	}
	
	public void setIdSnippet(Long idSnippet) {
		this.idSnippet = idSnippet;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getSnippet() {
		return snippet;
	}
	
	public void setSnippet(String snippet) {
		this.snippet = snippet;
	}	
	
	public Category getCategory() {
		return category;
	}
	
	public void setCategory(Category category) {
		this.category = category;
	}
}
