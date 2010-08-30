/*******************************************************************************
 * Copyright (c) 2006, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Rob Harrop - SpringSource Inc. (bug 247522)
 *******************************************************************************/
package org.eclipse.osgi.internal.resolver;

import java.util.*;
import org.eclipse.osgi.service.resolver.BundleDescription;
import org.eclipse.osgi.service.resolver.GenericDescription;
import org.osgi.framework.Constants;
import org.osgi.framework.Version;

public class GenericDescriptionImpl extends BaseDescriptionImpl implements GenericDescription {
	private Dictionary attributes;
	private volatile BundleDescription supplier;
	private volatile String type = GenericDescription.DEFAULT_TYPE;

	public Dictionary getAttributes() {
		synchronized (this.monitor) {
			return attributes;
		}
	}

	public BundleDescription getSupplier() {
		return supplier;
	}

	void setAttributes(Dictionary attributes) {
		synchronized (this.monitor) {
			this.attributes = attributes;
		}
	}

	void setSupplier(BundleDescription supplier) {
		this.supplier = supplier;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(StateBuilder.OSGI_PROVIDE_CAPABILITY).append(": ").append(getType()); //$NON-NLS-1$
		Map attrs = getDeclaredAttributes();
		sb.append(toString(attrs, false));
		return sb.toString();
	}

	/**
	 * @deprecated
	 */
	public String getName() {
		synchronized (this.monitor) {
			Object name = attributes != null ? attributes.get(getType()) : null;
			return name instanceof String ? (String) name : null;
		}
	}

	public String getType() {
		return type;
	}

	void setType(String type) {
		if (type == null || type.equals(GenericDescription.DEFAULT_TYPE))
			this.type = GenericDescription.DEFAULT_TYPE;
		else
			this.type = type;
	}

	/**
	 * @deprecated
	 */
	public Version getVersion() {
		Object version = attributes != null ? attributes.get(Constants.VERSION_ATTRIBUTE) : null;
		return version instanceof Version ? (Version) version : super.getVersion();
	}

	public Map<String, String> getDeclaredDirectives() {
		// TODO need to allow directives
		return Collections.EMPTY_MAP;
	}

	public Map<String, Object> getDeclaredAttributes() {
		synchronized (this.monitor) {
			Map<String, Object> result = new HashMap(5);
			if (attributes != null)
				for (Enumeration keys = attributes.keys(); keys.hasMoreElements();) {
					String key = (String) keys.nextElement();
					Object value = attributes.get(key);
					if (value instanceof List)
						value = Collections.unmodifiableList((List) value);
					result.put(key, value);
				}
			return Collections.unmodifiableMap(result);
		}
	}
}