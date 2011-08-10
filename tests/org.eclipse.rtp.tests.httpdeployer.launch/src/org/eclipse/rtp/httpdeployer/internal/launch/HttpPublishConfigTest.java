/*******************************************************************************
 * Copyright (c) 2011 Sebastian Schmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Sebastian Schmidt - initial API and implementation
 ******************************************************************************/
package org.eclipse.rtp.httpdeployer.internal.launch;

import org.junit.Test;
import static org.junit.Assert.*;

public class HttpPublishConfigTest {

	private static final String VALID_SERVER_URI = "http://localhost";

	@Test
	public void createDefaultConfigurationTest() {
		HttpPublishConfig config = HttpPublishConfig.createDefaultConfiguration(VALID_SERVER_URI);

		assertEquals(VALID_SERVER_URI, config.getHttpDeployerServiceUrl());
		assertEquals(true, config.isAllowBinaryCycles());
		assertArrayEquals(null, config.getJnlpInfo());
		assertEquals(null, config.getQualifier());
		assertEquals(true, config.isToDirectory());
		assertArrayEquals(null, config.getSigningInfo());
		assertArrayEquals(null, config.getTargets());
		assertEquals(true, config.isUseJarFormat());
		assertEquals(false, config.isUseWorkspaceCompiledClasses());
		assertEquals(true, config.isExportMetadata());
		assertEquals(true, config.isExportSource());
		assertEquals(true, config.isExportSourceBundle());
		assertEquals(null, config.getCategoryDefinition());
		assertArrayEquals(null, config.getItems());

		String destinationDirectory = config.getDestinationDirectory();
		assertTrue(destinationDirectory.startsWith(System.getProperty("java.io.tmpdir") + "/build_"));
	}
}
