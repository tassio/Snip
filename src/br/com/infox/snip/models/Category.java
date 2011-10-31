package br.com.infox.snip.models;

public class Category {
	
	private Long idCategory;
	private String name;
	
	public void setIdCategory(Long idCategory) {
		this.idCategory = idCategory;
	}
	public Long getIdCategory() {
		return idCategory;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
}
