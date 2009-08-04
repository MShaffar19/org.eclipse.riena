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
package org.eclipse.riena.ui.ridgets.marker;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.eclipse.riena.tests.RienaTestCase;
import org.eclipse.riena.tests.collect.NonUITestCase;
import org.eclipse.riena.ui.core.marker.MessageMarker;

/**
 * Test for the class {@link ValidationMessageMarker}.
 */
@NonUITestCase
public class ValidationMessageMarkerTest extends RienaTestCase {

	private final IValidator validationRule = new IValidator() {
		public IStatus validate(Object value) {
			return Status.OK_STATUS;
		}
	};

	public void testConstructorWithNullMessageMarker() {
		try {
			new ValidationMessageMarker(null);
			fail();
		} catch (RuntimeException rex) {
			ok();
		}

		try {
			new ValidationMessageMarker(null, validationRule);
			fail();
		} catch (RuntimeException rex) {
			ok();
		}
	}

	public void testGetMessage() {
		ValidationMessageMarker result1 = new ValidationMessageMarker(new MessageMarker("msg"));

		assertEquals("msg", result1.getMessage());

		ValidationMessageMarker result2 = new ValidationMessageMarker(new MessageMarker());

		assertEquals("", result2.getMessage());
	}

	public void testGetRule() {
		ValidationMessageMarker result1 = new ValidationMessageMarker(new MessageMarker("msg"), validationRule);

		assertSame(validationRule, result1.getValidationRule());

		ValidationMessageMarker result2 = new ValidationMessageMarker(new MessageMarker("msg"), null);

		assertNull(result2.getValidationRule());
	}

	public void testEquals() {
		ValidationMessageMarker result1a = new ValidationMessageMarker(new MessageMarker("1"), validationRule);
		ValidationMessageMarker result1b = new ValidationMessageMarker(new MessageMarker("1"), validationRule);
		ValidationMessageMarker result1c = new ValidationMessageMarker(new MessageMarker("1"), validationRule);
		ValidationMessageMarker result2a = new ValidationMessageMarker(new MessageMarker("2"));
		ValidationMessageMarker result2b = new ValidationMessageMarker(new MessageMarker("2"));

		assertTrue(result1a.equals(result1a));
		assertTrue(result1a.equals(result1b));
		assertTrue(result1b.equals(result1a));
		assertTrue(result1b.equals(result1c));
		assertTrue(result1a.equals(result1c));

		assertTrue(result2a.equals(result2a));
		assertTrue(result2a.equals(result2b));
		assertTrue(result2b.equals(result2a));

		assertFalse(result1a.equals(null));
		assertFalse(result1a.equals(new Object()));
		assertFalse(result1a.equals(result2a));
		assertFalse(result2a.equals(result1a));
	}

	public void testHashCodeEqual() {
		ValidationMessageMarker result1a = new ValidationMessageMarker(new MessageMarker("1"), validationRule);
		ValidationMessageMarker result1b = new ValidationMessageMarker(new MessageMarker("1"), validationRule);
		int hash1 = result1a.hashCode();

		assertEquals(hash1, result1a.hashCode());

		assertTrue(result1a.equals(result1b));
		assertEquals(hash1, result1b.hashCode());
	}

	public void testHashCodeNotEqual() {
		ValidationMessageMarker result1 = new ValidationMessageMarker(new MessageMarker("1"), validationRule);
		ValidationMessageMarker result2 = new ValidationMessageMarker(new MessageMarker("2"), validationRule);
		ValidationMessageMarker result3 = new ValidationMessageMarker(new MessageMarker("1"));

		assertFalse(result1.hashCode() == result2.hashCode());
		assertFalse(result1.hashCode() == result3.hashCode());
		assertFalse(result2.hashCode() == result3.hashCode());
	}
}
