package tn.esprit.autoloc.service;

import java.util.List;
import tn.esprit.autoloc.web.dto.ReservationRequestDTO;
import tn.esprit.autoloc.web.dto.ReservationResponseDTO;
import tn.esprit.autoloc.web.dto.ReservationWithContratRequestDTO;

public interface IReservationService {
    ReservationResponseDTO create(ReservationRequestDTO dto);
    ReservationResponseDTO findById(Long id);
    List<ReservationResponseDTO> findAll();
    ReservationResponseDTO update(Long id, ReservationRequestDTO dto);
    void deleteById(Long id);

    ReservationResponseDTO affecterReservationAClient(Long idReservation, String nom, String prenom);
    ReservationResponseDTO creerReservationEtContratAssocie(ReservationWithContratRequestDTO requestDTO);
}
