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
package org.eclipse.riena.internal.ui.ridgets.swt;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import org.eclipse.riena.beans.common.Person;
import org.eclipse.riena.beans.common.PersonManager;
import org.eclipse.riena.beans.common.TypedComparator;
import org.eclipse.riena.core.util.ReflectionUtils;
import org.eclipse.riena.internal.ui.swt.test.UITestHelper;
import org.eclipse.riena.ui.common.ISortableByColumn;
import org.eclipse.riena.ui.ridgets.IColumnFormatter;
import org.eclipse.riena.ui.ridgets.ITableRidget;
import org.eclipse.riena.ui.ridgets.ISelectableRidget.SelectionType;
import org.eclipse.riena.ui.ridgets.listener.SelectionEvent;
import org.eclipse.riena.ui.ridgets.swt.ColumnFormatter;
import org.eclipse.riena.ui.ridgets.swt.uibinding.SwtControlRidgetMapper;

/**
 * Tests of the class {@link TableRidget}.
 */
public class TableRidgetTest extends AbstractTableRidgetTest {

	@Override
	protected Control createWidget(Composite parent) {
		Table table = new Table(parent, SWT.MULTI);
		table.setHeaderVisible(true);
		new TableColumn(table, SWT.NONE);
		new TableColumn(table, SWT.NONE);
		return table;
	}

	@Override
	protected ITableRidget createRidget() {
		return new TableRidget();
	}

	@Override
	protected Table getWidget() {
		return (Table) super.getWidget();
	}

	@Override
	protected TableRidget getRidget() {
		return (TableRidget) super.getRidget();
	}

	@Override
	protected void bindRidgetToModel() {
		getRidget().bindToModel(manager, "persons", Person.class, new String[] { "firstname", "lastname" },
				new String[] { "First Name", "Last Name" });
	}

	// test methods
	// /////////////

	public void testRidgetMapping() {
		SwtControlRidgetMapper mapper = SwtControlRidgetMapper.getInstance();
		assertSame(TableRidget.class, mapper.getRidgetClass(getWidget()));
	}

	public void testBindToModel() {
		Table control = getWidget();

		assertEquals(manager.getPersons().size(), control.getItemCount());
		assertEquals(person1.getFirstname(), control.getItem(0).getText(0));
		assertEquals(person2.getFirstname(), control.getItem(1).getText(0));
		assertEquals(person3.getFirstname(), control.getItem(2).getText(0));

		assertEquals(person1.getLastname(), control.getItem(0).getText(1));
		assertEquals(person2.getLastname(), control.getItem(1).getText(1));
		assertEquals(person3.getLastname(), control.getItem(2).getText(1));
	}

	public void testTableColumnsNumAndHeader() {
		Table control = getWidget();

		TableColumn[] columns = control.getColumns();
		assertEquals(2, columns.length);
		assertEquals("First Name", columns[0].getText());
		assertEquals("Last Name", columns[1].getText());
		assertTrue(control.getHeaderVisible());
	}

	public void testTableColumnsNumAndHeaderWithMismatch() {
		String[] properties1 = new String[] { "firstname", "lastname" };
		String[] headers1 = new String[] { "First Name" };

		try {
			getRidget().bindToModel(manager, "persons", Person.class, properties1, headers1);
			fail();
		} catch (RuntimeException rex) {
			ok();
		}
	}

	public void testTableColumnsWithNullHeader() {
		ITableRidget ridget = getRidget();
		Table control = getWidget();

		control.setHeaderVisible(true);
		control.getColumn(0).setText("foo");
		control.getColumn(1).setText("bar");

		String[] properties1 = new String[] { "firstname", "lastname" };
		// null should hide the headers
		ridget.bindToModel(manager, "persons", Person.class, properties1, null);

		assertFalse(control.getHeaderVisible());
	}

	public void testTableColumnsWithNullHeaderEntry() {
		ITableRidget ridget = getRidget();
		Table control = getWidget();

		control.getColumn(0).setText("foo");
		control.getColumn(1).setText("bar");

		String[] properties1 = new String[] { "firstname", "lastname" };
		String[] headers = new String[] { "First Name", null };
		ridget.bindToModel(manager, "persons", Person.class, properties1, headers);

		assertEquals("First Name", control.getColumn(0).getText());
		assertEquals("", control.getColumn(1).getText());
	}

