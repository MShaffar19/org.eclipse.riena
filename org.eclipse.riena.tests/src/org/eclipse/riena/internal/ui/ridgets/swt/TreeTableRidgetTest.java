/*******************************************************************************
 * Copyright (c) 2007 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.riena.internal.ui.ridgets.swt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

import org.eclipse.riena.navigation.ui.swt.binding.DefaultSwtControlRidgetMapper;
import org.eclipse.riena.tests.TreeUtils;
import org.eclipse.riena.ui.ridgets.IRidget;
import org.eclipse.riena.ui.ridgets.tree2.TreeNode;
import org.eclipse.riena.ui.ridgets.util.beans.Person;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * Tests of the class {@link TreeTableRidget}.
 */
public class TreeTableRidgetTest extends AbstractSWTRidgetTest {

	PersonNode[] roots;
	PersonNode node1;
	PersonNode node2;
	PersonNode node3;
	PersonNode node4;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		roots = initializeTreeModel();
		String[] valueAccessors = new String[] { "firstname", "lastname" };
		String[] columnHeaders = new String[] { "First Name", "Last Name" };
		getRidget().bindToModel(roots, PersonNode.class, "children", "parent", valueAccessors, columnHeaders);
	}

	@Override
	protected Control createUIControl(Composite parent) {
		Tree result = new Tree(parent, SWT.MULTI);
		result.setHeaderVisible(true);
		new TreeColumn(result, SWT.NONE);
		new TreeColumn(result, SWT.NONE);
		return result;
	}

	@Override
	protected IRidget createRidget() {
		return new TreeTableRidget();
	}

	@Override
	protected Tree getUIControl() {
		return (Tree) super.getUIControl();
	}

	@Override
	protected TreeTableRidget getRidget() {
		return (TreeTableRidget) super.getRidget();
	}

	// test methods
	// /////////////

	public void testRidgetMapping() {
		DefaultSwtControlRidgetMapper mapper = new DefaultSwtControlRidgetMapper();
		assertSame(TreeTableRidget.class, mapper.getRidgetClass(getUIControl()));
	}

	public void testBindToModel() {
		TreeTableRidget ridget = getRidget();
		Tree control = getUIControl();

		ridget.expandTree();

		assertEquals(4, TreeUtils.getItemCount(control));
		assertEquals(node1.getFirstname(), getUIControlItem(0).getText(0));
		assertEquals(node2.getFirstname(), getUIControlItem(1).getText(0));
		assertEquals(node3.getFirstname(), getUIControlItem(2).getText(0));
		assertEquals(node4.getFirstname(), getUIControlItem(3).getText(0));

		assertEquals(node1.getLastname(), getUIControlItem(0).getText(1));
		assertEquals(node2.getLastname(), getUIControlItem(1).getText(1));
		assertEquals(node3.getLastname(), getUIControlItem(2).getText(1));
		assertEquals(node4.getLastname(), getUIControlItem(3).getText(1));
	}

	public void testBindToModelTooFewColumns() {
		// the tree widget has two columns, we bind only one
		getRidget().bindToModel(roots, PersonNode.class, "children", "parent", new String[] { "firstname" },
				new String[] { "First Name" });

		assertEquals(node1.getFirstname(), getUIControlItem(0).getText(0));
		assertEquals(node2.getFirstname(), getUIControlItem(1).getText(0));
		assertEquals(node3.getFirstname(), getUIControlItem(2).getText(0));
		assertEquals(node4.getFirstname(), getUIControlItem(3).getText(0));

		assertEquals("", getUIControlItem(0).getText(1));
		assertEquals("", getUIControlItem(1).getText(1));
		assertEquals("", getUIControlItem(2).getText(1));
		assertEquals("", getUIControlItem(3).getText(1));
	}

	public void testBindToModelWithTooManyColumns() {
		// the tree widget has two columns but we bind three
		getRidget().bindToModel(roots, PersonNode.class, "children", "parent",
				new String[] { "firstname", "lastname", "entry" },
				new String[] { "First Name", "Last Name", "First - Last" });

		assertEquals(node1.getFirstname(), getUIControlItem(0).getText(0));
		assertEquals(node2.getFirstname(), getUIControlItem(1).getText(0));
		assertEquals(node3.getFirstname(), getUIControlItem(2).getText(0));
		assertEquals(node4.getFirstname(), getUIControlItem(3).getText(0));

		assertEquals(node1.getLastname(), getUIControlItem(0).getText(1));
		assertEquals(node2.getLastname(), getUIControlItem(1).getText(1));
		assertEquals(node3.getLastname(), getUIControlItem(2).getText(1));
		assertEquals(node4.getLastname(), getUIControlItem(3).getText(1));
	}

	public void testTableColumnsNumAndHeader() {
		Tree control = getUIControl();

		TreeColumn[] columns = control.getColumns();
		assertEquals(2, columns.length);
		assertEquals("First Name", columns[0].getText());
		assertEquals("Last Name", columns[1].getText());
		assertTrue(control.getHeaderVisible());
	}

	public void testTableColumnsNumAndHeaderWithMismatch() {
		String[] properties1 = new String[] { "firstname", "lastname" };
		String[] headers1 = new String[] { "First Name" };

		try {
			getRidget().bindToModel(roots, PersonNode.class, "children", "parent", properties1, headers1);
			fail();
		} catch (RuntimeException rex) {
			// expected
		}
	}

	public void testTableColumnsWithNullHeader() {
		Tree control = getUIControl();

		control.setHeaderVisible(true);
		control.getColumn(0).setText("foo");
		control.getColumn(1).setText("bar");

		String[] properties1 = new String[] { "firstname", "lastname" };
		// null should hide the headers
		getRidget().bindToModel(roots, PersonNode.class, "children", "parent", properties1, null);

		assertFalse(control.getHeaderVisible());
	}

	public void testTableColumnsWithNullHeaderEntry() {
		Tree control = getUIControl();

		control.getColumn(0).setText("foo");
		control.getColumn(1).setText("bar");

		String[] properties1 = new String[] { "firstname", "lastname" };
		String[] headers = new String[] { "First Name", null };
		getRidget().bindToModel(roots, PersonNode.class, "children", "parent", properties1, headers);

		assertEquals("First Name", control.getColumn(0).getText());
		assertEquals("", control.getColumn(1).getText());
	}

	// public void testUpdateFromModel() {
	// ITableRidget ridget = getRidget();
	// Table control = getUIControl();
	//
	// int oldCount = manager.getPersons().size();
	//
	// assertEquals(oldCount, ridget.getObservableList().size());
	// assertEquals(oldCount, control.getItemCount());
	//
	// manager.getPersons().remove(person1);
	//
	// int newCount = oldCount - 1;
	//
	// assertEquals(newCount, manager.getPersons().size());
	// assertEquals(oldCount, ridget.getObservableList().size());
	// assertEquals(oldCount, control.getItemCount());
	//
	// ridget.updateFromModel();
	//
	// assertEquals(newCount, manager.getPersons().size());
	// assertEquals(newCount, ridget.getObservableList().size());
	// assertEquals(newCount, control.getItemCount());
	// }
	//
	// public void testUpdateFromModelPreservesSelection() {
	// ITableRidget ridget = getRidget();
	//
	// ridget.setSelection(person2);
	//
	// assertSame(person2, ridget.getSelection().get(0));
	//
	// manager.getPersons().remove(person1);
	// ridget.updateFromModel();
	//
	// assertSame(person2, ridget.getSelection().get(0));
	// }
	//
	// public void testContainsOption() {
	// ITableRidget ridget = getRidget();
	//
	// assertTrue(ridget.containsOption(person1));
	// assertTrue(ridget.containsOption(person2));
	// assertTrue(ridget.containsOption(person3));
	//
	// assertFalse(ridget.containsOption(null));
	// assertFalse(ridget.containsOption(new Person("", "")));
	//
	// java.util.List<Person> persons = Arrays.asList(new Person[] { person3 });
	// PersonManager manager = new PersonManager(persons);
	// getRidget().bindToModel(manager, "persons", Person.class, new String[] {
	// "firstname" }, new String[] { "" });
	//
	// assertFalse(ridget.containsOption(person1));
	// assertTrue(ridget.containsOption(person3));
	// }
	//
	// public void testSetSelectionType() {
	// ITableRidget ridget = getRidget();
	// Table control = getUIControl();
	//
	// assertEquals(SelectionType.SINGLE, ridget.getSelectionType());
	// assertTrue((control.getStyle() & SWT.MULTI) != 0);
	//
	// ridget.setSelection(new int[] { 0, 1 });
	//
	// // single selection is enforced
	// assertEquals(1, ridget.getSelectionIndices().length);
	// assertEquals(1, control.getSelectionCount());
	//
	// // multiple selection is now allowed
	// ridget.setSelectionType(SelectionType.MULTI);
	// ridget.setSelection(new int[] { 0, 1 });
	//
	// assertEquals(2, ridget.getSelectionIndices().length);
	// assertEquals(2, control.getSelectionCount());
	// }
	//
	// public void testAddDoubleClickListener() {
	// TableRidget ridget = getRidget();
	// Table control = getUIControl();
	//
	// try {
	// ridget.addDoubleClickListener(null);
	// fail();
	// } catch (RuntimeException npe) {
	// // expected
	// }
	//
	// FTActionListener listener1 = new FTActionListener();
	// ridget.addDoubleClickListener(listener1);
	//
	// FTActionListener listener2 = new FTActionListener();
	// ridget.addDoubleClickListener(listener2);
	// ridget.addDoubleClickListener(listener2);
	//
	// Event doubleClick = new Event();
	// doubleClick.widget = control;
	// doubleClick.type = SWT.MouseDoubleClick;
	// control.notifyListeners(SWT.MouseDoubleClick, doubleClick);
	//
	// assertEquals(1, listener1.getCount());
	// assertEquals(2, listener2.getCount());
	//
	// ridget.removeDoubleClickListener(listener1);
	//
	// control.notifyListeners(SWT.MouseDoubleClick, doubleClick);
	//
	// assertEquals(1, listener1.getCount());
	// }
	//
	// public void testSetComparator() {
	// TableRidget ridget = getRidget();
	// Table control = getUIControl();
	//
	// // sorts from a to z
	// Comparator<Object> comparator = new StringComparator();
	//
	// try {
	// ridget.setComparator(-1, comparator);
	// fail();
	// } catch (RuntimeException rex) {
	// // expected
	// }
	//
	// try {
	// ridget.setComparator(2, comparator);
	// fail();
	// } catch (RuntimeException rex) {
	// // expected
	// }
	//
	// ridget.setSortedAscending(true);
	//
	// int lastItemIndex = control.getItemCount() - 1;
	//
	// assertEquals("John", control.getItem(0).getText(0));
	// assertEquals("Frank", control.getItem(lastItemIndex).getText(0));
	//
	// ridget.setComparator(0, comparator);
	//
	// assertEquals("John", control.getItem(0).getText(0));
	// assertEquals("Frank", control.getItem(lastItemIndex).getText(0));
	//
	// ridget.setSortedColumn(0);
	//
	// assertEquals("Frank", control.getItem(0).getText(0));
	// assertEquals("John", control.getItem(lastItemIndex).getText(0));
	//
	// ridget.setComparator(0, null);
	//
	// assertEquals("John", control.getItem(0).getText(0));
	// assertEquals("Frank", control.getItem(lastItemIndex).getText(0));
	// }
	//
	// public void testGetSortedColumn() {
	// TableRidget ridget = getRidget();
	//
	// try {
	// ridget.setSortedColumn(2);
	// fail();
	// } catch (RuntimeException rex) {
	// // expected
	// }
	//
	// assertEquals(-1, ridget.getSortedColumn());
	//
	// ridget.setComparator(0, new StringComparator());
	//
	// assertEquals(-1, ridget.getSortedColumn());
	//
	// ridget.setSortedColumn(0);
	//
	// assertEquals(0, ridget.getSortedColumn());
	//
	// ridget.setComparator(0, null);
	//
	// assertEquals(-1, ridget.getSortedColumn());
	//
	// ridget.setComparator(1, new StringComparator());
	// ridget.setSortedColumn(1);
	//
	// assertEquals(1, ridget.getSortedColumn());
	//
	// ridget.setSortedColumn(-1);
	//
	// assertEquals(-1, ridget.getSortedColumn());
	//
	// // no comparator in column 0
	// ridget.setSortedColumn(0);
	//
	// assertEquals(-1, ridget.getSortedColumn());
	// }
	//
	// public void testIsColumnSortable() {
	// TableRidget ridget = getRidget();
	//
	// try {
	// assertFalse(ridget.isColumnSortable(-1));
	// fail();
	// } catch (RuntimeException rex) {
	// // expected
	// }
	//
	// try {
	// assertFalse(ridget.isColumnSortable(2));
	// fail();
	// } catch (RuntimeException rex) {
	// // expected
	// }
	//
	// for (int i = 0; i < 2; i++) {
	// assertFalse(ridget.isColumnSortable(i));
	//
	// // columns are sortable by default, when they have a comparator
	// ridget.setComparator(i, new StringComparator());
	//
	// assertTrue(ridget.isColumnSortable(i));
	//
	// ridget.setColumnSortable(i, false);
	//
	// assertFalse(ridget.isColumnSortable(i));
	//
	// ridget.setColumnSortable(i, true);
	//
	// assertTrue(ridget.isColumnSortable(i));
	//
	// // columns are not sortable without a comparator
	// ridget.setComparator(i, null);
	//
	// assertFalse(ridget.isColumnSortable(i));
	// }
	// }
	//
	// public void testSetSortedAscending() {
	// Table control = getUIControl();
	// TableRidget ridget = getRidget();
	//
	// ridget.bindToModel(manager, "persons", Person.class, new String[] {
	// "lastname" }, new String[] { "" });
	// int lastItemIndex = control.getItemCount() - 1;
	//
	// assertEquals(-1, ridget.getSortedColumn());
	// assertFalse(ridget.isSortedAscending());
	//
	// ridget.setComparator(0, new StringComparator());
	// ridget.setSortedColumn(0);
	//
	// assertTrue(ridget.isSortedAscending());
	// assertEquals("Doe", control.getItem(0).getText(0));
	// assertEquals("Zappa", control.getItem(lastItemIndex).getText(0));
	//
	// ridget.setSortedAscending(false);
	//
	// assertFalse(ridget.isSortedAscending());
	// assertEquals("Zappa", control.getItem(0).getText(0));
	// assertEquals("Doe", control.getItem(lastItemIndex).getText(0));
	//
	// ridget.setSortedAscending(true);
	//
	// assertTrue(ridget.isSortedAscending());
	// assertEquals("Doe", control.getItem(0).getText(0));
	// assertEquals("Zappa", control.getItem(lastItemIndex).getText(0));
	//
	// ridget.setComparator(0, null);
	//
	// assertEquals(-1, ridget.getSortedColumn());
	// assertFalse(ridget.isSortedAscending());
	// }
	//
	// public void testSetSortedAscendingFiresEvents() {
	// TableRidget ridget = getRidget();
	//
	// ridget.setSortedAscending(true);
	//
	// expectPropertyChangeEvents(new PropertyChangeEvent(ridget,
	// ISortableByColumn.PROPERTY_SORT_ASCENDING,
	// Boolean.TRUE, Boolean.FALSE));
	//
	// ridget.setSortedAscending(false);
	//
	// verifyPropertyChangeEvents();
	// expectNoPropertyChangeEvent();
	//
	// ridget.setSortedAscending(false);
	//
	// verifyPropertyChangeEvents();
	// expectPropertyChangeEvents(new PropertyChangeEvent(ridget,
	// ISortableByColumn.PROPERTY_SORT_ASCENDING,
	// Boolean.FALSE, Boolean.TRUE));
	//
	// ridget.setSortedAscending(true);
	//
	// verifyPropertyChangeEvents();
	// expectNoPropertyChangeEvent();
	//
	// ridget.setSortedAscending(true);
	//
	// verifyPropertyChangeEvents();
	// }
	//
	// public void testSetSortedColumnFiresEvents() {
	// TableRidget ridget = getRidget();
	//
	// assertEquals(-1, ridget.getSortedColumn());
	//
	// expectPropertyChangeEvents(new PropertyChangeEvent(ridget,
	// ISortableByColumn.PROPERTY_SORTED_COLUMN, Integer
	// .valueOf(-1), Integer.valueOf(0)));
	//
	// ridget.setSortedColumn(0);
	//
	// verifyPropertyChangeEvents();
	// expectNoPropertyChangeEvent();
	//
	// ridget.setSortedColumn(0);
	//
	// verifyPropertyChangeEvents();
	// expectPropertyChangeEvents(new PropertyChangeEvent(ridget,
	// ISortableByColumn.PROPERTY_SORTED_COLUMN, Integer
	// .valueOf(0), Integer.valueOf(1)));
	//
	// ridget.setSortedColumn(1);
	//
	// verifyPropertyChangeEvents();
	// }
	//
	// public void testSetColumnSortabilityFiresEvents() {
	// TableRidget ridget = getRidget();
	//
	// expectNoPropertyChangeEvent();
	//
	// ridget.setColumnSortable(0, true);
	//
	// verifyPropertyChangeEvents();
	// expectPropertyChangeEvents(new PropertyChangeEvent(ridget,
	// ISortableByColumn.PROPERTY_COLUMN_SORTABILITY, null,
	// Integer.valueOf(0)));
	//
	// ridget.setColumnSortable(0, false);
	//
	// verifyPropertyChangeEvents();
	// expectNoPropertyChangeEvent();
	//
	// ridget.setColumnSortable(0, false);
	//
	// verifyPropertyChangeEvents();
	// expectPropertyChangeEvents(new PropertyChangeEvent(ridget,
	// ISortableByColumn.PROPERTY_COLUMN_SORTABILITY, null,
	// Integer.valueOf(0)));
	//
	// ridget.setColumnSortable(0, true);
	//
	// verifyPropertyChangeEvents();
	// expectNoPropertyChangeEvent();
	//
	// ridget.setColumnSortable(0, true);
	//
	// verifyPropertyChangeEvents();
	// }
	//
	// public void testColumnHeaderChangesSortability() {
	// TableRidget ridget = getRidget();
	// Table table = getUIControl();
	//
	// ridget.setColumnSortable(0, true);
	// ridget.setComparator(0, new StringComparator());
	// ridget.setColumnSortable(1, true);
	// ridget.setComparator(1, new StringComparator());
	//
	// ridget.setSortedColumn(-1);
	//
	// assertEquals(-1, ridget.getSortedColumn());
	// assertFalse(ridget.isSortedAscending());
	//
	// Event e = new Event();
	// e.type = SWT.Selection;
	// e.widget = table.getColumn(0);
	// e.widget.notifyListeners(SWT.Selection, e);
	//
	// assertEquals(0, ridget.getSortedColumn());
	// assertTrue(ridget.isSortedAscending());
	//
	// e.widget.notifyListeners(SWT.Selection, e);
	//
	// assertEquals(0, ridget.getSortedColumn());
	// assertFalse(ridget.isSortedAscending());
	//
	// e.widget.notifyListeners(SWT.Selection, e);
	//
	// assertEquals(-1, ridget.getSortedColumn());
	// assertFalse(ridget.isSortedAscending());
	//
	// e.widget.notifyListeners(SWT.Selection, e);
	//
	// assertEquals(0, ridget.getSortedColumn());
	// assertTrue(ridget.isSortedAscending());
	// }
	//
	// public void testSetMoveableColumns() {
	// TableRidget ridget = getRidget();
	// Table table = getUIControl();
	//
	// assertFalse(ridget.hasMoveableColumns());
	// assertFalse(table.getColumn(0).getMoveable());
	// assertFalse(table.getColumn(1).getMoveable());
	//
	// ridget.setMoveableColumns(true);
	//
	// assertTrue(ridget.hasMoveableColumns());
	// assertTrue(table.getColumn(0).getMoveable());
	// assertTrue(table.getColumn(1).getMoveable());
	//
	// ridget.setMoveableColumns(false);
	//
	// assertFalse(ridget.hasMoveableColumns());
	// assertFalse(table.getColumn(0).getMoveable());
	// assertFalse(table.getColumn(1).getMoveable());
	// }
	//

	// helping methods
	// ////////////////

	private Collection<Person> createPersonList() {
		Collection<Person> newList = new ArrayList<Person>();

		Person person = new Person("Doe", "One");
		person.setEyeColor(1);
		newList.add(person);

		person = new Person("Jackson", "Two");
		person.setEyeColor(1);
		newList.add(person);

		person = new Person("Jackson", "Three");
		person.setEyeColor(1);
		newList.add(person);

		person = new Person("Jackson", "John");
		person.setEyeColor(3);
		newList.add(person);

		person = new Person("JJ Jr. Shabadoo", "Joey");
		person.setEyeColor(3);
		newList.add(person);

		person = new Person("Johnson", "Jack");
		person.setEyeColor(2);
		newList.add(person);

		person = new Person("Johnson", "Jane");
		person.setEyeColor(3);
		newList.add(person);

		person = new Person("Zappa", "Frank");
		person.setEyeColor(2);
		newList.add(person);

		return newList;
	}

	private PersonNode[] initializeTreeModel() {
		Collection<Person> persons = createPersonList();
		Iterator<Person> iter = persons.iterator();
		Person person1 = iter.next();
		Person person2 = iter.next();
		Person person3 = iter.next();
		Person person4 = iter.next();

		// node1 is the root
		node1 = new PersonNode(person1);
		// node2 and node4 will be visible by default because node1 gets
		// autoexpanded
		node2 = new PersonNode(node1, person2);
		node4 = new PersonNode(node1, person4);
		// node3 is on level-2, so it does not get autoexpanded
		node3 = new PersonNode(node2, person3);

		return new PersonNode[] { node1 };
	}

	/**
	 * Return the TreeItem corresponding to the following mock "index" scheme:
	 * 0: item for node1, 1: item for node2, 2: item for node3, 3: item for
	 * node4.
	 * <p>
	 * This method will fully expand the tree to ensure all tree items are
	 * created.
	 */
	private final TreeItem getUIControlItem(int index) {
		getRidget().expandTree();
		Tree control = getUIControl();
		switch (index) {
		case 0:
			return control.getItem(0);
		case 1:
			return control.getItem(0).getItem(0);
		case 2:
			return control.getItem(0).getItem(0).getItem(0);
		case 3:
			return control.getItem(0).getItem(1);
		}
		throw new IndexOutOfBoundsException("index= " + index);
	}

	//
	// @Override
	// protected void clearUIControlRowSelection() {
	// getUIControl().deselectAll();
	// fireSelectionEvent();
	// }
	//
	// @Override
	// protected int getUIControlSelectedRowCount() {
	// return getUIControl().getSelectionCount();
	// }
	//
	// @Override
	// protected int getUIControlSelectedRow() {
	// return getUIControl().getSelectionIndex();
	// }
	//
	// @Override
	// protected Object getRowValue(int i) {
	// // return getRidget().getRowObservables().get(i);
	// IObservableList rowObservables =
	// ReflectionUtils.invokeHidden(getRidget(), "getRowObservables");
	// return rowObservables.get(i);
	// }
	//
	// @Override
	// protected int[] getSelectedRows() {
	// // IObservableList rowObservables = getRidget().getRowObservables();
	// IObservableList rowObservables =
	// ReflectionUtils.invokeHidden(getRidget(), "getRowObservables");
	// Object[] elements = getRidget().getMultiSelectionObservable().toArray();
	// int[] result = new int[elements.length];
	// for (int i = 0; i < elements.length; i++) {
	// Object element = elements[i];
	// result[i] = rowObservables.indexOf(element);
	// }
	// return result;
	// }
	//
	// @Override
	// protected int[] getUIControlSelectedRows() {
	// return getUIControl().getSelectionIndices();
	// }
	//
	// @Override
	// protected void setUIControlRowSelection(int[] indices) {
	// getUIControl().setSelection(indices);
	// fireSelectionEvent();
	// }
	//
	// @Override
	// protected void setUIControlRowSelectionInterval(int start, int end) {
	// getUIControl().setSelection(start, end);
	// fireSelectionEvent();
	// }

	// helping classes
	// ////////////////

	/**
	 * Compares two strings.
	 */
	private static final class StringComparator implements Comparator<Object> {
		public int compare(Object o1, Object o2) {
			String s1 = (String) o1;
			String s2 = (String) o2;
			return s1.compareTo(s2);
		}
	}

	/**
	 * TODO [ev] docs
	 */
	private static final class PersonNode extends TreeNode {

		public PersonNode(Person value) {
			super(value);
		}

		public PersonNode(PersonNode parent, Person value) {
			super(parent, value);
		}

		public String getFirstname() {
			return ((Person) getValue()).getFirstname();
		}

		public String getLastname() {
			return ((Person) getValue()).getLastname();
		}

		public String getEntry() {
			// 'Last Name' - 'First Name'
			return ((Person) getValue()).getListEntry();
		}

	}

}
