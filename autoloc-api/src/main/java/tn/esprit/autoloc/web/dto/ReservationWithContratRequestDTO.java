package tn.esprit.autoloc.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.autoloc.domain.StatutReservation;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationWithContratRequestDTO {

    @NotNull(message = "La date de début est obligatoire")
    private LocalDate dateDebut;

    @NotNull(message = "La date de fin est obligatoire")
    private LocalDate dateFin;

    private StatutReservation statut;

    private Long idClient;

    private Long idVehicule;

    @NotNull(message = "Le contrat associé est obligatoire")
    @Valid
    private ContratDTO contrat;
}
