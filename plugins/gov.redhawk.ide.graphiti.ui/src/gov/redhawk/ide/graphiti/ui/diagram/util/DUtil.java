/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.ide.graphiti.ui.diagram.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.PictogramLink;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;

import gov.redhawk.core.graphiti.ui.ext.RHContainerShape;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.AbstractFindByPattern;
import mil.jpeojtrs.sca.partitioning.FindBy;
import mil.jpeojtrs.sca.partitioning.FindByStub;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.HostCollocation;

public class DUtil extends gov.redhawk.core.graphiti.ui.util.DUtil {

	/**
	 * Returns all of the shape children recursively
	 * @param diagramElement
	 * @return
	 */
	public static List<Shape> collectShapeChildren(Shape diagramElement) {

		List<Shape> children = new ArrayList<Shape>();
		children.add(diagramElement);
		// if containershape, collect children recursively
		if (diagramElement instanceof ContainerShape) {
			ContainerShape cs = (ContainerShape) diagramElement;
			for (Shape c : cs.getChildren()) {
				children.addAll(collectShapeChildren(c));
			}
		}
		return children;
	}

	/**
	 * Remove Business object from all linked PictogramElement
	 * @param diagram
	 * @param eObject
	 */
	public static void removeBusinessObjectFromAllPictogramElements(Diagram diagram, EObject eObject) {
		// get pe with link to bo
		List<PictogramElement> pictogramElements = Graphiti.getLinkService().getPictogramElements(diagram, eObject);

		// remove link
		for (PictogramElement pe : pictogramElements) {
			pe.getLink().getBusinessObjects().remove(eObject);
		}
	}

	public static void addLink(IFeatureProvider featureProvider, PictogramElement pe, EObject eObject) {
		if (eObject == null) {
			return;
		}

		if (pe.getLink() == null) {
			featureProvider.link(pe, eObject);
		} else {
			pe.getLink().getBusinessObjects().add(eObject);
		}
	}

	public static void addLinks(IFeatureProvider featureProvider, PictogramElement pe, Collection< ? extends EObject> eObjects) {
		if (eObjects == null || eObjects.size() < 1) {
			return;
		}

		if (pe.getLink() == null) {
			featureProvider.link(pe, eObjects.toArray());
		} else {
			pe.getLink().getBusinessObjects().addAll(eObjects);
		}
	}

	/**
	 * Returns the ancestor (parent chain) of the provided diagramElement with the provided PropertyContainer
	 * @param diagramElement
	 * @return
	 */
	public static ContainerShape findContainerShapeParentWithProperty(Shape shape, String propertyValue) {

		if (shape instanceof Diagram) {
			return null;
		}
		if (shape instanceof ContainerShape && DUtil.isPropertyElementType(shape, propertyValue)) {
			return (ContainerShape) shape;
		}
		if (DUtil.isPropertyElementType(shape.getContainer(), propertyValue)) {
			return shape.getContainer();
		}
		return findContainerShapeParentWithProperty(shape.getContainer(), propertyValue);

	}

	/**
	 * Returns the ancestor (parent chain) of the provided diagramElement with the provided PropertyContainer
	 * First checks self to see if it is a container with matching property
	 * @param diagramElement
	 * @return
	 */
	public static ContainerShape findContainerShapeParentWithProperty(PictogramElement pe, String propertyValue) {
		if (pe instanceof ContainerShape && DUtil.isPropertyElementType(pe, propertyValue)) {
			return (ContainerShape) pe;
		}
		PictogramElement peContainer = Graphiti.getPeService().getActiveContainerPe(pe);
		if (peContainer instanceof ContainerShape) {
			ContainerShape outerContainerShape = DUtil.findContainerShapeParentWithProperty((ContainerShape) peContainer, propertyValue);
			return outerContainerShape;
		}
		return null;
	}

