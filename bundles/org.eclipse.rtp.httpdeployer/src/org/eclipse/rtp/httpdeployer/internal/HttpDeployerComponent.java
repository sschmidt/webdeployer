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

import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.rtp.httpdeployer.bundle.BundleServlet;
import org.eclipse.rtp.httpdeployer.repository.RepositoryServlet;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

public class HttpDeployerComponent {

	public static final String ALIAS_BUNDLE = "/bundles";
	public static final String ALIAS_REPOSITORY = "/repositories";

	protected RepositoryServlet repositoryServlet;
	protected BundleServlet bundleServlet;
	
	protected HttpService httpService;
	protected IProvisioningAgent provisioningAgent;

	public void setHttpService(HttpService httpService) {
		this.httpService = httpService;
	}

	public void unsetHttpService(HttpService httpService) {
		this.httpService = null;
	}

	public void setProvisioningAgent(IProvisioningAgent provisioningAgent) {
		this.provisioningAgent = provisioningAgent;
	}

	public void unsetProvisioningAgent(IProvisioningAgent provisioningAgent) {
		this.provisioningAgent = null;
	}

	protected void startService() throws ServletException, NamespaceException {
		bundleServlet = new BundleServlet();
		repositoryServlet = new RepositoryServlet(provisioningAgent);
		httpService.registerServlet(ALIAS_BUNDLE, bundleServlet, null, null);
		httpService.registerServlet(ALIAS_REPOSITORY, repositoryServlet, null, null);
	}

	protected void shutdownService() {
		httpService.unregister(ALIAS_BUNDLE);
		httpService.unregister(ALIAS_REPOSITORY);
	}
}