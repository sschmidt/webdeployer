/*******************************************************************************
 * Copyright (c) 2011 Sebastian Schmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Sebastian Schmidt - initial API and implementation
 ******************************************************************************/
package org.eclipse.rtp.httpdeployer.repository;

public class RepositoryModificationError {

	private final String repository;
	private final String reason;

	public RepositoryModificationError(String repository, String reason) {
		this.repository = repository;
		this.reason = reason;
	}

	public String getReason() {
		return reason;
	}

	public String getRepository() {
		return repository;
	}
}