/*******************************************************************************
 * Copyright (c) 2007, 2009 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.riena.navigation.model;

import org.eclipse.riena.core.injector.extension.ExtensionInterface;
import org.eclipse.riena.core.wire.InjectExtension;
import org.eclipse.riena.core.wire.Wire;
import org.eclipse.riena.internal.navigation.Activator;
import org.eclipse.riena.navigation.INavigationNodeProvider;

/**
 * Convenience access to the configured {@code INavigationNodeProvider}.
 * 
 * @since 1.2
 */
public final class NavigationNodeProvider {

	private static NavigationNodeProvider navigationNodeProvider;

	static {
		navigationNodeProvider = new NavigationNodeProvider();
		Wire.instance(navigationNodeProvider).andStart(Activator.getDefault().getContext());
	}

	private INavigationNodeProvider provider;

	/**
	 * Default Constructor
	 */
	private NavigationNodeProvider() {
		//	utility
	}

	/**
	 * Provide the single instance of the object configured for
	 * {@code INavigationNodeProvider}.
	 * 
	 * @since 1.2
	 */
	public static INavigationNodeProvider getInstance() {
		return navigationNodeProvider.getProvider();
	}

	private INavigationNodeProvider getProvider() {
		return provider;
	}

	/**
	 * Configure the navigation node provider to be used. If there is more than
	 * one implementation we take the one having the highest priority according
	 * to attribute 'priority'. If the priority is not specified it is assumed
	 * to be zero (the default value). All implementations sharing the same
	 * priority are considered equivalent and an arbitrary one is chosen.
	 * 
	 * @param availableExtensions
	 *            Array containing all currently available navigation node
	 *            provider implementations. This may change over time as plugins
	 *            are activated or deactivated
	 * @since 1.2
	 */
	@InjectExtension
	public void update(INavigationNodeProviderExtension[] availableExtensions) {

		if (provider != null) {
			provider.cleanUp();
		}
		INavigationNodeProviderExtension found = null;
		int maxPriority = Integer.MIN_VALUE;

		provider = null;
		for (INavigationNodeProviderExtension probe : availableExtensions) {
			int p = probe.getPriority();
			if (found == null || p > maxPriority) {
				found = probe;
				maxPriority = p;
			}
		}

		if (found != null) {
			provider = found.createClass();
		}
	}

	/**
	 * @since 1.2
	 */
	@ExtensionInterface(id = "navigationNodeProvider")
	public interface INavigationNodeProviderExtension {

		/**
		 * An optional identifier for the implementation.
		 * 
		 * @return the identifier.
		 */
		String getId();

		/**
		 * The priority of this implementation among others. Integer.MIN_VALUE
		 * has the least chance to be selected, Integer.MAX_VALUE would most
		 * probably be used. Default is 0.
		 * 
		 * @return the priority.
		 */
		int getPriority();

		/**
		 * The implementation of INavigationNodeProvider to be used.
		 * 
		 * @return the object.
		 */
		INavigationNodeProvider createClass();
	}

}
