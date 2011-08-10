package org.eclipse.rtp.httpdeployer.internal.ui.launcher;

import org.eclipse.jface.window.Window;
import org.eclipse.pde.internal.core.PDECore;
import org.eclipse.pde.internal.core.ifeature.IFeatureModel;
import org.eclipse.pde.internal.ui.dialogs.FeatureSelectionDialog;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

@SuppressWarnings("restriction")
public class FeatureSelectionListener implements SelectionListener {

	private final CloudApplicationServerTab tab;

	public FeatureSelectionListener(CloudApplicationServerTab tab) {
		this.tab = tab;
	}

	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void widgetSelected(SelectionEvent e) {
		BusyIndicator.showWhile(tab.getDisplay(), new Runnable() {

			public void run() {
				IFeatureModel[] models = PDECore.getDefault().getFeatureModelManager().getModels();
				FeatureSelectionDialog dialog = new FeatureSelectionDialog(tab.getShell(), models, false);
				if (dialog.open() == Window.OK) {
					Object[] result = dialog.getResult();
					if (result.length == 1) {
						tab.setFeature((IFeatureModel) result[0]);
					}
				}
			}
		});
	}
}