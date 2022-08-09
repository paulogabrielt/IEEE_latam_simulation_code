/* Do not remove or modify this comment!  It is required for file identification!
DNL
platform:/resource/FDS/src/Models/dnl/AlertSystem.dnl
391285257
 Do not remove or modify this comment!  It is required for file identification! */
package Models.java;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.io.File;
import java.io.Serializable;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.ArrayList;

import com.ms4systems.devs.core.message.Message;
import com.ms4systems.devs.core.message.MessageBag;
import com.ms4systems.devs.core.message.Port;
import com.ms4systems.devs.core.message.impl.MessageBagImpl;
import com.ms4systems.devs.core.model.impl.AtomicModelImpl;
import com.ms4systems.devs.core.simulation.Simulation;
import com.ms4systems.devs.core.simulation.Simulator;
import com.ms4systems.devs.core.simulation.impl.SimulationImpl;
import com.ms4systems.devs.extensions.PhaseBased;
import com.ms4systems.devs.extensions.StateVariableBased;
import com.ms4systems.devs.helpers.impl.SimulationOptionsImpl;
import com.ms4systems.devs.simviewer.standalone.SimViewer;

@SuppressWarnings("unused")
public class AlertSystem extends AtomicModelImpl implements PhaseBased,
    StateVariableBased {
    private static final long serialVersionUID = 1L;

    //ID:SVAR:0
    private static final int ID_FALLOCCURRENCE = 0;

    //ENDID
    //ID:SVAR:1
    private static final int ID_DISTRESS = 1;

    // Declare state variables
    private PropertyChangeSupport propertyChangeSupport =
        new PropertyChangeSupport(this);
    protected FallOccurrence fallOccurrence;
    protected DistressSignal distress;

    //ENDID
    String phase = "s0";
    String previousPhase = null;
    Double sigma = Double.POSITIVE_INFINITY;
    Double previousSigma = Double.NaN;

    // End state variables

    // Input ports
    //ID:INP:0
    public final Port<FallOccurrence> inFallOccurrence =
        addInputPort("inFallOccurrence", FallOccurrence.class);

    //ENDID
    // End input ports

    // Output ports
    //ID:OUTP:0
    public final Port<DistressSignal> outDistressSignal =
        addOutputPort("outDistressSignal", DistressSignal.class);

    //ENDID
    // End output ports
    protected SimulationOptionsImpl options = new SimulationOptionsImpl();
    protected double currentTime;

    // This variable is just here so we can use @SuppressWarnings("unused")
    private final int unusedIntVariableForWarnings = 0;

    public AlertSystem() {
        this("AlertSystem");
    }

    public AlertSystem(String name) {
        this(name, null);
    }

    public AlertSystem(String name, Simulator simulator) {
        super(name, simulator);
    }

    public void initialize() {
        super.initialize();

        currentTime = 0;

        passivateIn("s0");

    }

    @Override
    public void internalTransition() {
        currentTime += sigma;

        if (phaseIs("s1")) {
            getSimulator().modelMessage("Internal transition from s1");

            //ID:TRA:s1
            passivateIn("s0");

            //ENDID
            return;
        }

        passivate();
    }

    @Override
    public void externalTransition(double timeElapsed, MessageBag input) {
        currentTime += timeElapsed;
        // Subtract time remaining until next internal transition (no effect if sigma == Infinity)
        sigma -= timeElapsed;

        // Store prior data
        previousPhase = phase;
        previousSigma = sigma;

        // Fire state transition functions
        if (phaseIs("s0")) {
            if (input.hasMessages(inFallOccurrence)) {
                ArrayList<Message<FallOccurrence>> messageList =
                    inFallOccurrence.getMessages(input);

                holdIn("s1", 1.0);
                // Fire state and port specific external transition functions
                //ID:EXT:s0:inFallOccurrence
                fallOccurrence = (FallOccurrence) messageList.get(0).getData();
                assert (fallOccurrence != null);

                //If a fall is not detected the system returns to a passive state
                if (!fallOccurrence.getValue().equals("Fall")) {
                    passivateIn("s0");
                }
                distress = new DistressSignal("Elderly Fall");

                //ENDID
                // End external event code
                return;
            }
        }
    }

    @Override
    public void confluentTransition(MessageBag input) {
        // confluentTransition with internalTransition first (by default)
        internalTransition();
        externalTransition(0, input);
    }

    @Override
    public Double getTimeAdvance() {
        return sigma;
    }

    @Override
    public MessageBag getOutput() {
        MessageBag output = new MessageBagImpl();

        if (phaseIs("s1")) {
            // Output event code
            //ID:OUT:s1
            output.add(outDistressSignal, distress);

            //ENDID
            // End output event code
        }
        return output;
    }

    // Custom function definitions

    // End custom function definitions
    public static void main(String[] args) {
        SimulationOptionsImpl options = new SimulationOptionsImpl(args, true);

        // Uncomment the following line to disable SimViewer for this model
        // options.setDisableViewer(true);

        // Uncomment the following line to disable plotting for this model
        // options.setDisablePlotting(true);
        AlertSystem model = new AlertSystem();
        model.options = options;

        if (options.isDisableViewer()) { // Command line output only
            Simulation sim =
                new SimulationImpl("AlertSystem Simulation", model, options);
            sim.startSimulation(0);
            sim.simulateIterations(Long.MAX_VALUE);
        } else { // Use SimViewer
            SimViewer viewer = new SimViewer();
            viewer.open(model, options);
        }
    }

    public void addPropertyChangeListener(String propertyName,
        PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    // Getter/setter for fallOccurrence
    public void setFallOccurrence(FallOccurrence fallOccurrence) {
        propertyChangeSupport.firePropertyChange("fallOccurrence",
            this.fallOccurrence, this.fallOccurrence = fallOccurrence);
    }

    public FallOccurrence getFallOccurrence() {
        return this.fallOccurrence;
    }

    // End getter/setter for fallOccurrence

    // Getter/setter for distress
    public void setDistress(DistressSignal distress) {
        propertyChangeSupport.firePropertyChange("distress", this.distress,
            this.distress = distress);
    }

    public DistressSignal getDistress() {
        return this.distress;
    }

    // End getter/setter for distress

    // State variables
    public String[] getStateVariableNames() {
        return new String[] { "fallOccurrence", "distress" };
    }

    public Object[] getStateVariableValues() {
        return new Object[] { fallOccurrence, distress };
    }

    public Class<?>[] getStateVariableTypes() {
        return new Class<?>[] { FallOccurrence.class, DistressSignal.class };
    }

    public void setStateVariableValue(int index, Object value) {
        switch (index) {

            case ID_FALLOCCURRENCE:
                setFallOccurrence((FallOccurrence) value);
                return;

            case ID_DISTRESS:
                setDistress((DistressSignal) value);
                return;

            default:
                return;
        }
    }

    // Convenience functions
    protected void passivate() {
        passivateIn("passive");
    }

    protected void passivateIn(String phase) {
        holdIn(phase, Double.POSITIVE_INFINITY);
    }

    protected void holdIn(String phase, Double sigma) {
        this.phase = phase;
        this.sigma = sigma;
        getSimulator()
            .modelMessage("Holding in phase " + phase + " for time " + sigma);
    }

    protected static File getModelsDirectory() {
        URI dirUri;
        File dir;
        try {
            dirUri = AlertSystem.class.getResource(".").toURI();
            dir = new File(dirUri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new RuntimeException(
                "Could not find Models directory. Invalid model URL: " +
                AlertSystem.class.getResource(".").toString());
        }
        boolean foundModels = false;
        while (dir != null && dir.getParentFile() != null) {
            if (dir.getName().equalsIgnoreCase("java") &&
                  dir.getParentFile().getName().equalsIgnoreCase("models")) {
                return dir.getParentFile();
            }
            dir = dir.getParentFile();
        }
        throw new RuntimeException(
            "Could not find Models directory from model path: " +
            dirUri.toASCIIString());
    }

    protected static File getDataFile(String fileName) {
        return getDataFile(fileName, "txt");
    }

    protected static File getDataFile(String fileName, String directoryName) {
        File modelDir = getModelsDirectory();
        File dir = new File(modelDir, directoryName);
        if (dir == null) {
            throw new RuntimeException("Could not find '" + directoryName +
                "' directory from model path: " + modelDir.getAbsolutePath());
        }
        File dataFile = new File(dir, fileName);
        if (dataFile == null) {
            throw new RuntimeException("Could not find '" + fileName +
                "' file in directory: " + dir.getAbsolutePath());
        }
        return dataFile;
    }

    protected void msg(String msg) {
        getSimulator().modelMessage(msg);
    }

    // Phase display
    public boolean phaseIs(String phase) {
        return this.phase.equals(phase);
    }

    public String getPhase() {
        return phase;
    }

    public String[] getPhaseNames() {
        return new String[] { "s0", "s1" };
    }
}
