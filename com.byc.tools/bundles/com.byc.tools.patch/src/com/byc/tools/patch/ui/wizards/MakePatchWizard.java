package com.byc.tools.patch.ui.wizards;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;

import com.byc.tools.patch.exceptions.ExceptionStatusUtils;
import com.byc.tools.patch.log.ExceptionLogger;
import com.byc.tools.patch.model.PatchInfo;
import com.byc.tools.patch.services.ChangeVersionService;
import com.byc.tools.patch.services.impls.ChangeVersionServiceImpl;

/**
 * 
 * @author ycbai
 *
 */
public class MakePatchWizard extends Wizard {

	private PatchInfo patchInfo;

	public MakePatchWizard() {
		super();
		patchInfo = new PatchInfo();
		setNeedsProgressMonitor(true);
	}

	@Override
	public String getWindowTitle() {
		return "Make Patch";
	}

	@Override
	public void addPages() {
		ChangeVersionPage changeVersionPage = new ChangeVersionPage(patchInfo);
		addPage(changeVersionPage);
	}

	@Override
	public boolean performFinish() {
		try {
			this.getContainer().run(true, true, new IRunnableWithProgress() {

				@Override
				public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					monitor.beginTask("Making patch...", 1000);
					try {
						ChangeVersionService service = new ChangeVersionServiceImpl();
						service.doChangeVersion(patchInfo, monitor);
					} catch (Exception ex) {
						throw new InvocationTargetException(ex);
					} finally {
						monitor.done();
					}
				}

			});
		} catch (Exception e) {
			ExceptionLogger.log(e);
			MessageDialog.openError(getShell(), "Error", "Fail to make the patch. Please check logs to find the causes.");
//			ErrorDialog.openError(getShell(), "Error", "Fail to make the patch. Please check logs to find the causes.",
//					ExceptionStatusUtils.getStatus(e));
			return false;
		}
		return true;
	}

}
