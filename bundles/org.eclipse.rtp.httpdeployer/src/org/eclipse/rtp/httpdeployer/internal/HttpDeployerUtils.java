/*******************************************************************************
 * Copyright (c) 2011 Sebastian Schmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Sebastian Schmidt - initial API and implementation
 ******************************************************************************/
package org.eclipse.rtp.httpdeployer.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

public class HttpDeployerUtils {

	private HttpDeployerUtils() {
		// prevent instantiation
	}

	public static Bundle[] receiveBundles() {
		Bundle currentBundle = FrameworkUtil.getBundle(HttpDeployerUtils.class);
		BundleContext context = currentBundle.getBundleContext();
		Bundle[] bundles = context.getBundles();
		return bundles;
	}

	public static Bundle[] receiveBundles(int requestType) {
		Bundle[] bundles = HttpDeployerUtils.receiveBundles();

		List<Bundle> validBundles = new ArrayList<Bundle>();
		for (Bundle bundle : bundles) {
			if (bundle.getState() == requestType || requestType == 0) {
				validBundles.add(bundle);
			}
		}

		return validBundles.toArray(new Bundle[0]);
	}

	public static Bundle searchBundle(String searchedBundle) {
		Bundle[] bundles = receiveBundles();
		for (Bundle bundle : bundles) {
			if (bundle.getSymbolicName().equalsIgnoreCase(searchedBundle)) {
				return bundle;
			}
		}

		return null;
	}

	public static Document parseXmlRequest(HttpServletRequest request) throws JDOMException, IOException {
		SAXBuilder builder = new SAXBuilder();
		Document xmlDocument = builder.build(request.getReader());
		return xmlDocument;
	}

	public static void outputDocument(HttpServletResponse response, Document document) throws IOException {
		response.setContentType(CommonConstants.RESPONSE_CONTENT_TYPE);
		XMLOutputter out = new XMLOutputter();
		out.output(document, response.getWriter());
	}

	@SuppressWarnings("unchecked")
	public static List<FileItem> parseMultipartRequest(HttpServletRequest request) throws FileUploadException {
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		return upload.parseRequest(request);
	}
}
