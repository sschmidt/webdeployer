/*******************************************************************************
 * Copyright (c) 2011 Sebastian Schmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Sebastian Schmidt - initial API and implementation
 ******************************************************************************/
package org.eclipse.rtp.httpdeployer.internal;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import java.util.Dictionary;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.eclipse.equinox.internal.provisional.configurator.Configurator;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

@SuppressWarnings("restriction")
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

		verify(service).registerServlet(eq(HttpDeployerInitializer.ALIAS_REPOSITORY), any(Servlet.class), any(Dictionary.class),
				any(HttpContext.class));
		verify(service).registerServlet(eq(HttpDeployerInitializer.ALIAS_BUNDLE), any(Servlet.class), any(Dictionary.class),
				any(HttpContext.class));
		verify(service).registerServlet(eq(HttpDeployerInitializer.ALIAS_FEATURE), any(Servlet.class), any(Dictionary.class),
				any(HttpContext.class));
		verify(service).registerServlet(eq(HttpDeployerInitializer.ALIAS_SYSTEM), any(Servlet.class), any(Dictionary.class),
				any(HttpContext.class));
		assertEquals(configurator, component.configurator);

		component.shutdownService();
		verify(service).unregister(HttpDeployerInitializer.ALIAS_BUNDLE);
		verify(service).unregister(HttpDeployerInitializer.ALIAS_REPOSITORY);
		verify(service).unregister(HttpDeployerInitializer.ALIAS_FEATURE);
		verify(service).unregister(HttpDeployerInitializer.ALIAS_SYSTEM);

		component.unsetHttpService(service);
		component.unsetProvisioningAgent(agent);
		component.unsetConfigurator(configurator);

		assertEquals(null, component.configurator);
		assertEquals(null, component.httpService);
		assertEquals(null, component.provisioningAgent);
	}
}
