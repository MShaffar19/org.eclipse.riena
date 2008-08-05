/*******************************************************************************
 * Copyright (c) 2007 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.riena.navigation.model;

import org.eclipse.riena.navigation.IApplicationModel;
import org.eclipse.riena.navigation.INavigationHistoryListener;
import org.eclipse.riena.navigation.ISubApplicationNode;
import org.eclipse.riena.navigation.listener.IApplicationModelListener;

/**
 * Default implementation for the ApplicationModel
 */
public class ApplicationModel extends NavigationNode<IApplicationModel, ISubApplicationNode, IApplicationModelListener>
		implements IApplicationModel {

	/**
	 * 
	 */
	public ApplicationModel() {
		super();
		initializeNavigationProcessor();
	}

	/**
	 * @param children
	 */
	public ApplicationModel(ISubApplicationNode... children) {
		super(children);
		initializeNavigationProcessor();
	}

	/**
	 * @param label
	 * @param children
	 */
	public ApplicationModel(String label, ISubApplicationNode... children) {
		super(label, children);
		initializeNavigationProcessor();
	}

	/**
	 * @param label
	 */
	public ApplicationModel(String label) {
		super(label);
		initializeNavigationProcessor();
	}

	/**
	 * 
	 */
	protected void initializeNavigationProcessor() {
		setNavigationProcessor(new NavigationProcessor());
	}

	/**
	 * @see org.eclipse.riena.navigation.INavigationHistoryListernable#
	 *      addNavigationHistoryListener
	 *      (org.eclipse.riena.navigation.INavigationHistoryListener)
	 */
	public void addNavigationHistoryListener(INavigationHistoryListener listener) {
		getNavigationProcessor().addNavigationHistoryListener(listener);
	}

	/**
	 * @see org.eclipse.riena.navigation.INavigationHistoryListernable#
	 *      removeNavigationHistoryListener
	 *      (org.eclipse.riena.navigation.INavigationHistoryListener)
	 */
	public void removeNavigationHistoryListener(INavigationHistoryListener listener) {
		getNavigationProcessor().removeNavigationHistoryListener(listener);
	}

	/**
	 * @see org.eclipse.riena.navigation.INavigationHistoryListernable#getHistoryBackSize()
	 */
	public int getHistoryBackSize() {
		return getNavigationProcessor().getHistoryBackSize();
	}

	/**
	 * @see org.eclipse.riena.navigation.INavigationHistoryListernable#getHistoryForwardSize()
	 */
	public int getHistoryForwardSize() {
		return getNavigationProcessor().getHistoryForwardSize();
	}
}
