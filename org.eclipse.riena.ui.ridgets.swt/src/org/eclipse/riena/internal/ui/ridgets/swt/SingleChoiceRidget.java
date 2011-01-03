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
package org.eclipse.riena.internal.ui.ridgets.swt;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.BindingException;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateListStrategy;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;

import org.eclipse.riena.core.util.ListenerList;
import org.eclipse.riena.ui.ridgets.IChoiceRidget;
import org.eclipse.riena.ui.ridgets.IMarkableRidget;
import org.eclipse.riena.ui.ridgets.IRidget;
import org.eclipse.riena.ui.ridgets.ISingleChoiceRidget;
import org.eclipse.riena.ui.ridgets.listener.ISelectionListener;
import org.eclipse.riena.ui.ridgets.swt.AbstractSWTRidget;
import org.eclipse.riena.ui.ridgets.swt.MarkerSupport;
import org.eclipse.riena.ui.swt.ChoiceComposite;
import org.eclipse.riena.ui.swt.lnf.LnFUpdater;
import org.eclipse.riena.ui.swt.utils.SwtUtilities;

/**
 * Ridget for a {@link ChoiceComposite} widget with single selection.
 */
public class SingleChoiceRidget extends AbstractSWTRidget implements ISingleChoiceRidget {

	private final static LnFUpdater LNF_UPDATER = new LnFUpdater();

	/** The list of available options. */
	private final WritableList optionsObservable;
	/** The selected option. */
	private final WritableValue selectionObservable;

	private Binding optionsBinding;
	private Binding selectionBinding;
	private String[] optionLabels;

	private Object selectionValueIgnoredRegardingMandatory;

	/** A list of selection listeners. */
	private ListenerList<ISelectionListener> selectionListeners;

	public SingleChoiceRidget() {
		optionsObservable = new WritableList();
		selectionObservable = new WritableValue();
		selectionObservable.addChangeListener(new IChangeListener() {
			public void handleChange(final ChangeEvent event) {
				disableMandatoryMarkers(hasMandatoryInput());
			}
		});
		addPropertyChangeListener(IRidget.PROPERTY_ENABLED, new PropertyChangeListener() {
			public void propertyChange(final PropertyChangeEvent evt) {
				updateSelection(getUIControl());
			}
		});
		addPropertyChangeListener(IMarkableRidget.PROPERTY_OUTPUT_ONLY, new PropertyChangeListener() {
			public void propertyChange(final PropertyChangeEvent evt) {
				final boolean isOutput = ((Boolean) evt.getNewValue()).booleanValue();
				updateEditable(getUIControl(), !isOutput);
			}
		});
	}

	@Override
	protected void bindUIControl() {
		if (optionsBinding != null) {
			createChildren(getUIControl());
		}
	}

	@Override
	protected void checkUIControl(final Object uiControl) {
		AbstractSWTRidget.assertType(uiControl, ChoiceComposite.class);
		if (uiControl != null) {
			final ChoiceComposite composite = (ChoiceComposite) uiControl;
			Assert.isTrue(!composite.isMultipleSelection(), "expected single selection ChoiceComposite"); //$NON-NLS-1$
		}
	}

	@Override
	protected void unbindUIControl() {
		super.unbindUIControl();
		disposeChildren(getUIControl());
	}

	// public methods
	// ///////////////

	@Override
	public ChoiceComposite getUIControl() {
		return (ChoiceComposite) super.getUIControl();
	}

	public void bindToModel(final IObservableList optionValues, final IObservableValue selectionValue) {
		Assert.isNotNull(optionValues, "optionValues"); //$NON-NLS-1$
		Assert.isNotNull(selectionValue, "selectionValue"); //$NON-NLS-1$
		bindToModel(optionValues, null, selectionValue);
	}

	public void bindToModel(final Object listHolder, final String listPropertyName, final Object selectionHolder,
			final String selectionPropertyName) {
		Assert.isNotNull(listHolder, "listHolder"); //$NON-NLS-1$
		Assert.isNotNull(listPropertyName, "listPropertyName"); //$NON-NLS-1$
		Assert.isNotNull(selectionHolder, "selectionHolder"); //$NON-NLS-1$
		Assert.isNotNull(selectionPropertyName, "selectionPropertyName"); //$NON-NLS-1$
		IObservableList optionValues;
		if (AbstractSWTRidget.isBean(listHolder.getClass())) {
			optionValues = BeansObservables.observeList(listHolder, listPropertyName);
		} else {
			optionValues = PojoObservables.observeList(listHolder, listPropertyName);
		}
		IObservableValue selectionValue;
		if (AbstractSWTRidget.isBean(selectionHolder.getClass())) {
			selectionValue = BeansObservables.observeValue(selectionHolder, selectionPropertyName);
		} else {
			selectionValue = PojoObservables.observeValue(selectionHolder, selectionPropertyName);
		}
		bindToModel(optionValues, null, selectionValue);
	}

