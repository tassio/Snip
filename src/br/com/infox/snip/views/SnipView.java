package br.com.infox.snip.views;

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
	private Action addAction;
	private Action editAction;
	private Action removeSnippetAction;
	private Action removeFolderAction;

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
		bars.getToolBarManager().add(addAction);
		bars.getToolBarManager().add(editAction);
		bars.getToolBarManager().add(removeSnippetAction);
		bars.getToolBarManager().add(removeFolderAction);
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
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void makeActions() {
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
				
				Snippet snippet = getSelected().getSnippet();
				try {
					doc.replace(offset, 0, snippet.getSnippet());
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
				
			}
		};
		
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
		
		addAction = new Action() {
			@Override
			public void run() {
			}
		};
		addAction.setText("Adicionar");
		addAction.setToolTipText("Adicionar snippet");
		addAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_ADD));
		
		editAction = new Action() {
			@Override
			public void run() {
				SnippetTreeObject o = getSelected();
				if (o != null) {
					SnippetDialog dialog = new SnippetDialog(getViewSite().getShell(), o.getSnippet());
					dialog.open();
				}
			}
		};
		editAction.setText("Editar");
		editAction.setToolTipText("Editar snippet");
		editAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_SAVE_EDIT));
		
		removeFolderAction = new Action() {
			@Override
			public void run() {
				SnippetTreeObject o = getSelected();
				if (o != null) {
					MessageDialog.openError(getViewSite().getShell(), "Erro", "Operação ainda não implementada");
				}
			}
		};
		removeFolderAction.setText("Remover pasta");
		removeFolderAction.setToolTipText("Remover pasta");
		removeFolderAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_REMOVEALL));
		
		removeSnippetAction = new Action() {
			@Override
			public void run() {
				SnippetTreeObject o = getSelected();
				if (o != null) {
					MessageDialog.openError(getViewSite().getShell(), "Erro", "Operação ainda não implementada");
				}
			}
		};
		removeSnippetAction.setText("Remover snippet");
		removeSnippetAction.setToolTipText("Remover snippet");
		removeSnippetAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
		
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
	
	private SnippetTreeObject getSelected() {
		ISelection selection = viewer.getSelection();
		return (SnippetTreeObject) ((IStructuredSelection) selection).getFirstElement();
	}
}