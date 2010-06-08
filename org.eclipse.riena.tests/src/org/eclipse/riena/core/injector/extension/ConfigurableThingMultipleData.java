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
package org.eclipse.riena.core.injector.extension;

/**
 * 
 */
public class ConfigurableThingMultipleData {

	private IData[] data;
	private boolean trace;

	public void update(IData[] data) {
		trace("update", data);
		// TODO warning suppression: Ignore FindBugs warning about internal
		// representation being exposed: seems ok for testing
		this.data = data;
	}

	public IData[] getData() {
		// TODO warning suppression: Ignore FindBugs warning about internal
		// representation being exposed: seems ok for testing
		return data;
	}

	public void setTrace(boolean trace) {
		this.trace = trace;
	}

	// helping methods
	//////////////////

	private void trace(String methodName, IData[] data) {
		if (trace) {
			if (data == null) {
				System.out.println(String.format("%s: null", methodName));
			} else {
				System.out.println(String.format("%s: %s - length= %d", methodName, data, data.length));
				for (IData entry : data) {
					System.out.println("\t" + entry.getText());
				}
			}
		}
	}

}
