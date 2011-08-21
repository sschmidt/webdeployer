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
import java.io.InputStream;
import java.io.StringReader;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.eclipse.rtp.httpdeployer.internal.AbstractHttpDeployerServlet;
import org.eclipse.rtp.httpdeployer.internal.CommonConstants.Action;
import org.eclipse.rtp.httpdeployer.internal.HttpDeployerUtils;
import org.eclipse.rtp.httpdeployer.internal.RequestResults;
import org.eclipse.rtp.httpdeployer.internal.XmlConstants;
import org.eclipse.rtp.httpdeployer.repository.RepositoryManager;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class FeatureServlet extends AbstractHttpDeployerServlet {

	private static final long serialVersionUID = 8439160853108236583L;
	private final FeatureManager featureManager;
	private final RepositoryManager repositoryManager;

	public FeatureServlet(FeatureManager featureManager, RepositoryManager repositoryManager) {
		this.featureManager = featureManager;
		this.repositoryManager = repositoryManager;
	}

	@Override
	public Document parsePostRequest(Document requestDocument) {
		return parseRequest(requestDocument, XmlConstants.XML_ELEMENT_FEATURE, Action.INSTALL);
	}

	@Override
	public Document parseDeleteRequest(Document requestDocument) {
		return parseRequest(requestDocument, XmlConstants.XML_ELEMENT_FEATURE, Action.UNINSTALL);
	}

	@Override
	public Document parseMultipartPostRequest(HttpServletRequest request) throws Exception {
		List<FileItem> files = HttpDeployerUtils.parseMultipartRequest(request);
		if (files.size() != 2) {
			throw new FileUploadException("Files not found");
		}

		InputStream repository = getRepositoryFromMultipart(files);
		repositoryManager.addRepository(repository);
		Document feature = getFeatureRequestFromMultipart(files);
		Document installRequest = parseRequest(feature, XmlConstants.XML_ELEMENT_FEATURE, Action.INSTALL);

		return installRequest;
	}

	private Document getFeatureRequestFromMultipart(List<FileItem> files) throws JDOMException, IOException {
		String feature;
		if (files.get(0).isFormField()) {
			feature = files.get(0).getString();
		} else {
			feature = files.get(1).getString();
		}
		SAXBuilder builder = new SAXBuilder();
		return builder.build(new StringReader(feature));
	}

	private InputStream getRepositoryFromMultipart(List<FileItem> files) throws IOException {
		if (files.get(0).isFormField()) {
			return files.get(1).getInputStream();
		} else {
			return files.get(0).getInputStream();
		}
	}

	@Override
	public void handleOperation(RequestResults result, Element currentElement, Action action) {
		String name = currentElement.getChildText(XmlConstants.XML_ELEMENT_NAME);
		String version = currentElement.getChildText(XmlConstants.XML_ELEMENT_VERSION);
		if (action == Action.UNINSTALL) {
			version = null;
		}

		handleOperation(result, name, version, action);
	}

	private void handleOperation(RequestResults result, String name, String version, Action action) {
		try {
			if (action.equals(Action.INSTALL)) {
				featureManager.installFeature(name, version);
				result.addResult(new FeatureModificationResult(name, version, action));
			} else if (action.equals(Action.UNINSTALL)) {
				featureManager.uninstallFeature(name, version);
				result.addResult(new FeatureModificationResult(name, version, action));
			}
		} catch (FeatureInstallException e) {
			// TODO: Not tested
			result.addResult(new FeatureModificationResult(name, version, action, e));
		}
	}
}
