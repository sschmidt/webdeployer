/*******************************************************************************
 * Copyright (c) 2011 Sebastian Schmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Sebastian Schmidt - initial API and implementation
 ******************************************************************************/
package org.eclipse.rtp.httpdeployer.bundle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rtp.httpdeployer.internal.CommonConstants;
import org.eclipse.rtp.httpdeployer.internal.XmlConstants;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

public class BundleServlet extends HttpServlet {

	private static final long serialVersionUID = 3749930485862030632L;
	protected static final String REQUEST_PATH_RESOLVED_BUNDLES = "/resolved"; //$NON-NLS-N$
	protected static final String REQUEST_PATH_ACTIVE_BUNDLES = "/active"; //$NON-NLS-N$
	protected static final int ALL_BUNDLES = 0;

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType(CommonConstants.RESPONSE_CONTENT_TYPE);
		Document responseXml = parseRequest(req);
		XMLOutputter out = new XMLOutputter();
		out.output(responseXml, resp.getWriter());
	}

	private Document parseRequest(HttpServletRequest req) {
		String requestPath = req.getPathInfo();
		Document responseXml;

		if (requestPath == null) {
			responseXml = generateBundleList(ALL_BUNDLES);
		} else if (requestPath.startsWith(REQUEST_PATH_ACTIVE_BUNDLES)) {
			responseXml = generateBundleList(Bundle.ACTIVE);
		} else if (requestPath.startsWith(REQUEST_PATH_RESOLVED_BUNDLES)) {
			responseXml = generateBundleList(Bundle.RESOLVED);
		} else {
			responseXml = generateBundleList(ALL_BUNDLES);
		}
		return responseXml;
	}

	private Document generateBundleList(int requestType) {
		List<Bundle> bundles = receiveBundles(requestType);
		Element root = generateBundleListXml(bundles);

		return new Document(root);
	}

	private Element generateBundleListXml(List<Bundle> bundles) {
		Element root = new Element(XmlConstants.XML_ELEMENT_BUNDLES);
		for (Bundle bundle : bundles) {
			Element bundleXml = new Element(XmlConstants.XML_ELEMENT_BUNDLE);
			bundleXml.addContent(new Element(XmlConstants.XML_ELEMENT_NAME).addContent(bundle.getSymbolicName()));
			bundleXml.addContent(new Element(XmlConstants.XML_ELEMENT_VERSION).addContent(bundle.getVersion().toString()));
			root.addContent(bundleXml);
		}

		return root;
	}

	private List<Bundle> receiveBundles(int requestType) {
		Bundle[] bundles = receiveBundlesFromContext();

		List<Bundle> validBundles = new ArrayList<Bundle>();
		for (Bundle bundle : bundles) {
			if (bundle.getState() == requestType || requestType == ALL_BUNDLES) {
				validBundles.add(bundle);
			}
		}

		return validBundles;
	}

	// TODO: Not tested
	protected Bundle[] receiveBundlesFromContext() {
		Bundle currentBundle = FrameworkUtil.getBundle(getClass());
		BundleContext context = currentBundle.getBundleContext();
		Bundle[] bundles = context.getBundles();
		return bundles;
	}
}