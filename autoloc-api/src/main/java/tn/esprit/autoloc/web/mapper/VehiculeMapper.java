package tn.esprit.autoloc.web.mapper;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.web.dto.VehiculeDTO;

@Component
public class VehiculeMapper {

    public VehiculeDTO toDto(Vehicule entity) {
        if (entity == null) {
            return null;
        }
        return VehiculeDTO.builder()
                .idVehicule(entity.getIdVehicule())
                .immatriculation(entity.getImmatriculation())
                .marque(entity.getMarque())
                .modele(entity.getModele())
                .categorie(entity.getCategorie())
                .tarifJournalier(entity.getTarifJournalier())
                .statut(entity.getStatut())
                .idAgence(entity.getAgence() != null ? entity.getAgence().getIdAgence() : null)
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
