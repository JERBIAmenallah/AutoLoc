package tn.esprit.autoloc.web.mapper;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.web.dto.ClientDTO;

@Component
public class ClientMapper {

    public ClientDTO toDto(Client entity) {
        if (entity == null) {
            return null;
        }
        return ClientDTO.builder()
                .idClient(entity.getIdClient())
                .nom(entity.getNom())
                .prenom(entity.getPrenom())
                .email(entity.getEmail())
                .telephone(entity.getTelephone())
                .numPermis(entity.getNumPermis())
                .dateInscription(entity.getDateInscription())
                .build();
    }

    public Client toEntity(ClientDTO dto) {
        if (dto == null) {
            return null;
        }
        Client entity = new Client();
        entity.setIdClient(dto.getIdClient());
        entity.setNom(dto.getNom());
        entity.setPrenom(dto.getPrenom());
        entity.setEmail(dto.getEmail());
        entity.setTelephone(dto.getTelephone());
        entity.setNumPermis(dto.getNumPermis());
        entity.setDateInscription(dto.getDateInscription());
        return entity;
    }
}
