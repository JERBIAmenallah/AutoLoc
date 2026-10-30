package tn.esprit.autoloc.web.mapper;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Paiement;
import tn.esprit.autoloc.web.dto.PaiementDTO;

@Component
public class PaiementMapper {

    public PaiementDTO toDto(Paiement entity) {
        if (entity == null) {
            return null;
        }
        return PaiementDTO.builder()
                .idPaiement(entity.getIdPaiement())
                .montant(entity.getMontant())
                .datePaiement(entity.getDatePaiement())
                .modePaiement(entity.getModePaiement())
                .idContrat(entity.getContrat() != null ? entity.getContrat().getIdContrat() : null)
                .build();
    }

    public Paiement toEntity(PaiementDTO dto) {
        if (dto == null) {
            return null;
        }
        Paiement entity = new Paiement();
        entity.setIdPaiement(dto.getIdPaiement());
        entity.setMontant(dto.getMontant());
        entity.setDatePaiement(dto.getDatePaiement());
        entity.setModePaiement(dto.getModePaiement());
        return entity;
    }
}
