/*******************************************************************************
 * Copyright (c) 2011 Sebastian Schmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Sebastian Schmidt - initial API and implementation
 ******************************************************************************/
package org.eclipse.rtp.httpdeployer.bundle;

import org.eclipse.rtp.httpdeployer.internal.CommonConstants.Action;
import org.eclipse.rtp.httpdeployer.internal.IModificationResult;
import org.eclipse.rtp.httpdeployer.internal.XmlConstants;
import org.jdom.Element;

public class BundleModificationResult implements IModificationResult {

	private final String name;
	private final Action action;
	private final String reason;

	public BundleModificationResult(String name, Action action) {
		this(name, action, null);
	}

	public BundleModificationResult(String name, Action action, String reason) {
		this.name = name;
		this.action = action;
		this.reason = reason;
	}

	@Override
	public Element getDocument() {
		Element xmlResult = new Element(XmlConstants.XML_ELEMENT_BUNDLE);
		xmlResult.addContent(new Element(XmlConstants.XML_ELEMENT_NAME).addContent(name));
		xmlResult.addContent(new Element(XmlConstants.XML_ELEMENT_ACTION).addContent(action.toString()));
		if (reason == null) {
			xmlResult.addContent(new Element(XmlConstants.XML_ELEMENT_STATUS)
					.addContent(XmlConstants.XML_VALUE_STATUS_SUCCESSFUL));
		} else {
			xmlResult.addContent(new Element(XmlConstants.XML_ELEMENT_STATUS).addContent(XmlConstants.XML_VALUE_STATUS_FAILED));
			xmlResult.addContent(new Element(XmlConstants.XML_ELEMENT_REASON).addContent(reason));
		}

		return xmlResult;
	}
}
