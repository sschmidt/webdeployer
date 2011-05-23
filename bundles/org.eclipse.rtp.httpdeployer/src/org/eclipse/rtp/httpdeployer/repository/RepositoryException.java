/*******************************************************************************
 * Copyright (c) 2011 Sebastian Schmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Sebastian Schmidt - initial API and implementation
 ******************************************************************************/
package org.eclipse.rtp.httpdeployer.repository;

public class RepositoryException extends Exception {

	private static final long serialVersionUID = 1L;

	public RepositoryException(String string) {
		super(string);
	}

	public RepositoryException(Exception e) {
		super(e);
	}

}
