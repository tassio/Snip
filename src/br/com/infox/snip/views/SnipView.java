package br.com.infox.snip.views;

import java.sql.SQLException;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import br.com.infox.snip.dao.CategoryDao;
import br.com.infox.snip.dao.SnippetDao;
import br.com.infox.snip.models.Category;
import br.com.infox.snip.models.Snippet;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class SnipView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "br.com.infox.snip.views.SnipView";

	private TreeViewer viewer;
	private DrillDownAdapter drillDownAdapter;
	private Action doubleClickAction;
	private Action refreshAction;
	private Action addSnippetAction;
	private Action addCategoryAction;
	private Action editAction;
	private Action removeAction;

	/**
	 * The constructor.
	 */
	public SnipView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		drillDownAdapter = new DrillDownAdapter(viewer);
		viewer.setContentProvider(new ViewContentProvider(this));
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setSorter(new NameSorter());
		viewer.setInput(getViewSite());
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		bars.getToolBarManager().add(refreshAction);
		bars.getToolBarManager().add(addSnippetAction);
		bars.getToolBarManager().add(editAction);
		bars.getToolBarManager().add(removeAction);
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				SnipView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(addCategoryAction);
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void makeActions() {
		makeDoubleClickAction();
		
		makeRefreshAction();
		
		makeAddSnippetAction();
		
		makeAddCategoryAction();
		
		makeEditAction();
		
		makeRemoveAction();
		
	}

	private void makeRemoveAction() {
		removeAction = new Action() {
			@Override
			public void run() {
				SnippetTreeObject snippetTreeObject = getSelectedSnippet();
				if (snippetTreeObject != null) {
					SnippetDao dao = new SnippetDao();
					try {
						if (MessageDialog.openConfirm(getViewSite().getShell(), "", "Deseja remover o snippet ?")) {
							dao.remove(snippetTreeObject.getSnippet());
							MessageDialog.openInformation(getViewSite().getShell(), "", "Snippet removido com sucesso!");	
						}
					} catch (SQLException e) {
						MessageDialog.openError(getViewSite().getShell(), "Erro ao remover", e.getMessage());
						e.printStackTrace();
					}
				} else {
					CategoryTreeParent categoryTreeParent = getSelectedCategory();
					if (categoryTreeParent != null) {
						CategoryDao dao = new CategoryDao();
						try {
							if (MessageDialog.openConfirm(getViewSite().getShell(), "", "Deseja remover a categoria (e todos os snippets pertencentes a ela) ?")) {
								dao.remove(categoryTreeParent.getCategory());
								MessageDialog.openInformation(getViewSite().getShell(), "", "Categoria removida com sucesso!");
							}
						} catch (SQLException e) {
							MessageDialog.openError(getViewSite().getShell(), "Erro ao remover", e.getMessage());
							e.printStackTrace();
						}
					}
				}
			}
		};
		removeAction.setText("Remover");
		removeAction.setToolTipText("Remover");
		removeAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
	}

	private void makeEditAction() {
		editAction = new Action() {
			@Override
			public void run() {
				SnippetTreeObject snippetTreeObject = getSelectedSnippet();
				if (snippetTreeObject != null) {
					Snippet snippet = snippetTreeObject.getSnippet();
					new SnippetDialog(getViewSite().getShell(), snippet).open();
				} else {
					CategoryTreeParent categoryTreeParent = getSelectedCategory();
					if (categoryTreeParent != null) {
						Category category = categoryTreeParent.getCategory();
						new CategoryDialog(getViewSite().getShell(), category).open();
					}
				}
			}
		};
		editAction.setText("Editar");
		editAction.setToolTipText("Editar snippet");
		editAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_SAVE_EDIT));
	}

	private void makeAddCategoryAction() {
		addCategoryAction = new Action() {
			@Override
			public void run() {
				new CategoryDialog(getViewSite().getShell(), new Category()).open();
			}
		};
		addCategoryAction.setText("Adicionar Categoria");
		addCategoryAction.setToolTipText("Adicionar Categoria");
	}

	private void makeAddSnippetAction() {
		addSnippetAction = new Action() {
			@Override
			public void run() {
				CategoryTreeParent categoryTreeParent = getSelectedCategory();
				if (categoryTreeParent != null) {
					Snippet snippet = new Snippet();
					snippet.setCategory(categoryTreeParent.getCategory());
					new SnippetDialog(getViewSite().getShell(), snippet).open();
				}
			}
		};
		addSnippetAction.setText("Adicionar");
		addSnippetAction.setToolTipText("Adicionar snippet");
		addSnippetAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_ADD));
	}

	private void makeRefreshAction() {
		refreshAction = new Action() {
			@Override
			public void run() {
				viewer.getContentProvider().inputChanged(viewer, null, null);
				viewer.refresh();
			}
		};
		refreshAction.setText("Atualizar");
		refreshAction.setToolTipText("Atualizar");
		refreshAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_REDO));
	}

	private void makeDoubleClickAction() {
		doubleClickAction = new Action() {
			public void run() {
				IEditorPart editorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
				if (!(editorPart instanceof ITextEditor)) {
					return;
				}
				ITextEditor editor = (ITextEditor) editorPart;
				ISelectionProvider selectionProvider = editor.getSelectionProvider();
				ISelection selection = selectionProvider.getSelection();
				if (!(selection instanceof ITextSelection)) {
					return;
				}
				
				ITextSelection textSelection = (ITextSelection) selection;
				int offset = textSelection.getOffset();
				IDocumentProvider provider = editor.getDocumentProvider();
				IDocument doc = provider.getDocument(editor.getEditorInput());
				
				Snippet snippet = getSelectedSnippet().getSnippet();
				try {
					doc.replace(offset, 0, snippet.getSnippet());
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
				
			}
		};
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
	
	private SnippetTreeObject getSelectedSnippet() {
		ISelection selection = viewer.getSelection();
		Object selected = ((IStructuredSelection) selection).getFirstElement();
		return selected instanceof SnippetTreeObject ? (SnippetTreeObject) selected : null;
	}
	
	private CategoryTreeParent getSelectedCategory() {
		ISelection selection = viewer.getSelection();
		Object selected = ((IStructuredSelection) selection).getFirstElement();
		return selected instanceof CategoryTreeParent ? (CategoryTreeParent) selected : null;
	}
}