package br.com.infox.snip.views;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

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
		parent.setLayout(new FormLayout());
		
		Label labelName = new Label(parent, SWT.LEFT);
		labelName.setText("Nome");
		FormData labelNameData = new FormData();
		labelNameData.left = new FormAttachment(0, 5);
		labelNameData.top = new FormAttachment(0, 5);
		labelName.setLayoutData(labelNameData);
		
		txtName = new Text(parent, SWT.SINGLE | SWT.BORDER);
		FormData nameData = new FormData(350, 20);
		nameData.left = new FormAttachment(labelName, 5);
		txtName.setLayoutData(nameData);
		
		Label labelSnippet = new Label(parent, SWT.LEFT);
		labelSnippet.setText("Snippet");
		FormData labelSnippetData = new FormData();
		labelSnippetData.left = new FormAttachment(0, 5);
		labelSnippetData.top = new FormAttachment(txtName, 10);
		labelSnippet.setLayoutData(labelSnippetData);
		
		Text txtSnippet = new Text(parent, SWT.MULTI | SWT.BORDER);
		FormData snippetData = new FormData(750, 500);
		snippetData.left = new FormAttachment(0, 15);
		snippetData.top = new FormAttachment(labelSnippet, 7);
		txtSnippet.setLayoutData(snippetData);
		
		Button cancelButton = new Button(parent, SWT.PUSH);
		cancelButton.setText("Cancelar");
		cancelButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				parent.close();
			}
		});
		FormData cancelData = new FormData(80, 30);
		cancelData.right = new FormAttachment(98);
		cancelData.bottom = new FormAttachment(95);
		cancelButton.setLayoutData(cancelData);
		
		Button saveButton = new Button(parent, SWT.PUSH);
		saveButton.setText("Salvar");
		saveButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageDialog.openInformation(parent, "Teste", "OK");
			}
		});
		FormData saveData = new FormData(80, 30);
		saveData.right = new FormAttachment(cancelButton, -5, SWT.LEFT);
		saveData.bottom = new FormAttachment(cancelButton, 0, SWT.BOTTOM);
		saveButton.setLayoutData(saveData);
	}
}