	public void testUpdateFromModel() {
		ITableRidget ridget = getRidget();
		Table control = getWidget();
		final List<ChangeEvent> changeEvents = new ArrayList<ChangeEvent>();
		IChangeListener listener = new IChangeListener() {
			public void handleChange(ChangeEvent event) {
				changeEvents.add(event);
			}
		};
		ridget.getObservableList().addChangeListener(listener);

		int oldCount = manager.getPersons().size();

		assertEquals(oldCount, ridget.getObservableList().size());
		assertEquals(oldCount, control.getItemCount());

		manager.getPersons().remove(person1);

		int newCount = oldCount - 1;

		assertEquals(newCount, manager.getPersons().size());
		assertEquals(oldCount, ridget.getObservableList().size());
		assertEquals(oldCount, control.getItemCount());
		assertEquals(0, changeEvents.size());

		ridget.updateFromModel();

		assertEquals(newCount, manager.getPersons().size());
		assertEquals(newCount, ridget.getObservableList().size());
		assertEquals(newCount, control.getItemCount());
		// TODO [ev] discuss with team
		//		assertEquals(1, changeEvents.size());
		assertEquals(0, changeEvents.size());
	}

	public void testUpdateFromModelPreservesSelection() {
		ITableRidget ridget = getRidget();

		ridget.setSelection(person2);

		assertSame(person2, ridget.getSelection().get(0));

		manager.getPersons().remove(person1);
		ridget.updateFromModel();

		assertSame(person2, ridget.getSelection().get(0));
	}

	public void testUpdateFromModelRemovesSelection() {
		ITableRidget ridget = getRidget();

		ridget.setSelection(person2);

		assertSame(person2, ridget.getSelection().get(0));

		manager.getPersons().remove(person2);
		ridget.updateFromModel();

		assertTrue(ridget.getSelection().isEmpty());
	}

	public void testContainsOption() {
		ITableRidget ridget = getRidget();

		assertTrue(ridget.containsOption(person1));
		assertTrue(ridget.containsOption(person2));
		assertTrue(ridget.containsOption(person3));

		assertFalse(ridget.containsOption(null));
		assertFalse(ridget.containsOption(new Person("", "")));

		java.util.List<Person> persons = Arrays.asList(new Person[] { person3 });
		PersonManager manager = new PersonManager(persons);
		ridget.bindToModel(manager, "persons", Person.class, new String[] { "firstname", "lastname" }, null);
		ridget.updateFromModel();

		assertFalse(ridget.containsOption(person1));
		assertTrue(ridget.containsOption(person3));
	}

	public void testSetSelectionType() {
		ITableRidget ridget = getRidget();
		Table control = getWidget();

		assertEquals(SelectionType.SINGLE, ridget.getSelectionType());
		assertTrue((control.getStyle() & SWT.MULTI) != 0);

		ridget.setSelection(new int[] { 0, 1 });

		// single selection is enforced
		assertEquals(1, ridget.getSelectionIndices().length);
		assertEquals(1, control.getSelectionCount());

		// multiple selection is now allowed
		ridget.setSelectionType(SelectionType.MULTI);
		ridget.setSelection(new int[] { 0, 1 });

		assertEquals(2, ridget.getSelectionIndices().length);
		assertEquals(2, control.getSelectionCount());
	}

	public void testAddDoubleClickListener() {
		TableRidget ridget = getRidget();
		Table control = getWidget();

		try {
			ridget.addDoubleClickListener(null);
			fail();
		} catch (RuntimeException npe) {
			ok();
		}

		FTActionListener listener1 = new FTActionListener();
		ridget.addDoubleClickListener(listener1);

		FTActionListener listener2 = new FTActionListener();
		ridget.addDoubleClickListener(listener2);
		ridget.addDoubleClickListener(listener2);

		Event doubleClick = new Event();
		doubleClick.widget = control;
		doubleClick.type = SWT.MouseDoubleClick;
		control.notifyListeners(SWT.MouseDoubleClick, doubleClick);

		assertEquals(1, listener1.getCount());
		assertEquals(1, listener2.getCount());

		ridget.removeDoubleClickListener(listener1);

		control.notifyListeners(SWT.MouseDoubleClick, doubleClick);

		assertEquals(1, listener1.getCount());
	}

