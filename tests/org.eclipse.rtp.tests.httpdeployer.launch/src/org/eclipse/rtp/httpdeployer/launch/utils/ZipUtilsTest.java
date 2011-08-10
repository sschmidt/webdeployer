/*******************************************************************************
 * Copyright (c) 2011 Sebastian Schmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Sebastian Schmidt - initial API and implementation
 ******************************************************************************/
package org.eclipse.rtp.httpdeployer.launch.utils;

import static org.junit.Assert.assertEquals;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.Test;

public class ZipUtilsTest {
	
	private HashSet<String> FIXTURE_FILES = new HashSet<String>();

	@Test
	public void zipRecursiveDirectoryTest() throws IOException {
		FIXTURE_FILES.add("root.txt");
		FIXTURE_FILES.add("folder1/folder1.txt");
		
		File input = new File("fixtures/zip");
		File output = new File(CloudApplicationLaunchUtils.getTempDir());
		ZipUtils.zip(input, output);

		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(output)));
		ZipEntry zipEntry;
		while((zipEntry = zis.getNextEntry()) != null) {
			FIXTURE_FILES.remove(zipEntry.getName());
		}
		output.delete();
		
		assertEquals(0, FIXTURE_FILES.size());
	}
	

	@Test
	public void zipFileTest() throws IOException {
		FIXTURE_FILES.add("root.txt");

		File input = new File("fixtures/zip/root.txt");
		File output = new File(CloudApplicationLaunchUtils.getTempDir());
		ZipUtils.zip(input, output);

		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(output)));
		ZipEntry zipEntry;
		while((zipEntry = zis.getNextEntry()) != null) {
			FIXTURE_FILES.remove(zipEntry.getName());
		}
		output.delete();
		
		assertEquals(0, FIXTURE_FILES.size());
	}
}
