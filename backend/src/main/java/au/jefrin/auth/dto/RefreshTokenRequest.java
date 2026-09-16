package au.jefrin.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RefreshTokenRequest {
    private String refreshToken;
}

