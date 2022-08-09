/* Do not remove or modify this comment!  It is required for file identification!
DNL
platform:/resource/FDS/src/Models/dnl/HumanBody.dnl
-1409244730
 Do not remove or modify this comment!  It is required for file identification! */
package Models.java;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

// Custom library code
//ID:LIB:0
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.ArrayList;
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
public class HumanBody extends AtomicModelImpl implements PhaseBased,
    StateVariableBased {
    private static final long serialVersionUID = 1L;

    //ID:SVAR:0
    private static final int ID_WEARABLE = 0;

    //ENDID
    //ID:SVAR:1
    private static final int ID_NONWEARABLE = 1;

    //ENDID
    //ID:SVAR:2
    private static final int ID_CSVFILE = 2;

    //ENDID
    //ID:SVAR:3
    private static final int ID_BR = 3;

    //ENDID
    //ID:SVAR:4
    private static final int ID_CSVDIVISOR = 4;

    //ENDID
    //ID:SVAR:5
    private static final int ID_COUNTER = 5;

    //ENDID
    //ID:SVAR:6
    private static final int ID_RAND = 6;

    //ENDID
    //ID:SVAR:7
    private static final int ID_WEARABLEPERC = 7;

    // Declare state variables
    private PropertyChangeSupport propertyChangeSupport =
        new PropertyChangeSupport(this);
    protected WearableData wearable = new WearableData();
    protected NonWearableData nonWearable = new NonWearableData();
    protected String csvFile = "/home/brlebtag/Documentos/Mestrado/input.csv";
    protected BufferedReader br;
    protected String csvDivisor = ",";
    protected int counter = 1;
    protected Random rand;
    protected double wearablePerc = 91;

    //ENDID
    String phase = "s0";
    String previousPhase = null;
    Double sigma = 1.0;
    Double previousSigma = Double.NaN;

    // End state variables

    // Input ports
    // End input ports

    // Output ports
    //ID:OUTP:0
    public final Port<WearableData> outWearableData =
        addOutputPort("outWearableData", WearableData.class);

    //ENDID
    //ID:OUTP:1
    public final Port<NonWearableData> outNonWearableData =
        addOutputPort("outNonWearableData", NonWearableData.class);

    //ENDID
    // End output ports
    protected SimulationOptionsImpl options = new SimulationOptionsImpl();
    protected double currentTime;

    // This variable is just here so we can use @SuppressWarnings("unused")
    private final int unusedIntVariableForWarnings = 0;

    public HumanBody() {
        this("HumanBody");
    }

    public HumanBody(String name) {
        this(name, null);
    }

    public HumanBody(String name, Simulator simulator) {
        super(name, simulator);
    }

    public void initialize() {
        super.initialize();

        currentTime = 0;

        // Default state variable initialization
        wearable = new WearableData();
        nonWearable = new NonWearableData();
        csvFile = "/home/brlebtag/Documentos/Mestrado/input.csv";
        csvDivisor = ",";
        counter = 1;
        wearablePerc = 91;

        holdIn("s0", 1.0);

        // Initialize Variables
        //ID:INIT
        rand = new Random();
        try {
            br = new BufferedReader(new FileReader(csvFile));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //ENDID
        // End initialize variables
    }

    @Override
    public void internalTransition() {
        currentTime += sigma;

        if (phaseIs("s0")) {
            getSimulator().modelMessage("Internal transition from s0");

            //ID:TRA:s0
            holdIn("s0", 1.0);

            //ENDID
            // Internal event code
            //ID:INT:s0
            try {

                //Tries to read a line in the dataset
                String line = br.readLine();

                if (line == null || line == "") {
                    System.out.println("end");
                    passivateIn("passive");
                    if (br != null) {
                        try {
                            br.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return;
                }

                String[] column = line.split(csvDivisor);

                float value = Float.parseFloat(column[0]);

                boolean _isWearable = isWearable(wearablePerc);

                System.out.println("-----------> IsWearable? " +
                    (_isWearable ? "true" : "false"));

                if (_isWearable) {
                    wearable.setValue(value);
                    nonWearable.setValue((float) -1.0);
                    holdIn("s1", 1.0);
                } else {
                    nonWearable.setValue(value);
                    wearable.setValue((float) -1.0);
                    holdIn("s2", 1.0);
                }
            } catch (Exception e) {
                System.out.println("Error end");
                passivateIn("passive");
                e.printStackTrace();
            }

            //ENDID
            // End internal event code
            return;
        }
        if (phaseIs("s1")) {
            getSimulator().modelMessage("Internal transition from s1");

            //ID:TRA:s1
            holdIn("wait", 500.0);

            //ENDID
            return;
        }
        if (phaseIs("s2")) {
            getSimulator().modelMessage("Internal transition from s2");

            //ID:TRA:s2
            holdIn("wait", 500.0);

            //ENDID
            return;
        }
        if (phaseIs("wait")) {
            getSimulator().modelMessage("Internal transition from wait");

            //ID:TRA:wait
            holdIn("s0", 1.0);

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
            output.add(outWearableData, wearable);

            //ENDID
            // End output event code
        }
        if (phaseIs("s2")) {
            // Output event code
            //ID:OUT:s2
            output.add(outNonWearableData, nonWearable);

            //ENDID
            // End output event code
        }
        return output;
    }

    // Custom function definitions

    //ID:CUST:0
    boolean isWearable(double maxPercentage) {
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
        HumanBody model = new HumanBody();
        model.options = options;

        if (options.isDisableViewer()) { // Command line output only
            Simulation sim =
                new SimulationImpl("HumanBody Simulation", model, options);
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

    // Getter/setter for csvFile
    public void setCsvFile(String csvFile) {
        propertyChangeSupport.firePropertyChange("csvFile", this.csvFile,
            this.csvFile = csvFile);
    }

    public String getCsvFile() {
        return this.csvFile;
    }

    // End getter/setter for csvFile

    // Getter/setter for br
    public void setBr(BufferedReader br) {
        propertyChangeSupport.firePropertyChange("br", this.br, this.br = br);
    }

    public BufferedReader getBr() {
        return this.br;
    }

    // End getter/setter for br

    // Getter/setter for csvDivisor
    public void setCsvDivisor(String csvDivisor) {
        propertyChangeSupport.firePropertyChange("csvDivisor", this.csvDivisor,
            this.csvDivisor = csvDivisor);
    }

    public String getCsvDivisor() {
        return this.csvDivisor;
    }

    // End getter/setter for csvDivisor

    // Getter/setter for counter
    public void setCounter(int counter) {
        propertyChangeSupport.firePropertyChange("counter", this.counter,
            this.counter = counter);
    }

    public int getCounter() {
        return this.counter;
    }

    // End getter/setter for counter

    // Getter/setter for rand
    public void setRand(Random rand) {
        propertyChangeSupport.firePropertyChange("rand", this.rand,
            this.rand = rand);
    }

    public Random getRand() {
        return this.rand;
    }

    // End getter/setter for rand

    // Getter/setter for wearablePerc
    public void setWearablePerc(double wearablePerc) {
        propertyChangeSupport.firePropertyChange("wearablePerc",
            this.wearablePerc, this.wearablePerc = wearablePerc);
    }

    public double getWearablePerc() {
        return this.wearablePerc;
    }

    // End getter/setter for wearablePerc

    // State variables
    public String[] getStateVariableNames() {
        return new String[] {
            "wearable", "nonWearable", "csvFile", "br", "csvDivisor", "counter",
            "rand", "wearablePerc"
        };
    }

    public Object[] getStateVariableValues() {
        return new Object[] {
            wearable, nonWearable, csvFile, br, csvDivisor, counter, rand,
            wearablePerc
        };
    }

    public Class<?>[] getStateVariableTypes() {
        return new Class<?>[] {
            WearableData.class, NonWearableData.class, String.class,
            BufferedReader.class, String.class, Integer.class, Random.class,
            Double.class
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

            case ID_CSVFILE:
                setCsvFile((String) value);
                return;

            case ID_BR:
                setBr((BufferedReader) value);
                return;

            case ID_CSVDIVISOR:
                setCsvDivisor((String) value);
                return;

            case ID_COUNTER:
                setCounter((Integer) value);
                return;

            case ID_RAND:
                setRand((Random) value);
                return;

            case ID_WEARABLEPERC:
                setWearablePerc((Double) value);
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
            dirUri = HumanBody.class.getResource(".").toURI();
            dir = new File(dirUri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new RuntimeException(
                "Could not find Models directory. Invalid model URL: " +
                HumanBody.class.getResource(".").toString());
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
        return new String[] { "s0", "s1", "s2", "wait", "passive" };
    }
}
