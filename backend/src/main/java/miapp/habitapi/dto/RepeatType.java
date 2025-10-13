package miapp.habitapi.dto;

import com.fasterxml.jackson.annotation.JsonValue;

public enum RepeatType {
 ONCE, DAILY, WEEKLY, MONTHLY;
 
    @JsonValue
    public String toLowerCase() {
        return this.name().toLowerCase();
    }
    
}

