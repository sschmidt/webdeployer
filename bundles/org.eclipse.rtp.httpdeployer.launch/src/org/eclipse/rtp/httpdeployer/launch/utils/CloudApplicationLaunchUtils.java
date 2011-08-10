/*******************************************************************************
 * Copyright (c) 2011 Sebastian Schmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Sebastian Schmidt - initial API and implementation
 ******************************************************************************/

package org.eclipse.rtp.httpdeployer.launch.utils;

import org.eclipse.pde.internal.core.PDECore;
import org.eclipse.pde.internal.core.ifeature.IFeatureModel;

@SuppressWarnings("restriction")
public final class CloudApplicationLaunchUtils {

	private CloudApplicationLaunchUtils() {
		// prevent instantiation
	}

	public static IFeatureModel getFeatureModel(String featureId, String featureVersion) {
		IFeatureModel[] models = PDECore.getDefault().getFeatureModelManager().getModels();
		for (IFeatureModel model : models) {
			if (model.getFeature().getId().equals(featureId) && model.getFeature().getVersion().equals(featureVersion)) {
				return model;
			}
		}

		return null;
	}

	public static String getTempDir() {
		return System.getProperty("java.io.tmpdir") + "/build_" + Long.toString(System.nanoTime());
	}
}