	public void testSetComparator() {
		TableRidget ridget = getRidget();
		Table control = getWidget();

		// sorts from a to z
		Comparator<Object> comparator = new TypedComparator<String>();

		try {
			ridget.setComparator(-1, comparator);
			fail();
		} catch (RuntimeException rex) {
			ok();
		}

		try {
			ridget.setComparator(2, comparator);
			fail();
		} catch (RuntimeException rex) {
			ok();
		}

		ridget.setSortedAscending(true);

		int lastItemIndex = control.getItemCount() - 1;

		assertEquals("John", control.getItem(0).getText(0));
		assertEquals("Frank", control.getItem(lastItemIndex).getText(0));

		ridget.setComparator(0, comparator);

		assertEquals("John", control.getItem(0).getText(0));
		assertEquals("Frank", control.getItem(lastItemIndex).getText(0));

		ridget.setSortedColumn(0);

		assertEquals("Frank", control.getItem(0).getText(0));
		assertEquals("John", control.getItem(lastItemIndex).getText(0));

		ridget.setComparator(0, null);

		assertEquals("John", control.getItem(0).getText(0));
		assertEquals("Frank", control.getItem(lastItemIndex).getText(0));

		ridget.setComparator(1, comparator);
		ridget.setSortedColumn(1);

		assertEquals("Doe", control.getItem(0).getText(1));
		assertEquals("Zappa", control.getItem(lastItemIndex).getText(1));

		ridget.setSortedAscending(false);

		assertEquals("Zappa", control.getItem(0).getText(1));
		assertEquals("Doe", control.getItem(lastItemIndex).getText(1));

	}

	public void testGetSortedColumn() {
		TableRidget ridget = getRidget();

		try {
			ridget.setSortedColumn(2);
			fail();
		} catch (RuntimeException rex) {
			ok();
		}

		assertEquals(-1, ridget.getSortedColumn());

		ridget.setComparator(0, new TypedComparator<String>());

		assertEquals(-1, ridget.getSortedColumn());

		ridget.setSortedColumn(0);

		assertEquals(0, ridget.getSortedColumn());

		ridget.setComparator(0, null);

		assertEquals(-1, ridget.getSortedColumn());

		ridget.setComparator(1, new TypedComparator<String>());
		ridget.setSortedColumn(1);

		assertEquals(1, ridget.getSortedColumn());

		ridget.setSortedColumn(-1);

		assertEquals(-1, ridget.getSortedColumn());

		// no comparator in column 0
		ridget.setSortedColumn(0);

		assertEquals(-1, ridget.getSortedColumn());
	}

	public void testIsColumnSortable() {
		TableRidget ridget = getRidget();

		try {
			assertFalse(ridget.isColumnSortable(-1));
			fail();
		} catch (RuntimeException rex) {
			ok();
		}

		try {
			assertFalse(ridget.isColumnSortable(2));
			fail();
		} catch (RuntimeException rex) {
			ok();
		}

		for (int i = 0; i < 2; i++) {
			assertFalse(ridget.isColumnSortable(i));

			// columns are sortable by default, when they have a comparator
			ridget.setComparator(i, new TypedComparator<String>());

			assertTrue(ridget.isColumnSortable(i));

			ridget.setColumnSortable(i, false);

			assertFalse(ridget.isColumnSortable(i));

			ridget.setColumnSortable(i, true);

			assertTrue(ridget.isColumnSortable(i));

			// columns are not sortable without a comparator
			ridget.setComparator(i, null);

			assertFalse(ridget.isColumnSortable(i));
		}
	}

	public void testSetSortedAscending() {
		Table control = getWidget();
		TableRidget ridget = getRidget();

		ridget.bindToModel(manager, "persons", Person.class, new String[] { "lastname", "firstname" }, null);
		ridget.updateFromModel();
		int lastItemIndex = control.getItemCount() - 1;

		assertEquals(-1, ridget.getSortedColumn());
		assertFalse(ridget.isSortedAscending());

		ridget.setComparator(0, new TypedComparator<String>());
		ridget.setSortedColumn(0);

		assertTrue(ridget.isSortedAscending());
		assertEquals("Doe", control.getItem(0).getText(0));
		assertEquals("Zappa", control.getItem(lastItemIndex).getText(0));

		ridget.setSortedAscending(false);

		assertFalse(ridget.isSortedAscending());
		assertEquals("Zappa", control.getItem(0).getText(0));
		assertEquals("Doe", control.getItem(lastItemIndex).getText(0));

		ridget.setSortedAscending(true);

		assertTrue(ridget.isSortedAscending());
		assertEquals("Doe", control.getItem(0).getText(0));
		assertEquals("Zappa", control.getItem(lastItemIndex).getText(0));

		ridget.setComparator(0, null);

		assertEquals(-1, ridget.getSortedColumn());
		assertFalse(ridget.isSortedAscending());
	}

