/*******************************************************************************
 * Copyright (c) 2011 Sebastian Schmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Sebastian Schmidt - initial API and implementation
 ******************************************************************************/
package org.eclipse.rtp.httpdeployer.client;

import java.io.File;
import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.jdom.Document;
import org.jdom.Element;

public class HttpDeployerClient {
	private static final String REPOSITORY_PATH = "/repository"; //$NON-NLS-N$
	private static final String BUNDLE_PATH = "/bundle"; //$NON-NLS-N$

	private final String serviceUri;
	private final HttpClient client;

	public HttpDeployerClient(String serviceUri) {
		this.serviceUri = serviceUri;
		client = new HttpClient();
	}

	public void uploadRepository(File repository) throws HttpException, IOException {
		PostMethod method = new PostMethod(serviceUri + REPOSITORY_PATH);
		Part[] parts = new Part[] { new FilePart("repository", repository) };
		MultipartRequestEntity entity = new MultipartRequestEntity(parts, method.getParams());
		method.setRequestEntity(entity);

		executeMethod(method);
	}

	public void startBundle(String name, String version) throws HttpException, IOException {
		PostMethod post = new PostMethod(serviceUri + BUNDLE_PATH);
		Document document = new Document();
		Element bundles = new Element("bundles");
		Element bundleXml = new Element("bundle");
		bundleXml.addContent(new Element("name").addContent(name));
		bundleXml.addContent(new Element("version").addContent(version));
		bundleXml.addContent(new Element("action").addContent("start"));
		bundles.addContent(bundleXml);
		document.setRootElement(bundles);
		post.setRequestEntity(new JdomRequestEntity(document));

		executeMethod(post);
	}

	private void executeMethod(HttpMethod method) throws IOException {
		int result = client.executeMethod(method);
		if(result > 299) {
			throw new HttpException("invalid response code: HTTP " + result);
		}
	}

	// TODO: extend, move to own project
}
