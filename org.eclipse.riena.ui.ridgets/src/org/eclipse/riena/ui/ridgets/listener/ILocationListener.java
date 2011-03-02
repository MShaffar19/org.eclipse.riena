/*******************************************************************************
 * Copyright (c) 2007, 2010 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.riena.ui.ridgets.listener;

/**
 * {@link ILocationListener} listeners are notified of URL changes, for example
 * when the user activates a hyperlink.
 * 
 * @since 3.0
 */
public interface ILocationListener {

	/**
	 * This method is called when the URL is about to change.
	 * <p>
	 * The listener may return false to reject the change. If several listeners
	 * are notified, the return values are aggregated (boolean AND).
	 * 
	 * @param event
	 *            a {@link LocationEvent} describing the change; never null
	 * @return true to accept the change, false to reject (block) the change.
	 */
	boolean locationChanging(LocationEvent event);
}
