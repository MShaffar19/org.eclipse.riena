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
package org.eclipse.riena.internal.ui.ridgets.swt;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.riena.tests.collect.NonGatherableTestCase;

/**
 * Tests all test cases within package:
 * <code>org.eclipse.riena.ui.ridgets.swt</code>
 */
@NonGatherableTestCase("This is not a �TestCase�!")
public class AllTests extends TestCase {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllTests.class.getName());
		suite.addTestSuite(AbstractItemPropertiesTest.class);
		suite.addTestSuite(ActionRidgetTest.class);
		suite.addTestSuite(ComboRidgetTest.class);
		suite.addTestSuite(CompositeTableRidgetTest.class);
		suite.addTestSuite(ChoiceCompositeTest.class);
		suite.addTestSuite(DateTextRidgetTest.class);
		suite.addTestSuite(DecimalTextRidgetTest.class);
		suite.addTestSuite(EmbeddedTitleBarRidgetTest.class);
		suite.addTestSuite(LabelRidgetTest.class);
		suite.addTestSuite(ListRidgetTest.class);
		suite.addTestSuite(MarkableRidgetTest.class);
		suite.addTestSuite(MenuItemPropertiesTest.class);
		suite.addTestSuite(MenuItemRidgetTest.class);
		suite.addTestSuite(MenuItemMarkerSupportTest.class);
		suite.addTestSuite(MenuPropertiesTest.class);
		suite.addTestSuite(MenuRidgetTest.class);
		suite.addTestSuite(MultipleChoiceRidgetTest.class);
		suite.addTestSuite(NoAbstractSWTRidgetTest.class);
		suite.addTestSuite(NumericStringTest.class);
		suite.addTestSuite(NumericTextRidgetTest.class);
		suite.addTestSuite(SegmentedStringTest.class);
		suite.addTestSuite(SharedResourcesTest.class);
		suite.addTestSuite(SingleChoiceRidgetTest.class);
		suite.addTestSuite(StatuslineNumberRidgetTest.class);
		suite.addTestSuite(TableRidgetTest.class);
		suite.addTestSuite(TableRidgetLabelProviderTest.class);
		suite.addTestSuite(TreeRidgetLabelProviderTest.class);
		suite.addTestSuite(TextRidgetTest.class);
		suite.addTestSuite(TextRidgetTest2.class);
		suite.addTestSuite(TreeRidgetTest.class);
		suite.addTestSuite(TreeRidgetTest2.class);
		suite.addTestSuite(TreeTableRidgetTest.class);
		suite.addTestSuite(ToggleButtonRidgetTest.class);
		suite.addTestSuite(ToolItemMarkerSupportTest.class);
		suite.addTestSuite(ToolItemPropertiesTest.class);
		suite.addTestSuite(MessageBoxRidgetTest.class);
		suite.addTestSuite(ShellRidgetTest.class);
		suite.addTestSuite(AbstractSWTWidgetRidgetTest.class);
		return suite;
	}
}
