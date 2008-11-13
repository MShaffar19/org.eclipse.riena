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
package org.eclipse.riena.internal.communication.core.registry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.equinox.log.Logger;
import org.eclipse.riena.communication.core.IRemoteServiceReference;
import org.eclipse.riena.communication.core.IRemoteServiceRegistration;
import org.eclipse.riena.communication.core.IRemoteServiceRegistry;
import org.eclipse.riena.internal.communication.core.Activator;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.log.LogService;

/**
 * @author Alexander Ziegler
 * @author Christian Campo
 * 
 */
public class RemoteServiceRegistry implements IRemoteServiceRegistry {
	private Map<String, IRemoteServiceRegistration> registeredServices;
	private final static Logger LOGGER = Activator.getDefault().getLogger(RemoteServiceRegistry.class);

	public synchronized void start() {
		registeredServices = new HashMap<String, IRemoteServiceRegistration>();
	}

	public synchronized void stop() {
		// @TODO unregisterService changes the registeredServices collection,
		// the for loop collapses with ConcurrentModificationException
		IRemoteServiceRegistration[] arrayRS = registeredServices.values().toArray(
				new IRemoteServiceRegistration[registeredServices.values().size()]);
		for (IRemoteServiceRegistration serviceReg : arrayRS) {
			// unregisters all services for this registry
			unregisterService(serviceReg.getReference());
		}
		registeredServices.clear();
		registeredServices = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.riena.communication.core.IRemoteServiceRegistry#registerService
	 * (org.eclipse.riena.communication.core.IRemoteServiceReference)
	 */
	public IRemoteServiceRegistration registerService(IRemoteServiceReference reference) {

		String url = reference.getDescription().getURL();
		synchronized (registeredServices) {
			IRemoteServiceRegistration foundRemoteServiceReg = registeredServices.get(url);
			if (foundRemoteServiceReg == null) {
				// it is a new entry, set properties
				Properties props = new Properties();
				props.put("service.url", url); //$NON-NLS-1$
				props.put("service.protocol", reference.getDescription().getProtocol()); //$NON-NLS-1$
				ServiceRegistration serviceRegistration = Activator.getDefault().getContext().registerService(
						reference.getServiceInterfaceClassName(), reference.getServiceInstance(), props);
				reference.setServiceRegistration(serviceRegistration);

				RemoteServiceRegistration remoteServiceReg = new RemoteServiceRegistration(reference, this);
				registeredServices.put(url, remoteServiceReg);

				LOGGER.log(LogService.LOG_DEBUG, "OSGi NEW service registered id: " //$NON-NLS-1$
						+ reference.getServiceInterfaceClassName());
				return remoteServiceReg;
			} else {
				// for existing services copy over the service registration
				// @TODO review this logic
				reference.setServiceRegistration(foundRemoteServiceReg.getReference().getServiceRegistration());
				foundRemoteServiceReg.setReference(reference);
				return foundRemoteServiceReg;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.riena.communication.core.IRemoteServiceRegistry#unregisterService
	 * (org.eclipse.riena.communication.core.IRemoteServiceReference)
	 */
	public void unregisterService(IRemoteServiceReference reference) {
		assert reference != null : "RemoteServiceReference must not be null"; //$NON-NLS-1$
		synchronized (registeredServices) {
			ServiceRegistration serviceRegistration = reference.getServiceRegistration();
			serviceRegistration.unregister();
			String id = reference.getServiceInterfaceClassName();
			registeredServices.remove(reference.getURL());
			reference.dispose();
			LOGGER.log(LogService.LOG_DEBUG, "OSGi service removed id: " + id); //$NON-NLS-1$
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.riena.communication.core.IRemoteServiceRegistry#
	 * registeredServices(java.lang.String)
	 */
	public List<IRemoteServiceRegistration> registeredServices(String hostId) {
		synchronized (registeredServices) {
			Collection<IRemoteServiceRegistration> values = registeredServices.values();
			// Answers all service registrations when hostId is null or "*"
			if (hostId == null || hostId.equals("*")) { //$NON-NLS-1$
				return new ArrayList<IRemoteServiceRegistration>(values);
			}

			List<IRemoteServiceRegistration> result = new ArrayList<IRemoteServiceRegistration>();
			for (IRemoteServiceRegistration serviceReg : values) {
				String registeredHostId = serviceReg.getReference().getHostId();
				if (hostId.equals(registeredHostId)) {
					result.add(serviceReg);
				}
			}
			return result;
		}
	}
}
