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

import br.com.infox.snip.dao.CategoryDao;
import br.com.infox.snip.models.Category;

public final class CategoryDialog extends Dialog {
	private Object result;
	private Category category;

	public CategoryDialog(Shell parent, Category category) {
		super(parent);
		this.category = category;
	}

	public Object open() {
		Shell parent = getParent();
		final Shell shell = new Shell(parent, SWT.DIALOG_TRIM);
		shell.setText(category.getName() == null ? "Nova Categoria" : category.getName());
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
				CategoryDao dao = new CategoryDao();
				try {
					category.setName(txtName.getText());
					if (category.getIdCategory() == null) {
						dao.insert(category);
					} else {
						dao.update(category);
					}
					MessageDialog.openInformation(parent, "", "Categoria salva com sucesso");
				} catch (SQLException e1) {
					MessageDialog.openError(parent, "Erro ao salvar a categoria", e1.getMessage());
					e1.printStackTrace();
				}
			}
		});
		FormData saveData = new FormData(80, 30);
		saveData.right = new FormAttachment(cancelButton, -5, SWT.LEFT);
		saveData.bottom = new FormAttachment(cancelButton, 0, SWT.BOTTOM);
		saveButton.setLayoutData(saveData);
		
		txtName.setText(category.getName());
	}
}
