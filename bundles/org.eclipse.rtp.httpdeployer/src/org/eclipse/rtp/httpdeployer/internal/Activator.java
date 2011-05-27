/*******************************************************************************
 * Copyright (c) 2011 Sebastian Schmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Sebastian Schmidt - initial API and implementation
 ******************************************************************************/
package org.eclipse.rtp.httpdeployer.internal;

import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.engine.IProfileRegistry;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

	public static final String DEFAULT_PROFILE_ID = "_HTTPDEPLOY_";
	private static Activator activator = null;
	private ServiceTracker<IProvisioningAgent, IProvisioningAgent> PAServiceTracker;

	private BundleContext context;
	private IProvisioningAgent provisioningAgent;
	private IProfileRegistry profileRegistry;

	public BundleContext getContext() {
		return context;
	}

	public IProvisioningAgent getProvisioningAgent() {
		return provisioningAgent;
	}

	public IProfileRegistry getProfileRegistry() {
		return profileRegistry;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.setInstance(this);
		getInstance().context = bundleContext;

		ProvisioningAgentServiceTracker PAServiceTrackerImpl = new ProvisioningAgentServiceTracker(this);
		/*
		 * TODO: You should be able to reference this service via ds as well.
		 */
		PAServiceTracker = new ServiceTracker<IProvisioningAgent, IProvisioningAgent>(bundleContext,
				IProvisioningAgent.SERVICE_NAME, PAServiceTrackerImpl);
		PAServiceTracker.open();
	}

	/*
	 * TODO: Move static methods and variables to the top
	 */
	public static Activator getInstance() {
		return activator;
	}

	public void setProvisioningAgent(IProvisioningAgent provisioningAgent) {
		this.provisioningAgent = provisioningAgent;
	}

	public void setProfileRegistry(IProfileRegistry profileRegistry) {
		this.profileRegistry = profileRegistry;
	}

	private static void setInstance(Activator newActivator) {
		activator = newActivator;
	}

	public void stop(BundleContext bundleContext) throws Exception {
		getInstance().context = null;
		PAServiceTracker.close();
	}

}
