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
import org.junit.Test;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@SuppressWarnings("restriction")
public class CloudApplicationLaunchUtilsTest {

	@Test
	public void getFeatureModelTest() {
		IFeatureModel[] models = PDECore.getDefault().getFeatureModelManager().getModels();
		if (models.length == 0) {
			fail("this test requires a valid feature in the target platform");
		}

		IFeatureModel featureModel = models[0];
		String id = featureModel.getFeature().getId();
		String version = featureModel.getFeature().getVersion();

		IFeatureModel model = CloudApplicationLaunchUtils.getFeatureModel(id, version);
		assertEquals(featureModel, model);

		version = version + "not available version number";
		model = CloudApplicationLaunchUtils.getFeatureModel(id, version);
		assertNull(model);
	}
}
