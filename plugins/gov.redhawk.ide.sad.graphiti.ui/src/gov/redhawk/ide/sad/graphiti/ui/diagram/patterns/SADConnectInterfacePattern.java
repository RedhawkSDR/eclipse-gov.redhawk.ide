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
package gov.redhawk.ide.sad.graphiti.ui.diagram.patterns;

import gov.redhawk.diagram.util.InterfacesUtil;
import gov.redhawk.ide.sad.graphiti.ui.diagram.RHGraphitiDiagramEditor;
import gov.redhawk.ide.sad.graphiti.ui.diagram.providers.ImageProvider;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.StyleUtil;
import gov.redhawk.sca.sad.validation.ConnectionsConstraint;
import gov.redhawk.sca.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

import mil.jpeojtrs.sca.partitioning.ConnectInterface;
import mil.jpeojtrs.sca.partitioning.ConnectionTarget;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SadConnectInterface;
import mil.jpeojtrs.sca.sad.SadFactory;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.context.IAddConnectionContext;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.IConnectionContext;
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.mm.algorithms.Image;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.pattern.AbstractConnectionPattern;
import org.eclipse.graphiti.pattern.IConnectionPattern;
import org.eclipse.graphiti.platform.IPlatformImageConstants;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;

public class SADConnectInterfacePattern extends AbstractConnectionPattern implements IConnectionPattern {

	public static final String NAME = "Connection";
	public static final String SHAPE_IMG_CONNECTION_DECORATOR = "imgConnectionDecorator";
	public static final String SHAPE_TEXT_CONNECTION_DECORATOR = "textConnectionDecorator";

	@Override
	public String getCreateName() {
		return NAME;
	}

	@Override
	public String getCreateDescription() {
		return "Create new Connect Interface";
	}

	@Override
	public String getCreateImageId() {
		return ImageProvider.IMG_CONNECTION;
	}

	/**
	 * Return true if use selected
	 */
	@Override
	public boolean canStartConnection(ICreateConnectionContext context) {

		// get sad from diagram
		final SoftwareAssembly sad = DUtil.getDiagramSAD(getFeatureProvider(), getDiagram());

		// source anchor (allow creating connection by starting from either direction)
		UsesPortStub source = getUsesPortStub(context);
		ConnectionTarget target = getConnectionTarget(context);

		if (sad != null && (source != null || target != null)) {
			return true;
		}

		return false;
	}

	/**
	 * Determines if a connection can be made.
	 */
	@Override
	public boolean canAdd(IAddContext context) {

		if (context instanceof IAddConnectionContext && context.getNewObject() instanceof SadConnectInterface) {
			return true;
		}
		return false;
	}

	/**
	 * Adds the connection to the diagram and associates the source/target port with the line
	 */
	@Override
	public PictogramElement add(IAddContext addContext) {

		IGaService gaService = Graphiti.getGaService();
		IPeCreateService peCreateService = Graphiti.getPeCreateService();
		IAddConnectionContext context = (IAddConnectionContext) addContext;
		SadConnectInterface connectInterface = (SadConnectInterface) addContext.getNewObject();

		// source and target
		UsesPortStub source = getUsesPortStub(context);
		ConnectionTarget target = getConnectionTarget(context);

		// Create connection (handle user selecting source or target)
		Connection connectionPE = peCreateService.createFreeFormConnection(getFeatureProvider().getDiagramTypeProvider().getDiagram());
//		Connection connection = peCreateService.createManhattanConnection(getFeatureProvider().getDiagramTypeProvider().getDiagram());
		if (source == getUsesPortStub(context.getSourceAnchor()) && target == getConnectionTarget(context.getTargetAnchor())) {
			connectionPE.setStart(context.getSourceAnchor());
			connectionPE.setEnd(context.getTargetAnchor());
		} else if (source == getUsesPortStub(context.getTargetAnchor()) && target == getConnectionTarget(context.getSourceAnchor())) {
			connectionPE.setStart(context.getTargetAnchor());
			connectionPE.setEnd(context.getSourceAnchor());
		}

		// decorate Connection with errors
		decorateConnection(connectionPE, connectInterface, getDiagram());

		// create line
		Polyline line = gaService.createPolyline(connectionPE);
		line.setLineWidth(2);
		line.setForeground(gaService.manageColor(getFeatureProvider().getDiagramTypeProvider().getDiagram(), StyleUtil.BLACK));

		// add static graphical arrow
		ConnectionDecorator cd;
		cd = peCreateService.createConnectionDecorator(connectionPE, false, 1.0, true);
		DUtil.createArrow(cd, getFeatureProvider(), gaService.manageColor(getFeatureProvider().getDiagramTypeProvider().getDiagram(), StyleUtil.BLACK));

		// link ports to connection
//		getFeatureProvider().link(connection, new Object[] { connectInterface, connectInterface.getSource(), connectInterface.getTarget()});
		getFeatureProvider().link(connectionPE, new Object[] { connectInterface, source, target });

		return connectionPE;
	}

