/*******************************************************************************
 * Copyright (c) 2007, 2014 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.riena.sample.app.client.rcpmail;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

import org.eclipse.riena.ui.swt.facades.ActionFactoryFacade;

/**
 * An action bar advisor is responsible for creating, adding, and disposing of
 * the actions added to a workbench window. Each window will be populated with
 * new actions.
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	// Actions - important to allocate these only in makeActions, and then use
	// them
	// in the fill methods. This ensures that the actions aren't recreated
	// when fillActionBars is called with FILL_PROXY.
	private IWorkbenchAction exitAction;
	private IWorkbenchAction aboutAction;
	private IWorkbenchAction newWindowAction;
	private OpenViewAction openViewAction;
	private Action messagePopupAction;

	public ApplicationActionBarAdvisor(final IActionBarConfigurer configurer) {
		super(configurer);
	}

	@Override
	protected void makeActions(final IWorkbenchWindow window) {
		// Creates the actions and registers them.
		// Registering is needed to ensure that key bindings work.
		// The corresponding commands keybindings are defined in the plugin.xml
		// file.
		// Registering also provides automatic disposal of the actions when
		// the window is closed.

		exitAction = ActionFactory.QUIT.create(window);
		register(exitAction);

		aboutAction = ActionFactoryFacade.getDefault().createAboutAction(window);
		if (aboutAction != null) {
			register(aboutAction);
		}

		newWindowAction = ActionFactory.OPEN_NEW_WINDOW.create(window);
		register(newWindowAction);

		openViewAction = new OpenViewAction(window, "Open Another Message View", MessageView.ID); //$NON-NLS-1$
		register(openViewAction);

		messagePopupAction = new MessagePopupAction("Open Message", window); //$NON-NLS-1$
		register(messagePopupAction);
	}

	@Override
	protected void fillMenuBar(final IMenuManager menuBar) {
		final MenuManager fileMenu = new MenuManager("&File", IWorkbenchActionConstants.M_FILE); //$NON-NLS-1$
		final MenuManager helpMenu = new MenuManager("&Help", IWorkbenchActionConstants.M_HELP); //$NON-NLS-1$

		menuBar.add(fileMenu);
		// Add a group marker indicating where action set menus will appear.
		menuBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
		menuBar.add(helpMenu);

		// File
		fileMenu.add(newWindowAction);
		fileMenu.add(new Separator());
		fileMenu.add(messagePopupAction);
		fileMenu.add(openViewAction);
		fileMenu.add(new Separator());
		fileMenu.add(exitAction);

		// Help
		if (aboutAction != null) {
			helpMenu.add(aboutAction);
		}
	}

	@Override
	protected void fillCoolBar(final ICoolBarManager coolBar) {
		final IToolBarManager toolbar = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
		coolBar.add(new ToolBarContributionItem(toolbar, "main")); //$NON-NLS-1$
		toolbar.add(openViewAction);
		toolbar.add(messagePopupAction);
	}
}
