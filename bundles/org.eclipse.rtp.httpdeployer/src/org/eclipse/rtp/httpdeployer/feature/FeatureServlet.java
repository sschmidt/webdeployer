/*******************************************************************************
 * Copyright (c) 2011 Sebastian Schmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Sebastian Schmidt - initial API and implementation
 ******************************************************************************/
package org.eclipse.rtp.httpdeployer.feature;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rtp.httpdeployer.internal.XmlConstants;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

public class FeatureServlet extends HttpServlet {

	private static final long serialVersionUID = 8439160853108236583L;
	private final FeatureManager featureManager;

	public FeatureServlet(FeatureManager featureManager) {
		this.featureManager = featureManager;
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			Document xmlDocument = parseXmlRequest(request);
			FeatureModificationResult result = parseRequest(xmlDocument, FeatureModificationResult.Action.INSTALL);
			returnOperationResult(response, result);
		} catch (JDOMException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			Document xmlDocument = parseXmlRequest(request);
			FeatureModificationResult result = parseRequest(xmlDocument, FeatureModificationResult.Action.UNINSTALL);
			returnOperationResult(response, result);
		} catch (JDOMException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	private Document parseXmlRequest(HttpServletRequest request) throws JDOMException, IOException {
		SAXBuilder builder = new SAXBuilder();
		Document xmlDocument = builder.build(request.getReader());
		return xmlDocument;
	}

	private void returnOperationResult(HttpServletResponse resp, FeatureModificationResult result) throws IOException {
		XMLOutputter out = new XMLOutputter();
		out.output(result.getDocument(), resp.getWriter());
	}

	// TODO: Three nesting levels. Maybe this method can be splitted?
	private FeatureModificationResult parseRequest(Document request, FeatureModificationResult.Action action)
			throws JDOMException {
		Element rootElement = request.getRootElement();
		FeatureModificationResult result = new FeatureModificationResult();

		for (Object child : rootElement.getChildren()) {
			if (child instanceof Element) {
				Element currentElement = (Element) child;
				if (currentElement.getName().equals(XmlConstants.XML_ELEMENT_FEATURE)) {
					String name = currentElement.getChildText(XmlConstants.XML_ELEMENT_NAME);
					String version = currentElement.getChildText(XmlConstants.XML_ELEMENT_VERSION);
					handleOperation(result, name, version, action);
				}
			}
		}

		return result;
	}

	private void handleOperation(FeatureModificationResult result, String name, String version,
			FeatureModificationResult.Action action) {
		try {
			if (action.equals(FeatureModificationResult.Action.INSTALL)) {
				featureManager.installFeature(name, version);
				result.addSuccess(name, version, action);
			} else if (action.equals(FeatureModificationResult.Action.UNINSTALL)) {
				featureManager.uninstallFeature(name, version);
				result.addSuccess(name, version, action);
			}
		} catch (FeatureInstallException e) {
		    // TODO: Not tested
			result.addFailed(name, version, action, e);
		}
	}
}
