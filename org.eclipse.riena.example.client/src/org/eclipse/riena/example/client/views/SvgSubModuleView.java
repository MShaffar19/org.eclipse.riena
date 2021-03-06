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
package org.eclipse.riena.example.client.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import org.eclipse.riena.navigation.ui.swt.views.SubModuleView;
import org.eclipse.riena.ui.core.resource.IconSize;
import org.eclipse.riena.ui.swt.lnf.LnfKeyConstants;
import org.eclipse.riena.ui.swt.lnf.LnfManager;
import org.eclipse.riena.ui.swt.utils.ImageStore;
import org.eclipse.riena.ui.swt.utils.UIControlsFactory;

/**
 *
 */
public class SvgSubModuleView extends SubModuleView {

	public static final String ID = SvgSubModuleView.class.getName();

	@Override
	protected void basicCreatePartControl(final Composite parent) {
		parent.setBackground(LnfManager.getLnf().getColor(LnfKeyConstants.SUB_MODULE_BACKGROUND));
		final RowLayout rowLayout = new RowLayout();
		rowLayout.type = SWT.VERTICAL;
		parent.setLayout(rowLayout);

		final Composite textComposite = new Composite(parent, SWT.NONE);
		textComposite.setLayout(rowLayout);
		final Label lblInfo = new Label(textComposite, SWT.WRAP);
		lblInfo.setText(
				"Actually available for this example are three svg images(cloud.svg,cloudX.svg and cloudY.svg). The cloud.svg is the default icon and will be used when the user requests a specific version of the cloud icon which is not available. The other icons were mapped to a specific IconSize."); //$NON-NLS-1$
		lblInfo.setLayoutData(new RowData(500, SWT.DEFAULT));

		final Label lblMapping = new Label(textComposite, SWT.WRAP);
		lblMapping.setText(
				"The mapping is a great way to reduce the amount of icons needed for different purposes, like icons in buttons or toolbars or for use as the application logo."); //$NON-NLS-1$
		lblMapping.setLayoutData(new RowData(500, SWT.DEFAULT));

		final Label lblmapping = new Label(textComposite, SWT.NONE);
		lblmapping.setText("The mapping looks like:\nIconSize.B22 -> X\nIconSize.E64 -> Y "); //$NON-NLS-1$

		final Composite pictureComposite = new Composite(parent, SWT.NONE);
		pictureComposite.setLayout(rowLayout);
		final GridLayout layout = new GridLayout();
		layout.numColumns = 2;

		final Composite compositeA = new Composite(pictureComposite, SWT.NONE);
		compositeA.setLayout(layout);
		final Label lblA = UIControlsFactory.createLabel(compositeA, "", "lblX");

		final Label lblTA = new Label(compositeA, SWT.WRAP);
		lblTA.setText(
				"This is the cloudX.svg. By requesting the cloud image with the IconSize of B22 the application automatically used the X variant of the cloud.svg. The X group is ment to be used for small Iconsizes with less details. "); //$NON-NLS-1$
		lblTA.setLayoutData(new GridData(500, SWT.DEFAULT));

		final Composite compositeB = new Composite(pictureComposite, SWT.NONE);
		compositeB.setLayout(layout);
		final Label lblB = UIControlsFactory.createLabel(compositeB, "", "lblY");

		final Label lblTB = new Label(compositeB, SWT.WRAP);
		lblTB.setText(
				"This is the cloudY.svg. By requesting the cloud image with the IconSize of E64 the application automatically used the Y variant of the cloud.svg. The Y group is ment to be used for bigger Iconsizes with more details."); //$NON-NLS-1$
		lblTB.setLayoutData(new GridData(500, SWT.DEFAULT));

		final Composite compositeC = new Composite(pictureComposite, SWT.NONE);
		compositeC.setLayout(layout);
		final Label lblC = new Label(compositeC, SWT.NONE);
		final Image imageC = ImageStore.getInstance().getImage("cloud", IconSize.C32); //$NON-NLS-1$
		lblC.setImage(imageC);

		final Label lblTC = new Label(compositeC, SWT.WRAP);
		lblTC.setText(
				"This is the cloud.svg. Here we tried to request the cloud Image with IconSize of C32, but the IconSize C32 was not mapped to a specific group, so the application used the default image"); //$NON-NLS-1$
		lblTC.setLayoutData(new GridData(500, SWT.DEFAULT));

	}

}
