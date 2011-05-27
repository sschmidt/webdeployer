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

import org.eclipse.rtp.httpdeployer.bundle.BundleServlet;
import org.eclipse.rtp.httpdeployer.repository.RepositoryServlet;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

public class HttpComponent {
  
	private static final String ALIAS_BUNDLE = "/bundles";
	private static final String ALIAS_REPOSITORY = "/repositories";

	private HttpService httpService;

	/*
	 * TODO: Rename the component.xml to something more meaningful. You can have more than one component
	 * in a bundle
	 * 
	 * TODO: The unbind reference method is missing.
	 */
	public void setHttpService(HttpService httpService) {
		this.httpService = httpService;
	}

	/*
	 * TODO: The exception handling in this method sucks ;)
	 */
	protected void startService() {
		try {
			BundleServlet bundleServlet = new BundleServlet();
			RepositoryServlet repositoryServlet = new RepositoryServlet();
			httpService.registerServlet(ALIAS_BUNDLE, bundleServlet, null, null);
			httpService.registerServlet(ALIAS_REPOSITORY, repositoryServlet, null, null);
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (NamespaceException e) {
			e.printStackTrace();
		}
	}

	protected void shutdownService() {
		httpService.unregister(ALIAS_BUNDLE);
		httpService.unregister(ALIAS_REPOSITORY);
	}

}
