package tn.esprit.autoloc.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IContratRepository;
import tn.esprit.autoloc.repository.IReservationRepository;
import tn.esprit.autoloc.web.dto.ContratDTO;
import tn.esprit.autoloc.web.mapper.ContratMapper;

@Service
@RequiredArgsConstructor
public class ContratServiceImpl implements IContratService {

    private final IContratRepository contratRepository;
    private final IReservationRepository reservationRepository;
    private final ContratMapper contratMapper;

    @Override
    @Transactional
    public ContratDTO create(ContratDTO dto) {
        if (dto.getIdContrat() != null) {
            throw new IllegalArgumentException("Un nouveau contrat ne doit pas avoir d'identifiant");
        }
        if (dto.getMontantTotal() != null && dto.getMontantTotal().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Le montant total ne peut pas être négatif");
        }
        Contrat contrat = contratMapper.toEntity(dto);
        if (contrat.getPaiements() != null) {
            contrat.getPaiements().forEach(p -> p.setContrat(contrat));
        }
        Contrat saved = contratRepository.save(contrat);
        return contratMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ContratDTO findById(Long id) {
        return contratRepository.findById(id)
                .map(contratMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Contrat", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContratDTO> findAll() {
        return contratRepository.findAll().stream()
                .map(contratMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public ContratDTO update(Long id, ContratDTO dto) {
        Contrat existant = contratRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contrat", id));

        if (dto.getMontantTotal() != null && dto.getMontantTotal().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Le montant total ne peut pas être négatif");
        }
        existant.setDateSignature(dto.getDateSignature());
        existant.setMontantTotal(dto.getMontantTotal());
        existant.setValide(dto.getValide());

        Contrat updated = contratRepository.save(existant);
        return contratMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!contratRepository.existsById(id)) {
            throw new ResourceNotFoundException("Contrat", id);
        }
        contratRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ContratDTO affecterContratAReservation(LocalDate dateSignature, LocalDate dateDebut, LocalDate dateFin) {
        if (dateSignature == null || dateDebut == null || dateFin == null) {
            throw new IllegalArgumentException("Les dates de signature, début et fin sont obligatoires");
        }
        if (dateFin.isBefore(dateDebut)) {
            throw new IllegalArgumentException("La date de fin ne peut pas être antérieure à la date de début");
        }

        List<Contrat> contrats = contratRepository.findByDateSignature(dateSignature);
        if (contrats.isEmpty()) {
            throw new ResourceNotFoundException(
                    "Contrat introuvable pour la date de signature : " + dateSignature);
        }
        Contrat contrat = contrats.stream()
                .filter(c -> c.getReservation() == null)
                .findFirst()
                .orElse(contrats.get(0));

        List<Reservation> reservations = reservationRepository.findByDateDebutAndDateFin(dateDebut, dateFin);
        if (reservations.isEmpty()) {
            throw new ResourceNotFoundException(
                    "Réservation introuvable pour la période du " + dateDebut + " au " + dateFin);
        }
        Reservation reservation = reservations.stream()
                .filter(r -> r.getContrat() == null)
                .findFirst()
                .orElse(reservations.get(0));

        if (reservation.getContrat() != null && !reservation.getContrat().getIdContrat().equals(contrat.getIdContrat())) {
            throw new IllegalArgumentException("Cette réservation est déjà liée à un autre contrat");
        }

        contrat.setReservation(reservation);
        reservation.setContrat(contrat);
        Contrat saved = contratRepository.save(contrat);
        return contratMapper.toDto(saved);
    }
}
