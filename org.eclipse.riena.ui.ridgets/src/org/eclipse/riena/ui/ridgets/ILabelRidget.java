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
package org.eclipse.riena.ui.ridgets;

import java.net.URL;

/**
 * Ridget for a label.
 * <p>
 * Because a label is read-only, this ridget will not modify the bound model
 * value (ref. {@link #bindToModel(Object, String)}) when the ridget value is
 * modified.
 */
public interface ILabelRidget extends IValueRidget {

	/**
	 * Property name of the text property.
	 * 
	 * @see #getText()
	 * @see #setText(String)
	 */
	String PROPERTY_TEXT = "text"; //$NON-NLS-1$

	/**
	 * @return Returns the label.
	 */
	String getText();

	/**
	 * @param text
	 *            The label to set.
	 */
	void setText(String text);

	/**
	 * Returns the location of the icon.
	 * 
	 * @return the URL for the image.
	 */
	URL getIconLocation();

	/**
	 * Sets the location of the icon. <br>
	 * Sets the ID of the icon. <br>
	 * Beware of the method setIcon(String). The latest set icon is displayed. <br>
	 * 
	 * @see #setIcon(String)
	 * 
	 * @param location
	 *            - the URL for the image.
	 */
	void setIconLocation(URL location);

	/**
	 * Returns the name of the icon.
	 * 
	 * @return icon name.
	 */
	String getIcon();

	/**
	 * Sets the name of the icon. <br>
	 * Beware of the method setIconLocation(URL). The latest set icon is
	 * displayed. <br>
	 * 
	 * @see #setIconLocation(URL)
	 * 
	 * @param icon
	 *            - icon name.
	 */
	void setIcon(String icon);
}
