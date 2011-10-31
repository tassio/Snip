package br.com.infox.snip.views;

import br.com.infox.snip.models.Snippet;

public class SnippetTreeObject extends TreeObject {

	private Snippet snippet;
	
	public SnippetTreeObject(String name, Snippet snippet) {
		super(name);
		this.snippet = snippet;
	}
	
	public Snippet getSnippet() {
		return snippet;
	}
}
