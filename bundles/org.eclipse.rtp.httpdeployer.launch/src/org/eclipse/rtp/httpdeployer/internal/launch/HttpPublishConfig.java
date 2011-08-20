/*******************************************************************************
 * Copyright (c) 2011 Sebastian Schmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Sebastian Schmidt - initial API and implementation
 ******************************************************************************/
package org.eclipse.rtp.httpdeployer.internal.launch;

import org.eclipse.pde.internal.core.exports.FeatureExportInfo;
import org.eclipse.rtp.httpdeployer.launch.utils.CloudApplicationLaunchUtils;

@SuppressWarnings("restriction")
public class HttpPublishConfig extends FeatureExportInfo {

	public String httpDeployerServiceUrl;

	public boolean isToDirectory() {
		return toDirectory;
	}

	public void setToDirectory(boolean toDirectory) {
		this.toDirectory = toDirectory;
	}

	public boolean isUseJarFormat() {
		return useJarFormat;
	}

	public void setUseJarFormat(boolean useJarFormat) {
		this.useJarFormat = useJarFormat;
	}

	public boolean isExportSource() {
		return exportSource;
	}

	public void setExportSource(boolean exportSource) {
		this.exportSource = exportSource;
	}

	public boolean isExportSourceBundle() {
		return exportSourceBundle;
	}

	public void setExportSourceBundle(boolean exportSourceBundle) {
		this.exportSourceBundle = exportSourceBundle;
	}

	public boolean isExportMetadata() {
		return exportMetadata;
	}

	public void setExportMetadata(boolean exportMetadata) {
		this.exportMetadata = exportMetadata;
	}

	public boolean isAllowBinaryCycles() {
		return allowBinaryCycles;
	}

	public void setAllowBinaryCycles(boolean allowBinaryCycles) {
		this.allowBinaryCycles = allowBinaryCycles;
	}

	public boolean isUseWorkspaceCompiledClasses() {
		return useWorkspaceCompiledClasses;
	}

	public void setUseWorkspaceCompiledClasses(boolean useWorkspaceCompiledClasses) {
		this.useWorkspaceCompiledClasses = useWorkspaceCompiledClasses;
	}

	public String getDestinationDirectory() {
		return destinationDirectory;
	}

	public void setDestinationDirectory(String destinationDirectory) {
		this.destinationDirectory = destinationDirectory;
	}

	public String getQualifier() {
		return qualifier;
	}

	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
	}

	public Object[] getItems() {
		return items;
	}

	public void setItems(Object[] items) {
		this.items = items;
	}

	public String[] getSigningInfo() {
		return signingInfo;
	}

	public void setSigningInfo(String[] signingInfo) {
		this.signingInfo = signingInfo;
	}

	public String[] getJnlpInfo() {
		return jnlpInfo;
	}

	public void setJnlpInfo(String[] jnlpInfo) {
		this.jnlpInfo = jnlpInfo;
	}

	public String[][] getTargets() {
		return targets;
	}

	public void setTargets(String[][] targets) {
		this.targets = targets;
	}

	public String getCategoryDefinition() {
		return categoryDefinition;
	}

	public void setCategoryDefinition(String categoryDefinition) {
		this.categoryDefinition = categoryDefinition;
	}

	public String getHttpDeployerServiceUrl() {
		return httpDeployerServiceUrl;
	}

	public void setHttpDeployerServiceUrl(String httpDeployerServiceUrl) {
		this.httpDeployerServiceUrl = httpDeployerServiceUrl;
	}

	public static HttpPublishConfig createDefaultConfiguration(String serverUri) {
		HttpPublishConfig config = new HttpPublishConfig();
		config.setAllowBinaryCycles(true);
		config.setJnlpInfo(null);
		config.setQualifier(String.valueOf(Math.round(System.currentTimeMillis() + Math.random() * 10000000)));
		config.setToDirectory(true);
		config.setSigningInfo(null);
		config.setTargets(null);
		config.setUseJarFormat(true);
		config.setUseWorkspaceCompiledClasses(false);
		config.setExportMetadata(true);
		config.setExportSource(true);
		config.setExportSourceBundle(true);
		config.setCategoryDefinition(null);
		config.setDestinationDirectory(CloudApplicationLaunchUtils.getTempDir());
		config.setHttpDeployerServiceUrl(serverUri);
		return config;
	}

}