	public void testSetSortedAscendingFiresEvents() {
		TableRidget ridget = getRidget();

		ridget.setSortedAscending(true);

		expectPropertyChangeEvents(new PropertyChangeEvent(ridget, ISortableByColumn.PROPERTY_SORT_ASCENDING,
				Boolean.TRUE, Boolean.FALSE));

		ridget.setSortedAscending(false);

		verifyPropertyChangeEvents();
		expectNoPropertyChangeEvent();

		ridget.setSortedAscending(false);

		verifyPropertyChangeEvents();
		expectPropertyChangeEvents(new PropertyChangeEvent(ridget, ISortableByColumn.PROPERTY_SORT_ASCENDING,
				Boolean.FALSE, Boolean.TRUE));

		ridget.setSortedAscending(true);

		verifyPropertyChangeEvents();
		expectNoPropertyChangeEvent();

		ridget.setSortedAscending(true);

		verifyPropertyChangeEvents();
	}

	public void testSetSortedColumnFiresEvents() {
		TableRidget ridget = getRidget();

		assertEquals(-1, ridget.getSortedColumn());

		expectPropertyChangeEvents(new PropertyChangeEvent(ridget, ISortableByColumn.PROPERTY_SORTED_COLUMN, Integer
				.valueOf(-1), Integer.valueOf(0)));

		ridget.setSortedColumn(0);

		verifyPropertyChangeEvents();
		expectNoPropertyChangeEvent();

		ridget.setSortedColumn(0);

		verifyPropertyChangeEvents();
		expectPropertyChangeEvents(new PropertyChangeEvent(ridget, ISortableByColumn.PROPERTY_SORTED_COLUMN, Integer
				.valueOf(0), Integer.valueOf(1)));

		ridget.setSortedColumn(1);

		verifyPropertyChangeEvents();
	}

	public void testSetColumnSortabilityFiresEvents() {
		TableRidget ridget = getRidget();

		expectNoPropertyChangeEvent();

		ridget.setColumnSortable(0, true);

		verifyPropertyChangeEvents();
		expectPropertyChangeEvents(new PropertyChangeEvent(ridget, ISortableByColumn.PROPERTY_COLUMN_SORTABILITY, null,
				Integer.valueOf(0)));

		ridget.setColumnSortable(0, false);

		verifyPropertyChangeEvents();
		expectNoPropertyChangeEvent();

		ridget.setColumnSortable(0, false);

		verifyPropertyChangeEvents();
		expectPropertyChangeEvents(new PropertyChangeEvent(ridget, ISortableByColumn.PROPERTY_COLUMN_SORTABILITY, null,
				Integer.valueOf(0)));

		ridget.setColumnSortable(0, true);

		verifyPropertyChangeEvents();
		expectNoPropertyChangeEvent();

		ridget.setColumnSortable(0, true);

		verifyPropertyChangeEvents();
	}

	public void testColumnHeaderChangesSortability() {
		TableRidget ridget = getRidget();
		Table table = getWidget();

		ridget.setColumnSortable(0, true);
		ridget.setComparator(0, new TypedComparator<String>());
		ridget.setColumnSortable(1, true);
		ridget.setComparator(1, new TypedComparator<String>());

		ridget.setSortedColumn(-1);

		assertEquals(-1, ridget.getSortedColumn());
		assertFalse(ridget.isSortedAscending());

		Event e = new Event();
		e.type = SWT.Selection;
		e.widget = table.getColumn(0);
		e.widget.notifyListeners(SWT.Selection, e);

		assertEquals(0, ridget.getSortedColumn());
		assertTrue(ridget.isSortedAscending());

		e.widget.notifyListeners(SWT.Selection, e);

		assertEquals(0, ridget.getSortedColumn());
		assertFalse(ridget.isSortedAscending());

		e.widget.notifyListeners(SWT.Selection, e);

		assertEquals(-1, ridget.getSortedColumn());
		assertFalse(ridget.isSortedAscending());

		e.widget.notifyListeners(SWT.Selection, e);

		assertEquals(0, ridget.getSortedColumn());
		assertTrue(ridget.isSortedAscending());
	}

