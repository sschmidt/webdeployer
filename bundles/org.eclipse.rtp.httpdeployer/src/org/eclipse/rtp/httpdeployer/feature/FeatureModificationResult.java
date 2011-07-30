/*******************************************************************************
 * Copyright (c) 2011 Sebastian Schmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Sebastian Schmidt - initial API and implementation
 ******************************************************************************/
package org.eclipse.rtp.httpdeployer.feature;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.rtp.httpdeployer.internal.XmlConstants;
import org.jdom.Document;
import org.jdom.Element;

public class FeatureModificationResult {

	private List<SingleFeatureModificationResult> results = new ArrayList<SingleFeatureModificationResult>();

	public enum Action {
		INSTALL, UNINSTALL
	}

	public void addSuccess(String name, String version, Action action) {
		results.add(new SingleFeatureModificationResult(name, version, action));
	}

	// TODO: Not tested
	public void addFailed(String name, String version, Action action, FeatureInstallException e) {
		results.add(new SingleFeatureModificationResult(name, version, action, e));
	}

	public Document getDocument() {
		Element root = new Element(XmlConstants.XML_ELEMENT_FEATURES);
		for (SingleFeatureModificationResult result : results) {
			root.addContent(result.getDocument());
		}

		return new Document(root);
	}

	// TODO: Not tested
	public Integer getResultSize() {
		return results.size();
	}

	private class SingleFeatureModificationResult {

		private final String name;
		private final String version;
		private final Action action;
		private final FeatureInstallException e;

		public SingleFeatureModificationResult(String name, String version, Action action, FeatureInstallException e) {
			this.name = name;
			this.version = version;
			this.action = action;
			this.e = e;
		}

		public SingleFeatureModificationResult(String name, String version, Action action) {
			this(name, version, action, null);
		}

		public Element getDocument() {
			Element xmlResult = new Element(XmlConstants.XML_ELEMENT_FEATURE);
			xmlResult.addContent(new Element(XmlConstants.XML_ELEMENT_NAME).addContent(name));
			xmlResult.addContent(new Element(XmlConstants.XML_ELEMENT_VERSION).addContent(version));
			xmlResult.addContent(new Element(XmlConstants.XML_ELEMENT_ACTION).addContent(action.toString()));
			if (e == null) {
				xmlResult.addContent(new Element(XmlConstants.XML_ELEMENT_STATUS)
						.addContent(XmlConstants.XML_VALUE_STATUS_SUCCESSFUL));
			} else {
			   // TODO: Not tested
				xmlResult.addContent(new Element(XmlConstants.XML_ELEMENT_STATUS)
						.addContent(XmlConstants.XML_VALUE_STATUS_FAILED));
				xmlResult.addContent(new Element(XmlConstants.XML_ELEMENT_REASON).addContent(e.getMessage()));
			}

			return xmlResult;
		}
	}

	public boolean isEmpty() {
		return results.size() == 0;
	}
}
