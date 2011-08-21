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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rtp.httpdeployer.internal.AbstractHttpDeployerServlet;
import org.eclipse.rtp.httpdeployer.internal.CommonConstants.Action;
import org.eclipse.rtp.httpdeployer.internal.HttpDeployerUtils;
import org.eclipse.rtp.httpdeployer.internal.XmlConstants;
import org.jdom.Document;
import org.jdom.Element;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkUtil;

public class BundleServlet extends AbstractHttpDeployerServlet {

	private static final long serialVersionUID = 3749930485862030632L;
	protected static final String REQUEST_PATH_RESOLVED_BUNDLES = "/resolved"; //$NON-NLS-N$
	protected static final String REQUEST_PATH_ACTIVE_BUNDLES = "/active"; //$NON-NLS-N$
	protected static final int ALL_BUNDLES = 0;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String requestPath = request.getPathInfo();
		Document document;

		if (requestPath == null) {
			document = generateBundleList(ALL_BUNDLES);
		} else if (requestPath.startsWith(REQUEST_PATH_ACTIVE_BUNDLES)) {
			document = generateBundleList(Bundle.ACTIVE);
		} else if (requestPath.startsWith(REQUEST_PATH_RESOLVED_BUNDLES)) {
			document = generateBundleList(Bundle.RESOLVED);
		} else {
			document = generateBundleList(ALL_BUNDLES);
		}

		HttpDeployerUtils.outputDocument(response, document);
	}

	@Override
	public Document parseDeleteRequest(Document request) {
		return parseRequest(request, Action.UNINSTALL);
	}

	@Override
	public Document parsePostRequest(Document request) {
		return parseRequest(request, Action.START);
	}

	@Override
	public Document parseMultipartPostRequest(HttpServletRequest request) throws Exception {
		throw new IllegalAccessException();
	}

	private Document generateBundleList(int requestType) {
		Bundle[] bundles = receiveBundles(requestType);
		Element root = new Element(XmlConstants.XML_ELEMENT_BUNDLES);
		for (Bundle bundle : bundles) {
			Element bundleXml = new Element(XmlConstants.XML_ELEMENT_BUNDLE);
			bundleXml.addContent(new Element(XmlConstants.XML_ELEMENT_NAME).addContent(bundle.getSymbolicName()));
			bundleXml.addContent(new Element(XmlConstants.XML_ELEMENT_VERSION).addContent(bundle.getVersion().toString()));
			root.addContent(bundleXml);
		}
		return new Document(root);
	}

	// TODO: Nesting Level of three.
	// TODO: Almost the same method exists in the RepositoryServlet. Eliminate
	// Code Duplication?
	private Document parseRequest(Document request, Action action) {
		Element rootElement = request.getRootElement();
		BundleModificationResult result = new BundleModificationResult();

		for (Object child : rootElement.getChildren()) {
			if (child instanceof Element) {
				Element currentElement = (Element) child;
				if (currentElement.getName().equals(XmlConstants.XML_ELEMENT_BUNDLE)) {
					String name = currentElement.getChildText(XmlConstants.XML_ELEMENT_NAME);
					String actionName = currentElement.getChildText(XmlConstants.XML_ELEMENT_ACTION);
					if (actionName != null && actionName.equalsIgnoreCase("stop")) {
						action = Action.STOP;
					}
					Bundle bundle = searchBundle(name);
					handleOperation(result, name, bundle, action);
				}
			}
		}

		return result.getDocument();
	}

	private void handleOperation(BundleModificationResult result, String name, Bundle bundle, Action action) {
		if (bundle == null) {
			result.addFailure(name, action, "bundle not found");
		} else {
			try {
				switch (action) {
				case START:
					bundle.start();
					break;
				case STOP:
					bundle.stop();
					break;
				case UNINSTALL:
					bundle.uninstall();
					break;
				}
				result.addSuccess(name, action);
			} catch (BundleException e) {
				result.addFailure(name, action, e.getMessage());
			}
		}
	}

	protected Bundle[] receiveBundles() {
		Bundle currentBundle = FrameworkUtil.getBundle(HttpDeployerUtils.class);
		BundleContext context = currentBundle.getBundleContext();
		Bundle[] bundles = context.getBundles();
		return bundles;
	}

	private Bundle[] receiveBundles(int requestType) {
		Bundle[] bundles = receiveBundles();

		List<Bundle> validBundles = new ArrayList<Bundle>();
		for (Bundle bundle : bundles) {
			if (bundle.getState() == requestType || requestType == 0) {
				validBundles.add(bundle);
			}
		}

		return validBundles.toArray(new Bundle[0]);
	}

	private Bundle searchBundle(String searchedBundle) {
		Bundle[] bundles = receiveBundles();
		for (Bundle bundle : bundles) {
			if (bundle.getSymbolicName().equalsIgnoreCase(searchedBundle)) {
				return bundle;
			}
		}

		return null;
	}
}