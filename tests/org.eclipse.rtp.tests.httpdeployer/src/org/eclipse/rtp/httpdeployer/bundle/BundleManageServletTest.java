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
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doThrow;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

public class BundleManageServletTest {

	@Mock
	private Bundle bundle;

	@Mock
	private HttpServletResponse response;

	@Mock
	private HttpServletRequest request;

	private BundleManageServlet objectUnderTest;

	private Writer responseWriter = new StringWriter();

	private PrintWriter responsePrintWriter = new PrintWriter(responseWriter);

	private static final String VALID_DELETE_REQUEST = "<bundles><bundle><name>bundle</name></bundle></bundles>";

	private static final String VALID_START_REQUEST = "<bundles><bundle><action>start</action><name>bundle</name></bundle></bundles>";

	private static final String VALID_STOP_REQUEST = "<bundles><bundle><action>stop</action><name>bundle</name></bundle></bundles>";

	@Before
	public void setUp() throws IOException {
		MockitoAnnotations.initMocks(this);
		this.objectUnderTest = new MockBundleManageServlet();
		when(response.getWriter()).thenReturn(responsePrintWriter);
	}

	@Test
	public void startUnknownBundleTest() throws IOException, ServletException, JDOMException {
		when(bundle.getSymbolicName()).thenReturn("bundle1");
		when(request.getReader()).thenReturn(new BufferedReader(new StringReader(VALID_START_REQUEST)));

		objectUnderTest.doPost(request, response);

		BundleManageResponse response = parseResponse();
		assertEquals("bundle", response.getName());
		assertEquals("START", response.getAction());
		assertEquals("bundle not found", response.getReason());
	}

	@Test
	public void startBundleTest() throws IOException, ServletException, JDOMException, BundleException {
		when(bundle.getSymbolicName()).thenReturn("bundle");
		when(request.getReader()).thenReturn(new BufferedReader(new StringReader(VALID_START_REQUEST)));

		objectUnderTest.doPost(request, response);

		BundleManageResponse response = parseResponse();
		assertEquals("bundle", response.getName());
		assertEquals("START", response.getAction());
		assertEquals(null, response.getReason());
		verify(bundle).start();
	}

	@Test
	public void startBundleExceptionTest() throws IOException, ServletException, JDOMException, BundleException {
		when(bundle.getSymbolicName()).thenReturn("bundle");
		doThrow(new BundleException("fancy exception")).when(bundle).start();
		when(request.getReader()).thenReturn(new BufferedReader(new StringReader(VALID_START_REQUEST)));

		objectUnderTest.doPost(request, response);

		BundleManageResponse response = parseResponse();
		assertEquals("bundle", response.getName());
		assertEquals("START", response.getAction());
		assertEquals("fancy exception", response.getReason());
		verify(bundle).start();
	}

	@Test
	public void stopBundleTest() throws IOException, ServletException, JDOMException, BundleException {
		when(bundle.getSymbolicName()).thenReturn("bundle");
		when(request.getReader()).thenReturn(new BufferedReader(new StringReader(VALID_STOP_REQUEST)));

		objectUnderTest.doPost(request, response);

		BundleManageResponse response = parseResponse();
		assertEquals("bundle", response.getName());
		assertEquals("STOP", response.getAction());
		assertEquals(null, response.getReason());
		verify(bundle).stop();
	}

	@Test
	public void deleteBundleTest() throws IOException, ServletException, JDOMException, BundleException {
		when(bundle.getSymbolicName()).thenReturn("bundle");
		when(request.getReader()).thenReturn(new BufferedReader(new StringReader(VALID_DELETE_REQUEST)));

		objectUnderTest.doDelete(request, response);

		BundleManageResponse response = parseResponse();
		assertEquals("bundle", response.getName());
		assertEquals("UNINSTALL", response.getAction());
		assertEquals(null, response.getReason());
		verify(bundle).uninstall();
	}

	@Test
	public void deleteInvalidRequestTest() throws IOException, ServletException, JDOMException, BundleException {
		when(request.getReader()).thenReturn(new BufferedReader(new StringReader("according to jim")));
		objectUnderTest.doDelete(request, response);
		verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);
	}

	@Test
	public void startInvalidRequestTest() throws IOException, ServletException, JDOMException, BundleException {
		when(request.getReader()).thenReturn(new BufferedReader(new StringReader("according to jim")));
		objectUnderTest.doPost(request, response);
		verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);
	}

	private BundleManageResponse parseResponse() throws JDOMException, IOException {
		SAXBuilder builder = new SAXBuilder();

		Document request = builder.build(new ByteArrayInputStream(responseWriter.toString().getBytes()));
		Element response = (Element) request.getRootElement().getChildren().get(0);

		return new BundleManageResponse(response.getChildText("name"), response.getChildText("action"),
				response.getChildText("reason"));
	}

	private class MockBundleManageServlet extends BundleManageServlet {
		private static final long serialVersionUID = 1L;

		@Override
		protected Bundle[] receiveBundlesFromContext() {
			return new Bundle[] { BundleManageServletTest.this.bundle };
		}
	}

	private class BundleManageResponse {

		private final String name;
		private final String action;
		private final String reason;

		public BundleManageResponse(String name, String action, String reason) {
			this.name = name;
			this.action = action;
			this.reason = reason;
		}

		public String getReason() {
			return reason;
		}

		public String getAction() {
			return action;
		}

		public String getName() {
			return name;
		}
	}
}
