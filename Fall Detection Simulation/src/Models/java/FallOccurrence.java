/* Do not remove or modify this comment!  It is required for file identification!
DNL
platform:/resource/FDS/src/Models/dnl/DataEvaluation.dnl
 Do not remove or modify this comment!  It is required for file identification! */
package Models.java;

import java.io.Serializable;

public class FallOccurrence implements Serializable {
    private static final long serialVersionUID = 1L;

    //ID:VAR:FallOccurrence:0
    String value;

    //ENDIF
    public FallOccurrence() {
    }

    public FallOccurrence(String value) {
        this.value = value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public String toString() {
        String str = "FallOccurrence";
        str += "\n\tvalue: " + this.value;
        return str;
    }
}
