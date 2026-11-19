package tn.esprit.autoloc.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceDTO {

    private Long idMaintenance;

    @NotNull(message = "La date de début de maintenance est obligatoire")
    private LocalDate dateDebut;

    private LocalDate dateFin;

    @Size(max = 500, message = "La description ne doit pas dépasser 500 caractères")
    private String description;

    private Long idVehicule;
}
