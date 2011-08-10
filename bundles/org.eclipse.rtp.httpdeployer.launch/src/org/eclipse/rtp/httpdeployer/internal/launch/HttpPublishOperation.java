/*******************************************************************************
 * Copyright (c) 2011 Sebastian Schmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Sebastian Schmidt - initial API and implementation
 ******************************************************************************/
package org.eclipse.rtp.httpdeployer.internal.launch;

import java.io.File;
import java.io.IOException;

import org.apache.commons.httpclient.HttpException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.pde.internal.core.PDECore;
import org.eclipse.pde.internal.core.PDECoreMessages;
import org.eclipse.pde.internal.core.exports.FeatureExportOperation;
import org.eclipse.pde.internal.core.util.CoreUtility;
import org.eclipse.rtp.httpdeployer.client.HttpDeployerClient;
import org.eclipse.rtp.httpdeployer.launch.utils.ZipUtils;

@SuppressWarnings("restriction")
public class HttpPublishOperation extends FeatureExportOperation {

	protected final HttpPublishConfig info;
	private File repositoryZip = null;

	public HttpPublishOperation(HttpPublishConfig info, String name) {
		super(info, name);
		this.info = info;
	}

	@Override
	public IStatus run(IProgressMonitor monitor) {
		monitor.beginTask("", 40);
		IStatus status = super.run(new SubProgressMonitor(monitor, 20));

		if (status.isOK()) {
			try {
				uploadRepository(monitor, new SubProgressMonitor(monitor, 20));
			} catch (HttpException e) {
				status = new Status(IStatus.ERROR, PDECore.PLUGIN_ID,
						PDECoreMessages.FeatureBasedExportOperation_ProblemDuringExport, e);
			} catch (IOException e) {
				status = new Status(IStatus.ERROR, PDECore.PLUGIN_ID,
						PDECoreMessages.FeatureBasedExportOperation_ProblemDuringExport, e);
			} finally {
				deleteBuildFiles();
				monitor.done();
			}
		}

		return status;
	}

	private void deleteBuildFiles() {
		CoreUtility.deleteContent(new File(info.getDestinationDirectory()));
		if (repositoryZip != null) {
			repositoryZip.delete();
		}
	}

	private void uploadRepository(IProgressMonitor localMonitor, IProgressMonitor monitor) throws HttpException, IOException {
		monitor.beginTask("", 20);
		monitor.setTaskName("Upload repository to " + info.getHttpDeployerServiceUrl());
		String targetUrl = info.getHttpDeployerServiceUrl();
		HttpDeployerClient service = new HttpDeployerClient(targetUrl);
		repositoryZip = zipRepositoryToFile();
		monitor.worked(10);
		service.uploadRepository(repositoryZip);
		monitor.worked(10);
		monitor.done();
	}

	private File zipRepositoryToFile() throws IOException {
		String destinationDirectory = info.getDestinationDirectory();
		File repository = File.createTempFile("repository", ".zip"); //$NON-NLS-N$
		repository.deleteOnExit();
		ZipUtils.zip(new File(destinationDirectory), repository);
		return repository;
	}
}
