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
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

// TODO: move to own package
// TODO: remove Strings
// TODO: Test
public class HttpDeployerClient {
	public static final String FEATURE_GROUP_SUFFIX = ".feature.group";
	public static final String REPOSITORY_PATH = "/repository"; //$NON-NLS-N$
	public static final String BUNDLE_PATH = "/bundle"; //$NON-NLS-N$
	public static final String FEATURE_PATH = "/feature"; //$NON-NLS-N$

	private final String serviceUri;
	private final HttpClient client;

	public HttpDeployerClient(String serviceUri) {
		// strip last /
		if (serviceUri.endsWith("/")) {
			serviceUri = serviceUri.substring(0, serviceUri.length() - 1);
		}
		this.serviceUri = serviceUri;
		client = new HttpClient();
	}

	public void installFeature(File repository, String id, String version) throws HttpException, IOException {
		deleteFeature(id, version);

		PostMethod method = new PostMethod(serviceUri + FEATURE_PATH);
		XMLOutputter outputter = new XMLOutputter();
		String featureInstall = outputter.outputString(getFeatureOperation(id, version));

		Part[] parts = new Part[] { new FilePart("repository", repository), new StringPart("feature", featureInstall) };
		MultipartRequestEntity entity = new MultipartRequestEntity(parts, method.getParams());
		method.setRequestEntity(entity);

		executeMethod(method);
	}

	private Document getFeatureOperation(String id, String version) {
		Document document = new Document();
		Element features = new Element("features");
		Element featureXml = new Element("feature");
		featureXml.addContent(new Element("name").addContent(id + FEATURE_GROUP_SUFFIX));
		featureXml.addContent(new Element("version").addContent(version));
		features.addContent(featureXml);
		document.setRootElement(features);

		return document;
	}

	public void uploadRepository(File repository) throws HttpException, IOException {
		PostMethod method = new PostMethod(serviceUri + REPOSITORY_PATH);
		Part[] parts = new Part[] { new FilePart("repository", repository) };
		MultipartRequestEntity entity = new MultipartRequestEntity(parts, method.getParams());
		method.setRequestEntity(entity);

		executeMethod(method);
	}

	public void installFeature(String id, String version) throws IOException {
		PostMethod post = new PostMethod(serviceUri + FEATURE_PATH);
		DeleteMethodRequestEntity delete = new DeleteMethodRequestEntity(serviceUri + FEATURE_PATH);
		Document document = getFeatureOperation(id, version);
		delete.setRequestEntity(new JdomRequestEntity(document));
		post.setRequestEntity(new JdomRequestEntity(document));

		executeMethod(delete);
		executeMethod(post);
	}

	public void deleteFeature(String id, String version) throws IOException {
		DeleteMethodRequestEntity delete = new DeleteMethodRequestEntity(serviceUri + FEATURE_PATH);
		Document document = getFeatureOperation(id, version);
		delete.setRequestEntity(new JdomRequestEntity(document));

		executeMethod(delete);
	}

	private void executeMethod(HttpMethod method) throws IOException {
		int result = client.executeMethod(method);
		if (result > 299) {
			throw new HttpException("invalid response code: HTTP " + result);
		}
	}

	public void startPlugin(String id, String version) throws IOException {
		PostMethod post = new PostMethod(serviceUri + BUNDLE_PATH);
		Document document = new Document();
		Element features = new Element("bundles");
		Element featureXml = new Element("bundle");
		featureXml.addContent(new Element("name").addContent(id));
		featureXml.addContent(new Element("version").addContent(version));
		features.addContent(featureXml);
		document.setRootElement(features);
		post.setRequestEntity(new JdomRequestEntity(document));

		executeMethod(post);
	}

	// TODO: extend, move to own project
}
