package Models.java;
import com.ms4systems.devs.core.model.impl.CoupledModelImpl;
import com.ms4systems.devs.core.message.Port;
import com.ms4systems.devs.core.simulation.Simulation;
import com.ms4systems.devs.helpers.impl.SimulationOptionsImpl;
import com.ms4systems.devs.simviewer.standalone.SimViewer;
import java.io.Serializable;
import com.ms4systems.devs.extensions.StateVariableBased;
import com.ms4systems.devs.core.model.AtomicModel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;

public class ReasoningLayer extends CoupledModelImpl implements StateVariableBased{ 
	private static final long serialVersionUID = 1L;
	protected SimulationOptionsImpl options = new SimulationOptionsImpl();
	
		public final Port<? extends Serializable> inHumanData= addInputPort("inHumanData",Serializable.class);
		public final Port<? extends Serializable> outFallOccurrence= addOutputPort("outFallOccurrence",Serializable.class);
		public final Port<? extends Serializable> outFallRegisterUnit= addOutputPort("outFallRegisterUnit",Serializable.class);
	public ReasoningLayer(){
		this("ReasoningLayer");
	}
	public ReasoningLayer(String nm) {
		super(nm);
		make();
	}
	public void make(){

		DataEvaluation DataEvaluation = new DataEvaluation();
		addChildModel(DataEvaluation);
		addCoupling(this.inHumanData,DataEvaluation.inHumanData);
		addCoupling(DataEvaluation.outFallOccurrence,this.outFallOccurrence);
		addCoupling(DataEvaluation.outFallRegisterUnit,this.outFallRegisterUnit);

	}
    @Override
    public String[] getStateVariableNames() {
        ArrayList<String> lst = new ArrayList<String>();
        for (AtomicModel child : getChildren())
            if (child instanceof StateVariableBased)
                for (String childVar : ((StateVariableBased) child)
                        .getStateVariableNames())
                    lst.add(child.getName() + "." + childVar);
        return lst.toArray(new String[0]);
    }

    @Override
    public Object[] getStateVariableValues() {
        ArrayList<Object> lst = new ArrayList<Object>();
        for (AtomicModel child : getChildren())
            if (child instanceof StateVariableBased)
                for (Object childVar : ((StateVariableBased) child)
                        .getStateVariableValues())
                    lst.add(childVar);
        return lst.toArray();
    }

    @Override
    public Class<?>[] getStateVariableTypes() {
        ArrayList<Class<?>> lst = new ArrayList<Class<?>>();
        for (AtomicModel child : getChildren())
            if (child instanceof StateVariableBased)
                for (Class<?> childVar : ((StateVariableBased) child)
                        .getStateVariableTypes())
                    lst.add(childVar);
        return lst.toArray(new Class<?>[0]);
    }

    @Override
    public void setStateVariableValue(int index, Object value) {
        int i = 0;
        for (AtomicModel child : getChildren())
            if (child instanceof StateVariableBased)
                for (int childIndex = 0; childIndex < ((StateVariableBased) child)
                        .getStateVariableNames().length; childIndex++) {
                    if (i == index) {
                        ((StateVariableBased) child).setStateVariableValue(
                                childIndex, value);
                        return;
                    }
                    i++;
                }
    }
    
	public static void main(String[] args){
		SimulationOptionsImpl options = new SimulationOptionsImpl(args, true);
		ReasoningLayer model = new ReasoningLayer();
		model.options = options;
		if(options.isDisableViewer()){ // Command Line output only
			Simulation sim = new com.ms4systems.devs.core.simulation.impl.SimulationImpl("ReasoningLayer Simulation",model,options);
			sim.startSimulation(0);
			sim.simulateIterations(Long.MAX_VALUE);
		}else { //Use SimViewer
			SimViewer viewer = new SimViewer();
			viewer.open(model,options);
		}
	}
}