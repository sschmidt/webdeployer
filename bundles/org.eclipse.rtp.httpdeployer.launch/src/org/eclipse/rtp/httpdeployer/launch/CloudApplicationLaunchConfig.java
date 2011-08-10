/*******************************************************************************
 * Copyright (c) 2011 Sebastian Schmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Sebastian Schmidt - initial API and implementation
 ******************************************************************************/
package org.eclipse.rtp.httpdeployer.launch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

public class CloudApplicationLaunchConfig {

	public static final String ATTR_PREFIX = "org.eclipse.rtp.httpdeployer.launch.";
	public static final String ATTR_FEATURE_ID = ATTR_PREFIX + "featureId";
	public static final String ATTR_FEATURE_VERSION = ATTR_PREFIX + "featureVersion";
	public static final String ATTR_SERVER_URI = ATTR_PREFIX + "serverUri";

	private final ILaunchConfiguration config;
	private final ILaunchConfigurationWorkingCopy workingCopy;

	public CloudApplicationLaunchConfig(final ILaunchConfiguration config) {
		this.config = config;
		if (config instanceof ILaunchConfigurationWorkingCopy) {
			workingCopy = (ILaunchConfigurationWorkingCopy) config;
		} else {
			workingCopy = null;
		}
	}

	public String getFeatureId() throws CoreException {
		return config.getAttribute(ATTR_FEATURE_ID, "");
	}

	public String getFeatureVersion() throws CoreException {
		return config.getAttribute(ATTR_FEATURE_VERSION, "");
	}

	public void setFeatureId(String featureId) {
		setAttribute(ATTR_FEATURE_ID, featureId);
	}

	public void setFeatureVersion(String featureVersion) {
		setAttribute(ATTR_FEATURE_VERSION, featureVersion);
	}

	protected void setAttribute(String attribute, String value) {
		if (workingCopy != null) {
			workingCopy.setAttribute(attribute, value);
		}
	}

	public String getServerUri() throws CoreException {
		return config.getAttribute(ATTR_SERVER_URI, "");
	}

	public void setServerUri(String server) {
		setAttribute(ATTR_SERVER_URI, server);
	}

}
