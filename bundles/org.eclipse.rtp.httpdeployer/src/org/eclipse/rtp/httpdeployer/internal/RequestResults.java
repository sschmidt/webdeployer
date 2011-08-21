/*******************************************************************************
 * Copyright (c) 2011 Sebastian Schmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Sebastian Schmidt - initial API and implementation
 ******************************************************************************/
package org.eclipse.rtp.httpdeployer.internal;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

public class RequestResults {
	private final List<IModificationResult> results = new ArrayList<IModificationResult>();

	public void addResult(IModificationResult result) {
		results.add(result);
	}

	public Document getDocument() {
		Element root = new Element(XmlConstants.XML_ELEMENT_BUNDLES);
		for (IModificationResult result : results) {
			root.addContent(result.getDocument());
		}

		return new Document(root);
	}
}
