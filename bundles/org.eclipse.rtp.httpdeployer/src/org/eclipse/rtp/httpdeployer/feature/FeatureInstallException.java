/*******************************************************************************
 * Copyright (c) 2011 Sebastian Schmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Sebastian Schmidt - initial API and implementation
 ******************************************************************************/
package org.eclipse.rtp.httpdeployer.feature;

public class FeatureInstallException extends Exception {

	private static final long serialVersionUID = 7654279780139556052L;

	public FeatureInstallException(Exception e) {
	  // TODO: Not tested
		super(e);
	}

	public FeatureInstallException(String message) {
		super(message);
	}
}
