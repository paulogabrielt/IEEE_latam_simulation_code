/* Do not remove or modify this comment!  It is required for file identification!
DNL
platform:/resource/FDS/src/Models/dnl/DataEvaluation.dnl
-278106149
 Do not remove or modify this comment!  It is required for file identification! */
package Models.java;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.io.File;
import java.io.Serializable;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.ArrayList;

// Custom library code
//ID:LIB:0
import java.util.Random;

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

//ENDID
// End custom library code
@SuppressWarnings("unused")
public class DataEvaluation extends AtomicModelImpl implements PhaseBased,
    StateVariableBased {
    private static final long serialVersionUID = 1L;

    //ID:SVAR:0
    private static final int ID_HUMANDATA = 0;

    //ENDID
    //ID:SVAR:1
    private static final int ID_FALLUNIT = 1;

    //ENDID
    //ID:SVAR:2
    private static final int ID_RAND = 2;

    //ENDID
    //ID:SVAR:3
    private static final int ID_FALLVALUE = 3;

    //ENDID
    //ID:SVAR:4
    private static final int ID_WEARABLEACCURACY = 4;

    //ENDID
    //ID:SVAR:5
    private static final int ID_NONWEARABLEACCURACY = 5;

    //ENDID
    //ID:SVAR:6
    private static final int ID_FALLOCCURRENCE = 6;

    // Declare state variables
    private PropertyChangeSupport propertyChangeSupport =
        new PropertyChangeSupport(this);
    protected HumanData humanData;
    protected FallRegisterUnit fallUnit = new FallRegisterUnit();
    protected Random rand = new Random();
    protected double fallValue = 3.0;
    protected double wearableAccuracy = 68.0;
    protected double nonWearableAccuracy = 79.91;
    protected FallOccurrence fallOccurrence = new FallOccurrence();

    //ENDID
    String phase = "s0";
    String previousPhase = null;
    Double sigma = Double.POSITIVE_INFINITY;
    Double previousSigma = Double.NaN;

    // End state variables

    // Input ports
    //ID:INP:0
    public final Port<HumanData> inHumanData =
        addInputPort("inHumanData", HumanData.class);

    //ENDID
    // End input ports

    // Output ports
    //ID:OUTP:0
    public final Port<FallRegisterUnit> outFallRegisterUnit =
        addOutputPort("outFallRegisterUnit", FallRegisterUnit.class);

    //ENDID
    //ID:OUTP:1
    public final Port<FallOccurrence> outFallOccurrence =
        addOutputPort("outFallOccurrence", FallOccurrence.class);

    //ENDID
    // End output ports
    protected SimulationOptionsImpl options = new SimulationOptionsImpl();
    protected double currentTime;

    // This variable is just here so we can use @SuppressWarnings("unused")
    private final int unusedIntVariableForWarnings = 0;

    public DataEvaluation() {
        this("DataEvaluation");
    }

    public DataEvaluation(String name) {
        this(name, null);
    }

    public DataEvaluation(String name, Simulator simulator) {
        super(name, simulator);
    }

    public void initialize() {
        super.initialize();

        currentTime = 0;

        // Default state variable initialization
        fallUnit = new FallRegisterUnit();
        rand = new Random();
        fallValue = 3.0;
        wearableAccuracy = 68.0;
        nonWearableAccuracy = 79.91;
        fallOccurrence = new FallOccurrence();

        passivateIn("s0");

    }

    @Override
    public void internalTransition() {
        currentTime += sigma;

        if (phaseIs("s1")) {
            getSimulator().modelMessage("Internal transition from s1");

            //ID:TRA:s1
            holdIn("s2", 1.0);

            //ENDID
            return;
        }
        if (phaseIs("s2")) {
            getSimulator().modelMessage("Internal transition from s2");

            //ID:TRA:s2
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
            if (input.hasMessages(inHumanData)) {
                ArrayList<Message<HumanData>> messageList =
                    inHumanData.getMessages(input);

                holdIn("s1", 1.0);
                // Fire state and port specific external transition functions
                //ID:EXT:s0:inHumanData

                //Reads the first message of the list
                humanData = (HumanData) messageList.get(0).getData();
                assert (humanData != null);

                fallUnit.setState("No Fall");
                fallUnit.setBathroomFlag(false);
                fallUnit.setUndetected(false);
                fallOccurrence.setValue("No Fall");

                float data = humanData.getWearableValue();

                // Identify a fall outside the bathroom
                if (data == fallValue) {
                    fallUnit.setState("Fall");
                    fallUnit.setBathroomFlag(false);
                    fallUnit.setUndetected(isUndetected(wearableAccuracy));
                    fallOccurrence.setValue("Fall");

                }

                data = humanData.getNonWearableValue();

                if (data == fallValue) {
                    fallUnit.setState("Fall");
                    fallUnit.setBathroomFlag(true);
                    fallUnit.setUndetected(isUndetected(nonWearableAccuracy));
                    fallOccurrence.setValue("Fall");
                } else if (data != -1) {
                    fallUnit.setBathroomFlag(true);
                }

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
            output.add(outFallRegisterUnit, fallUnit);

            //ENDID
            // End output event code
        }
        if (phaseIs("s2")) {
            // Output event code
            //ID:OUT:s2
            output.add(outFallOccurrence, fallOccurrence);

            //ENDID
            // End output event code
        }
        return output;
    }

    // Custom function definitions

    //ID:CUST:0
    boolean isUndetected(double maxPercentage) {
        double randomDouble = rand.nextDouble() * (100.0 - 0.0) + 0.0;

        return (randomDouble <= maxPercentage) ? true : false;
    }

    //ENDID

    // End custom function definitions
    public static void main(String[] args) {
        SimulationOptionsImpl options = new SimulationOptionsImpl(args, true);

        // Uncomment the following line to disable SimViewer for this model
        // options.setDisableViewer(true);

        // Uncomment the following line to disable plotting for this model
        // options.setDisablePlotting(true);
        DataEvaluation model = new DataEvaluation();
        model.options = options;

        if (options.isDisableViewer()) { // Command line output only
            Simulation sim =
                new SimulationImpl("DataEvaluation Simulation", model, options);
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

    // Getter/setter for humanData
    public void setHumanData(HumanData humanData) {
        propertyChangeSupport.firePropertyChange("humanData", this.humanData,
            this.humanData = humanData);
    }

    public HumanData getHumanData() {
        return this.humanData;
    }

    // End getter/setter for humanData

    // Getter/setter for fallUnit
    public void setFallUnit(FallRegisterUnit fallUnit) {
        propertyChangeSupport.firePropertyChange("fallUnit", this.fallUnit,
            this.fallUnit = fallUnit);
    }

    public FallRegisterUnit getFallUnit() {
        return this.fallUnit;
    }

    // End getter/setter for fallUnit

    // Getter/setter for rand
    public void setRand(Random rand) {
        propertyChangeSupport.firePropertyChange("rand", this.rand,
            this.rand = rand);
    }

    public Random getRand() {
        return this.rand;
    }

    // End getter/setter for rand

    // Getter/setter for fallValue
    public void setFallValue(double fallValue) {
        propertyChangeSupport.firePropertyChange("fallValue", this.fallValue,
            this.fallValue = fallValue);
    }

    public double getFallValue() {
        return this.fallValue;
    }

    // End getter/setter for fallValue

    // Getter/setter for wearableAccuracy
    public void setWearableAccuracy(double wearableAccuracy) {
        propertyChangeSupport.firePropertyChange("wearableAccuracy",
            this.wearableAccuracy, this.wearableAccuracy = wearableAccuracy);
    }

    public double getWearableAccuracy() {
        return this.wearableAccuracy;
    }

    // End getter/setter for wearableAccuracy

    // Getter/setter for nonWearableAccuracy
    public void setNonWearableAccuracy(double nonWearableAccuracy) {
        propertyChangeSupport.firePropertyChange("nonWearableAccuracy",
            this.nonWearableAccuracy,
            this.nonWearableAccuracy = nonWearableAccuracy);
    }

    public double getNonWearableAccuracy() {
        return this.nonWearableAccuracy;
    }

    // End getter/setter for nonWearableAccuracy

    // Getter/setter for fallOccurrence
    public void setFallOccurrence(FallOccurrence fallOccurrence) {
        propertyChangeSupport.firePropertyChange("fallOccurrence",
            this.fallOccurrence, this.fallOccurrence = fallOccurrence);
    }

    public FallOccurrence getFallOccurrence() {
        return this.fallOccurrence;
    }

    // End getter/setter for fallOccurrence

    // State variables
    public String[] getStateVariableNames() {
        return new String[] {
            "humanData", "fallUnit", "rand", "fallValue", "wearableAccuracy",
            "nonWearableAccuracy", "fallOccurrence"
        };
    }

    public Object[] getStateVariableValues() {
        return new Object[] {
            humanData, fallUnit, rand, fallValue, wearableAccuracy,
            nonWearableAccuracy, fallOccurrence
        };
    }

    public Class<?>[] getStateVariableTypes() {
        return new Class<?>[] {
            HumanData.class, FallRegisterUnit.class, Random.class, Double.class,
            Double.class, Double.class, FallOccurrence.class
        };
    }

    public void setStateVariableValue(int index, Object value) {
        switch (index) {

            case ID_HUMANDATA:
                setHumanData((HumanData) value);
                return;

            case ID_FALLUNIT:
                setFallUnit((FallRegisterUnit) value);
                return;

            case ID_RAND:
                setRand((Random) value);
                return;

            case ID_FALLVALUE:
                setFallValue((Double) value);
                return;

            case ID_WEARABLEACCURACY:
                setWearableAccuracy((Double) value);
                return;

            case ID_NONWEARABLEACCURACY:
                setNonWearableAccuracy((Double) value);
                return;

            case ID_FALLOCCURRENCE:
                setFallOccurrence((FallOccurrence) value);
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
            dirUri = DataEvaluation.class.getResource(".").toURI();
            dir = new File(dirUri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new RuntimeException(
                "Could not find Models directory. Invalid model URL: " +
                DataEvaluation.class.getResource(".").toString());
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
        return new String[] { "s0", "s1", "s2" };
    }
}
