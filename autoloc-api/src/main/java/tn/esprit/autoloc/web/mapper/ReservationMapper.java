package tn.esprit.autoloc.web.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.StatutReservation;
import tn.esprit.autoloc.web.dto.ReservationRequestDTO;
import tn.esprit.autoloc.web.dto.ReservationResponseDTO;
import tn.esprit.autoloc.web.dto.ReservationWithContratRequestDTO;

@Component
@RequiredArgsConstructor
public class ReservationMapper {

    private final ContratMapper contratMapper;

    public ReservationResponseDTO toResponseDto(Reservation entity) {
        if (entity == null) {
            return null;
        }
        return ReservationResponseDTO.builder()
                .idReservation(entity.getIdReservation())
                .dateDebut(entity.getDateDebut())
                .dateFin(entity.getDateFin())
                .statut(entity.getStatut())
                .idClient(entity.getClient() != null ? entity.getClient().getIdClient() : null)
                .nomClient(entity.getClient() != null ? entity.getClient().getNom() : null)
                .prenomClient(entity.getClient() != null ? entity.getClient().getPrenom() : null)
                .idVehicule(entity.getVehicule() != null ? entity.getVehicule().getIdVehicule() : null)
                .immatriculationVehicule(entity.getVehicule() != null ? entity.getVehicule().getImmatriculation() : null)
                .contrat(contratMapper.toDto(entity.getContrat()))
                .build();
    }

    public Reservation toEntity(ReservationRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        Reservation entity = new Reservation();
        entity.setDateDebut(dto.getDateDebut());
        entity.setDateFin(dto.getDateFin());
        entity.setStatut(dto.getStatut());
        return entity;
    }

    public Reservation toEntity(ReservationWithContratRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        Reservation entity = new Reservation();
        entity.setDateDebut(dto.getDateDebut());
        entity.setDateFin(dto.getDateFin());
        entity.setStatut(dto.getStatut() != null ? dto.getStatut() : StatutReservation.EN_ATTENTE);
        return entity;
    }
}