	public void testSetMoveableColumns() {
		TableRidget ridget = getRidget();
		Table table = getWidget();

		assertFalse(ridget.hasMoveableColumns());
		assertFalse(table.getColumn(0).getMoveable());
		assertFalse(table.getColumn(1).getMoveable());

		ridget.setMoveableColumns(true);

		assertTrue(ridget.hasMoveableColumns());
		assertTrue(table.getColumn(0).getMoveable());
		assertTrue(table.getColumn(1).getMoveable());

		ridget.setMoveableColumns(false);

		assertFalse(ridget.hasMoveableColumns());
		assertFalse(table.getColumn(0).getMoveable());
		assertFalse(table.getColumn(1).getMoveable());
	}

	/**
	 * Tests that for single selection, the ridget selection state and the ui
	 * selection state cannot be changed by the user when ridget is set to
	 * "output only".
	 */
	public void testOutputSingleSelectionCannotBeChangedFromUI() {
		TableRidget ridget = getRidget();
		Table control = getWidget();

		ridget.setSelectionType(SelectionType.SINGLE);

		assertEquals(0, ridget.getSelection().size());
		assertEquals(0, control.getSelectionCount());

		ridget.setOutputOnly(true);
		control.setFocus();
		// move down and up to select row 0; space does not select in tables
		UITestHelper.sendKeyAction(control.getDisplay(), UITestHelper.KC_ARROW_DOWN);
		UITestHelper.sendKeyAction(control.getDisplay(), UITestHelper.KC_ARROW_UP);

		assertEquals(0, ridget.getSelection().size());
		assertEquals(0, control.getSelectionCount());

		ridget.setOutputOnly(false);
		control.setFocus();
		// move down and up to select row 0; space does not select in tables
		UITestHelper.sendKeyAction(control.getDisplay(), UITestHelper.KC_ARROW_DOWN);
		UITestHelper.sendKeyAction(control.getDisplay(), UITestHelper.KC_ARROW_UP);

		assertEquals(1, ridget.getSelection().size());
		assertEquals(1, control.getSelectionCount());
	}

	/**
	 * Tests that for multiple selection, the ridget selection state and the ui
	 * selection state cannot be changed by the user when ridget is set to
	 * "output only".
	 */
	public void testOutputMultipleSelectionCannotBeChangedFromUI() {
		TableRidget ridget = getRidget();
		Table control = getWidget();

		ridget.setSelectionType(SelectionType.MULTI);

		assertEquals(0, ridget.getSelection().size());
		assertEquals(0, control.getSelectionCount());

		ridget.setOutputOnly(true);
		control.setFocus();
		// move down and up to select row 0; space does not select in tables
		UITestHelper.sendKeyAction(control.getDisplay(), UITestHelper.KC_ARROW_DOWN);
		UITestHelper.sendKeyAction(control.getDisplay(), UITestHelper.KC_ARROW_UP);

		assertEquals(0, ridget.getSelection().size());
		assertEquals(0, control.getSelectionCount());

		ridget.setOutputOnly(false);
		control.setFocus();
		// move down and up to select row 0; space does not select in tables
		UITestHelper.sendKeyAction(control.getDisplay(), UITestHelper.KC_ARROW_DOWN);
		UITestHelper.sendKeyAction(control.getDisplay(), UITestHelper.KC_ARROW_UP);

		assertEquals(1, ridget.getSelection().size());
		assertEquals(1, control.getSelectionCount());
	}

	/**
	 * Tests that toggling output state on/off does not change the selection.
	 */
	public void testTogglingOutputDoesNotChangeSelection() {
		TableRidget ridget = getRidget();

		ridget.setSelection(0);

		assertEquals(0, ridget.getSelectionIndex());

		ridget.setOutputOnly(true);

		assertEquals(0, ridget.getSelectionIndex());

		ridget.setSelection((Object) null);

		assertEquals(-1, ridget.getSelectionIndex());

		ridget.setOutputOnly(false);

		assertEquals(-1, ridget.getSelectionIndex());
	}

