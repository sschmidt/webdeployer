/*******************************************************************************
 * Copyright (c) 2011 Sebastian Schmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Sebastian Schmidt - initial API and implementation
 ******************************************************************************/
package org.eclipse.rtp.httpdeployer.internal;

public final class CommonConstants {
	public static final String RESPONSE_CONTENT_TYPE = "text/xml"; //$NON-NLS-N$
	public static final String DIR_SEPARATOR = "/"; //$NON-NLS-N$
	
	public enum Action {
		START, STOP, INSTALL, UNINSTALL, ADD, REMOVE
	}
	
	private CommonConstants() {
	  // prevent instantiation
	}
}
