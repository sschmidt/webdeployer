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
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.eclipse.rtp.httpdeployer.internal.AbstractHttpDeployerServlet;
import org.eclipse.rtp.httpdeployer.internal.CommonConstants.Action;
import org.eclipse.rtp.httpdeployer.internal.HttpDeployerUtils;
import org.eclipse.rtp.httpdeployer.internal.RequestResults;
import org.eclipse.rtp.httpdeployer.internal.XmlConstants;
import org.jdom.Document;
import org.jdom.Element;

public class RepositoryServlet extends AbstractHttpDeployerServlet {

	private static final long serialVersionUID = -4190823339335383710L;
	private final RepositoryManager repositoryManager;

	public RepositoryServlet(RepositoryManager repositoryManager) {
		this.repositoryManager = repositoryManager;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		URI[] repositories = repositoryManager.getRepositories();
		Element root = new Element(XmlConstants.XML_ELEMENT_REPOSITORIES);

		for (URI repository : repositories) {
			Element bundleXml = new Element(XmlConstants.XML_ELEMENT_REPOSITORY);
			bundleXml.addContent(new Element(XmlConstants.XML_ELEMENT_URI).addContent(repository.toString()));
			root.addContent(bundleXml);
		}

		HttpDeployerUtils.outputDocument(resp, new Document(root));
	}

	@Override
	public Document parseMultipartPostRequest(HttpServletRequest req) throws FileUploadException, IOException {
		RequestResults result = new RequestResults();
		List<FileItem> files = HttpDeployerUtils.parseMultipartRequest(req);

		if (files.size() != 1) {
			throw new FileUploadException("File not found");
		}

		try {
			InputStream repository = files.get(0).getInputStream();
			result.addResult(new RepositoryModificationResult(repositoryManager.addRepository(repository).toString(), null,
					Action.ADD));
		} catch (InvalidRepositoryException e) {
			result.addResult(new RepositoryModificationResult("local", e.getMessage(), Action.ADD));
		}

		// delete temporary files
		for (FileItem item : files) {
			item.delete();
		}

		return result.getDocument();
	}

	@Override
	public Document parseDeleteRequest(Document request) {
		return parseRequest(request, XmlConstants.XML_ELEMENT_REPOSITORY, Action.REMOVE);
	}

	@Override
	public Document parsePostRequest(Document request) {
		return parseRequest(request, XmlConstants.XML_ELEMENT_REPOSITORY, Action.ADD);
	}

	@Override
	public void handleOperation(RequestResults result, Element currentElement, Action action) {
		String repositoryPath = currentElement.getChildText(XmlConstants.XML_ELEMENT_URI);
		try {
			URI repository = new URI(repositoryPath);
			performRepositoryAction(repository, action);
			result.addResult(new RepositoryModificationResult(repositoryPath, null, action));
		} catch (URISyntaxException e) {
			// TODO: Not tested
			result.addResult(new RepositoryModificationResult(repositoryPath, e.getMessage(), action));
		}
	}

	private void performRepositoryAction(URI repository, Action action) {
		if (action.equals(Action.ADD)) {
			repositoryManager.addRepository(repository);
		} else {
			repositoryManager.removeRepository(repository);
		}
	}

}
