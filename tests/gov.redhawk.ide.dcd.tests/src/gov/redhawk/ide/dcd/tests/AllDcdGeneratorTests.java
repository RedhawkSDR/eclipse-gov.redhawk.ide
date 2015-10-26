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
package gov.redhawk.ide.dcd.tests;

import gov.redhawk.ide.dcd.generator.newdevice.tests.AllDeviceGeneratorTests;
import gov.redhawk.ide.dcd.generator.newnode.tests.AllNodeGeneratorTests;
import gov.redhawk.ide.dcd.generator.newservice.tests.AllServiceGeneratorTests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * 
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ AllDeviceGeneratorTests.class, AllNodeGeneratorTests.class, AllServiceGeneratorTests.class })
public class AllDcdGeneratorTests {

}