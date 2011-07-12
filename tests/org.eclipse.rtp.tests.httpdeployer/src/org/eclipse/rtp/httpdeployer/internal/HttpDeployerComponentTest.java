/*******************************************************************************
 * Copyright (c) 2011 Sebastian Schmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Sebastian Schmidt - initial API and implementation
 ******************************************************************************/
package org.eclipse.rtp.httpdeployer.internal;

import static org.mockito.Mockito.verify;

import javax.servlet.ServletException;

import org.eclipse.equinox.internal.provisional.configurator.Configurator;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.rtp.httpdeployer.repository.RepositoryServlet;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import static org.junit.Assert.assertEquals;

public class HttpDeployerComponentTest {

	@Mock
	IProvisioningAgent agent;

	@Mock
	HttpService service;
	
	@Mock
	Configurator configurator;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void deployerComponentTest() throws ServletException, NamespaceException {
		HttpDeployerComponent component = new HttpDeployerComponent();
		component.setHttpService(service);
		component.setProvisioningAgent(agent);
		component.setConfigurator(configurator);
		component.startService();

		verify(service).registerServlet(HttpDeployerComponent.ALIAS_BUNDLE, component.bundleServlet, null, null);
		verify(service).registerServlet(HttpDeployerComponent.ALIAS_REPOSITORY, component.repositoryServlet, null, null);
		RepositoryServlet repositoryServlet = component.repositoryServlet;
		assertEquals(agent, repositoryServlet.getRepositoryManager().getProvisioningAgent());
		assertEquals(configurator, component.configurator);

		component.shutdownService();
		verify(service).unregister(HttpDeployerComponent.ALIAS_BUNDLE);
		verify(service).unregister(HttpDeployerComponent.ALIAS_REPOSITORY);

		component.unsetHttpService(service);
		component.unsetProvisioningAgent(agent);
		component.unsetConfigurator(configurator);

		assertEquals(null, component.configurator);
		assertEquals(null, component.httpService);
		assertEquals(null, component.provisioningAgent);
	}
}
