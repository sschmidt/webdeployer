/*******************************************************************************
 * Copyright (c) 2011 Sebastian Schmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Sebastian Schmidt - initial API and implementation
 ******************************************************************************/
package org.eclipse.rtp.httpdeployer.repository;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.io.FileUtils;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepositoryManager;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepositoryManager;
import org.eclipse.rtp.httpdeployer.internal.CommonConstants;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class RepositoryManagerTest {

	@Mock
	private IProvisioningAgent paMock;
	@Mock
	private IMetadataRepositoryManager mrmMock;
	@Mock
	private IArtifactRepositoryManager armMock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		when(paMock.getService(IMetadataRepositoryManager.SERVICE_NAME)).thenReturn(mrmMock);
		when(paMock.getService(IArtifactRepositoryManager.SERVICE_NAME)).thenReturn(armMock);
	}

	@Test
	public void addRemoteRepositoryTest() throws URISyntaxException {
		RepositoryManager manager = new RepositoryManager(paMock);
		URI repository = new URI("file:/tmp");
		manager.addRepository(repository);

		verify(mrmMock).addRepository(repository);
		verify(armMock).addRepository(repository);
	}

	@Test
	public void removeRemoteRepositoryTest() throws URISyntaxException {
		RepositoryManager manager = new RepositoryManager(paMock);
		URI repository = new URI("file:/tmp");
		manager.removeRepository(repository);

		verify(mrmMock).removeRepository(repository);
		verify(armMock).removeRepository(repository);
	}

	@Test
	public void getAllRepositoriesTest() {
		RepositoryManager manager = new RepositoryManager(paMock);
		manager.getRepositories();

		verify(mrmMock).getKnownRepositories(IMetadataRepositoryManager.REPOSITORIES_ALL);
	}

	@Test(expected = InvalidRepositoryException.class)
	public void addEmptyLocalRepositoryTest() throws InvalidRepositoryException, FileNotFoundException, IOException {
		RepositoryManager manager = new RepositoryManager(paMock);
		InputStream stream = new ByteArrayInputStream(new byte[1]);
		manager.addRepository(stream);
	}

	@Test(expected = InvalidRepositoryException.class)
	public void addInvalidLocalRepositoryTest() throws InvalidRepositoryException, IOException {
		RepositoryManager manager = new RepositoryManager(paMock);
		InputStream stream = new FileInputStream("fixtures/invalidRepositoryPackage.zip");
		manager.addRepository(stream);
	}

	@Test
	public void addValidLocalRepository() throws InvalidRepositoryException, IOException {
		RepositoryManager manager = new RepositoryManager(paMock);
		InputStream stream = new FileInputStream("fixtures/validRepositoryPackage.zip");
		URI uri = manager.addRepository(stream);

		assertNotNull(uri);
		verify(mrmMock).addRepository(uri);
		verify(armMock).addRepository(uri);

		FileUtils.deleteDirectory(new File(uri));
	}
	
	@Test
	public void removeLocalRepositoryTest() throws URISyntaxException, IOException {
		RepositoryManager manager = new RepositoryManager(paMock);
		File repository = new File(System.getProperty("java.io.tmpdir") + CommonConstants.DIR_SEPARATOR + "repo_"
				+ Long.toString(System.nanoTime()));
		repository.mkdirs();
		URI repo = repository.toURI();
		manager.removeLocalRepository(repo);
		
		assertFalse(repository.exists());
		verify(mrmMock).removeRepository(repo);
		verify(armMock).removeRepository(repo);
	}

}
