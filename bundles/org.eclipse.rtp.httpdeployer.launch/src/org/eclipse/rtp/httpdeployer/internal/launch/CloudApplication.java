/*******************************************************************************
 * Copyright (c) 2011 Sebastian Schmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Sebastian Schmidt - initial API and implementation
 ******************************************************************************/
package org.eclipse.rtp.httpdeployer.internal.launch;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.rtp.httpdeployer.launch.CloudApplicationLaunchConfig;
import org.eclipse.rtp.httpdeployer.launch.utils.CloudApplicationLaunchUtils;

public class CloudApplication implements ILaunchConfigurationDelegate {

	private static final String HTTP_DEPLOYER_JOB_NAME = "Http Deployer Job";

	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {
		HttpPublishConfig deployerConfiguration = createDeployerConfiguration(configuration);
		HttpPublishOperation operation = createOperation(monitor, deployerConfiguration);
		IStatus status = operation.run(monitor);
		if (!status.isOK()) {
			throw new CoreException(status);
		}
	}

	protected HttpPublishConfig createDeployerConfiguration(ILaunchConfiguration configuration) throws CoreException {
		CloudApplicationLaunchConfig launchConfiguration = new CloudApplicationLaunchConfig(configuration);
		String featureId = launchConfiguration.getFeatureId();
		String featureVersion = launchConfiguration.getFeatureVersion();
		String serverUri = launchConfiguration.getServerUri();
		HttpPublishConfig deployerConfiguration = HttpPublishConfig.createDefaultConfiguration(serverUri);
		deployerConfiguration.setItems(new Object[] { CloudApplicationLaunchUtils.getFeatureModel(featureId, featureVersion) });
		return deployerConfiguration;
	}

	protected HttpPublishOperation createOperation(IProgressMonitor monitor, HttpPublishConfig config) {
		HttpPublishOperation job = new HttpPublishOperation(config, HTTP_DEPLOYER_JOB_NAME);
		job.setUser(true);
		job.setRule(ResourcesPlugin.getWorkspace().getRoot());
		return job;
	}
}
