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
		RepositoryModificationResult result = new RepositoryModificationResult();
		List<FileItem> files = HttpDeployerUtils.parseMultipartRequest(req);

		if (files.size() != 1) {
			throw new FileUploadException("File not found");
		}

		try {
			InputStream repository = files.get(0).getInputStream();
			result.addSuccess(repositoryManager.addRepository(repository).toString(), Action.ADD);
		} catch (InvalidRepositoryException e) {
			result.addFailure("local", e.getMessage(), Action.ADD);
		}

		// delete temporary files
		for (FileItem item : files) {
			item.delete();
		}

		return result.getDocument();
	}

	@Override
	public Document parseDeleteRequest(Document request) {
		return parseXmlRequest(request, Action.REMOVE);
	}

	@Override
	public Document parsePostRequest(Document request) {
		return parseXmlRequest(request, Action.ADD);
	}

	private Document parseXmlRequest(Document request, Action action) {
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

		return result.getDocument();
	}

	private void performRepositoryAction(URI repository, Action action) {
		if (action.equals(Action.ADD)) {
			repositoryManager.addRepository(repository);
		} else {
			repositoryManager.removeRepository(repository);
		}
	}

}
