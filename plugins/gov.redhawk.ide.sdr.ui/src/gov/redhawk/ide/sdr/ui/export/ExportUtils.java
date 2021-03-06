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
package gov.redhawk.ide.sdr.ui.export;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.core.externaltools.internal.IExternalToolConstants;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.ICoreRunnable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.emf.common.ui.dialogs.DiagnosticDialog;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreValidator;
import org.eclipse.emf.transaction.RunnableWithResult;
import org.eclipse.emf.transaction.RunnableWithResult.Impl;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.osgi.service.prefs.BackingStoreException;

import gov.redhawk.ide.RedhawkIdeActivator;
import gov.redhawk.ide.codegen.CodegenUtil;
import gov.redhawk.ide.codegen.ImplementationSettings;
import gov.redhawk.ide.natures.ScaComponentProjectNature;
import gov.redhawk.ide.natures.ScaNodeProjectNature;
import gov.redhawk.ide.natures.ScaWaveformProjectNature;
import gov.redhawk.ide.sdr.preferences.IdeSdrPreferences;
import gov.redhawk.ide.sdr.ui.SdrUiPlugin;
import gov.redhawk.model.sca.util.ModelUtil;
import mil.jpeojtrs.sca.dcd.DcdPackage;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;
import mil.jpeojtrs.sca.sad.SadPackage;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.scd.ComponentType;
import mil.jpeojtrs.sca.scd.SoftwareComponent;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SpdPackage;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

/**
 * @since 3.1
 */
@SuppressWarnings("restriction")
public class ExportUtils {

	private static final String BUILD_SCOPE_NONE = "${none}"; //$NON-NLS-1$

	private ExportUtils() {
	}

	/**
	 * Exports waveforms from the specified project using the provided exporter.
	 * @param proj The project to search for waveforms
	 * @param exporter The IScaExporter to use
	 * @param monitor The progress monitor to use for reporting progress to the user. It is the caller's responsibility
	 * to call done() on the given monitor. Accepts null, indicating that no progress should be reported and
	 * that the operation cannot be canceled.
	 * @since 2.0
	 */
	public static boolean exportWaveform(final IProject proj, final IScaExporter exporter, final IProgressMonitor monitor) throws CoreException, IOException {
		if (!proj.hasNature(ScaWaveformProjectNature.ID)) {
			throw new CoreException(new Status(IStatus.ERROR, RedhawkIdeActivator.PLUGIN_ID, "Project missing required nature"));
		}

		if (!checkProject(proj)) {
			return false;
		}

		// Find .sad.xml files
		final List<IResource> sadFiles = Arrays.stream(proj.members()) //
				.filter(resource -> resource.getName().endsWith(SadPackage.FILE_EXTENSION)) //
				.collect(Collectors.toList());

		final SubMonitor progress = SubMonitor.convert(monitor, "Exporting waveforms", sadFiles.size() * 3 + 2);
		Map<IResource, IPath> sourceFileToTargetFolder = new HashMap<>();
		Map<IResource, IPath> deletedFileToTargetFolder = new HashMap<>();
		for (final IResource sadFile : sadFiles) {
			// Load the SAD file
			final ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();
			final Resource resource = resourceSet.getResource(URI.createURI(sadFile.getLocationURI().toString()), true);
			final SoftwareAssembly sad = SoftwareAssembly.Util.getSoftwareAssembly(resource);

			// Validate name
			String name = sad.getName();
			if (name == null || name.isEmpty()) {
				throw new CoreException(
					new Status(IStatus.ERROR, SdrUiPlugin.PLUGIN_ID, "Cannot export a project with an empty name. Check the project's SAD file."));
			}

			// Validate against the ecore model
			if (!validateEcore(sad, sad.getName())) {
				return false;
			}

			// Determine install location
			final IPath outputFolder = new Path("dom/waveforms").append(name.replace('.', File.separatorChar));
			sourceFileToTargetFolder.put(sadFile, outputFolder);

			String basename = sadFile.getName().substring(0, sadFile.getName().length() - SadPackage.FILE_EXTENSION.length());

			// If there's a matching diagram file, include it
			for (String siblingFileName : Arrays.asList(basename + SadPackage.DIAGRAM_FILE_EXTENSION)) {
				IFile siblingFile = sadFile.getParent().getFile(new Path(siblingFileName));
				if (siblingFile.exists()) {
					sourceFileToTargetFolder.put(siblingFile, outputFolder);
				} else {
					deletedFileToTargetFolder.put(siblingFile, outputFolder);
				}
			}

			progress.worked(1);
		}

		// Install each file, delete files
		SubMonitor subProgress = progress.newChild(1).setWorkRemaining(sourceFileToTargetFolder.size() * 2);
		for (IResource sourceFile : sourceFileToTargetFolder.keySet()) {
			IPath outputFolder = sourceFileToTargetFolder.get(sourceFile);
			exporter.mkdir(outputFolder, subProgress.newChild(1));
			exporter.write(sourceFile, outputFolder.append(sourceFile.getName()), subProgress.newChild(1));
		}
		subProgress = progress.newChild(1).setWorkRemaining(deletedFileToTargetFolder.size());
		for (IResource deletedFile : deletedFileToTargetFolder.keySet()) {
			IPath outputFolder = deletedFileToTargetFolder.get(deletedFile);
			exporter.delete(outputFolder.append(deletedFile.getName()), subProgress.newChild(1));
		}

		return true;
	}

