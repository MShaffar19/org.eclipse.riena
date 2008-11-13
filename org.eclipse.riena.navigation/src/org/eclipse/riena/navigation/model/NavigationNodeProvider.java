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
package org.eclipse.riena.navigation.model;

import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.equinox.log.Logger;
import org.eclipse.riena.core.injector.Inject;
import org.eclipse.riena.internal.navigation.Activator;
import org.eclipse.riena.navigation.IGenericNavigationNodeBuilder;
import org.eclipse.riena.navigation.INavigationNode;
import org.eclipse.riena.navigation.INavigationNodeBuilder;
import org.eclipse.riena.navigation.INavigationNodeExtension;
import org.eclipse.riena.navigation.INavigationNodeProvider;
import org.eclipse.riena.navigation.ISubModuleExtension;
import org.eclipse.riena.navigation.ISubModuleNodeExtension;
import org.eclipse.riena.navigation.NavigationArgument;
import org.eclipse.riena.navigation.NavigationNodeId;
import org.osgi.service.log.LogService;

/**
 * This class provides service methods to get information provided by
 * WorkAreaPresentationDefinitions and NavigationNodePresentationDefitinios
 * identified by a given presentationID.
 */
public class NavigationNodeProvider implements INavigationNodeProvider {

	private final static Logger LOGGER = Activator.getDefault().getLogger(NavigationNodeProvider.class);

	private static final String EP_NAVNODETYPE = "org.eclipse.riena.navigation.navigationNode"; //$NON-NLS-1$

	private NavigationNodeExtensionInjectionHelper targetNN;

	private Map<String, ISubModuleNodeExtension> id2SubModuleCache = new WeakHashMap<String, ISubModuleNodeExtension>();

	/**
	 * 
	 */
	public NavigationNodeProvider() {

		targetNN = new NavigationNodeExtensionInjectionHelper();
		Inject.extension(EP_NAVNODETYPE).useType(getNavigationNodeTypeDefinitonIFSafe()).into(targetNN).andStart(
				Activator.getDefault().getContext());

	}

	private Class<? extends INavigationNodeExtension> getNavigationNodeTypeDefinitonIFSafe() {

		if (getNavigationNodeTypeDefinitonIF() != null && getNavigationNodeTypeDefinitonIF().isInterface()) {
			return getNavigationNodeTypeDefinitonIF();
		} else {
			return INavigationNodeExtension.class;
		}
	}

	public Class<? extends INavigationNodeExtension> getNavigationNodeTypeDefinitonIF() {

		return INavigationNodeExtension.class;
	}

	public Class<? extends ISubModuleExtension> getSubModuleTypeDefinitionIF() {

		return ISubModuleExtension.class;
	}

	/**
	 * @see org.eclipse.riena.navigation.INavigationNodeProvider#createNode(org.eclipse.riena.navigation.INavigationNode,
	 *      org.eclipse.riena.navigation.NavigationNodeId,
	 *      org.eclipse.riena.navigation.NavigationArgument)
	 */
	@SuppressWarnings("unchecked")
	protected INavigationNode<?> _provideNode(INavigationNode<?> sourceNode, NavigationNodeId targetId,
			NavigationArgument argument) {
		INavigationNode<?> targetNode = findNode(getRootNode(sourceNode), targetId);
		if (targetNode == null) {
			if (LOGGER.isLoggable(LogService.LOG_DEBUG)) {
				LOGGER.log(LogService.LOG_DEBUG, "createNode: " + targetId); //$NON-NLS-1$
			}
			INavigationNodeExtension navigationNodeTypeDefiniton = getNavigationNodeTypeDefinition(targetId);
			if (navigationNodeTypeDefiniton != null) {
				INavigationNodeBuilder builder = navigationNodeTypeDefiniton.createNodeBuilder();
				if (builder == null) {
					builder = createDefaultBuilder();
				}
				prepareNavigationNodeBuilder(navigationNodeTypeDefiniton, targetId, builder);
				targetNode = builder.buildNode(targetId, argument);
				INavigationNode parentNode = null;
				if (argument != null && argument.getParentNodeId() != null) {
					parentNode = _provideNode(sourceNode, argument.getParentNodeId(), null);
				} else {
					parentNode = _provideNode(sourceNode, new NavigationNodeId(navigationNodeTypeDefiniton
							.getParentTypeId()), null);
				}
				parentNode.addChild(targetNode);
			} else {
				throw new ExtensionPointFailure("NavigationNodeType not found. ID=" + targetId.getTypeId()); //$NON-NLS-1$
			}
		}
		return targetNode;
	}

