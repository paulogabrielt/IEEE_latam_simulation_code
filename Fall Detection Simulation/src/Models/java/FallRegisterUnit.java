/* Do not remove or modify this comment!  It is required for file identification!
DNL
platform:/resource/FDS/src/Models/dnl/DataEvaluation.dnl
 Do not remove or modify this comment!  It is required for file identification! */
package Models.java;

import java.io.Serializable;

public class FallRegisterUnit implements Serializable {
    private static final long serialVersionUID = 1L;

    //ID:VAR:FallRegisterUnit:0
    String state;

    //ENDIF
    //ID:VAR:FallRegisterUnit:1
    boolean undetected;

    //ENDIF
    //ID:VAR:FallRegisterUnit:2
    boolean bathroomFlag;

    //ENDIF
    public FallRegisterUnit() {
    }

    public FallRegisterUnit(String state, boolean undetected,
        boolean bathroomFlag) {
        this.state = state;
        this.undetected = undetected;
        this.bathroomFlag = bathroomFlag;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return this.state;
    }

    public void setUndetected(boolean undetected) {
        this.undetected = undetected;
    }

    public boolean isUndetected() {
        return this.undetected;
    }

    public void setBathroomFlag(boolean bathroomFlag) {
        this.bathroomFlag = bathroomFlag;
    }

    public boolean isBathroomFlag() {
        return this.bathroomFlag;
    }

    public String toString() {
        String str = "FallRegisterUnit";
        str += "\n\tstate: " + this.state;
        str += "\n\tundetected: " + this.undetected;
        str += "\n\tbathroomFlag: " + this.bathroomFlag;
        return str;
    }
}
