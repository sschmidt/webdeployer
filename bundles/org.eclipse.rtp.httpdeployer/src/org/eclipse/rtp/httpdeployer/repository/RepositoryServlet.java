/*******************************************************************************
 * Copyright (c) 2011 Sebastian Schmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Sebastian Schmidt - initial API and implementation
 ******************************************************************************/
package org.eclipse.rtp.httpdeployer.repository;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.eclipse.rtp.httpdeployer.internal.Activator;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

public class RepositoryServlet extends HttpServlet {

	private enum Action {
		DELETE, CREATE
	}

	private static final long serialVersionUID = 1L;

	protected RepositoryManager repoManager = new RepositoryManager(Activator.getInstance().getProvisioningAgent());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		URI[] repositories = repoManager.getRepositories();
		Element root = new Element("repositories");

		for (URI repository : repositories) {
			Element bundleXml = new Element("repository");
			bundleXml.addContent(new Element("uri").addContent(repository.toString()));
			root.addContent(bundleXml);
		}

		Document document = new Document(root);
		XMLOutputter out = new XMLOutputter();
		out.output(document, resp.getWriter());
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Result result = null;
		// create by URI
		if (!ServletFileUpload.isMultipartContent(req)) {
			SAXBuilder builder = new SAXBuilder();
			try {
				Document request = builder.build(req.getReader());
				result = parseRequestDocument(request, Action.CREATE);
			} catch (JDOMException e) {
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
		} else { // create by file upload
			FileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			try {
				@SuppressWarnings("unchecked")
				List<FileItem> files = upload.parseRequest(req);
				if (files.size() != 1) {
					resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
				}

				result = new Result();
				try {
					result.addSuccess(repoManager.addRepository(files.get(0).getInputStream()).toString());
				} catch (RepositoryException e) {
					result.addFailure("local", e.getMessage());
				}
			} catch (FileUploadException e) {
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
		}

		XMLOutputter out = new XMLOutputter();
		out.output(result.getDocument(), resp.getWriter());
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		SAXBuilder builder = new SAXBuilder();
		try {
			Document request = builder.build(req.getReader());
			Result result = parseRequestDocument(request, Action.DELETE);
			XMLOutputter out = new XMLOutputter();
			out.output(result.getDocument(), resp.getWriter());
		} catch (JDOMException e) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	private Result parseRequestDocument(Document request, Action action) {
		Element rootElement = request.getRootElement();
		Result result = new Result();

		for (Object child : rootElement.getChildren()) {
			if (child instanceof Element) {
				Element currentElement = (Element) child;
				if (currentElement.getName().equals("repository")) {
					try {
						URI repository = new URI(currentElement.getText());
						handleRepository(repository, action);
						result.addSuccess(currentElement.getText());
					} catch (URISyntaxException e) {
						result.addFailure(currentElement.getText(), e.getMessage());
					}
				}
			}
		}

		return result;
	}

	private void handleRepository(URI repository, Action action) {
		if (action.equals(Action.CREATE)) {
			repoManager.addRepository(repository);
		} else {
			repoManager.removeRepository(repository);
		}
	}

	private class Result {
		private List<String> successfulResults = new ArrayList<String>();
		private List<ErrorInfo> failedResults = new ArrayList<ErrorInfo>();

		public Document getDocument() {
			Element root = new Element("repositories");

			for (String repository : successfulResults) {
				Element bundleXml = new Element("repository");
				bundleXml.addContent(new Element("uri").addContent(repository));
				bundleXml.addContent(new Element("status").addContent("successful"));
				root.addContent(bundleXml);
			}

			for (ErrorInfo repository : failedResults) {
				Element bundleXml = new Element("repository");
				bundleXml.addContent(new Element("uri").addContent(repository.getRepository()));
				bundleXml.addContent(new Element("reason").addContent(repository.getReason()));
				bundleXml.addContent(new Element("status").addContent("failed"));
				root.addContent(bundleXml);
			}

			return new Document(root);
		}

		public void addSuccess(String repository) {
			successfulResults.add(repository);
		}

		public void addFailure(String repository, String reason) {
			failedResults.add(new ErrorInfo(repository, reason));
		}
	}

	private class ErrorInfo {

		private final String repository;
		private final String reason;

		public ErrorInfo(String repository, String reason) {
			this.repository = repository;
			this.reason = reason;
		}

		public String getReason() {
			return reason;
		}

		public String getRepository() {
			return repository;
		}
	}
}
