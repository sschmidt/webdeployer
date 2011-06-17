/*******************************************************************************
 * Copyright (c) 2011 Sebastian Schmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Sebastian Schmidt - initial API and implementation
 ******************************************************************************/
package org.eclipse.rtp.httpdeployer.repository;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;

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

public class RepositoryServletTest {

	private static final String TEST_REPOSITORY_URI = "file:/tmp/playyard";

	private static final String VALID_DELETE_CREATE_REQUEST = "<repositories><repository>" + TEST_REPOSITORY_URI
			+ "</repository></repositories>";

	private RepositoryServlet repositoryServlet;

	@Mock
	private RepositoryManager repoManagerMock;

	@Mock
	private HttpServletResponse responseMock;

	@Mock
	private HttpServletRequest requestMock;

	private Writer responseWriter = new StringWriter();
	private PrintWriter responsePrintWriter = new PrintWriter(responseWriter);

	private URI[] repositories = null;

	@Before
	public void setUp() throws URISyntaxException, IOException {
		MockitoAnnotations.initMocks(this);

		repositoryServlet = new RepositoryServlet(repoManagerMock);

		repositories = new URI[2];
		repositories[0] = new URI("file:/tmp");
		repositories[1] = new URI("file:/tmp/foo");

		when(repoManagerMock.getRepositories()).thenReturn(repositories);
		when(responseMock.getWriter()).thenReturn(responsePrintWriter);
	}

	@Test
	public void doGetTest() throws ServletException, IOException, JDOMException {
		repositoryServlet.doGet(null, responseMock);
		SAXBuilder builder = new SAXBuilder();
		Document request = builder.build(new ByteArrayInputStream(responseWriter.toString().getBytes()));
		Element repo1 = (Element) request.getRootElement().getChildren().get(0);
		Element repo2 = (Element) request.getRootElement().getChildren().get(1);

		assertEquals(repositories[0].toString(), repo1.getChildText("uri"));
		assertEquals(repositories[1].toString(), repo2.getChildText("uri"));
	}

	@Test
	public void doValidDeleteTest() throws ServletException, IOException, JDOMException, URISyntaxException {
		when(requestMock.getReader()).thenReturn(new BufferedReader(new StringReader(VALID_DELETE_CREATE_REQUEST)));
		repositoryServlet.doDelete(requestMock, responseMock);

		SAXBuilder builder = new SAXBuilder();
		Document request = builder.build(new ByteArrayInputStream(responseWriter.toString().getBytes()));
		Element repo = (Element) request.getRootElement().getChildren().get(0);

		assertEquals(TEST_REPOSITORY_URI, repo.getChildText("uri"));
		assertEquals("successful", repo.getChildText("status"));

		verify(repoManagerMock).removeRepository(new URI(TEST_REPOSITORY_URI));
	}

	@Test
	public void doInvalidDeleteTest() throws ServletException, IOException, JDOMException {
		when(requestMock.getReader()).thenReturn(new BufferedReader(new StringReader("not valid :-)")));
		repositoryServlet.doDelete(requestMock, responseMock);

		verify(responseMock).sendError(HttpServletResponse.SC_BAD_REQUEST);
	}

	@Test
	public void doInvalidCreateRemoteRepositoryTest() throws ServletException, IOException, JDOMException {
		when(requestMock.getReader()).thenReturn(new BufferedReader(new StringReader("not valid :-)")));
		// return stupid http method to break multiform-check in this test
		when(requestMock.getMethod()).thenReturn("fooo");
		repositoryServlet.doPost(requestMock, responseMock);

		verify(responseMock).sendError(HttpServletResponse.SC_BAD_REQUEST);
	}

	@Test
	public void doValidCreateRemoteRepositoryTest() throws ServletException, IOException, JDOMException,
			URISyntaxException {
		when(requestMock.getReader()).thenReturn(new BufferedReader(new StringReader(VALID_DELETE_CREATE_REQUEST)));
		// return stupid http method to break multiform-check in this test
		when(requestMock.getMethod()).thenReturn("fooo");

		repositoryServlet.doPost(requestMock, responseMock);

		SAXBuilder builder = new SAXBuilder();
		Document request = builder.build(new ByteArrayInputStream(responseWriter.toString().getBytes()));
		Element repo = (Element) request.getRootElement().getChildren().get(0);

		assertEquals(TEST_REPOSITORY_URI, repo.getChildText("uri"));
		assertEquals("successful", repo.getChildText("status"));

		verify(repoManagerMock).addRepository(new URI(TEST_REPOSITORY_URI));
	}
}
