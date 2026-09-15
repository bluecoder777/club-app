package au.jefrin.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Club {
    private Long id;
    private String name;
    private String description;
    private Timestamp dateOfCreation;
    private Long createdBy;
}

