package tn.esprit.autoloc.service;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.StatutReservation;
import tn.esprit.autoloc.repository.IReservationRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservationSchedulerService {

    private final IReservationRepository reservationRepository;

    @Scheduled(cron = "0 0 9 * * ?")
    @Transactional
    public void verifierReservations() {
        LocalDate aujourdhui = LocalDate.now();
        List<Reservation> reservations = reservationRepository
                .findByDateFinAndStatutNot(aujourdhui, StatutReservation.TERMINEE);

        for (Reservation reservation : reservations) {
            reservation.setStatut(StatutReservation.TERMINEE);
            reservationRepository.save(reservation);
            log.info("Réservation terminée : #{}", reservation.getIdReservation());
        }
    }
}
