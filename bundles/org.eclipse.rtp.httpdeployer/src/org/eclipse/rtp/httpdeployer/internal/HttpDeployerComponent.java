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
import org.eclipse.rtp.httpdeployer.bundle.BundleManageServlet;
import org.eclipse.rtp.httpdeployer.bundle.BundleServlet;
import org.eclipse.rtp.httpdeployer.feature.FeatureManager;
import org.eclipse.rtp.httpdeployer.feature.FeatureServlet;
import org.eclipse.rtp.httpdeployer.repository.RepositoryManager;
import org.eclipse.rtp.httpdeployer.repository.RepositoryServlet;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

@SuppressWarnings("restriction")
public class HttpDeployerComponent {

	public static final String ALIAS_BUNDLE = "/bundle"; //$NON-NLS-N$
	public static final String ALIAS_BUNDLE_MANAGE = "/bundle/manage"; //$NON-NLS-N$
	public static final String ALIAS_REPOSITORY = "/repository"; //$NON-NLS-N$
	public static final String ALIAS_FEATURE = "/feature"; //$NON-NLS-N$
	public static final String ALIAS_SYSTEM = "/system"; //$NON-NLS-N$

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
		RepositoryManager repositoryManager = new RepositoryManager(provisioningAgent);
		FeatureManager featureManager = new FeatureManager(provisioningAgent, repositoryManager, configurator);

		BundleServlet bundleServlet = new BundleServlet();
		RepositoryServlet repositoryServlet = new RepositoryServlet(repositoryManager);
		FeatureServlet featureServlet = new FeatureServlet(featureManager);
		SystemServlet systemServlet = new SystemServlet();
		BundleManageServlet bundleManageServlet = new BundleManageServlet();

		httpService.registerServlet(ALIAS_BUNDLE, bundleServlet, null, null);
		httpService.registerServlet(ALIAS_REPOSITORY, repositoryServlet, null, null);
		httpService.registerServlet(ALIAS_FEATURE, featureServlet, null, null);
		httpService.registerServlet(ALIAS_SYSTEM, systemServlet, null, null);
		httpService.registerServlet(ALIAS_BUNDLE_MANAGE, bundleManageServlet, null, null);
	}

	protected void shutdownService() {
		httpService.unregister(ALIAS_BUNDLE);
		httpService.unregister(ALIAS_REPOSITORY);
		httpService.unregister(ALIAS_FEATURE);
		httpService.unregister(ALIAS_SYSTEM);
		httpService.unregister(ALIAS_BUNDLE_MANAGE);
	}
}