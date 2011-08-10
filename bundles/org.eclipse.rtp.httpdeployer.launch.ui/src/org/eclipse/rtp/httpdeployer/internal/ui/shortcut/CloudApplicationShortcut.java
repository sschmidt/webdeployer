/*******************************************************************************
 * Copyright (c) 2011 Sebastian Schmidt and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Sebastian Schmidt - initial API and implementation
 ******************************************************************************/
package org.eclipse.rtp.httpdeployer.internal.ui.shortcut;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.pde.ui.launcher.OSGiLaunchShortcut;
import org.eclipse.ui.IEditorPart;

public class CloudApplicationShortcut extends OSGiLaunchShortcut {

	@Override
	public void launch(ISelection selection, String mode) {
		launch(mode);
	}

	@Override
	public void launch(IEditorPart editor, String mode) {
		launch(mode);
	}

	protected String getLaunchConfigurationTypeName() {
		return "org.eclipse.rtp.launch.CloudApplication"; //$NON-NLS-N$
	}
}