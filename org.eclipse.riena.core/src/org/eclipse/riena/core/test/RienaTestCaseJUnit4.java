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
package org.eclipse.riena.core.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;

/**
 * @since 5.0
 */
public class RienaTestCaseJUnit4 {
	@Rule
	public TestName name = new TestName();
	protected final TestingTools tools = new TestingTools(new JUnit4Wrapper(this));

	@Before
	public void setUp() {
		tools.setUp();
	}

	@After
	public void tearDown() {
		tools.tearDown();

	}
}
