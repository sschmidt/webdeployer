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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rtp.httpdeployer.internal.XmlConstants;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

public class BundleServletTest {

	private static final String MOCK_RESOLVED_BUNDLE_NAME = "resolvedBundle";
	private static final String MOCK_ACTIVE_BUNDLE_NAME = "activeBundle";

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
		List<Element> children = executeGetBundles();
		assertEquals(2, children.size());
		assertEquals(MOCK_ACTIVE_BUNDLE_NAME, children.get(0).getChild(XmlConstants.XML_ELEMENT_NAME).getValue());
		assertEquals(MOCK_RESOLVED_BUNDLE_NAME, children.get(1).getChild(XmlConstants.XML_ELEMENT_NAME).getValue());
	}

	@Test
	public void listAllBundles2Test() throws Exception {
		when(request.getPathInfo()).thenReturn(null);
		List<Element> children = executeGetBundles();
		assertEquals(2, children.size());
		assertEquals(MOCK_ACTIVE_BUNDLE_NAME, children.get(0).getChild(XmlConstants.XML_ELEMENT_NAME).getValue());
		assertEquals(MOCK_RESOLVED_BUNDLE_NAME, children.get(1).getChild(XmlConstants.XML_ELEMENT_NAME).getValue());
	}

	@Test
	public void listActiveBundlesTest() throws Exception {
		when(request.getPathInfo()).thenReturn(BundleServlet.REQUEST_PATH_ACTIVE_BUNDLES);
		List<Element> children = executeGetBundles();
		assertEquals(1, children.size());
		assertEquals(MOCK_ACTIVE_BUNDLE_NAME, children.get(0).getChild(XmlConstants.XML_ELEMENT_NAME).getValue());
	}

	@Test
	public void listResolvedBundlesTest() throws Exception {
		when(request.getPathInfo()).thenReturn(BundleServlet.REQUEST_PATH_RESOLVED_BUNDLES);
		List<Element> children = executeGetBundles();
		assertEquals(1, children.size());
		assertEquals(MOCK_RESOLVED_BUNDLE_NAME, children.get(0).getChild(XmlConstants.XML_ELEMENT_NAME).getValue());
	}

	@SuppressWarnings("unchecked")
	private List<Element> executeGetBundles() throws ServletException, IOException, JDOMException {
		BundleServlet servletUnderTest = new MockBundleServlet();
		servletUnderTest.doGet(request, response);

		SAXBuilder builder = new SAXBuilder();
		Document result = builder.build(new StringReader(responseStream.toString()));

		assertEquals(XmlConstants.XML_ELEMENT_BUNDLES, result.getRootElement().getName());
		List<Element> children = result.getRootElement().getChildren(XmlConstants.XML_ELEMENT_BUNDLE);

		Element firstBundle = children.get(0);
		assertNotNull(firstBundle);
		assertNotNull(firstBundle.getChild(XmlConstants.XML_ELEMENT_NAME));
		assertNotNull(firstBundle.getChild(XmlConstants.XML_ELEMENT_VERSION));

		return children;
	}

	public class MockBundleServlet extends BundleServlet {
		private static final long serialVersionUID = 1L;

		protected Bundle[] receiveBundles() {
			Bundle mockActiveBundle = mock(Bundle.class);
			when(mockActiveBundle.getState()).thenReturn(Bundle.ACTIVE);
			when(mockActiveBundle.getVersion()).thenReturn(new Version(0, 0, 0));
			when(mockActiveBundle.getSymbolicName()).thenReturn(MOCK_ACTIVE_BUNDLE_NAME);

			Bundle mockResolvedBundle = mock(Bundle.class);
			when(mockResolvedBundle.getState()).thenReturn(Bundle.RESOLVED);
			when(mockResolvedBundle.getVersion()).thenReturn(new Version(0, 0, 0));
			when(mockResolvedBundle.getSymbolicName()).thenReturn(MOCK_RESOLVED_BUNDLE_NAME);

			return new Bundle[] { mockActiveBundle, mockResolvedBundle };
		}
	}
}
