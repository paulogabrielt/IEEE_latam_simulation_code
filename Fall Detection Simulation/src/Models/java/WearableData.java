/* Do not remove or modify this comment!  It is required for file identification!
DNL
platform:/resource/FDS/src/Models/dnl/HumanBody.dnl
 Do not remove or modify this comment!  It is required for file identification! */
package Models.java;

import java.io.Serializable;

public class WearableData implements Serializable {
    private static final long serialVersionUID = 1L;

    //ID:VAR:WearableData:0
    float value;

    //ENDIF
    public WearableData() {
    }

    public WearableData(float value) {
        this.value = value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public float getValue() {
        return this.value;
    }

    public String toString() {
        String str = "WearableData";
        str += "\n\tvalue: " + this.value;
        return str;
    }
}
