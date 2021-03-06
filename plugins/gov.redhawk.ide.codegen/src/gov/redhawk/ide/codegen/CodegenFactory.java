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
 // BEGIN GENERATED CODE
package gov.redhawk.ide.codegen;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc --> The <b>Factory</b> for the model. It provides a
 * create method for each non-abstract class of the model. <!-- end-user-doc -->
 * @see gov.redhawk.ide.codegen.CodegenPackage
 * @generated
 */
public interface CodegenFactory extends EFactory {

	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	CodegenFactory eINSTANCE = gov.redhawk.ide.codegen.impl.CodegenFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Implementation Settings</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Implementation Settings</em>'.
	 * @generated
	 */
	ImplementationSettings createImplementationSettings();

	/**
	 * Returns a new object of class '<em>Property</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Property</em>'.
	 * @generated
	 */
	Property createProperty();

	/**
	 * Returns a new object of class '<em>Wave Dev Settings</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Wave Dev Settings</em>'.
	 * @generated
	 */
	WaveDevSettings createWaveDevSettings();

	/**
	 * Returns a new object of class '<em>File To CRC Map</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>File To CRC Map</em>'.
	 * @generated
	 */
	FileToCRCMap createFileToCRCMap();

	/**
	 * Returns a new object of class '<em>Port Rep To Generator Map</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Port Rep To Generator Map</em>'.
	 * @generated
	 */
	PortRepToGeneratorMap createPortRepToGeneratorMap();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	CodegenPackage getCodegenPackage();

} // CodegenFactory