	public void testSetColumnFormatter() {
		TableRidget ridget = getRidget();
		Table table = getWidget();
		IColumnFormatter formatter = new ColumnFormatter() {
			@Override
			public String getText(Object element) {
				Person person = (Person) element;
				return person.getLastname().toUpperCase();
			}
		};
		final String lastName = person1.getLastname();
		final String lastNameUpperCase = lastName.toUpperCase();

		try {
			ridget.setColumnFormatter(-1, formatter);
			fail();
		} catch (RuntimeException rex) {
			ok();
		}

		try {
			ridget.setColumnFormatter(99, formatter);
			fail();
		} catch (RuntimeException rex) {
			ok();
		}

		ridget.setColumnFormatter(1, formatter);

		assertEquals(lastName, table.getItem(0).getText(1));

		ridget.updateFromModel();

		assertEquals(lastNameUpperCase, table.getItem(0).getText(1));

		ridget.setColumnFormatter(1, null);

		assertEquals(lastNameUpperCase, table.getItem(0).getText(1));

		ridget.updateFromModel();

		assertEquals(lastName, table.getItem(0).getText(1));
	}

	public void testAddSelectionListener() {
		TableRidget ridget = getRidget();
		Table control = getWidget();

		try {
			ridget.addSelectionListener(null);
			fail();
		} catch (RuntimeException npe) {
			ok();
		}

		TestSelectionListener selectionListener = new TestSelectionListener();
		ridget.addSelectionListener(selectionListener);

		ridget.setSelection(person1);
		assertEquals(1, selectionListener.getCount());
		ridget.removeSelectionListener(selectionListener);
		ridget.setSelection(person2);
		assertEquals(1, selectionListener.getCount());
		ridget.clearSelection();

		ridget.addSelectionListener(selectionListener);
		ridget.setSelectionType(SelectionType.SINGLE);
		assertEquals(0, ridget.getSelection().size());
		assertEquals(0, control.getSelectionCount());

		control.setFocus();
		UITestHelper.sendKeyAction(control.getDisplay(), UITestHelper.KC_ARROW_DOWN);

		assertEquals(1, ridget.getSelection().size());
		assertEquals(1, control.getSelectionCount());
		assertEquals(2, selectionListener.getCount());
		SelectionEvent selectionEvent = selectionListener.getSelectionEvent();
		assertEquals(ridget, selectionEvent.getSource());
		assertTrue(selectionEvent.getOldSelection().isEmpty());
		assertEquals(ridget.getSelection(), selectionEvent.getNewSelection());
		// System.out.println("SelectionEvent: " + selectionListener.getSelectionEvent());

		UITestHelper.sendKeyAction(control.getDisplay(), UITestHelper.KC_ARROW_DOWN);

		assertEquals(1, ridget.getSelection().size());
		assertEquals(1, control.getSelectionCount());
		assertEquals(3, selectionListener.getCount());
		SelectionEvent selectionEvent2 = selectionListener.getSelectionEvent();
		assertEquals(ridget, selectionEvent.getSource());
		assertEquals(selectionEvent.getNewSelection(), selectionEvent2.getOldSelection());
		assertEquals(ridget.getSelection(), selectionEvent2.getNewSelection());
		// System.out.println("SelectionEvent: " + selectionListener.getSelectionEvent());

		ridget.removeSelectionListener(selectionListener);
	}

	/**
	 * As per Bug 285305
	 */
	public void testAutoCreateTableColumns() {
		ITableRidget ridget = createRidget();
		Table control = new Table(getShell(), SWT.FULL_SELECTION | SWT.SINGLE);
		ridget.setUIControl(control);

		assertEquals(0, control.getColumnCount());

		String[] columns3 = { Person.PROPERTY_FIRSTNAME, Person.PROPERTY_LASTNAME, Person.PROPERTY_BIRTHDAY };
		ridget.bindToModel(manager, "persons", Person.class, columns3, null);

		assertEquals(3, control.getColumnCount());

		String[] columns1 = { Person.PROPERTY_FIRSTNAME };
		ridget.bindToModel(manager, "persons", Person.class, columns1, null);

		assertEquals(1, control.getColumnCount());
	}