	protected GenericNodeBuilder createDefaultBuilder() {
		return new GenericNodeBuilder();
	}

	/**
	 * @see org.eclipse.riena.navigation.INavigationNodeProvider#createNode(org.eclipse.riena.navigation.INavigationNode,
	 *      org.eclipse.riena.navigation.NavigationNodeId,
	 *      org.eclipse.riena.navigation.NavigationArgument)
	 */
	public INavigationNode<?> provideNode(INavigationNode<?> sourceNode, NavigationNodeId targetId,
			NavigationArgument argument) {
		final INavigationNode<?> targetNode = _provideNode(sourceNode, targetId, argument);
		return targetNode;
	}

	/**
	 * Used to prepare the NavigationNodeBuilder in a application specific way.
	 * 
	 * @param targetId
	 * @param builder
	 */
	protected void prepareNavigationNodeBuilder(INavigationNodeExtension navigationNodeTypeDefiniton,
			NavigationNodeId targetId, INavigationNodeBuilder builder) {

		if (builder instanceof IGenericNavigationNodeBuilder) {
			// the extension interface of the navigation node definition is injected into the builder   
			((IGenericNavigationNodeBuilder) builder).setNodeDefinition(navigationNodeTypeDefiniton);
		}
	}

	/**
	 * @param targetId
	 * @return
	 */
	public INavigationNodeExtension getNavigationNodeTypeDefinition(NavigationNodeId targetId) {
		if (targetNN == null || targetNN.getData().length == 0 || targetId == null) {
			return null;
		} else {
			INavigationNodeExtension[] data = targetNN.getData();
			for (int i = 0; i < data.length; i++) {
				if (data[i].getTypeId() != null && data[i].getTypeId().equals(targetId.getTypeId())) {
					return data[i];
				}

			}
		}
		return null;
	}

	protected ISubModuleNodeExtension getSubModuleNodeDefinition(NavigationNodeId targetId) {
		return id2SubModuleCache.get(targetId.getTypeId());
	}

	/**
	 * @param node
	 * @return
	 */
	protected INavigationNode<?> getRootNode(INavigationNode<?> node) {
		if (node.getParent() == null) {
			return node;
		}
		return getRootNode(node.getParent());
	}

	/**
	 * @param node
	 * @param targetId
	 * @return
	 */
	protected INavigationNode<?> findNode(INavigationNode<?> node, NavigationNodeId targetId) {
		if (targetId == null) {
			return null;
		}
		if (targetId.equals(node.getNodeId())) {
			return node;
		}
		for (INavigationNode<?> child : node.getChildren()) {
			INavigationNode<?> foundNode = findNode(child, targetId);
			if (foundNode != null) {
				return foundNode;
			}
		}
		return null;
	}

	public void register(String subModuleNodeid, ISubModuleNodeExtension definition) {
		id2SubModuleCache.put(subModuleNodeid, definition);
	}

	/**
	 * @see org.eclipse.riena.navigation.INavigationNodeProvider#cleanUp()
	 */
	public void cleanUp() {
		// TODO: implement, does nothing special yet
	}

	public class NavigationNodeExtensionInjectionHelper {
		private INavigationNodeExtension[] data;

		public void update(INavigationNodeExtension[] data) {
			this.data = data.clone();

		}

		public INavigationNodeExtension[] getData() {
			return data.clone();
		}
	}
}
