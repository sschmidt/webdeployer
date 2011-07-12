/*******************************************************************************
 * Copyright (c) 2011 Sebastian Schmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Sebastian Schmidt - initial API and implementation
 ******************************************************************************/
package org.eclipse.rtp.httpdeployer.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ServletInputStream;

public class MockServletInputStream extends ServletInputStream {

	private static final String BOUNDARY = "boundary";
	private final InputStream is;

	public MockServletInputStream(InputStream is) throws IOException {
		StringBuilder builder = new StringBuilder();
		if (is != null) {
			BufferedReader inputReader = new BufferedReader(new InputStreamReader(is));
			builder.append("\r\n--" + BOUNDARY + "\r\n");
			builder.append("Content-Disposition: form-data; name=\"name\"\r\n\r\n");
			String current;
			while ((current = inputReader.readLine()) != null) {
				builder.append(current + "\n");
			}

			builder.append("\r\n--" + BOUNDARY + "--\r\n");
		}

		this.is = new ByteArrayInputStream(builder.toString().getBytes());
	}

	@Override
	public int read() throws IOException {
		return is.read();
	}

	@Override
	public int read(byte[] b) throws IOException {
		return is.read(b);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return is.read(b, off, len);
	}

	@Override
	public int available() throws IOException {
		return is.available();
	}

	@Override
	public long skip(long n) throws IOException {
		return is.skip(n);
	}

	@Override
	public boolean markSupported() {
		return is.markSupported();
	}
}
