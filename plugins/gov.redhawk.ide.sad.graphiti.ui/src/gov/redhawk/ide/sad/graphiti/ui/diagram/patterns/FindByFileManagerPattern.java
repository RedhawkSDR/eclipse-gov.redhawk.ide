package gov.redhawk.ide.sad.graphiti.ui.diagram.patterns;

import gov.redhawk.ide.sad.graphiti.ui.diagram.providers.ImageProvider;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.DiagramUtil;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.StyleUtil;
import mil.jpeojtrs.sca.partitioning.DomainFinder;
import mil.jpeojtrs.sca.partitioning.DomainFinderType;
import mil.jpeojtrs.sca.partitioning.FindByStub;
import mil.jpeojtrs.sca.partitioning.PartitioningFactory;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.pattern.AbstractPattern;
import org.eclipse.graphiti.pattern.IPattern;
import org.eclipse.graphiti.services.Graphiti;

public class FindByFileManagerPattern extends AbstractPattern implements IPattern{

	
	public static final String NAME = "File Manager";
	public static final String SHAPE_TITLE = "File Manager";
			
	public FindByFileManagerPattern(){
		super();
	}
	
	@Override
	public String getCreateName(){
		return NAME;
	}
	
	@Override
	public String getCreateDescription() {
		return "";
	}
	
	@Override
	public String getCreateImageId() {
		return ImageProvider.IMG_FIND_BY_FILE_MANAGER;
	}
	
	
	//THE FOLLOWING THREE METHODS DETERMINE IF PATTERN IS APPLICABLE TO OBJECT
	@Override
	public boolean isMainBusinessObjectApplicable(Object mainBusinessObject) {
		if(mainBusinessObject instanceof FindByStub){
			FindByStub findByStub = (FindByStub)mainBusinessObject;
			if(findByStub.getDomainFinder() != null && findByStub.getDomainFinder().getType().equals(DomainFinderType.FILEMANAGER)){
				return true;
			}
		}
		return false;
	}
	@Override
	protected boolean isPatternControlled(PictogramElement pictogramElement) {
		Object domainObject = getBusinessObjectForPictogramElement(pictogramElement);
		return isMainBusinessObjectApplicable(domainObject);
	}
	@Override
	protected boolean isPatternRoot(PictogramElement pictogramElement) {
		Object domainObject = getBusinessObjectForPictogramElement(pictogramElement);
		return isMainBusinessObjectApplicable(domainObject);
	}
	
	
	//DIAGRAM FEATURES
	@Override
	public boolean canAdd(IAddContext context) {
		if (context.getNewObject() instanceof FindByStub) {
			if (context.getTargetContainer() instanceof Diagram) {
					return true;
			}
		}
		return false;
	}
	
	@Override
	public PictogramElement add(IAddContext context) {
		FindByStub findByStub = (FindByStub) context.getNewObject();
		Diagram diagram = (Diagram) context.getTargetContainer();
		
		//OUTER RECTANGLE
		ContainerShape outerContainerShape = 
				DiagramUtil.addOuterRectangle(diagram, 
						NAME, 
						findByStub, getFeatureProvider(), ImageProvider.IMG_FIND_BY,
						StyleUtil.getStyleForFindByOuter(diagram));
		Graphiti.getGaLayoutService().setLocation(outerContainerShape.getGraphicsAlgorithm(), 
				context.getX(), context.getY());

		//INNER RECTANGLE
		DiagramUtil.addInnerRectangle(diagram,
				outerContainerShape,
				SHAPE_TITLE,
				getFeatureProvider(), getCreateImageId(),
				StyleUtil.getStyleForFindByInner(diagram));
		

		//add lollipop interface anchor to shape.
		DiagramUtil.addLollipop(outerContainerShape, diagram, findByStub.getInterface(), getFeatureProvider());

		//layout
		layoutPictogramElement(outerContainerShape);

		return outerContainerShape;
	}
	
	@Override
	public boolean canCreate(ICreateContext context) {
		return context.getTargetContainer() instanceof Diagram;
	}
	@Override
	public Object[] create(ICreateContext context) {
		

		final FindByStub[] findByStubs = new FindByStub[1];
		
		//editing domain for our transaction
		TransactionalEditingDomain editingDomain = getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();
		
		//Create Component Related objects in SAD model
		TransactionalCommandStack stack = (TransactionalCommandStack)editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain){
			@Override
            protected void doExecute() {
				
				findByStubs[0] = PartitioningFactory.eINSTANCE.createFindByStub();
				
				//interface stub (lollipop)
				findByStubs[0].setInterface(PartitioningFactory.eINSTANCE.createComponentSupportedInterfaceStub());
				
				//domain finder service of type domain manager
				DomainFinder domainFinder = PartitioningFactory.eINSTANCE.createDomainFinder();
				domainFinder.setType(DomainFinderType.FILEMANAGER);
				findByStubs[0].setDomainFinder(domainFinder);
				
				//add to diagram resource file
				getDiagram().eResource().getContents().add(findByStubs[0]);
				
			}
		});
		
		addGraphicalRepresentation(context, findByStubs[0]);
		
		return new Object[] { findByStubs[0] };
	}
	
	@Override
	public boolean canResizeShape(IResizeShapeContext context){
		return true;
	}
	
	@Override
	public boolean canLayout(ILayoutContext context){
		ContainerShape containerShape = (ContainerShape) context.getPictogramElement();
		Object obj = DiagramUtil.getBusinessObject(containerShape);
		if(obj instanceof FindByStub){
			return true;
		}
		return false;
	}
	
	/**
	 * Layout children of component
	 */
	@Override
	public boolean layout(ILayoutContext context){

		//get shape being laid out
        ContainerShape outerContainerShape = (ContainerShape) context.getPictogramElement();
        
        //get linked component
        FindByStub findByStub = (FindByStub)DiagramUtil.getBusinessObject(outerContainerShape);

		//layout outerContainerShape of component
		DiagramUtil.layoutOuterContainerShape(context.getPictogramElement(), NAME,
				SHAPE_TITLE,
				findByStub.getProvides(), findByStub.getUses());
		
		//something is always changing.
        return true;
	}
}
