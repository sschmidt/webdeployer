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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rtp.httpdeployer.internal.XmlConstants;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

public class BundleManageServlet extends BundleServlet {

	public enum Action {
		START, STOP, UNINSTALL
	}

	private static final long serialVersionUID = -4494496143821074375L;

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			Document responseDocument = parseRequest(request, Action.START);
			XMLOutputter out = new XMLOutputter();
			out.output(responseDocument, response.getWriter());
		} catch (JDOMException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			Document responseDocument = parseRequest(request, Action.UNINSTALL);
			XMLOutputter out = new XMLOutputter();
			out.output(responseDocument, response.getWriter());
		} catch (JDOMException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	private Document parseRequest(HttpServletRequest request, Action action) throws JDOMException, IOException {
		Document document = parseXmlRequest(request);
		Element rootElement = document.getRootElement();
		BundleModificationResult result = new BundleModificationResult();

		for (Object child : rootElement.getChildren()) {
			if (child instanceof Element) {
				Element currentElement = (Element) child;
				if (currentElement.getName().equals(XmlConstants.XML_ELEMENT_BUNDLE)) {
					String name = currentElement.getChildText(XmlConstants.XML_ELEMENT_NAME);
					String actionName = currentElement.getChildText(XmlConstants.XML_ELEMENT_ACTION);
					if(actionName != null && actionName.equalsIgnoreCase("stop")) {
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

	private Document parseXmlRequest(HttpServletRequest request) throws JDOMException, IOException {
		SAXBuilder builder = new SAXBuilder();
		Document xmlDocument = builder.build(request.getReader());
		return xmlDocument;
	}

	private Bundle searchBundle(String searchedBundle) {
		Bundle[] bundles = receiveBundlesFromContext();
		for (Bundle bundle : bundles) {
			if (bundle.getSymbolicName().equalsIgnoreCase(searchedBundle)) {
				return bundle;
			}
		}

		return null;
	}
}
