package au.jefrin.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateClubRequest {
    private String name;
    private String description;
    
    public boolean isValid() {
        return name != null && !name.trim().isEmpty();
    }
}