	/**
	 * As per Bug 285305
	 */
	public void testAutoCreateColumnsWithNoLayout() {
		ITableRidget ridget = createRidget();
		Table control = new Table(getShell(), SWT.FULL_SELECTION | SWT.SINGLE);
		ridget.setUIControl(control);

		getShell().setLayout(null);
		control.setSize(300, 100);
		String[] columns3 = { Person.PROPERTY_FIRSTNAME, Person.PROPERTY_LASTNAME, Person.PROPERTY_BIRTHDAY };
		ridget.bindToModel(manager, "persons", Person.class, columns3, null);

		assertEquals(null, control.getParent().getLayout());
		assertEquals(null, control.getLayout());
		for (int i = 0; i < 3; i++) {
			assertEquals("col #" + i, 100, control.getColumn(i).getWidth());
		}
	}

	/**
	 * As per Bug 285305
	 */
	public void testAutoCreateColumnsWithTableLayout() {
		ITableRidget ridget = createRidget();
		Table control = new Table(getShell(), SWT.FULL_SELECTION | SWT.SINGLE);
		control.setLayout(new TableLayout());
		ridget.setUIControl(control);

		String[] columns3 = { Person.PROPERTY_FIRSTNAME, Person.PROPERTY_LASTNAME, Person.PROPERTY_BIRTHDAY };
		ridget.bindToModel(manager, "persons", Person.class, columns3, null);

		Class<?> shellLayout = getShell().getLayout().getClass();
		assertSame(shellLayout, control.getParent().getLayout().getClass());
		assertTrue(control.getLayout() instanceof TableLayout);
		for (int i = 0; i < 3; i++) {
			assertEquals("col #" + i, 50, control.getColumn(i).getWidth());
		}
	}

	/**
	 * As per Bug 285305
	 */
	public void testAutoCreateColumnsWithTableColumnLayout() {
		ITableRidget ridget = createRidget();
		for (Control control : getShell().getChildren()) {
			control.dispose();
		}
		Table control = new Table(getShell(), SWT.FULL_SELECTION | SWT.SINGLE);
		ridget.setUIControl(control);
		getShell().setLayout(new TableColumnLayout());

		String[] columns3 = { Person.PROPERTY_FIRSTNAME, Person.PROPERTY_LASTNAME, Person.PROPERTY_BIRTHDAY };
		ridget.bindToModel(manager, "persons", Person.class, columns3, null);

		assertTrue(control.getParent().getLayout() instanceof TableColumnLayout);
		assertEquals(null, control.getLayout());
		for (int i = 0; i < 3; i++) {
			assertEquals("col #" + i, 50, control.getColumn(i).getWidth());
		}
	}

	// helping methods
	// ////////////////

	@Override
	protected void clearUIControlRowSelection() {
		getWidget().deselectAll();
		fireSelectionEvent();
	}

	@Override
	protected int getUIControlSelectedRowCount() {
		return getWidget().getSelectionCount();
	}

	@Override
	protected int getUIControlSelectedRow() {
		return getWidget().getSelectionIndex();
	}

	@Override
	protected Object getRowValue(int i) {
		// return getRidget().getRowObservables().get(i);
		IObservableList rowObservables = ReflectionUtils.invokeHidden(getRidget(), "getRowObservables");
		return rowObservables.get(i);
	}

	@Override
	protected int[] getSelectedRows() {
		// IObservableList rowObservables = getRidget().getRowObservables();
		IObservableList rowObservables = ReflectionUtils.invokeHidden(getRidget(), "getRowObservables");
		Object[] elements = getRidget().getMultiSelectionObservable().toArray();
		int[] result = new int[elements.length];
		for (int i = 0; i < elements.length; i++) {
			Object element = elements[i];
			result[i] = rowObservables.indexOf(element);
		}
		return result;
	}

	@Override
	protected int[] getUIControlSelectedRows() {
		return getWidget().getSelectionIndices();
	}

	@Override
	protected void setUIControlRowSelection(int[] indices) {
		getWidget().setSelection(indices);
		fireSelectionEvent();
	}

	@Override
	protected void setUIControlRowSelectionInterval(int start, int end) {
		getWidget().setSelection(start, end);
		fireSelectionEvent();
	}

	@Override
	protected boolean supportsMulti() {
		return true;
	}

}
