package br.com.infox.snip.views;

import java.sql.SQLException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import br.com.infox.snip.dao.SnippetDao;
import br.com.infox.snip.models.Snippet;

public final class SnippetDialog extends Dialog {
	private Object result;
	private Snippet snippet;

	public SnippetDialog(Shell parent, Snippet snippet) {
		super(parent);
		this.snippet = snippet;
	}

	public Object open() {
		Shell parent = getParent();
		final Shell shell = new Shell(parent, SWT.DIALOG_TRIM);
		shell.setText(snippet.getName() == null ? "Novo Snippet" : snippet.getName());
		createContents(shell);
		shell.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				e.doit = MessageDialog.openConfirm(shell, "Confirme", "Deseja fechar a janela?");
			}
		});
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
		
		final Text txtName = new Text(parent, SWT.SINGLE | SWT.BORDER);
		FormData nameData = new FormData(350, 20);
		nameData.left = new FormAttachment(labelName, 5);
		txtName.setLayoutData(nameData);
		
		Label labelSnippet = new Label(parent, SWT.LEFT);
		labelSnippet.setText("Snippet");
		FormData labelSnippetData = new FormData();
		labelSnippetData.left = new FormAttachment(0, 5);
		labelSnippetData.top = new FormAttachment(txtName, 10);
		labelSnippet.setLayoutData(labelSnippetData);
		
		final Text txtSnippet = new Text(parent, SWT.MULTI | SWT.BORDER);
		FormData snippetData = new FormData(750, 500);
		snippetData.left = new FormAttachment(0, 15);
		snippetData.top = new FormAttachment(labelSnippet, 7);
		txtSnippet.setLayoutData(snippetData);
		
		Button cancelButton = new Button(parent, SWT.PUSH);
		cancelButton.setText("Cancelar");
		cancelButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (MessageDialog.openConfirm(parent, "Confirme", "Deseja fechar a janela?")) {
					parent.close();
				}
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
				SnippetDao dao = new SnippetDao();
				try {
					snippet.setName(txtName.getText());
					snippet.setSnippet(txtSnippet.getText());
					if (snippet.getIdSnippet() == null) {
						dao.insert(snippet);
					} else {
						dao.update(snippet);
					}
					MessageDialog.openInformation(parent, "", "Snippet salvo com sucesso");
				} catch (SQLException e1) {
					MessageDialog.openError(parent, "Erro ao salvar o snippet", e1.getMessage());
					e1.printStackTrace();
				}
			}
		});
		FormData saveData = new FormData(80, 30);
		saveData.right = new FormAttachment(cancelButton, -5, SWT.LEFT);
		saveData.bottom = new FormAttachment(cancelButton, 0, SWT.BOTTOM);
		saveButton.setLayoutData(saveData);
		
		txtName.setText(snippet.getName());
		txtSnippet.setText(snippet.getSnippet());
	}
}
