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
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepositoryManager;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepository;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepositoryManager;

public class RepositoryManager {

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

	public URI addRepository(InputStream inputStream) throws RepositoryException {
		ZipInputStream zis = new ZipInputStream(inputStream);

		File repository = null;

		try {
			repository = new File(System.getProperty("java.io.tmpdir") + "/repo_" + Long.toString(System.nanoTime()));

			if (!repository.mkdirs()) {
				throw new RepositoryException("error creating repository directory");
			}

			createLocalRepository(zis, repository);
			try {
				validateLocalRepository(repository);
			} catch (RepositoryException e) {
				FileUtils.deleteDirectory(repository);
				throw e;
			}
		} catch (IOException e) {
			throw new RepositoryException(e);
		}

		URI repositoryURI = repository.toURI();
		addRepository(repositoryURI);
		return repositoryURI;
	}

	public void removeRepository(URI repository) {
		IMetadataRepositoryManager metaRepoManager = getMetadataRepositoryManager();
		IArtifactRepositoryManager artiRepoManager = getArtifactRepositoryManager();

		metaRepoManager.removeRepository(repository);
		artiRepoManager.removeRepository(repository);
	}

	public IMetadataRepository getMetadataRepository(URI uri) throws ProvisionException, OperationCanceledException {
		return getMetadataRepositoryManager().loadRepository(uri, new NullProgressMonitor());
	}

	private void createLocalRepository(ZipInputStream zis, File repository) throws IOException, FileNotFoundException {
		ZipEntry currentFile;
		while ((currentFile = zis.getNextEntry()) != null) {
			if (currentFile.isDirectory()) {
				new File(repository.getAbsolutePath() + "/" + currentFile.getName()).mkdir();
			} else {
				File file = new File(repository.getAbsolutePath() + "/" + currentFile.getName());
				file.createNewFile();
				FileOutputStream fos = new FileOutputStream(file);
				BufferedOutputStream bfos = new BufferedOutputStream(fos, FILE_BUFFER);

				int resultLength;
				byte[] data = new byte[FILE_BUFFER];
				while ((resultLength = zis.read(data, 0, FILE_BUFFER)) != -1) {
					bfos.write(data, 0, resultLength);
				}

				bfos.flush();
				bfos.close();
			}
		}
	}

	private void validateLocalRepository(File repository) throws RepositoryException {
		File[] files = repository.listFiles();
		boolean artifactsFound = false;
		boolean contentFound = false;

		for (File file : files) {
			if (file.getName().equals("artifacts.jar")) {
				artifactsFound = true;
			} else if (file.getName().equals("content.jar")) {
				contentFound = true;
			}
		}

		if (!artifactsFound) {
			throw new RepositoryException("invalid repository: artifacts.jar expected");
		}

		if (!contentFound) {
			throw new RepositoryException("invalid repository: content.jar expected");
		}
	}

	private IMetadataRepositoryManager getMetadataRepositoryManager() {
		IMetadataRepositoryManager service = (IMetadataRepositoryManager) provisioningAgent
				.getService(IMetadataRepositoryManager.SERVICE_NAME);
		if (service == null) {
			throw new IllegalStateException("IMetadataRepositoryManager-Service not found");
		}

		return service;
	}

	private IArtifactRepositoryManager getArtifactRepositoryManager() {
		IArtifactRepositoryManager service = (IArtifactRepositoryManager) provisioningAgent
				.getService(IArtifactRepositoryManager.SERVICE_NAME);
		if (service == null) {
			throw new IllegalStateException("IMetadataRepositoryManager-Service not found");
		}

		return service;
	}
}