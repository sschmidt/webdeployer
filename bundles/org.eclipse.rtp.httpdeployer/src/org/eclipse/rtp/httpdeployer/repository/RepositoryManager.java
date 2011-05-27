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

	/*
	 * TODO: This is a fake class. It seems that it holds a state but it doesn't. 
	 * You initialize an object of this class only once with this agent:
	 * Activator.getInstance().getProvisioningAgent()
	 * 
	 * So, why do this class needs to hold a reference to the agent when you access it in a static way?
	 * 
	 * This class looks like a utility to me so far.
	 * 
	 */
	public RepositoryManager(IProvisioningAgent provisioningAgent) {
		this.provisioningAgent = provisioningAgent;
	}

    /*
     * TODO: Can be static
     */	
	public URI[] getRepositories() {
		IMetadataRepositoryManager metaRepoManager = getMetadataRepositoryManager();

		return metaRepoManager.getKnownRepositories(IMetadataRepositoryManager.REPOSITORIES_ALL);
	}

    /*
     * TODO: Can be static
     */	
	public void addRepository(URI repository) {
		IMetadataRepositoryManager metaRepoManager = getMetadataRepositoryManager();
		IArtifactRepositoryManager artiRepoManager = getArtifactRepositoryManager();

		metaRepoManager.addRepository(repository);
		artiRepoManager.addRepository(repository);
	}

    /*
     * TODO: Can be static
     */	
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
	
    /*
     * TODO: Can be static
     */
	public void removeRepository(URI repository) {
		IMetadataRepositoryManager metaRepoManager = getMetadataRepositoryManager();
		IArtifactRepositoryManager artiRepoManager = getArtifactRepositoryManager();

		metaRepoManager.removeRepository(repository);
		artiRepoManager.removeRepository(repository);
	}

    /*
     * TODO: Can be static
     */	
	public IMetadataRepository getMetadataRepository(URI uri) throws ProvisionException, OperationCanceledException {
		return getMetadataRepositoryManager().loadRepository(uri, new NullProgressMonitor());
	}
	
	/*
	 * TODO: This method is very long. I picked it out as an example. You are using many blank lines
	 * in your code. I don't like them ;) 
	 * Blank lines are always an indicator that the method is to long. There is one Exception.
	 * When you are writing test it's valid to separate the init, do and assert blocks.
	 * 
	 * Regarding the method below. It would split this in 4 separate methods. I think you can do this too ;)
	 */
	 /*
     * TODO: Can be static
     */
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

	 /*
     * TODO: Can be static
     */
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

	/*
	 * TODO: Can be static
	 */
	private IMetadataRepositoryManager getMetadataRepositoryManager() {
		IMetadataRepositoryManager service = (IMetadataRepositoryManager) provisioningAgent
				.getService(IMetadataRepositoryManager.SERVICE_NAME);
		if (service == null) {
			throw new IllegalStateException("IMetadataRepositoryManager-Service not found");
		}

		return service;
	}

	/*
	 * TODO: Can be static
	 */
	private IArtifactRepositoryManager getArtifactRepositoryManager() {
		IArtifactRepositoryManager service = (IArtifactRepositoryManager) provisioningAgent
				.getService(IArtifactRepositoryManager.SERVICE_NAME);
		if (service == null) {
			throw new IllegalStateException("IMetadataRepositoryManager-Service not found");
		}

		return service;
	}
}