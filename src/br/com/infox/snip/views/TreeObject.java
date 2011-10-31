package br.com.infox.snip.views;

import org.eclipse.core.runtime.IAdaptable;

/*
 * The content provider class is responsible for
 * providing objects to the view. It can wrap
 * existing objects in adapters or simply return
 * objects as-is. These objects may be sensitive
 * to the current input of the view, or ignore
 * it and always show the same content 
 * (like Task List, for example).
 */
 
public class TreeObject implements IAdaptable {
	private String name;
	private TreeParent parent;
	
	public TreeObject(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setParent(TreeParent parent) {
		this.parent = parent;
	}
	
	public TreeParent getParent() {
		return parent;
	}
	
	public String toString() {
		return getName();
	}
	
	@SuppressWarnings("unchecked")
	public Object getAdapter(Class key) {
		return null;
	}
}

