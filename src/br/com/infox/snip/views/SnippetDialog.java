package br.com.infox.snip.views;

import java.sql.SQLException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import br.com.infox.snip.dao.SnippetDao;
import br.com.infox.snip.models.Snippet;

public class SnippetDialog extends Dialog {
	private Object result;
	private Snippet snippet;
	
	private Text txtName;

	public SnippetDialog(Shell parent, Snippet snippet) {
		super(parent);
		this.snippet = snippet;
	}

	public Object open() {
		Shell parent = getParent();
		Shell shell = new Shell(parent, SWT.DIALOG_TRIM);
		shell.setText(snippet.getName());
		createContents(shell);
		//shell.pack();
		shell.open();
		Display display = parent.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		return result;
	}
	
	private void createContents(final Shell parent) {
		parent.setLayout(new RowLayout(SWT.HORIZONTAL));
		txtName = new Text(parent, SWT.NONE);
		txtName.setText(snippet.getName());
		
		Button saveButton = new Button(parent, SWT.PUSH);
		saveButton.setText("Save");
		saveButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SnippetDao dao = new SnippetDao();
				snippet.setName(txtName.getText());
				try {
					dao.update(snippet);
				} catch (SQLException e1) {
					MessageDialog.openError(parent, "Snip View", e1.getMessage());
				}
			}
		});
	}
}
