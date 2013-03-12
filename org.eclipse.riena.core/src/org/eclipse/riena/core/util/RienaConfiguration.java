/*******************************************************************************
 * Copyright (c) 2007, 2013 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.riena.core.util;

import org.eclipse.riena.core.singleton.SingletonProvider;
import org.eclipse.riena.core.wire.InjectExtension;

/**
 * This class facilitates the access to Riena configuration extensions. See extension point <tt>org.eclipse.riena.core.configuration</tt>.
 * 
 * @since 5.0
 */
public class RienaConfiguration {
	private final static SingletonProvider<RienaConfiguration> IS = new SingletonProvider<RienaConfiguration>(RienaConfiguration.class);
	private IConfigurationExtension[] extensions;

	private RienaConfiguration() {
		// utility class
	}

	/**
	 * Returns an instance (always the same) of this class.
	 * 
	 * @return instance of {@code RienaConfiguration}
	 */
	public static RienaConfiguration getInstance() {
		return IS.getInstance();
	}

	/**
	 * Retrieves the configured value for the given key. If there are several definitions (several extensions to <tt>org.eclipse.riena.core.configuration</tt>)
	 * with this key it is not defines which of them will be evaluated. This method returns <code>null</code> if there is no definition with the given key.
	 * 
	 * @param key
	 *            the property key
	 * @return the property value or <code>null</code> if a property with that key is not defined
	 */
	public String getProperty(final String key) {
		for (final IConfigurationExtension e : extensions) {
			if (e.getKey().equals(key)) {
				return e.getValue();
			}
		}
		return null;
	}

	@InjectExtension
	public void update(final IConfigurationExtension[] extensions) {
		this.extensions = extensions;
	}
}
