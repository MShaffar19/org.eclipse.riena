/*******************************************************************************
 * Copyright (c) 2007, 2008 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.riena.ui.core.marker;

/**
 * A Marker working as ErrorMarker and as MessageMarker
 */
public class ErrorMessageMarker extends ErrorMarker implements IMessageMarker {

	/**
	 * @return the Message in this Marker
	 */
	public String getMessage() {
		return (String) super.getAttribute(MESSAGE);
	}

	/**
	 * Basic constructor for the Message marker
	 * 
	 * @param pMessage
	 *            - the Message of the Marker
	 */
	public ErrorMessageMarker(String pMessage) {
		super();
		setMessage(pMessage);
	}

	/**
	 * Change the error message stored in this marker
	 * 
	 * @param pMessage
	 *            - the Message of the Marker
	 */
	public final void setMessage(String pMessage) {
		String msg = pMessage == null ? "" : pMessage; //$NON-NLS-1$
		setAttribute(MESSAGE, msg);
	}

}
