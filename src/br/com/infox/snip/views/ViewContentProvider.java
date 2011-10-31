package br.com.infox.snip.views;

import java.sql.SQLException;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import br.com.infox.snip.dao.CategoryDao;
import br.com.infox.snip.models.Category;
import br.com.infox.snip.models.Snippet;

public class ViewContentProvider implements IStructuredContentProvider, ITreeContentProvider {
	private TreeParent invisibleRoot;
	private SnipView view;
	
	public ViewContentProvider(SnipView view) {
		this.view = view;
		invisibleRoot = new TreeParent("");
	}

	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		clear();
		initialize();
	}

	public void dispose() {
	}

	public Object[] getElements(Object parent) {
		if (parent.equals(view.getViewSite())) {
			if (!invisibleRoot.hasChildren())
				initialize();
			return getChildren(invisibleRoot);
		}
		return getChildren(parent);
	}

	public Object getParent(Object child) {
		if (child instanceof TreeObject) {
			return ((TreeObject) child).getParent();
		}
		return null;
	}

	public Object[] getChildren(Object parent) {
		if (parent instanceof TreeParent) {
			return ((TreeParent) parent).getChildren();
		}
		return new Object[0];
	}

	public boolean hasChildren(Object parent) {
		if (parent instanceof TreeParent)
			return ((TreeParent) parent).hasChildren();
		return false;
	}

	private void initialize() {
		CategoryDao categoryDao = new CategoryDao();
		List<Category> categories;
		try {
			categories = categoryDao.list();
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}

		for (Category category : categories) {
			TreeParent parent = new TreeParent(category.getName());
			List<Snippet> snippets;
			try {
				snippets = categoryDao.findSnippets(category);
			} catch (SQLException e) {
				e.printStackTrace();
				continue;
			}

			for (Snippet snippet : snippets) {
				TreeObject o = new SnippetTreeObject(snippet.getName(), snippet);
				parent.addChild(o);
			}

			invisibleRoot.addChild(parent);
		}
	}
	
	private void clear() {
		for (TreeObject child : invisibleRoot.getChildren()) {
			invisibleRoot.removeChild(child);
		}
	}
}