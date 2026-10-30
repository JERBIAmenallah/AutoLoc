package tn.esprit.autoloc.web.mapper;

import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Paiement;
import tn.esprit.autoloc.web.dto.ContratDTO;
import tn.esprit.autoloc.web.dto.PaiementDTO;

@Component
@RequiredArgsConstructor
public class ContratMapper {

    private final PaiementMapper paiementMapper;

    public ContratDTO toDto(Contrat entity) {
        if (entity == null) {
            return null;
        }
        List<PaiementDTO> paiementsDto = entity.getPaiements() != null
                ? entity.getPaiements().stream().map(paiementMapper::toDto).toList()
                : Collections.emptyList();

        return ContratDTO.builder()
                .idContrat(entity.getIdContrat())
                .dateSignature(entity.getDateSignature())
                .montantTotal(entity.getMontantTotal())
                .valide(entity.getValide())
                .idReservation(entity.getReservation() != null ? entity.getReservation().getIdReservation() : null)
                .paiements(paiementsDto)
                .build();
    }

    public Contrat toEntity(ContratDTO dto) {
        if (dto == null) {
            return null;
        }
        Contrat entity = new Contrat();
        entity.setIdContrat(dto.getIdContrat());
        entity.setDateSignature(dto.getDateSignature());
        entity.setMontantTotal(dto.getMontantTotal());
        entity.setValide(dto.getValide());

        if (dto.getPaiements() != null) {
            List<Paiement> paiements = dto.getPaiements().stream()
                    .map(pDto -> {
                        Paiement p = paiementMapper.toEntity(pDto);
                        p.setContrat(entity);
                        return p;
                    }).toList();
            entity.setPaiements(paiements);
        }
        return entity;
    }
}
