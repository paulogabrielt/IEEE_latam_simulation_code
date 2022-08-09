/* Do not remove or modify this comment!  It is required for file identification!
DNL
platform:/resource/FDS/src/Models/dnl/Wearable.dnl
2065647293
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
public class Wearable extends AtomicModelImpl implements PhaseBased,
    StateVariableBased {
    private static final long serialVersionUID = 1L;

    //ID:SVAR:0
    private static final int ID_WEARABLE = 0;

    // Declare state variables
    private PropertyChangeSupport propertyChangeSupport =
        new PropertyChangeSupport(this);
    protected WearableData wearable;

    //ENDID
    String phase = "passive";
    String previousPhase = null;
    Double sigma = Double.POSITIVE_INFINITY;
    Double previousSigma = Double.NaN;

    // End state variables

    // Input ports
    //ID:INP:0
    public final Port<WearableData> inWearableData =
        addInputPort("inWearableData", WearableData.class);

    //ENDID
    // End input ports

    // Output ports
    //ID:OUTP:0
    public final Port<WearableData> outWearableData =
        addOutputPort("outWearableData", WearableData.class);

    //ENDID
    // End output ports
    protected SimulationOptionsImpl options = new SimulationOptionsImpl();
    protected double currentTime;

    // This variable is just here so we can use @SuppressWarnings("unused")
    private final int unusedIntVariableForWarnings = 0;

    public Wearable() {
        this("Wearable");
    }

    public Wearable(String name) {
        this(name, null);
    }

    public Wearable(String name, Simulator simulator) {
        super(name, simulator);
    }

    public void initialize() {
        super.initialize();

        currentTime = 0;

        passivateIn("passive");

    }

    @Override
    public void internalTransition() {
        currentTime += sigma;

        if (phaseIs("active")) {
            getSimulator().modelMessage("Internal transition from active");

            //ID:TRA:active
            passivateIn("passive");

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
        if (phaseIs("passive")) {
            if (input.hasMessages(inWearableData)) {
                ArrayList<Message<WearableData>> messageList =
                    inWearableData.getMessages(input);

                holdIn("active", 1.0);
                // Fire state and port specific external transition functions
                //ID:EXT:passive:inWearableData
                wearable = (WearableData) messageList.get(0).getData();
                assert (wearable != null);

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

        if (phaseIs("active")) {
            // Output event code
            //ID:OUT:active
            output.add(outWearableData, wearable);

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
        Wearable model = new Wearable();
        model.options = options;

        if (options.isDisableViewer()) { // Command line output only
            Simulation sim =
                new SimulationImpl("Wearable Simulation", model, options);
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

    // Getter/setter for wearable
    public void setWearable(WearableData wearable) {
        propertyChangeSupport.firePropertyChange("wearable", this.wearable,
            this.wearable = wearable);
    }

    public WearableData getWearable() {
        return this.wearable;
    }

    // End getter/setter for wearable

    // State variables
    public String[] getStateVariableNames() {
        return new String[] { "wearable" };
    }

    public Object[] getStateVariableValues() {
        return new Object[] { wearable };
    }

    public Class<?>[] getStateVariableTypes() {
        return new Class<?>[] { WearableData.class };
    }

    public void setStateVariableValue(int index, Object value) {
        switch (index) {

            case ID_WEARABLE:
                setWearable((WearableData) value);
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
            dirUri = Wearable.class.getResource(".").toURI();
            dir = new File(dirUri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new RuntimeException(
                "Could not find Models directory. Invalid model URL: " +
                Wearable.class.getResource(".").toString());
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
        return new String[] { "passive", "active" };
    }
}
