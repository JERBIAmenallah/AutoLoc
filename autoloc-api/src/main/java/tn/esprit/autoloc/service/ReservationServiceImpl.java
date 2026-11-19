package tn.esprit.autoloc.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IClientRepository;
import tn.esprit.autoloc.repository.IContratRepository;
import tn.esprit.autoloc.repository.IReservationRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;
import tn.esprit.autoloc.web.dto.ReservationRequestDTO;
import tn.esprit.autoloc.web.dto.ReservationResponseDTO;
import tn.esprit.autoloc.web.dto.ReservationWithContratRequestDTO;
import tn.esprit.autoloc.web.mapper.ContratMapper;
import tn.esprit.autoloc.web.mapper.ReservationMapper;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements IReservationService {

    private final IReservationRepository reservationRepository;
    private final IClientRepository clientRepository;
    private final IVehiculeRepository vehiculeRepository;
    private final IContratRepository contratRepository;
    private final ReservationMapper reservationMapper;
    private final ContratMapper contratMapper;

    @Override
    @Transactional
    public ReservationResponseDTO create(ReservationRequestDTO dto) {
        validerDates(dto.getDateDebut(), dto.getDateFin());
        Reservation reservation = reservationMapper.toEntity(dto);

        if (dto.getIdClient() != null) {
            Client client = clientRepository.findById(dto.getIdClient())
                    .orElseThrow(() -> new ResourceNotFoundException("Client", dto.getIdClient()));
            reservation.setClient(client);
        }
        if (dto.getIdVehicule() != null) {
            Vehicule vehicule = vehiculeRepository.findById(dto.getIdVehicule())
                    .orElseThrow(() -> new ResourceNotFoundException("Véhicule", dto.getIdVehicule()));
            reservation.setVehicule(vehicule);
        }

        Reservation saved = reservationRepository.save(reservation);
        return reservationMapper.toResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ReservationResponseDTO findById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation", id));
        return reservationMapper.toResponseDto(reservation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponseDTO> findAll() {
        return reservationRepository.findAll().stream()
                .map(reservationMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public ReservationResponseDTO update(Long id, ReservationRequestDTO dto) {
        validerDates(dto.getDateDebut(), dto.getDateFin());
        Reservation existant = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation", id));

        existant.setDateDebut(dto.getDateDebut());
        existant.setDateFin(dto.getDateFin());
        existant.setStatut(dto.getStatut());

        if (dto.getIdClient() != null) {
            Client client = clientRepository.findById(dto.getIdClient())
                    .orElseThrow(() -> new ResourceNotFoundException("Client", dto.getIdClient()));
            existant.setClient(client);
        }
        if (dto.getIdVehicule() != null) {
            Vehicule vehicule = vehiculeRepository.findById(dto.getIdVehicule())
                    .orElseThrow(() -> new ResourceNotFoundException("Véhicule", dto.getIdVehicule()));
            existant.setVehicule(vehicule);
        }

        Reservation updated = reservationRepository.save(existant);
        return reservationMapper.toResponseDto(updated);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Réservation", id);
        }
        reservationRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ReservationResponseDTO affecterReservationAClient(Long idReservation, String nom, String prenom) {
        if (nom == null || nom.isBlank() || prenom == null || prenom.isBlank()) {
            throw new IllegalArgumentException("Le nom et le prénom du client sont obligatoires");
        }

        Reservation reservation = reservationRepository.findById(idReservation)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation", idReservation));

        Client client = clientRepository.findFirstByNomIgnoreCaseAndPrenomIgnoreCase(nom.trim(), prenom.trim())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Client introuvable avec le nom " + nom + " et le prénom " + prenom));

        reservation.setClient(client);
        Reservation saved = reservationRepository.save(reservation);
        return reservationMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    public ReservationResponseDTO creerReservationEtContratAssocie(ReservationWithContratRequestDTO requestDTO) {
        if (requestDTO == null || requestDTO.getContrat() == null) {
            throw new IllegalArgumentException("Les données de la réservation et du contrat sont obligatoires");
        }
        validerDates(requestDTO.getDateDebut(), requestDTO.getDateFin());

        if (requestDTO.getContrat().getIdContrat() != null) {
            throw new IllegalArgumentException("Un nouveau contrat ne doit pas avoir d'identifiant");
        }
        if (requestDTO.getContrat().getMontantTotal() != null
                && requestDTO.getContrat().getMontantTotal().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Le montant total du contrat ne peut pas être négatif");
        }

        Reservation reservation = reservationMapper.toEntity(requestDTO);

        if (requestDTO.getIdClient() != null) {
            Client client = clientRepository.findById(requestDTO.getIdClient())
                    .orElseThrow(() -> new ResourceNotFoundException("Client", requestDTO.getIdClient()));
            reservation.setClient(client);
        }
        if (requestDTO.getIdVehicule() != null) {
            Vehicule vehicule = vehiculeRepository.findById(requestDTO.getIdVehicule())
                    .orElseThrow(() -> new ResourceNotFoundException("Véhicule", requestDTO.getIdVehicule()));
            reservation.setVehicule(vehicule);
        }

        Reservation savedReservation = reservationRepository.save(reservation);

        Contrat contrat = contratMapper.toEntity(requestDTO.getContrat());
        contrat.setReservation(savedReservation);
        if (contrat.getPaiements() != null) {
            contrat.getPaiements().forEach(p -> p.setContrat(contrat));
        }

        Contrat savedContrat = contratRepository.save(contrat);
        savedReservation.setContrat(savedContrat);

        return reservationMapper.toResponseDto(savedReservation);
    }

    private void validerDates(LocalDate dateDebut, LocalDate dateFin) {
        if (dateDebut == null || dateFin == null) {
            throw new IllegalArgumentException("Les dates de réservation sont obligatoires");
        }
        if (dateFin.isBefore(dateDebut)) {
            throw new IllegalArgumentException("La date de fin ne peut pas être antérieure à la date de début");
        }
    }
}
