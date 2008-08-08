/*******************************************************************************
 * Copyright (c) 2008 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.riena.navigation;

import org.eclipse.riena.core.exception.Failure;

/**
 * This failure signifies a fatal error on the management of the context.
 * 
 */

public class ApplicationModelFailure extends Failure {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5195851919605393494L;

	/**
	 * Creates a new instance of this failure.
	 * 
	 * @param msg
	 *            The failure message text.
	 * @param cause
	 *            the cause if any
	 */
	public ApplicationModelFailure(String msg, Throwable cause) {
		super(msg, cause);
	}

	/**
	 * Creates a new instance of this failure.
	 * 
	 * @param msg
	 *            The failure message text.
	 */
	public ApplicationModelFailure(String msg) {
		super(msg);
	}

}
