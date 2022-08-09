/* Do not remove or modify this comment!  It is required for file identification!
DNL
platform:/resource/FDS/src/Models/dnl/HumanBody.dnl
 Do not remove or modify this comment!  It is required for file identification! */
package Models.java;

import java.io.Serializable;

public class NonWearableData implements Serializable {
    private static final long serialVersionUID = 1L;

    //ID:VAR:NonWearableData:0
    float value;

    //ENDIF
    public NonWearableData() {
    }

    public NonWearableData(float value) {
        this.value = value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public float getValue() {
        return this.value;
    }

    public String toString() {
        String str = "NonWearableData";
        str += "\n\tvalue: " + this.value;
        return str;
    }
}