	/**
	 * Add error/warning decorators to connection if applicable
	 * Note: Unfortunately Graphiti doesn't support ConnectionDecorators with tooltips like it does with Shape
	 * Decorators (see RHToolBehaviorProvider)
	 * @param connectionPE
	 */
	public static void decorateConnection(Connection connectionPE, SadConnectInterface connectInterface, Diagram diagram) {
		IGaService gaService = Graphiti.getGaService();
		IPeCreateService peCreateService = Graphiti.getPeCreateService();

		// if not compatible draw error/warning decorator
		if (connectInterface.getSource() != null && connectInterface.getTarget() != null) {
			// connection validation
			boolean uniqueConnection = ConnectionsConstraint.uniqueConnection(connectInterface);
			boolean compatibleConnection = InterfacesUtil.areCompatible(connectInterface.getSource(), connectInterface.getTarget());
			if (!compatibleConnection || !uniqueConnection) {

				// set decorator image/text
				String decoratorMessage = "";
				String decoratorImageId = "";
				if (!compatibleConnection) {
					decoratorMessage = "Incompatible Connection";
					decoratorImageId = IPlatformImageConstants.IMG_ECLIPSE_ERROR_TSK;
				} else if (!uniqueConnection) {
					decoratorMessage = "Redundant connection";
					decoratorImageId = IPlatformImageConstants.IMG_ECLIPSE_WARNING_TSK;
				}

				// image
				ConnectionDecorator imgConnectionDecorator;
				imgConnectionDecorator = peCreateService.createConnectionDecorator(connectionPE, true, 0.5, true); // must
																													// be
																													// active
																													// in
																													// order
																													// to
																													// display
				Graphiti.getPeService().setPropertyValue(imgConnectionDecorator, DUtil.SHAPE_TYPE, SHAPE_IMG_CONNECTION_DECORATOR);
				Image errorImage = gaService.createImage(imgConnectionDecorator, decoratorImageId);
				errorImage.setHeight(20);
				errorImage.setWidth(20);
				gaService.setLocation(errorImage, -60, -18);
				// text
				ConnectionDecorator textConnectionDecorator = peCreateService.createConnectionDecorator(connectionPE, true, 0.5, true); // must
																																		// be
																																		// active
																																		// in
																																		// order
																																		// to
																																		// display
				Graphiti.getPeService().setPropertyValue(imgConnectionDecorator, DUtil.SHAPE_TYPE, SHAPE_TEXT_CONNECTION_DECORATOR);
				Text text = gaService.createPlainText(textConnectionDecorator);
				text.setValue(decoratorMessage);
				text.setStyle(StyleUtil.getStyleForErrorTextConnections((diagram)));
				gaService.setLocation(text, -40, -15);
				// this unfortunately hides the connection line
//				Graphiti.getPeService().sendToFront(imgDecorator);
//				Graphiti.getPeService().sendToFront(textDecorator);
			}
		}
	}

	/**
	 * Determines whether creation of an interface connection is possible between source and destination anchors.
	 * user can begin drawing connection from either source->dest, or dest->source. This method will create the
	 * connection in the correct direction.
	 * Source anchor of connection must be UsesPort. Target Anchor must be ConnectionTarget which is the parent class
	 * for a variety of types.
	 * If ConnectionTarget->ProvidesPortStub is the most simple target in that PictogramElements already link to
	 * ProvidesPortStub. ConnectionTarget->ComponentSupportedInterfaceStub isn't actually
	 */
	@Override
	public boolean canCreate(ICreateConnectionContext context) {
		// get sad from diagram
		final SoftwareAssembly sad = DUtil.getDiagramSAD(getFeatureProvider(), getDiagram());
		if (sad == null) {
			return false;
		}
		// determine source
		UsesPortStub source = getUsesPortStub(context);
		if (source == null) {
			return false;
		}
		// determine destination
		// getConnectionTarget handles connecting to ports on components, not ports or interfaces on FindBy Shapes
		ConnectionTarget target = getConnectionTarget(context);
		if (target == null) {
			// TODO: check if interface on findBy Shape
			// TODO: check if provides port on findBy...not sure how were doing all this??
			return false;
		}

		// not currently used but will select the portRectangeShape instead of the anchorRectangle
//		ContainerShape portRectangleShape = null;
//		if(target != null){
//			portRectangleShape = DiagramUtil.findContainerShapeParentWithProperty(
//					context.getSourcePictogramElement(), DiagramUtil.SHAPE_providesPortRectangleShape);
//		}
//		if(source != null){
//			portRectangleShape = DiagramUtil.findContainerShapeParentWithProperty(
//					context.getSourcePictogramElement(), DiagramUtil.SHAPE_usesPortRectangleShape);
//		}
//		
//		getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior()
//		.getDiagramContainer().selectPictogramElements(
//				new PictogramElement[] {portRectangleShape});

		// doing the null check because it breaks when loading a findby without a diagram
		if (((RHGraphitiDiagramEditor) getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getDiagramContainer()).getGraphicalViewer() != null) {

			// force selection of shape so that we can then right click for contextual options
			// this is kind of a hack, it would be better if selection happened automatically when its clicked.
			getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getDiagramContainer().selectPictogramElements(
				new PictogramElement[] { context.getSourcePictogramElement() });
		}
		return true;

	}

