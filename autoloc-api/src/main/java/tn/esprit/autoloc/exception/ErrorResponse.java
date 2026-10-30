package tn.esprit.autoloc.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        int statut,
        String message,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime horodatage,
        Map<String, String> erreursValidation
) {
    public ErrorResponse(int statut, String message, LocalDateTime horodatage) {
        this(statut, message, horodatage, null);
    }
}
