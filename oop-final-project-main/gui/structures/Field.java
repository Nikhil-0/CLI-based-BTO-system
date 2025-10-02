package gui.structures;
public class Field {
    private String fieldKey;
    private String fieldValue;

    public Field(String fieldKey, String fieldValue) {
        this.fieldKey = fieldKey;
        this.fieldValue = fieldValue;
    }

    public String getFieldKey() {
        if(fieldKey != null){
            return fieldKey;
        } else{
            return "";
        }
        
    }

    public String getFieldValue() {
        if(fieldValue != null){
            return fieldValue;
        } else{
            return "";
        }
    }

    public void setFieldKey(String fieldKey) {
        this.fieldKey = fieldKey;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }
}