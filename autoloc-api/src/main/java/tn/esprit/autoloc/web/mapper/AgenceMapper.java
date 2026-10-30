package tn.esprit.autoloc.web.mapper;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.web.dto.AgenceDTO;

@Component
public class AgenceMapper {

    public AgenceDTO toDto(Agence entity) {
        if (entity == null) {
            return null;
        }
        return AgenceDTO.builder()
                .idAgence(entity.getIdAgence())
                .nom(entity.getNom())
                .ville(entity.getVille())
                .adresse(entity.getAdresse())
                .telephone(entity.getTelephone())
                .build();
    }

    public Agence toEntity(AgenceDTO dto) {
        if (dto == null) {
            return null;
        }
        Agence entity = new Agence();
        entity.setIdAgence(dto.getIdAgence());
        entity.setNom(dto.getNom());
        entity.setVille(dto.getVille());
        entity.setAdresse(dto.getAdresse());
        entity.setTelephone(dto.getTelephone());
        return entity;
    }
}
