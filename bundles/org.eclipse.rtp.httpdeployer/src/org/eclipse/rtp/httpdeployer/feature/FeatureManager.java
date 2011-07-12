/*******************************************************************************
 * Copyright (c) 2011 Sebastian Schmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Sebastian Schmidt - initial API and implementation
 ******************************************************************************/
package org.eclipse.rtp.httpdeployer.feature;

import java.io.IOException;
import java.util.Collection;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.equinox.internal.provisional.configurator.Configurator;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.engine.ProvisioningContext;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.metadata.Version;
import org.eclipse.equinox.p2.operations.InstallOperation;
import org.eclipse.equinox.p2.operations.ProvisioningJob;
import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.eclipse.rtp.httpdeployer.repository.RepositoryManager;

@SuppressWarnings("restriction")
public class FeatureManager {
	private final IProvisioningAgent provisioningAgent;
	private final NullProgressMonitor installationProgressMonitor;
	private final RepositoryManager repositoryManager;
	private final Configurator configurator;

	public FeatureManager(IProvisioningAgent provisioningAgent, RepositoryManager repositoryManager, Configurator configurator) {
		this.provisioningAgent = provisioningAgent;
		this.repositoryManager = repositoryManager;
		this.configurator = configurator;
		this.installationProgressMonitor = new NullProgressMonitor();
	}

	public Collection<IInstallableUnit> installFeature(String featureId, String version) throws FeatureInstallException {
		ProvisioningSession session = new ProvisioningSession(provisioningAgent);
		ProvisioningContext context = createProvisioningContext();

		Collection<IInstallableUnit> units = getInstallableUnits(context, featureId, version);
		InstallOperation installOperation = getInstallOperation(context, session, units);
		executeInstallOperation(installOperation);
		applyChanges();

		return units;
	}

	public Collection<IInstallableUnit> uninstallFeature(String name, String version) {
		// TODO: continue;
		return null;
	}

	private void applyChanges() throws FeatureInstallException {
		try {
			configurator.applyConfiguration();
		} catch (IOException e) {
			throw new FeatureInstallException(e);
		}
	}

	protected ProvisioningContext createProvisioningContext() {
		ProvisioningContext context = new ProvisioningContext(provisioningAgent);
		context.setArtifactRepositories(repositoryManager.getRepositories());
		context.setArtifactRepositories(repositoryManager.getRepositories());
		return context;
	}

	protected void executeInstallOperation(InstallOperation installOperation) throws FeatureInstallException {
		ProvisioningJob provisioningJob = installOperation.getProvisioningJob(new NullProgressMonitor());
		IStatus result = provisioningJob.runModal(new NullProgressMonitor());

		if (!result.isOK()) {
			throw new FeatureInstallException(result.getMessage());
		}
	}

	protected InstallOperation getInstallOperation(ProvisioningContext context, ProvisioningSession session,
			Collection<IInstallableUnit> units) throws FeatureInstallException {
		InstallOperation installOperation = new InstallOperation(session, units);
		installOperation.setProvisioningContext(context);
		IStatus result = installOperation.resolveModal(null);

		if (!result.isOK()) {
			String errorMessage = generateErrorMessage(result);
			throw new FeatureInstallException(errorMessage);
		}

		return installOperation;
	}

	private String generateErrorMessage(IStatus result) {
		IStatus[] children = result.getChildren();
		String message = "";
		for (int i = 0; i < children.length; i++) {
			message = children[i].getMessage() + "\n";
		}
		return message;
	}

	protected Collection<IInstallableUnit> getInstallableUnits(ProvisioningContext context, String id, String version) {
		Collection<IInstallableUnit> toInstall = context.getMetadata(installationProgressMonitor)
				.query(QueryUtil.createIUQuery(id, Version.create(version)), installationProgressMonitor).toUnmodifiableSet();
		return toInstall;
	}
}