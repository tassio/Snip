package br.com.infox.snip.views;

import br.com.infox.snip.models.Category;

public class CategoryTreeParent extends TreeParent {

	private Category category;
	
	public CategoryTreeParent(String name, Category category) {
		super(name);
		this.category = category;
	}

	public Category getCategory() {
		return category;
	}
}
