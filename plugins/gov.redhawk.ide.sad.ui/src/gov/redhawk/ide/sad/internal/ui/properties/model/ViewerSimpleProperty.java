/*******************************************************************************
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package gov.redhawk.ide.sad.internal.ui.properties.model;

import java.util.Collection;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;

import gov.redhawk.sca.util.PluginUtil;
import mil.jpeojtrs.sca.prf.PrfFactory;
import mil.jpeojtrs.sca.prf.PrfPackage;
import mil.jpeojtrs.sca.prf.Simple;
import mil.jpeojtrs.sca.prf.SimpleRef;

public class ViewerSimpleProperty extends ViewerProperty<Simple> {

	private String value;

	public ViewerSimpleProperty(Simple def, Object parent) {
		super(def, parent);
	}

	@Override
	public String getValue() {
		SimpleRef simpleRef = getRef();
		if (simpleRef != null) {
			return simpleRef.getValue();
		}
		return null;
	}

	@Override
	protected SimpleRef getRef() {
		return (SimpleRef) super.getRef();
	}

	@Override
	public String getPrfValue() {
		return getDefinition().getValue();
	}

	@Override
	protected Collection< ? > getKindTypes() {
		return getDefinition().getKind();
	}

	@Override
	public void setToDefault() {
		setValue((String) null);
	}

	public void setValue(SimpleRef value) {
		if (value != null) {
			setValue(value.getValue());
		} else {
			setToDefault();
		}
	}

	public void setValue(String newValue) {
		if (newValue != null) {
			newValue = newValue.trim();
			if (newValue.isEmpty()) {
				newValue = null;
			}
		}
		String oldValue = this.value;
		this.value = newValue;
		if (!PluginUtil.equals(oldValue, value)) {
			if (getParent() instanceof ViewerComponent) {
				((ViewerComponent) getParent()).setRef(this, newValue);
			}
			firePropertyChangeEvent();
		}
	}

	protected SimpleRef createRef() {
		SimpleRef ref = PrfFactory.eINSTANCE.createSimpleRef();
		ref.setRefID(getID());
		return ref;
	}

	@Override
	public Command createSetCommand(EditingDomain editingDomain, Object owner, Object value) {
		SimpleRef ref = getRef();
		if (ref == null) {
			ref = createRef();
			ref.setValue((String) value);
			return ((ViewerItemProvider)getParent()).createAddCommand(editingDomain, null, ref);
		}
		return SetCommand.create(editingDomain, ref, PrfPackage.Literals.SIMPLE_REF__VALUE, value);
	}

	public boolean checkValue(String text) {
		if (text == null || text.isEmpty() || def.getType().isValueOfType(text, def.isComplex())) {
			return true;
		}
		return false;
	}

}
