/*******************************************************************************
 * Copyright (c) 2011 Sebastian Schmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Sebastian Schmidt - initial API and implementation
 ******************************************************************************/
package org.eclipse.rtp.httpdeployer.client;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.httpclient.methods.RequestEntity;
import org.jdom.Document;
import org.jdom.output.XMLOutputter;

public class JdomRequestEntity implements RequestEntity {

	private String result;

	public JdomRequestEntity(Document document) {
		XMLOutputter outputter = new XMLOutputter();
		result = outputter.outputString(document);
	}

	@Override
	public boolean isRepeatable() {
		return false;
	}

	@Override
	public void writeRequest(OutputStream out) throws IOException {
		out.write(result.getBytes());
	}

	@Override
	public long getContentLength() {
		return result.length();
	}

	@Override
	public String getContentType() {
		return "text/xml";
	}

}
