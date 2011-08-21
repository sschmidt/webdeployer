/*******************************************************************************
 * Copyright (c) 2011 Sebastian Schmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Sebastian Schmidt - initial API and implementation
 ******************************************************************************/
package org.eclipse.rtp.httpdeployer.internal;

import javax.servlet.ServletException;

import org.eclipse.equinox.internal.provisional.configurator.Configurator;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

@SuppressWarnings("restriction")
public class HttpDeployerComponent {

	private HttpDeployerInitializer initializer = new HttpDeployerInitializer();

	protected HttpService httpService;
	protected IProvisioningAgent provisioningAgent;
	protected Configurator configurator;

	public void setHttpService(HttpService httpService) {
		this.httpService = httpService;
	}

	public void unsetHttpService(HttpService httpService) {
		this.httpService = null;
	}

	public void setConfigurator(Configurator configurator) {
		this.configurator = configurator;
	}

	public void unsetConfigurator(Configurator configurator) {
		this.configurator = null;
	}

	public void setProvisioningAgent(IProvisioningAgent provisioningAgent) {
		this.provisioningAgent = provisioningAgent;
	}

	public void unsetProvisioningAgent(IProvisioningAgent provisioningAgent) {
		this.provisioningAgent = null;
	}

	protected void startService() throws ServletException, NamespaceException {
		initializer.setProvisioningAgent(provisioningAgent);
		initializer.setConfigurator(configurator);
		initializer.setHttpService(httpService);
		initializer.init();
	}

	protected void shutdownService() {
		initializer.unregister();
	}
}