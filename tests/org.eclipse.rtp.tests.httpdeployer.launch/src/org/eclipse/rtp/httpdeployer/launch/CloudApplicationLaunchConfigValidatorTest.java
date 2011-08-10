/*******************************************************************************
 * Copyright (c) 2011 Sebastian Schmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Sebastian Schmidt - initial API and implementation
 ******************************************************************************/
package org.eclipse.rtp.httpdeployer.launch;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.junit.Test;

public class CloudApplicationLaunchConfigValidatorTest {

	@Test
	public void launchConfigValidatorTest() throws CoreException {
		ILaunchConfiguration config = mock(ILaunchConfiguration.class);
		when(config.getAttribute(CloudApplicationLaunchConfig.ATTR_FEATURE_ID, "")).thenReturn("test");
		when(config.getAttribute(CloudApplicationLaunchConfig.ATTR_FEATURE_VERSION, "")).thenReturn("foo");
		when(config.getAttribute(CloudApplicationLaunchConfig.ATTR_SERVER_URI, "")).thenReturn("localhost");

		assertTrue(CloudApplicationLaunchConfigValidator.validate(config));
	}

	@Test
	public void launchConfigValidatorInvalidTest() throws CoreException {
		ILaunchConfiguration config = mock(ILaunchConfiguration.class);
		when(config.getAttribute(CloudApplicationLaunchConfig.ATTR_FEATURE_ID, "")).thenReturn("");
		when(config.getAttribute(CloudApplicationLaunchConfig.ATTR_FEATURE_VERSION, "")).thenReturn("");
		when(config.getAttribute(CloudApplicationLaunchConfig.ATTR_SERVER_URI, "")).thenReturn("");

		assertFalse(CloudApplicationLaunchConfigValidator.validate(config));
	}
}
