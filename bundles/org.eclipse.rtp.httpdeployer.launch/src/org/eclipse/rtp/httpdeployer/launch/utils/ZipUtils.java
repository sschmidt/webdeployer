/*******************************************************************************
 * Copyright (c) 2011 Sebastian Schmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Sebastian Schmidt - initial API and implementation
 ******************************************************************************/
package org.eclipse.rtp.httpdeployer.launch.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class ZipUtils {

	private ZipUtils() {
	}

	public static void zip(File file, File output) throws IOException {
		FileOutputStream outputStream = new FileOutputStream(output);
		ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);

		String rootDirectory;
		if (file.isDirectory()) {
			rootDirectory = file.getPath();
		} else {
			rootDirectory = file.getParentFile().getPath();
		}

		zip(file, rootDirectory, zipOutputStream);
		zipOutputStream.close();
	}

	private static void zip(File currentFile, String directory, ZipOutputStream zipOutputStream) throws IOException {
		if (currentFile.isDirectory()) {
			for (File file : currentFile.listFiles()) {
				zip(file, directory, zipOutputStream);
			}
		} else {
			ZipEntry entry = new ZipEntry(currentFile.getPath().substring(directory.length() + 1));
			zipOutputStream.putNextEntry(entry);
			copyFileToStream(currentFile, zipOutputStream);
		}
	}

	private static void copyFileToStream(File currentFile, OutputStream outputStream) throws IOException {
		int read = 0;
		byte[] buffer = new byte[8192];
		FileInputStream in = new FileInputStream(currentFile);
		while (-1 != (read = in.read(buffer))) {
			outputStream.write(buffer, 0, read);
		}
		in.close();
	}
}