/* Do not remove or modify this comment!  It is required for file identification!
DNL
platform:/resource/FDS/src/Models/dnl/FallRegister.dnl
1481744236
 Do not remove or modify this comment!  It is required for file identification! */
package Models.java;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.io.File;

// Custom library code
//ID:LIB:0
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
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

//ENDID
// End custom library code
@SuppressWarnings("unused")
public class FallRegister extends AtomicModelImpl implements PhaseBased,
    StateVariableBased {
    private static final long serialVersionUID = 1L;

    //ID:SVAR:0
    private static final int ID_FALLUNIT = 0;

    //ENDID
    //ID:SVAR:1
    private static final int ID_CSVFILE = 1;

    //ENDID
    //ID:SVAR:2
    private static final int ID_CSVOUTPUTFILE = 2;

    //ENDID
    //ID:SVAR:3
    private static final int ID_WRITER = 3;

    //ENDID
    //ID:SVAR:4
    private static final int ID_CSVWRITER = 4;

    // Declare state variables
    private PropertyChangeSupport propertyChangeSupport =
        new PropertyChangeSupport(this);
    protected FallRegisterUnit fallUnit;
    protected String csvFile = "/home/brlebtag/Documentos/Mestrado/output.csv";
    protected File csvOutputFile;
    protected PrintWriter writer;
    protected FileWriter csvWriter;

    //ENDID
    String phase = "s0";
    String previousPhase = null;
    Double sigma = Double.POSITIVE_INFINITY;
    Double previousSigma = Double.NaN;

    // End state variables

    // Input ports
    //ID:INP:0
    public final Port<FallRegisterUnit> inFallRegisterUnit =
        addInputPort("inFallRegisterUnit", FallRegisterUnit.class);

    //ENDID
    // End input ports

    // Output ports
    // End output ports
    protected SimulationOptionsImpl options = new SimulationOptionsImpl();
    protected double currentTime;

    // This variable is just here so we can use @SuppressWarnings("unused")
    private final int unusedIntVariableForWarnings = 0;

    public FallRegister() {
        this("FallRegister");
    }

    public FallRegister(String name) {
        this(name, null);
    }

    public FallRegister(String name, Simulator simulator) {
        super(name, simulator);
    }

    public void initialize() {
        super.initialize();

        currentTime = 0;

        // Default state variable initialization
        csvFile = "/home/brlebtag/Documentos/Mestrado/output.csv";

        passivateIn("s0");

    }

    @Override
    public void internalTransition() {
        currentTime += sigma;

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
            if (input.hasMessages(inFallRegisterUnit)) {
                ArrayList<Message<FallRegisterUnit>> messageList =
                    inFallRegisterUnit.getMessages(input);

                passivateIn("s0");
                // Fire state and port specific external transition functions
                //ID:EXT:s0:inFallRegisterUnit
                fallUnit = (FallRegisterUnit) messageList.get(0).getData();
                assert (fallUnit != null);

                try {
                    csvWriter = new FileWriter(csvFile, true);
                    csvWriter.append(fallUnit.getState() + " ; " +
                        fallUnit.isUndetected() + " ; " +
                        fallUnit.isBathroomFlag() + " ;");
                    csvWriter.append("\n");
                    csvWriter.flush();
                    csvWriter.close();

                } catch (Exception e) {
                    System.out.println(e.getMessage());
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
        FallRegister model = new FallRegister();
        model.options = options;

        if (options.isDisableViewer()) { // Command line output only
            Simulation sim =
                new SimulationImpl("FallRegister Simulation", model, options);
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

    // Getter/setter for fallUnit
    public void setFallUnit(FallRegisterUnit fallUnit) {
        propertyChangeSupport.firePropertyChange("fallUnit", this.fallUnit,
            this.fallUnit = fallUnit);
    }

    public FallRegisterUnit getFallUnit() {
        return this.fallUnit;
    }

    // End getter/setter for fallUnit

    // Getter/setter for csvFile
    public void setCsvFile(String csvFile) {
        propertyChangeSupport.firePropertyChange("csvFile", this.csvFile,
            this.csvFile = csvFile);
    }

    public String getCsvFile() {
        return this.csvFile;
    }

    // End getter/setter for csvFile

    // Getter/setter for csvOutputFile
    public void setCsvOutputFile(File csvOutputFile) {
        propertyChangeSupport.firePropertyChange("csvOutputFile",
            this.csvOutputFile, this.csvOutputFile = csvOutputFile);
    }

    public File getCsvOutputFile() {
        return this.csvOutputFile;
    }

    // End getter/setter for csvOutputFile

    // Getter/setter for writer
    public void setWriter(PrintWriter writer) {
        propertyChangeSupport.firePropertyChange("writer", this.writer,
            this.writer = writer);
    }

    public PrintWriter getWriter() {
        return this.writer;
    }

    // End getter/setter for writer

    // Getter/setter for csvWriter
    public void setCsvWriter(FileWriter csvWriter) {
        propertyChangeSupport.firePropertyChange("csvWriter", this.csvWriter,
            this.csvWriter = csvWriter);
    }

    public FileWriter getCsvWriter() {
        return this.csvWriter;
    }

    // End getter/setter for csvWriter

    // State variables
    public String[] getStateVariableNames() {
        return new String[] {
            "fallUnit", "csvFile", "csvOutputFile", "writer", "csvWriter"
        };
    }

    public Object[] getStateVariableValues() {
        return new Object[] { fallUnit, csvFile, csvOutputFile, writer, csvWriter };
    }

    public Class<?>[] getStateVariableTypes() {
        return new Class<?>[] {
            FallRegisterUnit.class, String.class, File.class, PrintWriter.class,
            FileWriter.class
        };
    }

    public void setStateVariableValue(int index, Object value) {
        switch (index) {

            case ID_FALLUNIT:
                setFallUnit((FallRegisterUnit) value);
                return;

            case ID_CSVFILE:
                setCsvFile((String) value);
                return;

            case ID_CSVOUTPUTFILE:
                setCsvOutputFile((File) value);
                return;

            case ID_WRITER:
                setWriter((PrintWriter) value);
                return;

            case ID_CSVWRITER:
                setCsvWriter((FileWriter) value);
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
            dirUri = FallRegister.class.getResource(".").toURI();
            dir = new File(dirUri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new RuntimeException(
                "Could not find Models directory. Invalid model URL: " +
                FallRegister.class.getResource(".").toString());
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
        return new String[] { "s0" };
    }
}