	private static boolean validateEcore(EObject object, String projectName) {
		// Get all resource contents, we are validating everything
		TreeIterator<EObject> allContents = object.eResource().getAllContents();

		boolean modelIsValid = true;
		BasicDiagnostic diagnostic = new BasicDiagnostic();
		while (allContents.hasNext()) {
			boolean validatorResult = EcoreValidator.INSTANCE.validate(allContents.next(), diagnostic, null);

			// Update flag only once if error found. But we need to cycle through everything so we
			// can display multiple errors at once via BasicDiagnostic.getChildren()
			modelIsValid = modelIsValid && validatorResult;
		}

		if (!modelIsValid) {
			boolean exportProject = showWarningDialog(projectName, diagnostic);
			if (!exportProject) {
				// User chose not to continue export
				return false;
			}
		}
		return true;
	}

	/**
	 * @return false if the user wants to cancel the export operation
	 */
	private static boolean showWarningDialog(final String projectName, final BasicDiagnostic diagnostic) {
		// Dialog has to be in the UI thread. Update the static variable since Runnable is a void return.
		Impl<Boolean> runnable = new RunnableWithResult.Impl<Boolean>() {
			public void run() {
				setResult(true);
				Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				Dialog warnDialog = new DiagnosticDialog(shell, "Invalid Model", null, diagnostic, Diagnostic.ERROR | Diagnostic.WARNING) {

					@Override
					protected Control createMessageArea(final Composite composite) {
						this.message = "Errors have been detected in " + projectName + ". Do you want to continue export to Target SDR?";
						return super.createMessageArea(composite);
					}

					@Override
					protected void createButtonsForButtonBar(final Composite parent) {
						// create Yes, No and Details buttons
						createButton(parent, IDialogConstants.OK_ID, IDialogConstants.YES_LABEL, true);
						createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.NO_LABEL, true);
						createDetailsButton(parent);
					}
				};

				if (warnDialog.open() == IDialogConstants.CANCEL_ID) {
					setResult(false);
				}
			}
		};
		Display.getDefault().syncExec(runnable);
		return runnable.getResult();
	}

	/**
	 * Exports components from the specified project using the provided exporter.
	 * 
	 * @param proj The project to search for components
	 * @param exporter The IScaExporter to use
	 * @param monitor The progress monitor to use for reporting progress to the user. It is the caller's responsibility
	 * to call done() on the given monitor. Accepts null, indicating that no progress should be reported and
	 * that the operation cannot be canceled.
	 * @since 2.0
	 */
	public static boolean exportComponent(final IProject proj, final IScaExporter exporter, final IProgressMonitor monitor) throws CoreException, IOException {
		if (!proj.hasNature(ScaComponentProjectNature.ID)) {
			throw new CoreException(new Status(IStatus.ERROR, RedhawkIdeActivator.PLUGIN_ID, "Project missing required nature"));
		}

		if (!checkProject(proj)) {
			return false;
		}

		if (useBuildSH(proj)) {
			return buildSH(monitor, proj);
		} else {
			return basicExportComponent(proj, exporter, true, monitor);
		}
	}

	/**
	 * Exports nodes from the specified project using the provided exporter.
	 * @param proj The project to search for nodes
	 * @param exporter The IScaExporter to use
	 * @param monitor The progress monitor to use for reporting progress to the user. It is the caller's responsibility
	 * to call done() on the given monitor. Accepts null, indicating that no progress should be reported and
	 * that the operation cannot be canceled.
	 * @since 2.0
	 */
	public static boolean exportNode(final IProject proj, final IScaExporter exporter, final IProgressMonitor monitor) throws CoreException, IOException {
		if (!proj.hasNature(ScaNodeProjectNature.ID)) {
			throw new CoreException(new Status(IStatus.ERROR, RedhawkIdeActivator.PLUGIN_ID, "Project missing required nature"));
		}

		if (!checkProject(proj)) {
			return false;
		}

		// Find node files
		final List<IResource> dcdFiles = Arrays.stream(proj.members()) //
				.filter(resource -> resource.getName().endsWith(DcdPackage.FILE_EXTENSION)) //
				.collect(Collectors.toList());

		final SubMonitor progress = SubMonitor.convert(monitor, "Exporting nodes", dcdFiles.size() * 3 + 2);

		// Validate each file and determine where we'll install it
		Map<IResource, IPath> sourceFileToTargetFolder = new HashMap<>();
		Map<IResource, IPath> deletedFileToTargetFolder = new HashMap<>();
		for (final IResource dcdFile : dcdFiles) {
			// Load the DCD file
			final ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();
			final Resource resource = resourceSet.getResource(URI.createURI(dcdFile.getLocationURI().toString()), true);
			final DeviceConfiguration dcd = DeviceConfiguration.Util.getDeviceConfiguration(resource);

			// Validate name
			String name = dcd.getName();
			if (name == null || name.isEmpty()) {
				throw new CoreException(
					new Status(IStatus.ERROR, SdrUiPlugin.PLUGIN_ID, "Cannot export a project with an empty name. Check the project's DCD file."));
			}

			// Validate against the ecore model
			if (!validateEcore(dcd, dcd.getName())) {
				return false;
			}

			// Determine install location
			final IPath outputFolder = new Path("dev/nodes").append(name.replace('.', File.separatorChar));
			sourceFileToTargetFolder.put(dcdFile, outputFolder);

			String basename = dcdFile.getName().substring(0, dcdFile.getName().length() - DcdPackage.FILE_EXTENSION.length());

			// If there's a matching diagram file, include it
			for (String siblingFileName : Arrays.asList(basename + DcdPackage.DIAGRAM_FILE_EXTENSION)) {
				IFile siblingFile = dcdFile.getParent().getFile(new Path(siblingFileName));
				if (siblingFile.exists()) {
					sourceFileToTargetFolder.put(siblingFile, outputFolder);
				}
			}

			progress.worked(1);
		}

		// Install each file, delete files
		SubMonitor subProgress = progress.newChild(1).setWorkRemaining(sourceFileToTargetFolder.size() * 2);
		for (IResource sourceFile : sourceFileToTargetFolder.keySet()) {
			IPath outputFolder = sourceFileToTargetFolder.get(sourceFile);
			exporter.mkdir(outputFolder, subProgress.newChild(1));
			exporter.write(sourceFile, outputFolder.append(sourceFile.getName()), subProgress.newChild(1));
		}
		subProgress = progress.newChild(1).setWorkRemaining(deletedFileToTargetFolder.size());
		for (IResource deletedFile : deletedFileToTargetFolder.keySet()) {
			IPath outputFolder = deletedFileToTargetFolder.get(deletedFile);
			exporter.delete(outputFolder.append(deletedFile.getName()), subProgress.newChild(1));
		}

		progress.done();
		return true;
	}

	/**
	 * @return System environment overridden with preference values
	 * @since 4.0
	 */
	public static Map<String, String> getEnv() {
		Map<String, String> env = new HashMap<String, String>(System.getenv());

		IPath targetSDRPath = IdeSdrPreferences.getTargetSdrPath();
		env.put("SDRROOT", targetSDRPath.toOSString());
		IPath ossiehome = RedhawkIdeActivator.getDefault().getRuntimePath();
		env.put("OSSIEHOME", ossiehome.toOSString());

		prependPathToEnv(env, "CLASSPATH", ossiehome.toOSString() + "/lib/*");
		prependPathToEnv(env, "PATH", ossiehome.toOSString() + "/bin/");
		if (Platform.ARCH_X86_64.equals(Platform.getOSArch())) {
			prependPathToEnv(env, "PYTHONPATH", ossiehome.toOSString() + "/lib64/python:" + ossiehome.toOSString() + "/lib/python");
			prependPathToEnv(env, "LD_LIBRARY_PATH", ossiehome.toOSString() + "/lib64:" + ossiehome.toOSString() + "/lib");
		} else {
			prependPathToEnv(env, "PYTHONPATH", ossiehome.toOSString() + "/lib/python");
			prependPathToEnv(env, "LD_LIBRARY_PATH", ossiehome.toOSString() + "/lib");
		}
		return env;
	}

	private static void prependPathToEnv(Map<String, String> env, String envKey, String prependPath) {
		String suffix = (env.containsKey(envKey)) ? ':' + env.get(envKey) : "";
		env.put(envKey, prependPath + suffix);
	}

	/**
	 * Shared by {@link #exportDevice(IProject, IScaExporter, IProgressMonitor)},
	 * {@link #exportComponent(IProject, IScaExporter, IProgressMonitor)}, and
	 * {@link #exportNode(IProject, IScaExporter, IProgressMonitor)} to perform common export functions.
	 * 
	 * @param proj The project to search for software packages
	 * @param exporter The IScaExporter to use
	 * @param includeCode If code should be copied with the component
	 * @param monitor The progress monitor to use for reporting progress to the user. It is the caller's responsibility
	 * to call done() on the given monitor. Accepts null, indicating that no progress should be reported and
	 * that the operation cannot be canceled.
	 */
	private static boolean basicExportComponent(final IProject proj, final IScaExporter exporter, final boolean includeCode, final IProgressMonitor monitor)
		throws CoreException {
		if (!proj.hasNature(ScaComponentProjectNature.ID)) {
			throw new CoreException(new Status(IStatus.ERROR, RedhawkIdeActivator.PLUGIN_ID, "Project missing required nature"));
		}

		// Find .spd.xml files
		final List<IResource> spds = new ArrayList<IResource>();
		for (final IResource child : proj.members()) {
			if (child.getName().endsWith(SpdPackage.FILE_EXTENSION)) {
				spds.add(child);
			}
		}

		final SubMonitor progress = SubMonitor.convert(monitor, "Exporting SPD resources...", spds.size() * 2);

		// Perform checks, figure out what we're installing where
		List<ICoreRunnable> runnables = new ArrayList<>();
		for (final IResource spdResource : spds) {
			final SubMonitor prepProgress = progress.split(1);

			// Now load the SPD file and copy the prf and scd files, if any
			final ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();
			final Resource resource = resourceSet.getResource(URI.createURI(spdResource.getLocationURI().toString()), true);
			final SoftPkg softPkg = SoftPkg.Util.getSoftPkg(resource);

			ComponentType type = ComponentType.OTHER;
			if ((softPkg.getDescriptor() != null) && (softPkg.getDescriptor().getComponent() != null)) {
				final SoftwareComponent component = softPkg.getDescriptor().getComponent();
				type = SoftwareComponent.Util.getWellKnownComponentType(component);
			}

			IPath prefix;
			switch (type) {
			case DEVICE:
				prefix = new Path("dev/devices");
				break;
			case DEVICE_MANAGER:
				prefix = new Path("dev/nodes");
				break;
			case DOMAIN_MANAGER:
				prefix = new Path("dom/mgr");
				break;
			case EVENT_SERVICE:
			case SERVICE:
				prefix = new Path("dev/services");
				break;
			case FILE_MANAGER:
			case FILE_SYSTEM:
			case LOG:
			case NAMING_SERVICE:
			case RESOURCE:
			case OTHER:
			case RESOURCE_FACTORY:
				prefix = new Path("dom/components");
				break;
			default:
				throw new CoreException(new Status(IStatus.ERROR, SdrUiPlugin.PLUGIN_ID, "Unknown component type: " + type));
			}
			IPath outputFolder = prefix.append(proj.getName().replace('.', File.separatorChar));

			// SPD
			final IPath spdRootPath = spdResource.getFullPath().removeLastSegments(1);
			if (includeCode) {
				// If we were told to include code make sure at least one implementation has generated code.
				if (!checkProjectImplsForExport(softPkg, spdRootPath)) {
					return false;
				}
			}
			final IPath spdOutputPath = outputFolder.append(spdResource.getName());

			// PRF
			IResource prfResource;
			IPath prfOutputPath;
			if (softPkg.getPropertyFile() != null) {
				final IPath prfPath = new Path(softPkg.getPropertyFile().getLocalFile().getName());
				if (prfPath.isAbsolute()) {
					throw new CoreException(new Status(IStatus.ERROR, RedhawkIdeActivator.PLUGIN_ID, "Cannot export absolute path localfile paths"));
				}
				prfResource = getWorkspaceResource(spdRootPath.append(prfPath));
				if (prfResource == null) {
					throw new CoreException(new Status(IStatus.ERROR, RedhawkIdeActivator.PLUGIN_ID, "Expected file " + prfPath + " does not exist"));
				}
				prfOutputPath = outputFolder.append(prfPath);
			} else {
				prfResource = null;
				prfOutputPath = null;
			}

			// SCD
			IResource scdResourcePath;
			IPath scdOutputPath;
			if (softPkg.getDescriptor() != null) {
				final IPath scdPath = new Path(softPkg.getDescriptor().getLocalfile().getName());
				if (scdPath.isAbsolute()) {
					throw new CoreException(new Status(IStatus.ERROR, RedhawkIdeActivator.PLUGIN_ID, "Cannot export absolute path localfile paths"));
				}
				scdResourcePath = getWorkspaceResource(spdRootPath.append(scdPath));
				if (scdResourcePath == null) {
					throw new CoreException(new Status(IStatus.ERROR, RedhawkIdeActivator.PLUGIN_ID, "Expected file " + scdPath + " does not exist"));
				}
				scdOutputPath = outputFolder.append(scdPath);
			} else {
				scdResourcePath = null;
				scdOutputPath = null;
			}

			// Prepare (but don't execute) the export
			runnables.add((monitor2) -> {
				try {
					SubMonitor installProgress = SubMonitor.convert(monitor2);
					final int WORK_PER_IMPL_INSTALL = 100;

					boolean useExporter = true;
					if (softPkg.getImplementation() != null) {
						installProgress.setWorkRemaining(softPkg.getImplementation().size() * WORK_PER_IMPL_INSTALL + 4);

						for (final Implementation impl : softPkg.getImplementation()) {
							useExporter &= exportImpl(exporter, includeCode, installProgress.split(WORK_PER_IMPL_INSTALL), outputFolder, spdRootPath, impl);
						}
					} else {
						installProgress.setWorkRemaining(4);
					}

					if (useExporter) {
						exporter.mkdir(outputFolder, installProgress.split(1));
						exporter.write(spdResource, spdOutputPath, installProgress.split(1));
						if (prfResource != null) {
							exporter.write(prfResource, prfOutputPath, installProgress.split(1));
						}
						if (scdResourcePath != null) {
							exporter.write(scdResourcePath, scdOutputPath, installProgress.split(1));
						}
					}
				} catch (IOException e) {
					throw new CoreException(new Status(IStatus.ERROR, SdrUiPlugin.PLUGIN_ID, e.getMessage(), e));
				}
			});
			prepProgress.worked(1);
		}

		// Perform each export
		for (ICoreRunnable runnable : runnables) {
			runnable.run(progress.split(1));
		}

		progress.done();
		return true;
	}

	private static boolean exportImpl(final IScaExporter exporter, final boolean includeCode, final SubMonitor progress, IPath outputFolder,
		final IPath spdRootPath, final Implementation impl) throws CoreException, IOException {
		progress.beginTask("Exporting Implementation " + impl.getId(), 1);

		ImplementationSettings implSettings = CodegenUtil.getImplementationSettings(impl);
		String outputDir = implSettings.getOutputDir();
		IResource outputDirResource = getWorkspaceResource(spdRootPath.append(outputDir));

		if (useInstallImplScript(outputDirResource)) {
			installImpl(progress, impl.getId(), (IContainer) outputDirResource);
			return false;
		}

		final String localFileName = impl.getCode().getLocalFile().getName();
		final IPath codeLocalFile;
		if (localFileName != null) {
			codeLocalFile = new Path(localFileName);
		} else {
			codeLocalFile = null;
		}
		if (codeLocalFile != null && codeLocalFile.isAbsolute()) {
			throw new CoreException(new Status(IStatus.ERROR, RedhawkIdeActivator.PLUGIN_ID, "Cannot export absolute 'localfile' paths"));
		}
		final IResource srcPath = getWorkspaceResource(spdRootPath.append(codeLocalFile));

		// Check if the path exists, there may be multiple implementations in this, only one needs to be built
		if (includeCode && (srcPath != null) && srcPath.exists()) {
			IPath outputPath = outputFolder.append(codeLocalFile);
			exporter.write(srcPath, outputPath, progress.newChild(1));
		}

		if (impl.getPropertyFile() != null) {
			final IPath prfPath = new Path(impl.getPropertyFile().getLocalFile().getName());
			if (prfPath.isAbsolute()) {
				throw new CoreException(new Status(IStatus.ERROR, RedhawkIdeActivator.PLUGIN_ID, "Cannot export absolute path property paths"));
			}
			final IResource srcPrfPath = getWorkspaceResource(spdRootPath.append(prfPath));
			if (srcPrfPath == null) {
				throw new CoreException(new Status(IStatus.ERROR, RedhawkIdeActivator.PLUGIN_ID, "Expected file " + prfPath + " does not exist"));
			}
			IPath outputPath = outputFolder.append(prfPath);
			exporter.write(srcPrfPath, outputPath, progress.newChild(1));
		}

		return true;
	}

	private static void installImpl(IProgressMonitor monitor, String name, IContainer implSourceContainer) throws CoreException, IOException {
		String configTypeId = IExternalToolConstants.ID_PROGRAM_LAUNCH_CONFIGURATION_TYPE;
		final ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		final String launchConfigName = launchManager.generateLaunchConfigurationName("Build install implementation " + name);
		final ILaunchConfigurationType configType = launchManager.getLaunchConfigurationType(configTypeId);
		final ILaunchConfigurationWorkingCopy retVal = configType.newInstance(null, launchConfigName);

		retVal.setAttribute(ILaunchManager.ATTR_APPEND_ENVIRONMENT_VARIABLES, true);
		retVal.setAttribute(ILaunchManager.ATTR_ENVIRONMENT_VARIABLES, getEnv());
		retVal.setAttribute(IExternalToolConstants.ATTR_BUILDER_ENABLED, false);
		retVal.setAttribute(IExternalToolConstants.ATTR_BUILD_SCOPE, BUILD_SCOPE_NONE);
		retVal.setAttribute(IExternalToolConstants.ATTR_BUILDER_SCOPE, BUILD_SCOPE_NONE);
		retVal.setAttribute(IExternalToolConstants.ATTR_SHOW_CONSOLE, true);

		URL fileUrl = FileLocator.toFileURL(FileLocator.find(SdrUiPlugin.getDefault().getBundle(), new Path("resources/installImpl.sh"), null));
		try {
			File file = new File(fileUrl.toURI());
			if (!file.canExecute()) {
				file.setExecutable(true);
			}
			retVal.setAttribute(IExternalToolConstants.ATTR_LOCATION, file.getAbsolutePath());
		} catch (URISyntaxException e1) {
			throw new CoreException(new Status(IStatus.ERROR, SdrUiPlugin.PLUGIN_ID, "Failed to find install script.", e1));
		}
		retVal.setAttribute(IExternalToolConstants.ATTR_WORKING_DIRECTORY, implSourceContainer.getLocation().toOSString());

		ILaunch launch = retVal.launch("run", monitor, false);
		while (!launch.isTerminated()) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				launch.terminate();
				return;
			}
			if (monitor.isCanceled()) {
				launch.terminate();
				return;
			}
		}
		if (launch.getProcesses()[0].getExitValue() != 0) {
			throw new CoreException(new Status(IStatus.ERROR, SdrUiPlugin.PLUGIN_ID,
				"Install script returned with error code " + launch.getProcesses()[0].getExitValue() + "\n\nSee console output for details.", null));
		}
	}

	private static boolean useInstallImplScript(IResource outputDir) {
		if (outputDir instanceof IContainer) {
			IContainer container = (IContainer) outputDir;
			return container.getFile(new Path("reconf")).exists() || container.getFile(new Path("configure")).exists()
				|| container.getFile(new Path("Makefile")).exists();
		}
		return false;
	}

	private static boolean useBuildSH(IProject project) {
		if (project.getFile(new Path("build.sh")).exists()) {
			IScopeContext projectScope = new ProjectScope(project);
			IEclipsePreferences node = projectScope.getNode(SdrUiPlugin.PLUGIN_ID);
			return node.getBoolean("useBuild.sh", false);
		}
		return false;
	}

	/**
	 * Sets a preference on the project to enable the use of build.sh for installing to the SDRROOT.
	 * @since 4.0
	 */
	public static void setUseBuildSH(@NonNull IProject project) {
		IScopeContext projectScope = new ProjectScope(project);
		IEclipsePreferences node = projectScope.getNode(SdrUiPlugin.PLUGIN_ID);
		node.putBoolean("useBuild.sh", true);
		try {
			node.flush();
		} catch (BackingStoreException e) {
			SdrUiPlugin.getDefault().logError("Unable to enable useBuild.sh project preference for " + project, e);
		}
	}

	/**
	 * Invokes "build.sh install" for a project.
	 * @param monitor
	 * @param project
	 * @throws CoreException
	 * @throws IOException
	 * @throws DebugException
	 */
	private static boolean buildSH(final IProgressMonitor monitor, IProject project) throws CoreException, IOException {
		SubMonitor progress = SubMonitor.convert(monitor, 10);

		String configTypeId = IExternalToolConstants.ID_PROGRAM_LAUNCH_CONFIGURATION_TYPE;
		final ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		final String launchConfigName = launchManager.generateLaunchConfigurationName("Build Install " + project.getName());
		final ILaunchConfigurationType configType = launchManager.getLaunchConfigurationType(configTypeId);
		final ILaunchConfigurationWorkingCopy retVal = configType.newInstance(null, launchConfigName);

		retVal.setAttribute(ILaunchManager.ATTR_APPEND_ENVIRONMENT_VARIABLES, true);
		retVal.setAttribute(ILaunchManager.ATTR_ENVIRONMENT_VARIABLES, getEnv());
		retVal.setAttribute(IExternalToolConstants.ATTR_BUILDER_ENABLED, false);
		retVal.setAttribute(IExternalToolConstants.ATTR_BUILD_SCOPE, BUILD_SCOPE_NONE);
		retVal.setAttribute(IExternalToolConstants.ATTR_BUILDER_SCOPE, BUILD_SCOPE_NONE);
		retVal.setAttribute(IExternalToolConstants.ATTR_SHOW_CONSOLE, true);

		URL fileUrl = FileLocator.toFileURL(FileLocator.find(SdrUiPlugin.getDefault().getBundle(), new Path("resources/install.sh"), null));
		try {
			File file = new File(fileUrl.toURI());
			if (!file.canExecute()) {
				file.setExecutable(true);
			}
			retVal.setAttribute(IExternalToolConstants.ATTR_LOCATION, file.getAbsolutePath());
		} catch (URISyntaxException e1) {
			throw new CoreException(new Status(IStatus.ERROR, SdrUiPlugin.PLUGIN_ID, "Failed to find install script.", e1));
		}
		retVal.setAttribute(IExternalToolConstants.ATTR_WORKING_DIRECTORY, project.getLocation().toOSString());

		ILaunch launch = retVal.launch("run", progress.newChild(1), false);
		while (!launch.isTerminated()) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				launch.terminate();
				return false;
			}
			if (progress.isCanceled()) {
				launch.terminate();
				return false;
			}
		}
		progress.newChild(9);
		if (launch.getProcesses()[0].getExitValue() != 0) {
			throw new CoreException(new Status(IStatus.ERROR, SdrUiPlugin.PLUGIN_ID,
				"Install script returned with error code " + launch.getProcesses()[0].getExitValue() + "\n\nSee console output for details.", null));
		}
		return true;
	}

	/**
	 * Checks the implementations within the soft pkg provided to see if any implementations have not been generated.
	 * If an implementation has not been generated, or no implementations exist at all, the user is asked whether or not
	 * to continue. True is returned if the user wishes to continue or all implementations were found, false is returned
	 * if the user has chosen to cancel the export.
	 * 
	 * @param softPkg The Soft Pkg for this project
	 * @param spdRootPath The full path to the SPD resource. May be found by:
	 * spdResource.getFullPath().removeLastSegments(1)
	 * @return False if the user has chosen to stop the export, True otherwise.
	 * @throws CoreException
	 */
	private static boolean checkProjectImplsForExport(SoftPkg softPkg, IPath spdRootPath) throws CoreException {

		final String projectName = ModelUtil.getProject(softPkg).getProject().getName();
		if (softPkg.getImplementation() != null && !softPkg.getImplementation().isEmpty()) {
			for (final Implementation impl : softPkg.getImplementation()) {
				final String localFileName = impl.getCode().getLocalFile().getName();
				final IPath codeLocalFile;
				if (localFileName != null) {
					codeLocalFile = new Path(localFileName);
				} else {
					codeLocalFile = null;
				}
				if (codeLocalFile != null && codeLocalFile.isAbsolute()) {
					throw new CoreException(new Status(IStatus.ERROR, RedhawkIdeActivator.PLUGIN_ID, "Cannot export absolute path localfile paths"));
				}

				final IResource srcPath = getWorkspaceResource(spdRootPath.append(codeLocalFile));
				// Check if the path exists, there may be multiple implementations in this, only one needs to be built
				if ((srcPath == null) || !srcPath.exists()) {
					final String inProjStr = "in project '" + projectName + "' ";

					final boolean[] shouldExport = { false };
					Display.getDefault().syncExec(new Runnable() {
						@Override
						public void run() {
							shouldExport[0] = MessageDialog.openQuestion(null, "File does not exist",
								"The file '" + localFileName + "' " + inProjStr + "does not exist, export implementation anyway?");
						}
					});
					if (!shouldExport[0]) {
						SdrUiPlugin.getDefault().logError(
							"Expected file '" + codeLocalFile + "' " + inProjStr + "does not exist, not exporting implementation");
						return false;
					}
				}
			}
		} else {
			final boolean[] shouldExport = { false };
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					shouldExport[0] = MessageDialog.openQuestion(null, "No Implementations Found",
						"No Implementations were found in " + projectName + ", export anyway?");
				}
			});
			if (!shouldExport[0]) {
				SdrUiPlugin.getDefault().logError("No implementations found in " + projectName + ", not exporting project");
				return false;
			}
		}

		return true;
	}

	private static IResource getWorkspaceResource(final IPath path) {
		return ResourcesPlugin.getWorkspace().getRoot().findMember(path);
	}

	/**
	 * This checks a project for errors and prompts the user to continue if any are found.
	 * 
	 * @param proj the project to check
	 * @return true if there are no errors or the user still wants to export
	 * @throws CoreException
	 */
	private static boolean checkProject(final IProject proj) throws CoreException {
		final int maxSeverity = proj.findMaxProblemSeverity(IMarker.PROBLEM, true, 2);
		boolean retVal = true;

		if (maxSeverity == IMarker.SEVERITY_ERROR) {
			final boolean[] continueExport = { true };
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					continueExport[0] = MessageDialog.openQuestion(null, "Project has errors", "Project '" + proj.getName() + "' has errors. Export anyway?");
				}
			});
			if (!continueExport[0]) {
				retVal = false;
			}
		}

		return retVal;
	}

}
