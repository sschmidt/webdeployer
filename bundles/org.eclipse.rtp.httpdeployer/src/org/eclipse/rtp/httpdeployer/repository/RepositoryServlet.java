/*******************************************************************************
 * Copyright (c) 2011 Sebastian Schmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Sebastian Schmidt - initial API and implementation
 ******************************************************************************/
package org.eclipse.rtp.httpdeployer.repository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
import org.eclipse.rtp.httpdeployer.internal.CommonConstants;
import org.eclipse.rtp.httpdeployer.internal.XmlConstants;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

public class RepositoryServlet extends HttpServlet {

	private static final long serialVersionUID = -4190823339335383710L;
	private final RepositoryManager repositoryManager;

	public enum Action {
		REMOVE, ADD
	}

	public RepositoryServlet(RepositoryManager repositoryManager) {
		this.repositoryManager = repositoryManager;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType(CommonConstants.RESPONSE_CONTENT_TYPE);
		URI[] repositories = repositoryManager.getRepositories();
		Element root = new Element(XmlConstants.XML_ELEMENT_REPOSITORIES);

		for (URI repository : repositories) {
			Element bundleXml = new Element(XmlConstants.XML_ELEMENT_REPOSITORY);
			bundleXml.addContent(new Element(XmlConstants.XML_ELEMENT_URI).addContent(repository.toString()));
			root.addContent(bundleXml);
		}

		Document document = new Document(root);
		XMLOutputter out = new XMLOutputter();
		out.output(document, resp.getWriter());
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		RepositoryModificationResult result = null;

		// create repository by URI
		if (!ServletFileUpload.isMultipartContent(req)) {
			try {
				result = parseUriAddRequest(req);
			} catch (JDOMException e) {
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
		} else { // create repository by file upload
			try {
				result = parseUploadRequest(req);
			} catch (FileUploadException e) {
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
		}

		XMLOutputter out = new XMLOutputter();
		out.output(result.getDocument(), resp.getWriter());
	}

	private RepositoryModificationResult parseUploadRequest(HttpServletRequest req) throws FileUploadException,
			FileNotFoundException, IOException {
		RepositoryModificationResult result = new RepositoryModificationResult();

		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		@SuppressWarnings("unchecked")
		List<FileItem> files = upload.parseRequest(req);
		if (files.size() != 1) {
			throw new FileUploadException("File not found");
		}

		try {
			result.addSuccess(repositoryManager.addRepository(files.get(0).getInputStream()).toString(), Action.ADD);
		} catch (InvalidRepositoryException e) {
			result.addFailure("local", e.getMessage(), Action.ADD);
		}
		return result;
	}

	private RepositoryModificationResult parseUriAddRequest(HttpServletRequest req) throws JDOMException, IOException {
		SAXBuilder builder = new SAXBuilder();
		Document request = builder.build(req.getReader());
		return parseRequestDocument(request, Action.ADD);
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		SAXBuilder builder = new SAXBuilder();
		try {
			Document request = builder.build(req.getReader());
			RepositoryModificationResult result = parseRequestDocument(request, Action.REMOVE);
			XMLOutputter out = new XMLOutputter();
			out.output(result.getDocument(), resp.getWriter());
		} catch (JDOMException e) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	private RepositoryModificationResult parseRequestDocument(Document request, Action action) {
		Element rootElement = request.getRootElement();
		RepositoryModificationResult result = new RepositoryModificationResult();

		for (Object child : rootElement.getChildren()) {
			if (child instanceof Element) {
				Element currentElement = (Element) child;
				if (currentElement.getName().equals(XmlConstants.XML_ELEMENT_REPOSITORY)) {
					String repositoryPath = currentElement.getChildText(XmlConstants.XML_ELEMENT_URI);
					try {
						URI repository = new URI(repositoryPath);
						performRepositoryAction(repository, action);
						result.addSuccess(repositoryPath, action);
					} catch (URISyntaxException e) {
					  // TODO: Not tested
						result.addFailure(repositoryPath, e.getMessage(), action);
					}
				}
			}
		}

		return result;
	}

	
	private void performRepositoryAction(URI repository, Action action) {
		if (action.equals(Action.ADD)) {
			repositoryManager.addRepository(repository);
		} else {
			repositoryManager.removeRepository(repository);
		}
	}

	   // TODO: Not tested
	public RepositoryManager getRepositoryManager() {
		return repositoryManager;
	}
}
