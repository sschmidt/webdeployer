/*******************************************************************************
 * Copyright (c) 2011 Sebastian Schmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Sebastian Schmidt - initial API and implementation
 ******************************************************************************/
package org.eclipse.rtp.httpdeployer.servlets.bundles;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rtp.httpdeployer.internal.Activator;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class ListServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/xml");

		String requestPath = req.getPathInfo();

		Document responseXml;
		if (requestPath.startsWith("/active")) {
			responseXml = generateResponseXml(Bundle.ACTIVE);
		} else if (requestPath.startsWith("/resolved")) {
			responseXml = generateResponseXml(Bundle.RESOLVED);
		} else {
			responseXml = generateResponseXml(0);
		}

		XMLOutputter out = new XMLOutputter();
		out.output(responseXml, resp.getWriter());
	}

	private Document generateResponseXml(int bundleState) {
		List<Bundle> bundles = receiveBundles(bundleState);
		Element root = new Element("bundles");

		for (Bundle bundle : bundles) {
			Element bundleXml = new Element("bundle");
			bundleXml.addContent(new Element("name").addContent(bundle.getSymbolicName()));
			bundleXml.addContent(new Element("version").addContent(bundle.getVersion().toString()));
			root.addContent(bundleXml);
		}

		return new Document(root);
	}

	private List<Bundle> receiveBundles(int bundleState) {
		BundleContext context = Activator.getContext();
		Bundle[] bundles = context.getBundles();

		if (bundleState == 0) {
			return Arrays.asList(bundles);
		}

		List<Bundle> validBundles = new ArrayList<Bundle>();
		for (Bundle bundle : bundles) {
			if (bundle.getState() == bundleState) {
				validBundles.add(bundle);
			}
		}

		return validBundles;

	}
}
