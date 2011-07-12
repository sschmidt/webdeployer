/*******************************************************************************
 * Copyright (c) 2011 Sebastian Schmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Sebastian Schmidt - initial API and implementation
 ******************************************************************************/
package org.eclipse.rtp.httpdeployer.feature;

import org.eclipse.equinox.internal.provisional.configurator.Configurator;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepositoryManager;
import org.eclipse.rtp.httpdeployer.repository.RepositoryManager;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@SuppressWarnings("restriction")
public class FeatureManagerTest {

	@Mock
	IProvisioningAgent provisioningAgent;

	@Mock
	RepositoryManager repositoryManager;

	@Mock
	Configurator configurator;

	@Mock
	IMetadataRepositoryManager metadataRepositoryMock;
	
	FeatureManager objectUnderTest;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		this.objectUnderTest = new FeatureManager(provisioningAgent, repositoryManager, configurator);
	}
}
