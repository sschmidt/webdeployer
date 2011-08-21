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

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.eclipse.rtp.httpdeployer.internal.CommonConstants.Action;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

public abstract class AbstractHttpDeployerServlet extends HttpServlet {

	private static final long serialVersionUID = -1882775409591126187L;

	@Override
	public void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			Document requestDocument = HttpDeployerUtils.parseXmlRequest(request);
			Document responseDocument = parseDeleteRequest(requestDocument);
			HttpDeployerUtils.outputDocument(response, responseDocument);
		} catch (JDOMException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Document responseDocument = null;

		// create repository by URI
		if (!ServletFileUpload.isMultipartContent(request)) {
			try {
				responseDocument = parsePostRequest(HttpDeployerUtils.parseXmlRequest(request));
			} catch (JDOMException e) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
		} else { // create repository by file upload
			try {
				responseDocument = parseMultipartPostRequest(request);
			} catch (Exception e) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
		}

		HttpDeployerUtils.outputDocument(response, responseDocument);
	}

	protected Document parseRequest(Document request, String searchedElement, Action action) {
		Element rootElement = request.getRootElement();
		RequestResults result = new RequestResults();

		for (Object child : rootElement.getChildren()) {
			if (child instanceof Element) {
				Element currentElement = (Element) child;
				if (currentElement.getName().equals(searchedElement)) {
					handleOperation(result, currentElement, action);
				}
			}
		}

		return result.getDocument();
	}

	public abstract void handleOperation(RequestResults result, Element currentElement, Action action);

	public abstract Document parseDeleteRequest(Document requestDocument);

	public abstract Document parsePostRequest(Document requestDocument);

	public abstract Document parseMultipartPostRequest(HttpServletRequest request) throws Exception;

}
