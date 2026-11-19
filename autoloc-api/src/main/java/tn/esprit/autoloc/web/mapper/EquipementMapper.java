package tn.esprit.autoloc.web.mapper;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.web.dto.EquipementDTO;

@Component
public class EquipementMapper {

    public EquipementDTO toDto(Equipement entity) {
        if (entity == null) {
            return null;
        }
        return EquipementDTO.builder()
                .idEquipement(entity.getIdEquipement())
                .libelle(entity.getLibelle())
                .build();
    }

    public Equipement toEntity(EquipementDTO dto) {
        if (dto == null) {
            return null;
        }
        Equipement entity = new Equipement();
        entity.setIdEquipement(dto.getIdEquipement());
        entity.setLibelle(dto.getLibelle());
        return entity;
    }
}
