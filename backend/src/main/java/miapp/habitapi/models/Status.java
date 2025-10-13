package miapp.habitapi.models;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Status {
    DONE,
    PARTIALLY,
    NOT_DONE,
    UNDEFINED;
    
    @JsonValue
    public String toLowerCase() {
        return this.name().toLowerCase();
    }
    
}

