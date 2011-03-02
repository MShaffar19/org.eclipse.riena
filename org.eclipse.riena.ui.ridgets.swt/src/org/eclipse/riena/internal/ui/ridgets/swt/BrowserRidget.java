/*******************************************************************************
 * Copyright � 2009, 2011 Florian Pirchner and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Florian Pirchner � initial API and implementation (based on other ridgets of
 *                    compeople AG)
 * compeople AG     - adjustments for Riena v1.2 - 3.0
 *******************************************************************************/
package org.eclipse.riena.internal.ui.ridgets.swt;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;

import org.eclipse.riena.core.util.ListenerList;
import org.eclipse.riena.core.util.StringUtils;
import org.eclipse.riena.ui.ridgets.AbstractMarkerSupport;
import org.eclipse.riena.ui.ridgets.IBrowserRidget;
import org.eclipse.riena.ui.ridgets.listener.ILocationListener;
import org.eclipse.riena.ui.ridgets.swt.AbstractSWTRidget;
import org.eclipse.riena.ui.ridgets.swt.AbstractValueRidget;
import org.eclipse.riena.ui.ridgets.swt.BasicMarkerSupport;
import org.eclipse.riena.ui.swt.facades.BrowserFacade;

/**
 * Ridget for an SWT {@link Browser} widget.
 * <p>
 * Implementation note: because of SWT <a
 * href="http://bugs.eclipse.org/84532">Bug 84532</a> the
 * {@link #setFocusable(boolean)} methods has no effect.
 * 
 * @since 1.2
 */
public class BrowserRidget extends AbstractValueRidget implements IBrowserRidget {

	/**
	 * This property is used by the databinding to sync ridget and model. It is
	 * always fired before its sibling {@link IBrowserRidget#PROPERTY_URL} to
	 * ensure that the model is updated before any listeners try accessing it.
	 * <p>
	 * This property is not API. Do not use in client code.
	 */
	private static final String PROPERTY_URL_INTERNAL = "urlInternal"; //$NON-NLS-1$

	private final InternalLocationListener internalLocationListener;

	private String url;
	private String text;

	public BrowserRidget() {
		internalLocationListener = new InternalLocationListener();
	}

	@Override
	protected void checkUIControl(final Object uiControl) {
		AbstractSWTRidget.assertType(uiControl, Browser.class);
	}

	@Override
	protected void bindUIControl() {
		final Browser control = getUIControl();
		if (control != null) {
			updateUIControl();
			control.addLocationListener(internalLocationListener);
		}
	}

	@Override
	protected void unbindUIControl() {
		final Browser control = getUIControl();
		if (control != null) {
			control.removeLocationListener(internalLocationListener);
		}
		super.unbindUIControl();
	}

	@Override
	protected AbstractMarkerSupport createMarkerSupport() {
		return new BasicMarkerSupport(this, propertyChangeSupport);
	}

	@Override
	protected IObservableValue getRidgetObservable() {
		return BeansObservables.observeValue(this, PROPERTY_URL_INTERNAL);
	}

	public void addLocationListener(final ILocationListener listener) {
		internalLocationListener.addLocationListener(listener);
	}

	@Override
	public Browser getUIControl() {
		return (Browser) super.getUIControl();
	}

	public String getText() {
		return text;
	}

	public String getUrl() {
		return url;
	}

	/**
	 * This method is not API. Do not use in client code.
	 * 
	 * @noreference This method is not intended to be referenced by clients.
	 */
	public final String getUrlInternal() {
		return getUrl();
	}

	/**
	 * Always returns true because mandatory markers do not make sense for this
	 * ridget.
	 */
	@Override
	public boolean isDisableMandatoryMarker() {
		return true;
	}

	public void removeLocationListener(final ILocationListener listener) {
		internalLocationListener.removeLocationListener(listener);
	}

	public void setText(final String text) {
		if (!StringUtils.equals(this.text, text)) {
			this.text = text;
			final String oldUrl = this.url;
			this.url = null;
			updateUIControl();
			firePropertyChange(PROPERTY_URL_INTERNAL, oldUrl, this.url);
			firePropertyChange(IBrowserRidget.PROPERTY_URL, oldUrl, this.url);
		}
	}

	public void setUrl(final String url) {
		if (!StringUtils.equals(this.url, url)) {
			final String oldUrl = this.getUrl();
			this.text = null;
			this.url = url;
			updateUIControl();
			firePropertyChange(PROPERTY_URL_INTERNAL, oldUrl, this.url);
			firePropertyChange(IBrowserRidget.PROPERTY_URL, oldUrl, this.url);
		}
	}

	// helping methods
	//////////////////

	private String convertNullToEmpty(final String string) {
		return string != null ? string : ""; //$NON-NLS-1$
	}

	private void updateUIControl() {
		final Browser browser = getUIControl();
		if (browser != null) {
			if (text != null) {
				final String browserText = BrowserFacade.getDefault().getText(browser);
				if (!text.equals(browserText)) {
					internalLocationListener.unblock();
					browser.setText(text);
				}
			} else {
				final String notNullUrl = convertNullToEmpty(url);
				if (!notNullUrl.equals(browser.getUrl())) {
					internalLocationListener.unblock();
					browser.setUrl(notNullUrl);
				}
			}
		}
	}

	// helping classes
	//////////////////

	/**
	 * Listens to location changes in the Browser widget and update's the
	 * Ridget's URL if necessary.
	 */
	private final class InternalLocationListener implements LocationListener {

		private ListenerList<ILocationListener> listeners;
		private boolean block;

		void addLocationListener(final ILocationListener listener) {
			Assert.isNotNull(listener);
			if (listeners == null) {
				listeners = new ListenerList<ILocationListener>(ILocationListener.class);
			}
			listeners.add(listener);
		}

		void removeLocationListener(final ILocationListener listener) {
			if (listeners != null) {
				listeners.remove(listener);
			}
		}

		/**
		 * Allow the next url-change, even if output-only marker is set.
		 * <p>
		 * This is used by updateUIControl() to permit updating a widget on
		 * rebind, setText, setUrl.
		 * <p>
		 * Implementation notes: {@link #changing(LocationEvent)} is invoked an
		 * undefined time after {@link #unblock()}, since the page load happens
		 * asynchronously. Currently there is no synchronisation build in - we
		 * simply allow the next change. This is not likely to cause problems,
		 * however it could allow another change to happen, if it is processed
		 * before the intended LocationEvent. The event.location value is
		 * formatted by the browser and may have things added (parameters,
		 * http://www prefix) so checking for BrowserRidget.url equality is not
		 * an option for identifying which url to unblock.
		 */
		void unblock() {
			block = false;
		}

		public void changing(final LocationEvent event) {
			if (block) {
				if (isOutputOnly()) {
					event.doit = false;
				}
				final org.eclipse.riena.ui.ridgets.listener.LocationEvent locEvent = new org.eclipse.riena.ui.ridgets.listener.LocationEvent(
						event.location, event.top);
				for (final ILocationListener listener : listeners) {
					event.doit &= listener.locationChanging(locEvent);
				}
			}
			block = true;
		}

		public void changed(final LocationEvent event) {
			if (event.top && !isNullOrAboutBlank(event.location)) {
				setUrl(event.location);
			}
		}

		private boolean isNullOrAboutBlank(final String url) {
			return url == null || "about:blank".equals(url); //$NON-NLS-1$
		}
	}

}