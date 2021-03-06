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
package gov.redhawk.ide.debug.internal;

import org.eclipse.core.externaltools.internal.launchConfigurations.ProgramLaunchDelegate;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

import gov.redhawk.ide.debug.SpdLauncherUtil;
import gov.redhawk.sca.launch.ScaLaunchConfigurationConstants;
import mil.jpeojtrs.sca.spd.SoftPkg;

/**
 * An Eclipse launch delegate which handles launching a SoftPkg (component, device, etc) installed in the SDRROOT
 * locally in the Sandbox. It does not handle launching workspace projects (which are handled on a per-language basis).
 * Shared-address-space components are handled by {@link LocalContainedComponentLaunchDelegate}.
 */
@SuppressWarnings("restriction")
public class LocalComponentLaunchDelegate extends ProgramLaunchDelegate {
	public static final String ID = "gov.redhawk.ide.debug.localComponentProgram";

	@Override
	public void launch(final ILaunchConfiguration configuration, final String mode, final ILaunch launch, final IProgressMonitor monitor) throws CoreException {
		// Validate all XML before doing anything else
		final SoftPkg spd = SpdLauncherUtil.getSpd(configuration);
		IStatus status = SpdLauncherUtil.validateAllXML(spd);
		if (!status.isOK()) {
			throw new CoreException(status);
		}

		final ILaunchConfigurationWorkingCopy workingCopy = configuration.getWorkingCopy();
		ComponentProgramLaunchUtils.insertProgramArguments(spd, launch, workingCopy);

		try {
			launchComponent(spd, workingCopy, mode, launch, monitor);
		} finally {
			if (monitor != null) {
				monitor.done();
			}
		}
	}

	protected void launchComponent(SoftPkg spd, ILaunchConfigurationWorkingCopy workingCopy, String mode, ILaunch launch, IProgressMonitor monitor)
		throws CoreException {
		final int WORK_LAUNCH = 10;
		final int WORK_POST_LAUNCH = 100;
		SubMonitor subMonitor = SubMonitor.convert(monitor, WORK_LAUNCH + WORK_POST_LAUNCH);
		super.launch(workingCopy, mode, launch, subMonitor.split(WORK_LAUNCH));
		SpdLauncherUtil.postLaunch(spd, workingCopy, mode, launch, subMonitor.split(WORK_POST_LAUNCH));
	}

	@Override
	protected boolean saveBeforeLaunch(ILaunchConfiguration configuration, String mode, IProgressMonitor monitor) throws CoreException {
		return true;
	}

	/**
	 * Overridden to handle labels of process consoles the way we want
	 */
	@Override
	public ILaunch getLaunch(ILaunchConfiguration configuration, String mode) throws CoreException {
		// ComponentHost objects need to have a unique ILaunch
		String launchProfile = configuration.getAttribute(ScaLaunchConfigurationConstants.ATT_PROFILE, (String) null);
		if (SoftPkg.Util.getComponentHostURI().toFileString().equals(launchProfile)) {
			return new ComponentHostLaunch(configuration, mode, null);
		}

		return new ComponentLaunch(configuration, mode, null);
	}

}
