/*******************************************************************************
 * Copyright (c) 2011 Sebastian Schmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Sebastian Schmidt - initial API and implementation
 ******************************************************************************/
package org.eclipse.rtp.httpdeployer.internal.launch;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.rtp.httpdeployer.launch.CloudApplicationLaunchConfig;
import org.junit.Test;

public class CloudApplicationTest {

	private static final String SERVER_URI = "localhost";

	@Test
	public void launchTest() throws CoreException {
		CloudApplication app = spy(new CloudApplication());
		ILaunchConfiguration conf = mock(ILaunchConfiguration.class);
		HttpPublishOperation operation = mock(HttpPublishOperation.class);
		doReturn(operation).when(app).createOperation(any(IProgressMonitor.class), any(HttpPublishConfig.class));

		IProgressMonitor monitor = mock(IProgressMonitor.class);
		when(operation.run(monitor)).thenReturn(new Status(Status.OK, "cloudapplication", "ok"));
		app.launch(conf, "run", null, monitor);

		verify(app).createDeployerConfiguration(conf);
		verify(app).createOperation(eq(monitor), any(HttpPublishConfig.class));
	}

	@Test(expected=CoreException.class)
	public void invalidLaunchTest() throws CoreException {
		CloudApplication app = spy(new CloudApplication());
		ILaunchConfiguration conf = mock(ILaunchConfiguration.class);
		HttpPublishOperation operation = mock(HttpPublishOperation.class);
		doReturn(operation).when(app).createOperation(any(IProgressMonitor.class), any(HttpPublishConfig.class));

		IProgressMonitor monitor = mock(IProgressMonitor.class);
		when(operation.run(monitor)).thenReturn(new Status(Status.ERROR, "error", "error"));
		app.launch(conf, "run", null, monitor);
	}

	@Test
	public void createConfigurationTest() throws CoreException {
		CloudApplication app = new CloudApplication();
		ILaunchConfiguration configuration = mock(ILaunchConfiguration.class);
		when(configuration.getAttribute(CloudApplicationLaunchConfig.ATTR_SERVER_URI, "")).thenReturn(SERVER_URI);
		
		HttpPublishConfig publishConfig = app.createDeployerConfiguration(configuration);
		assertEquals(SERVER_URI, publishConfig.getHttpDeployerServiceUrl());
	}
	
	@Test
	public void createDeployerConfigurationTest() {
		CloudApplication app = new CloudApplication();
		HttpPublishConfig config = spy(new HttpPublishConfig());
		config.setQualifier("foo");
		HttpPublishOperation createOperation = app.createOperation(mock(IProgressMonitor.class), config);
		assertEquals(true, createOperation.isUser());
		assertEquals(ResourcesPlugin.getWorkspace().getRoot(), createOperation.getRule());
		assertEquals("Http Deployer Job", createOperation.getName());
	}

}
