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
import org.eclipse.equinox.p2.operations.ProfileChangeOperation;
import org.eclipse.equinox.p2.operations.ProvisioningJob;
import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.equinox.p2.operations.UninstallOperation;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.eclipse.rtp.httpdeployer.repository.RepositoryManager;

@SuppressWarnings("restriction")
public class FeatureManager {

	public enum Action {
		UNINSTALL, INSTALL
	}

	private final IProvisioningAgent provisioningAgent;
	private final RepositoryManager repositoryManager;
	private final Configurator configurator;

	public FeatureManager(IProvisioningAgent provisioningAgent, RepositoryManager repositoryManager, Configurator configurator) {
		this.provisioningAgent = provisioningAgent;
		this.repositoryManager = repositoryManager;
		this.configurator = configurator;
	}

	// TODO: Not tested
	public void installFeature(String featureId, String version) throws FeatureInstallException {
		ProfileChangeOperation operation = resolveProfileChangeOperation(featureId, version, Action.INSTALL);
		executeProfileChangeOperation(operation);
		applyChanges();
	}

	// TODO: Not tested
	public void uninstallFeature(String featureId, String version) throws FeatureInstallException {
		ProfileChangeOperation operation = resolveProfileChangeOperation(featureId, version, Action.UNINSTALL);
		executeProfileChangeOperation(operation);
		applyChanges();
	}

	// TODO: Not tested
	private ProfileChangeOperation resolveProfileChangeOperation(String featureId, String version, Action action)
			throws FeatureInstallException {
		ProvisioningSession session = new ProvisioningSession(provisioningAgent);
		ProvisioningContext context = createProvisioningContext();

		Collection<IInstallableUnit> units = getInstallableUnits(context, featureId, version);
		ProfileChangeOperation operation = getOperation(session, units, action);
		operation.setProvisioningContext(context);
		resolveOperation(operation);

		return operation;
	}

	private ProfileChangeOperation getOperation(ProvisioningSession session, Collection<IInstallableUnit> units, Action action)
			throws FeatureInstallException {
		ProfileChangeOperation operation;
		if (action.equals(Action.UNINSTALL)) {
			operation = new UninstallOperation(session, units);
		} else {
			operation = new InstallOperation(session, units);
		}

		return operation;
	}

	private void resolveOperation(ProfileChangeOperation operation) throws FeatureInstallException {
		IStatus result = operation.resolveModal(null);

		if (!result.isOK()) {
			String errorMessage = generateErrorMessage(result);
			throw new FeatureInstallException(errorMessage);
		}
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

	protected void executeProfileChangeOperation(ProfileChangeOperation operation) throws FeatureInstallException {
		ProvisioningJob provisioningJob = operation.getProvisioningJob(new NullProgressMonitor());
		if (provisioningJob == null) {
			return;
		}

		IStatus result = provisioningJob.runModal(new NullProgressMonitor());

		if (!result.isOK()) {
			throw new FeatureInstallException(result.getMessage());
		}
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
		Collection<IInstallableUnit> toInstall = context.getMetadata(null)
				.query(QueryUtil.createIUQuery(id, Version.create(version)), null).toUnmodifiableSet();
		return toInstall;
	}
}