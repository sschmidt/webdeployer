/*******************************************************************************
 * Copyright (c) 2011 Sebastian Schmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Sebastian Schmidt - initial API and implementation
 ******************************************************************************/
package org.eclipse.rtp.httpdeployer.launch;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.junit.Test;

public class CloudApplicationLaunchConfigTest {

	private static final String FEATURE_ID = "according to jim";
	private static final String FEATURE_VERSION = "S01E12";
	private static final String SERVER_URI = "localhost";

	@Test
	public void launchConfigTest() throws CoreException {
		ILaunchConfiguration conf = mock(ILaunchConfiguration.class);
		CloudApplicationLaunchConfig cloudApp = new CloudApplicationLaunchConfig(conf);

		when(conf.getAttribute(CloudApplicationLaunchConfig.ATTR_FEATURE_ID, "")).thenReturn(FEATURE_ID);
		when(conf.getAttribute(CloudApplicationLaunchConfig.ATTR_FEATURE_VERSION, "")).thenReturn(FEATURE_VERSION);
		when(conf.getAttribute(CloudApplicationLaunchConfig.ATTR_SERVER_URI, "")).thenReturn(SERVER_URI);

		assertEquals(FEATURE_ID, cloudApp.getFeatureId());
		assertEquals(FEATURE_VERSION, cloudApp.getFeatureVersion());
		assertEquals(SERVER_URI, cloudApp.getServerUri());
	}

	@Test
	public void launchConfigSetterTest() {
		ILaunchConfigurationWorkingCopy conf = mock(ILaunchConfigurationWorkingCopy.class);
		CloudApplicationLaunchConfig cloudApp = new CloudApplicationLaunchConfig(conf);
		cloudApp.setFeatureId(FEATURE_ID);
		verify(conf).setAttribute(CloudApplicationLaunchConfig.ATTR_FEATURE_ID, FEATURE_ID);
		cloudApp.setFeatureVersion(FEATURE_VERSION);
		verify(conf).setAttribute(CloudApplicationLaunchConfig.ATTR_FEATURE_VERSION, FEATURE_VERSION);
		cloudApp.setServerUri(SERVER_URI);
		verify(conf).setAttribute(CloudApplicationLaunchConfig.ATTR_SERVER_URI, SERVER_URI);
	}
}
