/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     EclipseSource - initial API and implementation
 ******************************************************************************/

package org.eclipse.birt.newcharts.views;

import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

  public ApplicationWorkbenchWindowAdvisor( IWorkbenchWindowConfigurer configurer )
  {
    super( configurer );
  }

  public ActionBarAdvisor createActionBarAdvisor( IActionBarConfigurer configurer )
  {
    return new ApplicationActionBarAdvisor( configurer );
  }

  public void preWindowOpen() {
    IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
    configurer.setInitialSize( new Point( 800, 600 ) );
    configurer.setShowCoolBar( false );
    configurer.setShowStatusLine( false );
    configurer.setTitle( "Chart Demo Application" );
  }
}
