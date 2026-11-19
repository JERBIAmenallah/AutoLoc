package tn.esprit.autoloc.web.mapper;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Maintenance;
import tn.esprit.autoloc.web.dto.MaintenanceDTO;

@Component
public class MaintenanceMapper {

    public MaintenanceDTO toDto(Maintenance entity) {
        if (entity == null) {
            return null;
        }
        return MaintenanceDTO.builder()
                .idMaintenance(entity.getIdMaintenance())
                .dateDebut(entity.getDateDebut())
                .dateFin(entity.getDateFin())
                .description(entity.getDescription())
                .idVehicule(entity.getVehicule() != null ? entity.getVehicule().getIdVehicule() : null)
                .build();
    }

    public Maintenance toEntity(MaintenanceDTO dto) {
        if (dto == null) {
            return null;
        }
        Maintenance entity = new Maintenance();
        entity.setIdMaintenance(dto.getIdMaintenance());
        entity.setDateDebut(dto.getDateDebut());
        entity.setDateFin(dto.getDateFin());
        entity.setDescription(dto.getDescription());
        return entity;
    }
}
