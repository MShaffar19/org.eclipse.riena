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
package org.eclipse.riena;

import java.util.List;

import junit.framework.TestCase;

import org.eclipse.riena.internal.core.test.collect.NonUITestCase;
import org.eclipse.riena.internal.core.test.collect.TestCollector;
import org.eclipse.riena.internal.tests.Activator;

/**
 * Check all {@code TestCase}s for any of our test case constraint violations.
 */
@NonUITestCase
public class CheckTestConstraintsTest extends TestCase {

	public void testUnmarkedTests() {
		List<Class<? extends TestCase>> unmarked = TestCollector.collectUnmarked(Activator.getDefault().getBundle(),
				null);
		if (unmarked.size() > 0) {
			System.err.println(">> Found unmarked tests:");
			for (Class<?> testCase : unmarked) {
				System.err.println("  unmarked: " + testCase.getName());
			}
		}
		assertEquals(unmarked.size() + " unmarked test(s) found: " + unmarked, 0, unmarked.size());
	}

	// FIXME: There are currently two test cases that violate this constraint.
	// We should fix that and set the expected count to zero!
	public void testBadlyNamedTests() {
		List<Class<? extends TestCase>> badlyNamed = TestCollector.collectBadlyNamed(
				Activator.getDefault().getBundle(), null);
		if (badlyNamed.size() > 0) {
			System.err.println(">> Found badly named tests:");
			for (Class<?> testCase : badlyNamed) {
				System.err.println("  badly named: " + testCase.getName());
			}
		}
		assertEquals(badlyNamed.size() + " badly named test(s) found: " + badlyNamed, 2, badlyNamed.size());
	}

}