	public void bindToModel(final List<? extends Object> optionValues, final List<String> optionLabels,
			final Object selectionHolder, final String selectionPropertyName) {
		Assert.isNotNull(optionValues, "optionValues"); //$NON-NLS-1$
		Assert.isNotNull(selectionHolder, "selectionHolder"); //$NON-NLS-1$
		Assert.isNotNull(selectionPropertyName, "selectionPropertyName"); //$NON-NLS-1$
		final IObservableList list = new WritableList(optionValues, null);
		IObservableValue selectionValue;
		if (AbstractSWTRidget.isBean(selectionHolder.getClass())) {
			selectionValue = BeansObservables.observeValue(selectionHolder, selectionPropertyName);
		} else {
			selectionValue = PojoObservables.observeValue(selectionHolder, selectionPropertyName);
		}
		bindToModel(list, optionLabels, selectionValue);
	}

	@Override
	public void updateFromModel() {
		assertIsBoundToModel();
		super.updateFromModel();
		optionsBinding.updateModelToTarget();
		final Object oldSelection = selectionObservable.getValue();
		selectionBinding.updateModelToTarget();
		final ChoiceComposite control = getUIControl();
		final int oldCount = getChildrenCount(control);
		disposeChildren(control);
		createChildren(control);
		final int newCount = getChildrenCount(control);
		if (oldCount != newCount) {
			// if the number of children has changed
			// update the layout of the parent composite
			control.getParent().layout(true, false);
		}
		// remove unavailable element and re-apply selection
		Object newSelection = oldSelection;
		if (newSelection != null && !optionsObservable.contains(newSelection)) {
			selectionObservable.setValue(null);
			newSelection = null;
		}
		firePropertyChange(PROPERTY_SELECTION, oldSelection, newSelection);
	}

	/**
	 * Returns the number of the children of the given UI control.
	 * 
	 * @param control
	 *            UI control
	 * 
	 * @return number of children
	 */
	private int getChildrenCount(final Composite control) {
		if (SwtUtilities.isDisposed(control)) {
			return 0;
		}
		return control.getChildren().length;
	}

	public Object getSelection() {
		return selectionObservable.getValue();
	}

	public void setSelection(final Object candidate) {
		assertIsBoundToModel();
		if (candidate != null && !optionsObservable.contains(candidate)) {
			throw new BindingException("candidate not in option list: " + candidate); //$NON-NLS-1$
		}
		final Object oldSelection = selectionObservable.getValue();
		selectionObservable.setValue(candidate);
		updateSelection(getUIControl());
		firePropertyChange(PROPERTY_SELECTION, oldSelection, candidate);
	}

	public Object getSelectionValueIgnoredRegardingMandatory() {
		return selectionValueIgnoredRegardingMandatory;
	}

	public void setSelectionValueIgnoredRegardingMandatory(final Object selectionValueIgnoredRegardingMandatory) {
		this.selectionValueIgnoredRegardingMandatory = selectionValueIgnoredRegardingMandatory;
	}

	public IObservableList getObservableList() {
		return optionsObservable;
	}

