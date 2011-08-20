/*******************************************************************************
 * Copyright (c) 2011 Sebastian Schmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Sebastian Schmidt - initial API and implementation
 ******************************************************************************/
package org.eclipse.rtp.httpdeployer.bundle;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.rtp.httpdeployer.internal.CommonConstants.Action;
import org.eclipse.rtp.httpdeployer.internal.XmlConstants;
import org.jdom.Document;
import org.jdom.Element;

public class BundleModificationResult {

	private final List<SingleBundleModificationResult> results = new ArrayList<SingleBundleModificationResult>();

	public void addFailure(String name, Action action, String string) {
		results.add(new SingleBundleModificationResult(name, action, string));
	}

	public void addSuccess(String name, Action action) {
		results.add(new SingleBundleModificationResult(name, action));
	}

	public Document getDocument() {
		Element root = new Element(XmlConstants.XML_ELEMENT_BUNDLES);
		for (SingleBundleModificationResult result : results) {
			root.addContent(result.getDocument());
		}

		return new Document(root);
	}

	private class SingleBundleModificationResult {

		private final String name;
		private final Action action;
		private final String reason;

		public SingleBundleModificationResult(String name, Action action) {
			this(name, action, null);
		}

		public SingleBundleModificationResult(String name, Action action, String reason) {
			this.name = name;
			this.action = action;
			this.reason = reason;
		}

		public Element getDocument() {
			Element xmlResult = new Element(XmlConstants.XML_ELEMENT_BUNDLE);
			xmlResult.addContent(new Element(XmlConstants.XML_ELEMENT_NAME).addContent(name));
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
