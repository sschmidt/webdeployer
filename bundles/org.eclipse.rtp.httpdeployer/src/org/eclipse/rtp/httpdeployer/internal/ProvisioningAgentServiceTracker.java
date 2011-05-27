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
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.engine.IProfileRegistry;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/*
 * TODO: DS is for avoiding writing code that uses the framework explicitly.
 * 
 * I think you can simply avoid this tracker by using ds and set the ference policy to dynamic and the 
 * cardinality to 1..n.
 */
public class ProvisioningAgentServiceTracker implements
		ServiceTrackerCustomizer<IProvisioningAgent, IProvisioningAgent> {

	private final Activator activator;

	public ProvisioningAgentServiceTracker(Activator activator) {
		this.activator = activator;
	}

	@Override
	public IProvisioningAgent addingService(ServiceReference<IProvisioningAgent> reference) {
		IProvisioningAgent agent = activator.getContext().getService(reference);
		activator.setProvisioningAgent(agent);

		IProfileRegistry profileRegistry = (IProfileRegistry) agent.getService(IProfileRegistry.SERVICE_NAME);
		activator.setProfileRegistry(profileRegistry);

		createDefaultProfile(profileRegistry);
		return agent;
	}

	private void createDefaultProfile(IProfileRegistry profileRegistry) {
		try {
			if (profileRegistry.getProfile(Activator.DEFAULT_PROFILE_ID) == null) {
				profileRegistry.addProfile(Activator.DEFAULT_PROFILE_ID);
			}
		} catch (ProvisionException e) {
			throw new RuntimeException("error adding default profile id");
		}
	}

	/*
	 * TODO: See what I mean? Here you have to override a method which is not used. This doesn't happen
	 * when using ds
	 */
	@Override
	public void modifiedService(ServiceReference<IProvisioningAgent> reference, IProvisioningAgent service) {

	}

	@Override
	public void removedService(ServiceReference<IProvisioningAgent> reference, IProvisioningAgent service) {
		activator.setProvisioningAgent(null);
		activator.setProfileRegistry(null);
	}
}
