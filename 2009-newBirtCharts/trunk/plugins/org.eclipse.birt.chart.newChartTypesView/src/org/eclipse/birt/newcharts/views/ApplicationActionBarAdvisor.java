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

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

/**
 * An action bar advisor is responsible for creating, adding, and disposing of
 * the actions added to a workbench window. Each window will be populated with
 * new actions.
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

  private IWorkbenchAction exitAction;

  public ApplicationActionBarAdvisor( IActionBarConfigurer configurer ) {
    super( configurer );
  }

  protected void makeActions( final IWorkbenchWindow window ) {
    exitAction = ActionFactory.QUIT.create( window );
    register( exitAction );
  }

  protected void fillMenuBar( IMenuManager menuBar ) {
    MenuManager fileMenu = new MenuManager( "&File",
                                            IWorkbenchActionConstants.M_FILE );
    menuBar.add( fileMenu );
    fileMenu.add( exitAction );
  }
}
