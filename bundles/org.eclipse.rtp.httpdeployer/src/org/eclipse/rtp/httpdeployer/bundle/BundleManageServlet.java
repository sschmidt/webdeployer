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

// TODO: Manage is not a good word for a type because its not a noun.
public class BundleManageServlet extends BundleServlet {
  
    // TODO: There are other Action Enums. Maybe these can be merged?
	public enum Action {
		START, STOP, UNINSTALL
	}

	private static final long serialVersionUID = -4494496143821074375L;

	// TODO: The pattern of this method occurs very often in your code. I think every Servlet does 
	// implement it? There is also a hint because the exception handling of these methods is always
	// the same. This is a sign to encapsulate this behaviour in a util or some kind of an OutputHandler
	// or something similar.
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

	// TODO: Nesting Level of three.
	// TODO: Almost the same method exists in the RepositoryServlet. Eliminate Code Duplication?
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

	// TODO: Extracts this to a util? It also exists more than once right?
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
