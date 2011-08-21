/*******************************************************************************
 * Copyright (c) 2011 Sebastian Schmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Sebastian Schmidt - initial API and implementation
 ******************************************************************************/
package org.eclipse.rtp.httpdeployer.internal.ui.launcher;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.pde.internal.core.ifeature.IFeatureModel;
import org.eclipse.pde.internal.ui.SWTFactory;
import org.eclipse.rtp.httpdeployer.launch.CloudApplicationLaunchConfig;
import org.eclipse.rtp.httpdeployer.launch.CloudApplicationLaunchConfigValidator;
import org.eclipse.rtp.httpdeployer.launch.utils.CloudApplicationLaunchUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

@SuppressWarnings("restriction")
public class CloudApplicationServerTab extends AbstractLaunchConfigurationTab {

	private Text featureText;
	private IFeatureModel feature;
	private Composite comp;
	private Text server;

	@Override
	public void createControl(final Composite parent) {
		comp = new Composite(parent, SWT.NONE);
		setControl(comp);
		comp.setLayout(new GridLayout(1, true));
		comp.setFont(parent.getFont());

		createFeatureSelectBox();
		createServerBox();
	}

	private void createServerBox() {
		String text = "Server";
		Group group = SWTFactory.createGroup(comp, text, 2, 1, GridData.FILL_HORIZONTAL);
		server = SWTFactory.createSingleText(group, 1);
		server.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				setDirty(true);
				updateLaunchConfigurationDialog();
			}
		});
	}

	private void createFeatureSelectBox() {
		String text = "Select Feature";
		Group group = SWTFactory.createGroup(comp, text, 2, 1, GridData.FILL_HORIZONTAL);
		featureText = SWTFactory.createSingleText(group, 1);
		featureText.setEditable(false);

		Button featureSearchButton = createPushButton(group, "Select Feature", null);
		featureSearchButton.addSelectionListener(new FeatureSelectionListener(this));
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy rootConfiguration) {
		//CloudApplicationLaunchConfig config = new CloudApplicationLaunchConfig(rootConfiguration);
		//config.setServerUri("http://localhost:8080/");
	}

	@Override
	public void initializeFrom(ILaunchConfiguration rootConfiguration) {
		CloudApplicationLaunchConfig config = new CloudApplicationLaunchConfig(rootConfiguration);
		try {
			if (!config.getFeatureId().equals("")) {
				featureText.setText(config.getFeatureId() + " (" + config.getFeatureVersion() + ")");
				feature = CloudApplicationLaunchUtils.getFeatureModel(config.getFeatureId(), config.getFeatureVersion());
			}
			server.setText(config.getServerUri());
		} catch (CoreException e) {
			DebugPlugin.log(e);
		}
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy workingCopy) {
		CloudApplicationLaunchConfig config = new CloudApplicationLaunchConfig(workingCopy);

		if (feature != null && feature.getFeature() != null) {
			config.setFeatureId(feature.getFeature().getId());
			config.setFeatureVersion(feature.getFeature().getVersion());
		}

		config.setServerUri(server.getText());
	}

	@Override
	public boolean isValid(ILaunchConfiguration launchConfig) {
		try {
			return CloudApplicationLaunchConfigValidator.validate(launchConfig);
		} catch (CoreException e) {
			return false;
		}
	}

	@Override
	public String getName() {
		return "Server";
	}

	public Display getDisplay() {
		return comp.getDisplay();
	}

	public Shell getShell() {
		return comp.getShell();
	}

	public void setFeature(IFeatureModel iFeatureModel) {
		setDirty(true);
		feature = iFeatureModel;
		featureText.setText(feature.getFeature().getId() + " (" + feature.getFeature().getVersion() + ")");
		updateLaunchConfigurationDialog();
	}
}
