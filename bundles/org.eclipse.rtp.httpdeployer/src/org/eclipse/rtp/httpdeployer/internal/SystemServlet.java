/*******************************************************************************
 * Copyright (c) 2011 Sebastian Schmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Sebastian Schmidt - initial API and implementation
 ******************************************************************************/
package org.eclipse.rtp.httpdeployer.internal;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.Version;

public class SystemServlet extends HttpServlet {

	private static final long serialVersionUID = 3404658151450270259L;
	private static final String REQUEST_PATH_VERSION = "/-version"; //$NON-NLS-N$

    // TODO: Not tested	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType(CommonConstants.RESPONSE_CONTENT_TYPE);
		if (req.getPathInfo() != null && req.getPathInfo().startsWith(REQUEST_PATH_VERSION)) {
			printVersion(resp);
		}
	}

	private void printVersion(HttpServletResponse resp) throws IOException {
		Element root = new Element(XmlConstants.XML_ELEMENT_SYSTEM);
		Element bundleXml = new Element(XmlConstants.XML_ELEMENT_FEATURE);
		bundleXml.addContent(new Element(XmlConstants.XML_ELEMENT_NAME).addContent(getHttpDeployerName()));
		bundleXml.addContent(new Element(XmlConstants.XML_ELEMENT_VERSION).addContent(getHttpDeployerVersion().toString()));
		root.addContent(bundleXml);
		XMLOutputter out = new XMLOutputter();
		out.output(root, resp.getWriter());
	}

	protected Version getHttpDeployerVersion() {
		return FrameworkUtil.getBundle(getClass()).getVersion();
	}

	protected String getHttpDeployerName() {
		return FrameworkUtil.getBundle(getClass()).getSymbolicName();
	}
}
