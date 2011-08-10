/*******************************************************************************
 * Copyright (c) 2011 Sebastian Schmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Sebastian Schmidt - initial API and implementation
 ******************************************************************************/
package org.eclipse.rtp.httpdeployer.launch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;

public final class CloudApplicationLaunchConfigValidator {

	private CloudApplicationLaunchConfigValidator() {
		// prevent instantiation
	}

	public static boolean validate(ILaunchConfiguration config) throws CoreException {
		return validate(new CloudApplicationLaunchConfig(config));
	}

	public static boolean validate(CloudApplicationLaunchConfig config) throws CoreException {
		if (!config.getFeatureId().equals("") && !config.getServerUri().equals("")) {
			return true;
		} else {
			return false;
		}
	}
}
