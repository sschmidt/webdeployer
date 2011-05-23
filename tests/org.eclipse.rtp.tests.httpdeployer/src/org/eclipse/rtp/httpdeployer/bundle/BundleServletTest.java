/*******************************************************************************
 * Copyright (c) 2011 Sebastian Schmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Sebastian Schmidt - initial API and implementation
 ******************************************************************************/
package org.eclipse.rtp.httpdeployer.bundle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rtp.httpdeployer.bundle.BundleServlet;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class BundleServletTest {
	@Mock
	private HttpServletResponse response;
	@Mock
	private HttpServletRequest request;
	
	private OutputStream responseStream;

	@Before
	public void setUp() throws IOException {
		MockitoAnnotations.initMocks(this);

		responseStream = new ByteArrayOutputStream();

		when(response.getWriter()).thenReturn(new PrintWriter(responseStream));
	}

	@Test
	public void listAllBundlesTest() throws Exception {
		when(request.getPathInfo()).thenReturn("/");
		testResultXml();
	}

	@Test
	public void listActiveBundlesTest() throws Exception {
		when(request.getPathInfo()).thenReturn("/active");
		testResultXml();
	}

	@Test
	public void listResolvedBundlesTest() throws Exception {
		when(request.getPathInfo()).thenReturn("/resolved");
		testResultXml();
	}

	private void testResultXml() throws Exception {
		BundleServlet servletUnderTest = new BundleServlet();
		servletUnderTest.doGet(request, response);

		SAXBuilder builder = new SAXBuilder();
		Document result = builder.build(new StringReader(responseStream.toString()));

		assertEquals("bundles", result.getRootElement().getName());
		Element firstBundle = result.getRootElement().getChild("bundle");
		assertNotNull(firstBundle);
		assertNotNull(firstBundle.getChild("name"));
		assertNotNull(firstBundle.getChild("version"));
	}
}
