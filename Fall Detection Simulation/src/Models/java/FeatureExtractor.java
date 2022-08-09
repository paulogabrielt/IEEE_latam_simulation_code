/* Do not remove or modify this comment!  It is required for file identification!
DNL
platform:/resource/FDS/src/Models/dnl/FeatureExtractor.dnl
-1564792728
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
public class FeatureExtractor extends AtomicModelImpl implements PhaseBased,
    StateVariableBased {
    private static final long serialVersionUID = 1L;

    //ID:SVAR:0
    private static final int ID_WEARABLE = 0;

    //ENDID
    //ID:SVAR:1
    private static final int ID_NONWEARABLE = 1;

    //ENDID
    //ID:SVAR:2
    private static final int ID_HUMANDATA = 2;

    // Declare state variables
    private PropertyChangeSupport propertyChangeSupport =
        new PropertyChangeSupport(this);
    protected WearableData wearable;
    protected NonWearableData nonWearable;
    protected HumanData humanData = new HumanData();

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
    //ID:INP:1
    public final Port<NonWearableData> inNonWearableData =
        addInputPort("inNonWearableData", NonWearableData.class);

    //ENDID
    // End input ports

    // Output ports
    //ID:OUTP:0
    public final Port<HumanData> outHumanData =
        addOutputPort("outHumanData", HumanData.class);

    //ENDID
    // End output ports
    protected SimulationOptionsImpl options = new SimulationOptionsImpl();
    protected double currentTime;

    // This variable is just here so we can use @SuppressWarnings("unused")
    private final int unusedIntVariableForWarnings = 0;

    public FeatureExtractor() {
        this("FeatureExtractor");
    }

    public FeatureExtractor(String name) {
        this(name, null);
    }

    public FeatureExtractor(String name, Simulator simulator) {
        super(name, simulator);
    }

    public void initialize() {
        super.initialize();

        currentTime = 0;

        // Default state variable initialization
        humanData = new HumanData();

        passivateIn("passive");

        // Initialize Variables
        //ID:INIT
        humanData.setWearableValue(-1.0f);
        humanData.setNonWearableValue(-1.0f);

        //ENDID
        // End initialize variables
    }

    @Override
    public void internalTransition() {
        currentTime += sigma;

        if (phaseIs("s1")) {
            getSimulator().modelMessage("Internal transition from s1");

            //ID:TRA:s1
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
            if (input.hasMessages(inNonWearableData)) {
                ArrayList<Message<NonWearableData>> messageList =
                    inNonWearableData.getMessages(input);

                holdIn("s1", 1.0);
                // Fire state and port specific external transition functions
                //ID:EXT:passive:inNonWearableData
                nonWearable = (NonWearableData) messageList.get(0).getData();
                assert (nonWearable != null);
                humanData.setNonWearableValue(nonWearable.getValue());
                humanData.setWearableValue(-1.0f);

                //ENDID
                // End external event code
                return;
            }
            if (input.hasMessages(inWearableData)) {
                ArrayList<Message<WearableData>> messageList =
                    inWearableData.getMessages(input);

                holdIn("s1", 1.0);
                // Fire state and port specific external transition functions
                //ID:EXT:passive:inWearableData
                wearable = (WearableData) messageList.get(0).getData();
                assert (wearable != null);
                humanData.setWearableValue(wearable.getValue());
                humanData.setNonWearableValue(-1.0f);

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
            output.add(outHumanData, humanData);

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
        FeatureExtractor model = new FeatureExtractor();
        model.options = options;

        if (options.isDisableViewer()) { // Command line output only
            Simulation sim =
                new SimulationImpl("FeatureExtractor Simulation", model, options);
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

    // Getter/setter for nonWearable
    public void setNonWearable(NonWearableData nonWearable) {
        propertyChangeSupport.firePropertyChange("nonWearable",
            this.nonWearable, this.nonWearable = nonWearable);
    }

    public NonWearableData getNonWearable() {
        return this.nonWearable;
    }

    // End getter/setter for nonWearable

    // Getter/setter for humanData
    public void setHumanData(HumanData humanData) {
        propertyChangeSupport.firePropertyChange("humanData", this.humanData,
            this.humanData = humanData);
    }

    public HumanData getHumanData() {
        return this.humanData;
    }

    // End getter/setter for humanData

    // State variables
    public String[] getStateVariableNames() {
        return new String[] { "wearable", "nonWearable", "humanData" };
    }

    public Object[] getStateVariableValues() {
        return new Object[] { wearable, nonWearable, humanData };
    }

    public Class<?>[] getStateVariableTypes() {
        return new Class<?>[] {
            WearableData.class, NonWearableData.class, HumanData.class
        };
    }

    public void setStateVariableValue(int index, Object value) {
        switch (index) {

            case ID_WEARABLE:
                setWearable((WearableData) value);
                return;

            case ID_NONWEARABLE:
                setNonWearable((NonWearableData) value);
                return;

            case ID_HUMANDATA:
                setHumanData((HumanData) value);
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
            dirUri = FeatureExtractor.class.getResource(".").toURI();
            dir = new File(dirUri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new RuntimeException(
                "Could not find Models directory. Invalid model URL: " +
                FeatureExtractor.class.getResource(".").toString());
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
        return new String[] { "passive", "s1" };
    }
}
