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
import org.eclipse.pde.internal.core.ifeature.IFeatureModel;
import org.eclipse.pde.internal.core.ifeature.IFeaturePlugin;
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
		monitor.beginTask("", 80);
		IStatus status = super.run(new SubProgressMonitor(monitor, 20));

		if (status.isOK()) {
			try {
				installFeature(monitor, new SubProgressMonitor(monitor, 20));
				startBundles(monitor, new SubProgressMonitor(monitor, 20));
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

	private void startBundles(IProgressMonitor monitor, SubProgressMonitor subProgressMonitor) throws IOException {
		HttpDeployerClient service = createHttpDeployerClient();
		for (Object item : info.getItems()) {
			if (item instanceof IFeatureModel) {
				IFeatureModel feature = (IFeatureModel) item;
				IFeaturePlugin[] plugins = feature.getFeature().getPlugins();
				for (IFeaturePlugin plugin : plugins) {
					String version = plugin.getVersion().replaceAll("qualifier", info.getQualifier());
					service.startPlugin(plugin.getId(), version);
				}
			}
		}
	}

	private void installFeature(IProgressMonitor monitor, SubProgressMonitor subProgressMonitor) throws IOException {
		monitor.beginTask("", 20);
		monitor.setTaskName("Upload repository to " + info.getHttpDeployerServiceUrl());
		repositoryZip = zipRepositoryToFile();
		HttpDeployerClient service = createHttpDeployerClient();
		monitor.worked(10);
		for (Object item : info.getItems()) {
			if (item instanceof IFeatureModel) {
				IFeatureModel feature = (IFeatureModel) item;
				String version = feature.getFeature().getVersion();
				String id = feature.getFeature().getId();
				version = version.replaceAll("qualifier", info.getQualifier());
				service.installFeature(repositoryZip, id, version);
			}
		}
		monitor.worked(10);
		monitor.done();
	}

	private void deleteBuildFiles() {
		CoreUtility.deleteContent(new File(info.getDestinationDirectory()));
		if (repositoryZip != null) {
			repositoryZip.delete();
		}
	}

	private HttpDeployerClient createHttpDeployerClient() {
		String targetUrl = info.getHttpDeployerServiceUrl();
		HttpDeployerClient service = new HttpDeployerClient(targetUrl);
		return service;
	}

	private File zipRepositoryToFile() throws IOException {
		String destinationDirectory = info.getDestinationDirectory();
		File repository = File.createTempFile("repository", ".zip"); //$NON-NLS-N$
		ZipUtils.zip(new File(destinationDirectory), repository);
		return repository;
	}
}
