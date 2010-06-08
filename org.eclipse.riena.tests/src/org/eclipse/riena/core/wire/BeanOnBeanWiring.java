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
package org.eclipse.riena.core.wire;

import org.osgi.framework.BundleContext;

import org.eclipse.riena.core.injector.Inject;
import org.eclipse.riena.core.injector.service.ServiceInjector;

/**
 *
 */
public class BeanOnBeanWiring extends AbstractWiring {
	private ServiceInjector injector;

	@Override
	public void wire(Object bean, BundleContext context) {
		injector = Inject.service(Stunk.class).into(bean).andStart(context);
		SequenceUtil.add(BeanOnBeanWiring.class);
	}

	@Override
	public void unwire(Object bean, BundleContext context) {
		injector.stop();
		SequenceUtil.add(BeanOnBeanWiring.class);
	}

}
