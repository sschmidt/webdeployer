/*******************************************************************************
 * Copyright (c) 2011 Sebastian Schmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Sebastian Schmidt - initial API and implementation
 ******************************************************************************/
package org.eclipse.rtp.httpdeployer.feature;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

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

import org.eclipse.rtp.httpdeployer.internal.XmlConstants;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class FeatureServletTest {

	@Mock
	private FeatureManager featureManager;

	@Mock
	private HttpServletResponse responseMock;

	@Mock
	private HttpServletRequest requestMock;

	private static final String VALID_INSTALL_REQUEST = "<features><feature><name>installMock</name><version>1.0</version>"
			+ "</feature></features>";

	private FeatureServlet featureServlet;
	private Writer responseWriter = new StringWriter();
	private PrintWriter responsePrintWriter = new PrintWriter(responseWriter);

	@Before
	public void setUp() throws IOException {
		MockitoAnnotations.initMocks(this);

		featureServlet = new FeatureServlet(featureManager, null);
		when(responseMock.getWriter()).thenReturn(responsePrintWriter);
	}

	@Test
	public void doInstallFeatureTest() throws ServletException, IOException, JDOMException, FeatureInstallException {
		when(requestMock.getReader()).thenReturn(new BufferedReader(new StringReader(VALID_INSTALL_REQUEST)));
		// return stupid http method to break multiform-check in this test
		when(requestMock.getMethod()).thenReturn("fooo");

		featureServlet.doPost(requestMock, responseMock);

		SAXBuilder builder = new SAXBuilder();
		Document request = builder.build(new ByteArrayInputStream(responseWriter.toString().getBytes()));
		Element response = (Element) request.getRootElement().getChildren().get(0);

		verify(featureManager).installFeature("installMock", "1.0");
		assertEquals(XmlConstants.XML_VALUE_STATUS_SUCCESSFUL, response.getChildText("status"));
		assertEquals("1.0", response.getChildText("version"));
		assertEquals("installMock", response.getChildText("name"));
	}

	@Test
	public void doInvalidInstallFeatureTest() throws ServletException, IOException, JDOMException, FeatureInstallException {
		when(requestMock.getReader()).thenReturn(new BufferedReader(new StringReader("really not valid...")));
		// return stupid http method to break multiform-check in this test
		when(requestMock.getMethod()).thenReturn("fooo");

		featureServlet.doPost(requestMock, responseMock);

		verify(responseMock).sendError(HttpServletResponse.SC_BAD_REQUEST);
	}

	@Test
	public void doInvalidUninstallFeatureTest() throws ServletException, IOException, JDOMException, FeatureInstallException {
		when(requestMock.getReader()).thenReturn(new BufferedReader(new StringReader("really not valid...")));
		featureServlet.doDelete(requestMock, responseMock);

		verify(responseMock).sendError(HttpServletResponse.SC_BAD_REQUEST);
	}

	@Test
	public void doValidUninstallFeatureTest() throws ServletException, IOException, JDOMException, FeatureInstallException {
		when(requestMock.getReader()).thenReturn(new BufferedReader(new StringReader(VALID_INSTALL_REQUEST)));
		featureServlet.doDelete(requestMock, responseMock);

		SAXBuilder builder = new SAXBuilder();
		Document request = builder.build(new ByteArrayInputStream(responseWriter.toString().getBytes()));
		Element response = (Element) request.getRootElement().getChildren().get(0);

		verify(featureManager).uninstallFeature("installMock", null);
		assertEquals(XmlConstants.XML_VALUE_STATUS_SUCCESSFUL, response.getChildText("status"));
		assertEquals("", response.getChildText("version"));
		assertEquals("installMock", response.getChildText("name"));
	}
}
