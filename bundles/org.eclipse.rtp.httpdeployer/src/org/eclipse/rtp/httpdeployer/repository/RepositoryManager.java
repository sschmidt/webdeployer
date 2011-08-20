/*******************************************************************************
 * Copyright (c) 2011 Sebastian Schmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Sebastian Schmidt - initial API and implementation
 ******************************************************************************/
package org.eclipse.rtp.httpdeployer.repository;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepositoryManager;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepositoryManager;
import org.eclipse.rtp.httpdeployer.internal.CommonConstants;

public class RepositoryManager {

	private static final String LOCAL_REPOSITORY_PREFIX = "repo_"; //$NON-NLS-N$
	private static final String FILENAME_CONTENT = "content.jar"; //$NON-NLS-N$
	private static final String FILENAME_ARTIFACTS = "artifacts.jar"; //$NON-NLS-N$
	private static final int FILE_BUFFER = 8192;
	private final IProvisioningAgent provisioningAgent;

	public RepositoryManager(IProvisioningAgent provisioningAgent) {
		this.provisioningAgent = provisioningAgent;
	}

	public URI[] getRepositories() {
		IMetadataRepositoryManager metaRepoManager = getMetadataRepositoryManager();

		return metaRepoManager.getKnownRepositories(IMetadataRepositoryManager.REPOSITORIES_ALL);
	}

	public void addRepository(URI repository) {
		IMetadataRepositoryManager metaRepoManager = getMetadataRepositoryManager();
		IArtifactRepositoryManager artiRepoManager = getArtifactRepositoryManager();

		metaRepoManager.addRepository(repository);
		artiRepoManager.addRepository(repository);
	}

	public URI addRepository(InputStream inputStream) throws InvalidRepositoryException, FileNotFoundException, IOException {
		ZipInputStream zis = new ZipInputStream(inputStream);
		File repository = createLocalRepository(zis);
		URI repositoryURI = repository.toURI();
		addRepository(repositoryURI);

		return repositoryURI;
	}

	private File createLocalRepository(ZipInputStream zis) throws InvalidRepositoryException, IOException, FileNotFoundException {
		File repository = new File(System.getProperty("java.io.tmpdir") + CommonConstants.DIR_SEPARATOR + LOCAL_REPOSITORY_PREFIX
				+ Long.toString(System.nanoTime()));
		repository.mkdirs();
		createLocalRepositoryStructure(zis, repository);

		try {
			validateLocalRepository(repository);
		} catch (InvalidRepositoryException e) {
			FileUtils.deleteDirectory(repository);
			throw e;
		}

		return repository;
	}

	public void removeRepository(URI repository) {
		IMetadataRepositoryManager metaRepoManager = getMetadataRepositoryManager();
		IArtifactRepositoryManager artiRepoManager = getArtifactRepositoryManager();

		metaRepoManager.removeRepository(repository);
		artiRepoManager.removeRepository(repository);
	}

	private void createLocalRepositoryStructure(ZipInputStream zis, File repository) throws IOException, FileNotFoundException {
		ZipEntry currentFile;
		while ((currentFile = zis.getNextEntry()) != null) {
			if (currentFile.isDirectory()) {
				createLocalDirectory(repository, currentFile);
			} else {
				createLocalFile(zis, repository, currentFile);
			}
		}
	}

	private void createLocalDirectory(File repository, ZipEntry currentFile) {
		File file = new File(repository.getAbsolutePath() + CommonConstants.DIR_SEPARATOR + currentFile.getName());
		file.mkdirs();
	}

	private void createLocalFile(ZipInputStream zis, File repository, ZipEntry currentFile) throws IOException,
			FileNotFoundException {
		File file = new File(repository.getAbsolutePath() + CommonConstants.DIR_SEPARATOR + currentFile.getName());
		file.getParentFile().mkdirs();
		file.createNewFile();
		FileOutputStream fos = new FileOutputStream(file);
		BufferedOutputStream bfos = new BufferedOutputStream(fos, FILE_BUFFER);
		saveFileData(zis, bfos);
	}

	private void saveFileData(ZipInputStream zis, BufferedOutputStream bfos) throws IOException {
		int resultLength;
		byte[] data = new byte[FILE_BUFFER];
		while ((resultLength = zis.read(data, 0, FILE_BUFFER)) != -1) {
			bfos.write(data, 0, resultLength);
		}
		bfos.flush();
		bfos.close();
	}

	public void validateLocalRepository(File repository) throws InvalidRepositoryException {
		File[] files = repository.listFiles();
		boolean artifactsFound = false;
		boolean contentFound = false;

		for (File file : files) {
			if (file.getName().equals(FILENAME_ARTIFACTS)) {
				artifactsFound = true;
			} else if (file.getName().equals(FILENAME_CONTENT)) {
				contentFound = true;
			}
		}

		if (!artifactsFound || !contentFound) {
			throw new InvalidRepositoryException("invalid repository: required files not found");
		}
	}

	public IMetadataRepositoryManager getMetadataRepositoryManager() {
		IMetadataRepositoryManager service = (IMetadataRepositoryManager) provisioningAgent
				.getService(IMetadataRepositoryManager.SERVICE_NAME);

		return service;
	}

	public IArtifactRepositoryManager getArtifactRepositoryManager() {
		IArtifactRepositoryManager service = (IArtifactRepositoryManager) provisioningAgent
				.getService(IArtifactRepositoryManager.SERVICE_NAME);

		return service;
	}

	public void removeLocalRepository(URI repository) throws IOException {
		FileUtils.deleteDirectory(new File(repository));
		removeRepository(repository);
	}
}