	/**
	 * Creates a new connection between the selected usesPortStub and ConnectionTarget
	 */
	@Override
	public Connection create(ICreateConnectionContext context) {

		Connection newConnection = null;

		// source and destination targets
		final UsesPortStub source = getUsesPortStub(context);
		final ConnectionTarget target = getConnectionTarget(context);

		// TODO: handle bad situations
		if (source == null || target == null) {
			return null;
		}

		// editing domain for our transaction
		TransactionalEditingDomain editingDomain = getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();

		// get sad from diagram
		final SoftwareAssembly sad = DUtil.getDiagramSAD(getFeatureProvider(), getDiagram());

		// container for new SadConnectInterface, necessary for reference after command execution
		final SadConnectInterface[] sadConnectInterfaces = new SadConnectInterface[1];

		// Create Connect Interface & related objects
		TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain) {
			@Override
			protected void doExecute() {

				// create connections if necessary
				if (sad.getConnections() == null) {
					sad.setConnections(SadFactory.eINSTANCE.createSadConnections());
				}

				// create connect interface
				sadConnectInterfaces[0] = SadFactory.eINSTANCE.createSadConnectInterface();

				// add to connections
				sad.getConnections().getConnectInterface().add(sadConnectInterfaces[0]);

				// set connection id
				sadConnectInterfaces[0].setId(createConnectionId(sad));
				// source
				sadConnectInterfaces[0].setSource(source);
				// target
				sadConnectInterfaces[0].setTarget(target);

				// TODO: evaluate when and where these should be set
//				sadConnectInterfaces[0].setProvidesPort(value);
//				sadConnectInterfaces[0].setTarget(value);
//				sadConnectInterfaces[0].setFindBy(value);
//				sadConnectInterfaces[0].setComponentSupportedInterface(value);

			}
		});

		// add connection for business object
		AddConnectionContext addContext = new AddConnectionContext(context.getSourceAnchor(), context.getTargetAnchor());
		addContext.setNewObject(sadConnectInterfaces[0]);
		newConnection = (Connection) getFeatureProvider().addIfPossible(addContext);

		return newConnection;
	}

	// Return UsesPortStub from either the source or target anchor. Depends on how user drew connection.
	private UsesPortStub getUsesPortStub(IConnectionContext context) {
		UsesPortStub source = getUsesPortStub(context.getSourceAnchor());
		if (source != null) {
			return source;
		}
		source = getUsesPortStub(context.getTargetAnchor());
		return source;
	}

	// Return ConnectionTarget from either the source or target anchor. Depends on how user drew connection.
	private ConnectionTarget getConnectionTarget(IConnectionContext context) {
		ConnectionTarget connectionTarget = getConnectionTarget(context.getSourceAnchor());
		if (connectionTarget != null) {
			return connectionTarget;
		}
		connectionTarget = getConnectionTarget(context.getTargetAnchor());
		return connectionTarget;
	}

	// Return SadComponentInstantiation from either source or target anchor. Depends on how user drew connection.
	private SadComponentInstantiation getSadComponentInstantiation(IConnectionContext context) {
		SadComponentInstantiation target = getSadComponentInstantiation(context.getSourceAnchor());
		if (target != null) {
			return target;
		}
		target = getSadComponentInstantiation(context.getTargetAnchor());
		return target;
	}

	private UsesPortStub getUsesPortStub(Anchor anchor) {
		if (anchor != null) {
			Object object = getBusinessObjectForPictogramElement(anchor.getParent());
			if (object instanceof UsesPortStub) {
				return (UsesPortStub) object;
			}
		}
		return null;
	}

	private ConnectionTarget getConnectionTarget(Anchor anchor) {
		if (anchor != null) {
			Object object = getBusinessObjectForPictogramElement(anchor.getParent());
			if (object instanceof ConnectionTarget) {
				return (ConnectionTarget) object;
			}
		}
		return null;
	}

	private SadComponentInstantiation getSadComponentInstantiation(Anchor anchor) {
		if (anchor != null) {
			Object object = getBusinessObjectForPictogramElement(anchor.getParent());
			if (object instanceof SadComponentInstantiation) {
				return (SadComponentInstantiation) object;
			}
		}
		return null;
	}

	/**
	 * Returns the next available connection id
	 * @param sad
	 * @return
	 */
	private String createConnectionId(SoftwareAssembly sad) {
		final List<String> ids = new ArrayList<String>();
		final List< ? extends ConnectInterface< ? , ? , ? >> connections = sad.getConnections().getConnectInterface();
		for (final ConnectInterface< ? , ? , ? > connection : connections) {
			ids.add(connection.getId());
		}
		return StringUtil.defaultCreateUniqueString("connection_1", ids);
	}

}
