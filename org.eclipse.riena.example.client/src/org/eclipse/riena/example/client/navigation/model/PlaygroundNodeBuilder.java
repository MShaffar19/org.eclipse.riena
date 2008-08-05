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
package org.eclipse.riena.example.client.navigation.model;

import org.eclipse.riena.navigation.IModuleGroupNode;
import org.eclipse.riena.navigation.IModuleNode;
import org.eclipse.riena.navigation.INavigationNode;
import org.eclipse.riena.navigation.INavigationNodeBuilder;
import org.eclipse.riena.navigation.INavigationNodeId;
import org.eclipse.riena.navigation.model.ModuleGroupNode;
import org.eclipse.riena.navigation.model.ModuleNode;
import org.eclipse.riena.navigation.model.NavigationNodeId;
import org.eclipse.riena.navigation.model.SubModuleNode;

/**
 *
 */
public class PlaygroundNodeBuilder implements INavigationNodeBuilder {

	/**
	 * @see org.eclipse.riena.navigation.INavigationNodeBuilder#buildNode(org.eclipse.riena.navigation.INavigationNodeId)
	 */
	public INavigationNode<?> buildNode(INavigationNodeId INavigationNodeId) {

		IModuleGroupNode moduleGroup = new ModuleGroupNode("Playground"); //$NON-NLS-1$

		IModuleNode playgroundModule = new ModuleNode("Playground"); //$NON-NLS-1$
		moduleGroup.addChild(playgroundModule);

		SubModuleNode buttonsSubModule = new SubModuleNode("Buttons"); //$NON-NLS-1$
		buttonsSubModule.setPresentationId(new NavigationNodeId("org.eclipse.riena.example.buttons"));
		playgroundModule.addChild(buttonsSubModule);

		SubModuleNode comboSubModule = new SubModuleNode("Combo"); //$NON-NLS-1$
		comboSubModule.setPresentationId(new NavigationNodeId("org.eclipse.riena.example.combo"));
		playgroundModule.addChild(comboSubModule);

		SubModuleNode listSubModule = new SubModuleNode("List"); //$NON-NLS-1$
		listSubModule.setPresentationId(new NavigationNodeId("org.eclipse.riena.example.list"));
		playgroundModule.addChild(listSubModule);

		SubModuleNode textSubModule = new SubModuleNode("Text"); //$NON-NLS-1$
		textSubModule.setPresentationId(new NavigationNodeId("org.eclipse.riena.example.text"));
		playgroundModule.addChild(textSubModule);

		SubModuleNode markerSubModule = new SubModuleNode("Marker"); //$NON-NLS-1$
		markerSubModule.setPresentationId(new NavigationNodeId("org.eclipse.riena.example.marker"));
		playgroundModule.addChild(markerSubModule);

		SubModuleNode focusableSubModule = new SubModuleNode("Focusable"); //$NON-NLS-1$
		focusableSubModule.setPresentationId(new NavigationNodeId("org.eclipse.riena.example.focusable"));
		playgroundModule.addChild(focusableSubModule);

		SubModuleNode validationSubModule = new SubModuleNode("Validation"); //$NON-NLS-1$
		validationSubModule.setPresentationId(new NavigationNodeId("org.eclipse.riena.example.validation"));
		playgroundModule.addChild(validationSubModule);

		SubModuleNode treeSubModule = new SubModuleNode("Tree"); //$NON-NLS-1$
		treeSubModule.setPresentationId(new NavigationNodeId("org.eclipse.riena.example.tree"));
		playgroundModule.addChild(treeSubModule);

		SubModuleNode treeTableSubModule = new SubModuleNode("Tree Table"); //$NON-NLS-1$
		treeTableSubModule.setPresentationId(new NavigationNodeId("org.eclipse.riena.example.treeTable"));
		playgroundModule.addChild(treeTableSubModule);

		SubModuleNode tableSubModule = new SubModuleNode("Table");
		tableSubModule.setPresentationId(new NavigationNodeId("org.eclipse.riena.example.table"));
		playgroundModule.addChild(tableSubModule);

		SubModuleNode systemPropertiesSubModule = new SubModuleNode("System Properties"); //$NON-NLS-1$
		systemPropertiesSubModule.setPresentationId(new NavigationNodeId("org.eclipse.riena.example.systemProperties"));
		playgroundModule.addChild(systemPropertiesSubModule);

		SubModuleNode statusLineSubModule = new SubModuleNode("Statusline"); //$NON-NLS-1$
		statusLineSubModule.setPresentationId(new NavigationNodeId("org.eclipse.riena.example.statusLine"));
		playgroundModule.addChild(statusLineSubModule);

		return moduleGroup;
	}

}
