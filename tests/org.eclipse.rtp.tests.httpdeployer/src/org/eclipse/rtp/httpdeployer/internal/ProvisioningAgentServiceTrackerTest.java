/*******************************************************************************
 * Copyright (c) 2011 Sebastian Schmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Sebastian Schmidt - initial API and implementation
 ******************************************************************************/
package org.eclipse.rtp.httpdeployer.internal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.engine.IProfileRegistry;
import org.eclipse.rtp.httpdeployer.internal.Activator;
import org.eclipse.rtp.httpdeployer.internal.ProvisioningAgentServiceTracker;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class ProvisioningAgentServiceTrackerTest {

	@Mock
	private Activator activatorMock;

	@Mock
	private BundleContext contextMock;

	@Mock
	private IProvisioningAgent agentMock;

	@Mock
	private IProfileRegistry profileRegMock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		when(activatorMock.getContext()).thenReturn(contextMock);
		when(agentMock.getService(IProfileRegistry.SERVICE_NAME)).thenReturn(profileRegMock);
	}

	@Test
	public void addingServiceTest() throws ProvisionException {
		ProvisioningAgentServiceTracker tracker = new ProvisioningAgentServiceTracker(activatorMock);

		@SuppressWarnings("unchecked")
		ServiceReference<IProvisioningAgent> paMock = (ServiceReference<IProvisioningAgent>) mock(ServiceReference.class);
		when(contextMock.getService(paMock)).thenReturn(agentMock);
		tracker.addingService(paMock);

		verify(activatorMock).setProvisioningAgent(agentMock);
		verify(activatorMock).setProfileRegistry(profileRegMock);
		verify(profileRegMock).getProfile(Activator.DEFAULT_PROFILE_ID);
		verify(profileRegMock).addProfile(Activator.DEFAULT_PROFILE_ID);
	}

	@Test
	public void removingServiceTest() {
		ProvisioningAgentServiceTracker tracker = new ProvisioningAgentServiceTracker(activatorMock);
		tracker.removedService(null, null);

		verify(activatorMock).setProvisioningAgent(null);
		verify(activatorMock).setProfileRegistry(null);
	}
}
