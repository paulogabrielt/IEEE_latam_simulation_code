/* Do not remove or modify this comment!  It is required for file identification!
DNL
platform:/resource/FDS/src/Models/dnl/FeatureExtractor.dnl
 Do not remove or modify this comment!  It is required for file identification! */
package Models.java;

import java.io.Serializable;

public class HumanData implements Serializable {
    private static final long serialVersionUID = 1L;

    //ID:VAR:HumanData:0
    float wearableValue;

    //ENDIF
    //ID:VAR:HumanData:1
    float nonWearableValue;

    //ENDIF
    public HumanData() {
    }

    public HumanData(float wearableValue, float nonWearableValue) {
        this.wearableValue = wearableValue;
        this.nonWearableValue = nonWearableValue;
    }

    public void setWearableValue(float wearableValue) {
        this.wearableValue = wearableValue;
    }

    public float getWearableValue() {
        return this.wearableValue;
    }

    public void setNonWearableValue(float nonWearableValue) {
        this.nonWearableValue = nonWearableValue;
    }

    public float getNonWearableValue() {
        return this.nonWearableValue;
    }

    public String toString() {
        String str = "HumanData";
        str += "\n\twearableValue: " + this.wearableValue;
        str += "\n\tnonWearableValue: " + this.nonWearableValue;
        return str;
    }
}
