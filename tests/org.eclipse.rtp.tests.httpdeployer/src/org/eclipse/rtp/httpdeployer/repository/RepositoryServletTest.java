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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.eclipse.rtp.httpdeployer.util.MockServletInputStream;
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

	private static final String VALID_DELETE_CREATE_REQUEST = "<repositories><repository><uri>" + TEST_REPOSITORY_URI
			+ "</uri></repository></repositories>";
	
	private static final String VALID_DELETE_REQUEST_INVALID_URI = "<repositories><repository><uri>not an uri</uri></repository></repositories>";
	
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
	public void doValidDeleteInvalidUriTest() throws ServletException, IOException, JDOMException, URISyntaxException {
		when(requestMock.getReader()).thenReturn(new BufferedReader(new StringReader(VALID_DELETE_REQUEST_INVALID_URI)));
		repositoryServlet.doDelete(requestMock, responseMock);

		SAXBuilder builder = new SAXBuilder();
		Document request = builder.build(new ByteArrayInputStream(responseWriter.toString().getBytes()));
		Element repo = (Element) request.getRootElement().getChildren().get(0);
		assertEquals("not an uri", repo.getChildText("uri"));
		assertEquals("failed", repo.getChildText("status"));
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
	
	@Test
	public void doInvalidMultipartUploadTest() throws ServletException, IOException {
		when(requestMock.getMethod()).thenReturn("POST");
		when(requestMock.getContentType()).thenReturn(ServletFileUpload.MULTIPART_FORM_DATA + ";NO_BOUNDARY!!!!!!");
		repositoryServlet.doPost(requestMock, responseMock);
		verify(responseMock).sendError(400);
	}

	@Test
	public void doInvalidMultipartRequestTest() throws ServletException, IOException {
		when(requestMock.getMethod()).thenReturn("POST");
		when(requestMock.getContentType()).thenReturn(ServletFileUpload.MULTIPART_FORM_DATA + ";boundary=boundary");
		when(requestMock.getInputStream()).thenReturn(new MockServletInputStream(null));
		repositoryServlet.doPost(requestMock, responseMock);
		verify(responseMock).sendError(400);
	}

	@Test
	public void doValidMultipartUploadTest() throws Exception {
		when(requestMock.getMethod()).thenReturn("POST");
		when(requestMock.getContentType()).thenReturn(ServletFileUpload.MULTIPART_FORM_DATA + ";boundary=boundary");
		when(requestMock.getInputStream()).thenReturn(new MockServletInputStream(new FileInputStream("fixtures/validRepositoryPackage.zip")));
		URI uri = new URI("tmp://foo/bar");
		when(repoManagerMock.addRepository(any(InputStream.class))).thenReturn(uri);
		repositoryServlet.doPost(requestMock, responseMock);

		SAXBuilder builder = new SAXBuilder();
		Document request = builder.build(new ByteArrayInputStream(responseWriter.toString().getBytes()));
		Element repo = (Element) request.getRootElement().getChildren().get(0);

		assertEquals(uri.toString(), repo.getChildText("uri"));
		assertEquals("successful", repo.getChildText("status"));
	}
	
	@Test
	public void doValidMultipartUploadWithInvalidRepoTest() throws Exception {
		when(requestMock.getMethod()).thenReturn("POST");
		when(requestMock.getContentType()).thenReturn(ServletFileUpload.MULTIPART_FORM_DATA + ";boundary=boundary");
		when(requestMock.getInputStream()).thenReturn(new MockServletInputStream(new FileInputStream("fixtures/invalidRepositoryPackage.zip")));
		when(repoManagerMock.addRepository(any(InputStream.class))).thenThrow(new InvalidRepositoryException(""));
		repositoryServlet.doPost(requestMock, responseMock);

		SAXBuilder builder = new SAXBuilder();
		Document request = builder.build(new ByteArrayInputStream(responseWriter.toString().getBytes()));
		Element repo = (Element) request.getRootElement().getChildren().get(0);

		URI uri = new URI("local");
		assertEquals(uri.toString(), repo.getChildText("uri"));
		assertEquals("failed", repo.getChildText("status"));
	}
}
