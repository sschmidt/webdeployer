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
import org.eclipse.rtp.httpdeployer.repository.RepositoryServlet.Action;
import org.jdom.Document;
import org.jdom.Element;

public  class RepositoryModificationResult {
	private final List<SingleRepositoryModificationResult> results = new ArrayList<SingleRepositoryModificationResult>();

	public Document getDocument() {
		Element root = new Element(XmlConstants.XML_ELEMENT_REPOSITORIES);
		for (SingleRepositoryModificationResult result : results) {
			root.addContent(result.getDocument());
		}

		return new Document(root);
	}

	public void addSuccess(String repository, Action action) {
		results.add(new SingleRepositoryModificationResult(repository, null, action));
	}

	public void addFailure(String repository, String reason, Action action) {
		results.add(new SingleRepositoryModificationResult(repository, reason, action));
	}

	private class SingleRepositoryModificationResult {

		private final Action action;
		private final String repository;
		private final String reason;

		private SingleRepositoryModificationResult(String repository, String reason, Action action) {
			this.repository = repository;
			this.reason = reason;
			this.action = action;
		}

		public Element getDocument() {
			Element xmlResult = new Element(XmlConstants.XML_ELEMENT_REPOSITORY);
			xmlResult.addContent(new Element(XmlConstants.XML_ELEMENT_URI).addContent(repository));
			xmlResult.addContent(new Element(XmlConstants.XML_ELEMENT_ACTION).addContent(action.toString()));
			if (reason == null) {
				xmlResult.addContent(new Element(XmlConstants.XML_ELEMENT_STATUS)
						.addContent(XmlConstants.XML_VALUE_STATUS_SUCCESSFUL));
			} else {
				xmlResult.addContent(new Element(XmlConstants.XML_ELEMENT_STATUS)
						.addContent(XmlConstants.XML_VALUE_STATUS_FAILED));
				xmlResult.addContent(new Element(XmlConstants.XML_ELEMENT_REASON).addContent(reason));
			}

			return xmlResult;
		}
	}
}
