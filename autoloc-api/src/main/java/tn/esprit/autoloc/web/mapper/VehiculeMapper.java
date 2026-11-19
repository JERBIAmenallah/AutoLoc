package tn.esprit.autoloc.web.mapper;

import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.web.dto.EquipementDTO;
import tn.esprit.autoloc.web.dto.MaintenanceDTO;
import tn.esprit.autoloc.web.dto.VehiculeDTO;

@Component
@RequiredArgsConstructor
public class VehiculeMapper {

    private final EquipementMapper equipementMapper;
    private final MaintenanceMapper maintenanceMapper;

    public VehiculeDTO toDto(Vehicule entity) {
        if (entity == null) {
            return null;
        }

        List<EquipementDTO> equipementsDto = entity.getEquipements() != null
                ? entity.getEquipements().stream().map(equipementMapper::toDto).toList()
                : Collections.emptyList();

        List<MaintenanceDTO> maintenancesDto = entity.getMaintenances() != null
                ? entity.getMaintenances().stream().map(maintenanceMapper::toDto).toList()
                : Collections.emptyList();

        return VehiculeDTO.builder()
                .idVehicule(entity.getIdVehicule())
                .immatriculation(entity.getImmatriculation())
                .marque(entity.getMarque())
                .modele(entity.getModele())
                .categorie(entity.getCategorie())
                .tarifJournalier(entity.getTarifJournalier())
                .statut(entity.getStatut())
                .idAgence(entity.getAgence() != null ? entity.getAgence().getIdAgence() : null)
                .equipements(equipementsDto)
                .maintenances(maintenancesDto)
                .build();
    }

    public Vehicule toEntity(VehiculeDTO dto) {
        if (dto == null) {
            return null;
        }
        Vehicule entity = new Vehicule();
        entity.setIdVehicule(dto.getIdVehicule());
        entity.setImmatriculation(dto.getImmatriculation());
        entity.setMarque(dto.getMarque());
        entity.setModele(dto.getModele());
        entity.setCategorie(dto.getCategorie());
        entity.setTarifJournalier(dto.getTarifJournalier());
        entity.setStatut(dto.getStatut());
        return entity;
    }
}
