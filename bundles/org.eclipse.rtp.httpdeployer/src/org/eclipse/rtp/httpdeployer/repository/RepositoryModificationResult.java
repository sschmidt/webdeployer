/*******************************************************************************
 * Copyright (c) 2011 Sebastian Schmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Sebastian Schmidt - initial API and implementation
 ******************************************************************************/
package org.eclipse.rtp.httpdeployer.repository;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.rtp.httpdeployer.internal.XmlConstants;
import org.jdom.Document;
import org.jdom.Element;

public class RepositoryModificationResult {
	private final List<String> successfulResults = new ArrayList<String>();
	private final List<RepositoryModificationError> failedResults = new ArrayList<RepositoryModificationError>();

	public Document getDocument() {
		Element root = new Element(XmlConstants.XML_ELEMENT_REPOSITORIES);
		addSuccessfulResults(root);
		addFailedResults(root);

		return new Document(root);
	}

	private void addFailedResults(Element root) {
		for (RepositoryModificationError repository : failedResults) {
			Element bundleXml = new Element(XmlConstants.XML_ELEMENT_REPOSITORY);
			bundleXml.addContent(new Element(XmlConstants.XML_ELEMENT_URI).addContent(repository.getRepository()));
			bundleXml.addContent(new Element(XmlConstants.XML_ELEMENT_REASON).addContent(repository.getReason()));
			bundleXml.addContent(new Element(XmlConstants.XML_ELEMENT_STATUS).addContent(XmlConstants.XML_VALUE_STATUS_FAILED));
			root.addContent(bundleXml);
		}
	}

	private void addSuccessfulResults(Element root) {
		for (String repository : successfulResults) {
			Element bundleXml = new Element(XmlConstants.XML_ELEMENT_REPOSITORY);
			bundleXml.addContent(new Element(XmlConstants.XML_ELEMENT_URI).addContent(repository));
			bundleXml.addContent(new Element(XmlConstants.XML_ELEMENT_STATUS)
					.addContent(XmlConstants.XML_VALUE_STATUS_SUCCESSFUL));
			root.addContent(bundleXml);
		}
	}

	public void addSuccess(String repository) {
		successfulResults.add(repository);
	}

	public void addFailure(String repository, String reason) {
		failedResults.add(new RepositoryModificationError(repository, reason));
	}
}
