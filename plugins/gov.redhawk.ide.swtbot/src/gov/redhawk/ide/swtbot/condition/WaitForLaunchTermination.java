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
package gov.redhawk.ide.swtbot.condition;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.ICondition;
import org.junit.Assert;

/**
 * Waits for any/all launches to terminated
 */
public class WaitForLaunchTermination implements ICondition {

	private final ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
	private boolean forceTerminate;

	public WaitForLaunchTermination() {
		this(false);
	}

	/**
	 * @param forceTerminate True to first tell all launches to terminate
	 */
	public WaitForLaunchTermination(boolean forceTerminate) {
		this.forceTerminate = forceTerminate;
	}

	@Override
	public boolean test() throws Exception {
		if (this.forceTerminate) {
			this.forceTerminate = false;
			for (ILaunch launch : launchManager.getLaunches()) {
				if (launch.canTerminate()) {
					try {
						launch.terminate();
					} catch (DebugException e) {
						Assert.fail(e.toString());
					}
				}
			}
		}

		for (ILaunch launch : launchManager.getLaunches()) {
			if (!launch.isTerminated()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void init(SWTBot bot) {
	}

	@Override
	public String getFailureMessage() {
		return "One or more ILaunches are still running";
	}

}