	@Override
	public final boolean isDisableMandatoryMarker() {
		return hasMandatoryInput();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.2
	 */
	public void addSelectionListener(final ISelectionListener selectionListener) {
		Assert.isNotNull(selectionListener, "selectionListener is null"); //$NON-NLS-1$
		if (selectionListeners == null) {
			selectionListeners = new ListenerList<ISelectionListener>(ISelectionListener.class);
			addPropertyChangeListener(IChoiceRidget.PROPERTY_SELECTION, new PropertyChangeListener() {
				public void propertyChange(final PropertyChangeEvent evt) {
					notifySelectionListeners(Arrays.asList(evt.getOldValue()), Arrays.asList(evt.getNewValue()));
				}
			});
		}
		selectionListeners.add(selectionListener);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.2
	 */
	public void removeSelectionListener(final ISelectionListener selectionListener) {
		if (selectionListeners != null) {
			selectionListeners.remove(selectionListener);
		}
	}

	// helping methods
	// ////////////////

	private void assertIsBoundToModel() {
		if (optionsBinding == null || selectionBinding == null) {
			throw new BindingException("ridget not bound to model"); //$NON-NLS-1$
		}
	}

	private void bindToModel(final IObservableList optionValues, final List<String> optionLabels,
			final IObservableValue selectionValue) {
		if (optionLabels != null) {
			final String msg = "Mismatch between number of optionValues and optionLabels"; //$NON-NLS-1$
			Assert.isLegal(optionValues.size() == optionLabels.size(), msg);
		}

		unbindUIControl();

		// clear observables as they may be bound to another model
		// must dispose old bindings first to avoid updating the old model
		if (optionsBinding != null) {
			optionsBinding.dispose();
			optionsBinding = null;
			optionsObservable.clear();
		}
		if (selectionBinding != null) {
			selectionBinding.dispose();
			selectionBinding = null;
			selectionObservable.setValue(null);
		}

		// set up new binding
		final DataBindingContext dbc = new DataBindingContext();
		optionsBinding = dbc.bindList(optionsObservable, optionValues, new UpdateListStrategy(
				UpdateListStrategy.POLICY_UPDATE), new UpdateListStrategy(UpdateListStrategy.POLICY_ON_REQUEST));
		selectionBinding = dbc.bindValue(selectionObservable, selectionValue, new UpdateValueStrategy(
				UpdateValueStrategy.POLICY_UPDATE), new UpdateValueStrategy(UpdateValueStrategy.POLICY_ON_REQUEST));
		if (optionLabels != null) {
			this.optionLabels = optionLabels.toArray(new String[optionLabels.size()]);
		} else {
			this.optionLabels = null;
		}

		bindUIControl();
	}

	private void createChildren(final ChoiceComposite control) {
		if (control != null && !control.isDisposed()) {
			final Object[] values = optionsObservable.toArray();
			for (int i = 0; i < values.length; i++) {
				final Object value = values[i];
				final String caption = optionLabels != null ? optionLabels[i] : String.valueOf(value);

				final Button button = control.createChild(caption);
				button.setData(value);
				button.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(final SelectionEvent e) {
						final Button button = (Button) e.widget;
						final Object data = button.getData();
						if (button.getSelection() && !isOutputOnly()) {
							// this is a workaround to make composite table aware of focus changes, Bug #264627
							SingleChoiceRidget.this.setSelection(data);
							fireFocusIn(button.getParent());
						}
					}
				});
			}
			updateSelection(control);
			LNF_UPDATER.updateUIControls(control, true);
		}
	}

	private void disposeChildren(final Composite control) {
		if (control != null && !control.isDisposed()) {
			for (final Control child : control.getChildren()) {
				child.dispose();
			}
		}
	}

	private void fireFocusIn(final Control control) {
		final Event event = new Event();
		event.type = SWT.FocusIn;
		event.widget = control;
		control.notifyListeners(SWT.FocusIn, event);
	}

	private boolean hasInput() {
		return selectionObservable.getValue() != null;
	}

	private boolean hasMandatoryInput() {
		return hasInput()
				&& (selectionValueIgnoredRegardingMandatory == null || !selectionValueIgnoredRegardingMandatory
						.equals(getSelection()));
	}

	private void updateEditable(final ChoiceComposite control, final boolean isEditable) {
		if (control != null && !control.isDisposed()) {
			control.setEditable(isEditable);
		}
	}

	/**
	 * Iterates over the composite's children, disabling all buttons, except the
	 * one that has value as it's data element. If the ridget is not enabled, it
	 * may deselect all buttons, as mandated by
	 * {@link MarkerSupport#isHideDisabledRidgetContent()}.
	 */
	private void updateSelection(final ChoiceComposite control) {
		final boolean canSelect = isEnabled() || !MarkerSupport.isHideDisabledRidgetContent();
		if (control != null && !control.isDisposed()) {
			final Object value = selectionObservable.getValue();
			for (final Control child : control.getChildren()) {
				final Button button = (Button) child;
				final boolean isSelected = canSelect && (value != null) && (value.equals(child.getData()));
				button.setSelection(isSelected);
			}
		}
		updateEditable(control, !isOutputOnly());
	}

	private void notifySelectionListeners(final List<?> oldSelectionList, final List<?> newSelectionList) {
		if (selectionListeners != null) {
			final org.eclipse.riena.ui.ridgets.listener.SelectionEvent event = new org.eclipse.riena.ui.ridgets.listener.SelectionEvent(
					this, oldSelectionList, newSelectionList);
			for (final ISelectionListener listener : selectionListeners.getListeners()) {
				listener.ridgetSelected(event);
			}
		}
	}
}