	/**
	 * Returns true if the specified area overlaps any part of a host collocation.
	 * @param diagram
	 * @param width
	 * @param height
	 * @param x Absolute x
	 * @param y Absolute y
	 * @return
	 */
	public static boolean overlapsHostCollocation(Diagram diagram, int width, int height, int x, int y) {
		for (Shape shape : diagram.getChildren()) {
			PictogramElement pe = shape.getLink().getPictogramElement();
			if (!(DUtil.getBusinessObject(pe) instanceof HostCollocation)) {
				continue;
			}
			if (shapeExistsPartiallyInArea(shape, width, height, x, y)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks the container shape and all its children and returns any which do not overlap any of the specified area.
	 * @param containerShape Usually this should be the {@link Diagram}
	 * @param width
	 * @param height
	 * @param x Absolute x
	 * @param y Absolute y
	 * @return
	 */
	public static List<Shape> getShapesOutsideArea(final ContainerShape containerShape, int width, int height, int x, int y) {
		List<Shape> retList = new ArrayList<Shape>();
		EList<Shape> shapes = containerShape.getChildren();
		for (Shape s : shapes) {
			if (!shapeExistsPartiallyInArea(s, width, height, x, y)) {
				retList.add(s);
			}
		}
		return retList;
	}

	/**
	 * Returns all ContainerShapes with the provided property value
	 * @param containerShape
	 * @param propertyValue
	 * @return
	 */
	public static List<ContainerShape> getAllContainerShapes(ContainerShape containerShape, String propertyValue) {
		List<ContainerShape> children = new ArrayList<ContainerShape>();
		if (containerShape instanceof ContainerShape && isPropertyElementType(containerShape, propertyValue)) {
			children.add(containerShape);
		} else {
			for (Shape s : containerShape.getChildren()) {
				if (s instanceof ContainerShape) {
					children.addAll(getAllContainerShapes((ContainerShape) s, propertyValue));
				}
			}
		}
		return children;
	}

	/**
	 * Returns true if Pictogram Link contains an object of the provided Class
	 * @param <T>
	 * @param link
	 * @param cls
	 * @return
	 */
	public static < T > boolean doesLinkContainObjectTypeInstance(PictogramLink link, Class<T> cls) {
		if (link != null) {
			for (EObject eObj : link.getBusinessObjects()) {
				if (cls.isInstance(eObj)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Update PictogramElement via feature
	 * Relies on the framework determining which feature should be used and whether it can be added to diagram
	 * @param featureProvider
	 * @param pe
	 * @return
	 */
	public static boolean updateShapeViaFeature(IFeatureProvider featureProvider, Diagram diagram, PictogramElement pe) {
		UpdateContext updateContext = new UpdateContext(pe);
		IUpdateFeature updateFeature = featureProvider.getUpdateFeature(updateContext);
		if (updateFeature.canUpdate(updateContext)) {
			return updateFeature.update(updateContext);
		}
		return false;
	}

	/**
	 * Search for the FindByStub in the diagram given the findBy object
	 * @param findBy
	 * @param diagram
	 * @return
	 */
	public static FindByStub findFindByStub(FindBy findBy, Diagram diagram) {
		for (RHContainerShape findByShape : AbstractFindByPattern.getAllFindByShapes(diagram)) {
			FindByStub findByStub = (FindByStub) DUtil.getBusinessObject(findByShape);
			// determine findBy match
			if (findByStub != null && AbstractFindByPattern.doFindByObjectsMatch(findBy, findByStub)) {
				// it matches
				return findByStub;
			}
		}
		return null;
	}

	/**
	 * Return true if target is HostCollocation ContainerShape
	 * @param context
	 */
	public static HostCollocation getHostCollocation(final ContainerShape targetContainerShape) {
		if (targetContainerShape instanceof ContainerShape) {
			if (targetContainerShape.getLink() != null && targetContainerShape.getLink().getBusinessObjects() != null) {
				for (EObject obj : targetContainerShape.getLink().getBusinessObjects()) {
					if (obj instanceof HostCollocation) {
						return (HostCollocation) obj;
					}
				}
			}
		}
		return null;
	}

	// convenient method for getting diagram for a ContainerShape
	public static Diagram findDiagram(ContainerShape containerShape) {
		return Graphiti.getPeService().getDiagramForShape(containerShape);
	}

	/**
	 * If a provides port with the port name still exists, return the new anchor so the connection can be redrawn
	 * @param providesPortStubs
	 * @param oldPortName
	 * @return Anchor object that is associated with the port
	 */
	public static Anchor getProvidesAnchor(Diagram diagram, EList<ProvidesPortStub> providesPortStubs, String oldPortName) {
		for (ProvidesPortStub port : providesPortStubs) {
			if (port.getName().equals(oldPortName)) {
				Anchor anchor = (Anchor) DUtil.getPictogramElementForBusinessObject(diagram, (EObject) port, Anchor.class);
				return anchor;
			}
		}
		return null;
	}

	/**
	 * If a uses port with the port name still exists, return the new anchor so the connection can be redrawn
	 * @param usesPortStubs
	 * @param oldPortName
	 * @return Anchor object that is associated with the port
	 */
	public static Anchor getUsesAnchor(Diagram diagram, EList<UsesPortStub> usesPortStubs, String oldPortName) {
		for (UsesPortStub port : usesPortStubs) {
			if (port.getName().equals(oldPortName)) {
				Anchor anchor = (Anchor) DUtil.getPictogramElementForBusinessObject(diagram, (EObject) port, Anchor.class);
				return anchor;
			}
		}
		return null;
	}
}
