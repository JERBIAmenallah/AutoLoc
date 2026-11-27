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
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.repository.IReservationRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class VehiculeSchedulerService {

    private final IReservationRepository reservationRepository;
    private final IVehiculeRepository vehiculeRepository;

    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void libererVehiculesFinLocation() {
        LocalDate aujourdhui = LocalDate.now();
        List<StatutReservation> statutsExclus = List.of(StatutReservation.TERMINEE, StatutReservation.ANNULEE);
        List<Reservation> reservationsTerminees = reservationRepository
                .findByDateFinBeforeAndStatutNotIn(aujourdhui, statutsExclus);

        if (reservationsTerminees.isEmpty()) {
            log.info("Aucune réservation terminée à traiter pour le {}", aujourdhui);
            return;
        }

        for (Reservation reservation : reservationsTerminees) {
            reservation.setStatut(StatutReservation.TERMINEE);

            Vehicule vehicule = reservation.getVehicule();
            if (vehicule != null) {
                vehicule.setStatut(StatutVehicule.DISPONIBLE);
                vehiculeRepository.save(vehicule);
            }

            reservationRepository.save(reservation);

            if (vehicule != null) {
                log.info("Réservation #{} passée à {} - Véhicule {} remis à {}",
                        reservation.getIdReservation(),
                        reservation.getStatut(),
                        vehicule.getImmatriculation(),
                        vehicule.getStatut());
            } else {
                log.info("Réservation #{} passée à {} (aucun véhicule associé)",
                        reservation.getIdReservation(),
                        reservation.getStatut());
            }
        }
    }

    @Scheduled(cron = "0 0 9 ? * MON")
    @Transactional(readOnly = true)
    public void afficherVehicules() {
        int anneeCourante = LocalDate.now().getYear();
        LocalDate debutAnnee = LocalDate.of(anneeCourante, 1, 1);
        LocalDate finAnnee = LocalDate.of(anneeCourante, 12, 31);

        List<Vehicule> vehicules = vehiculeRepository.findByDateCreationBetween(debutAnnee, finAnnee);
        if (vehicules.isEmpty()) {
            log.info("Aucun véhicule créé durant l'année {}", anneeCourante);
            return;
        }

        for (Vehicule vehicule : vehicules) {
            log.info("Véhicule créé en {} -> Immatriculation : {} | Marque : {}",
                    anneeCourante, vehicule.getImmatriculation(), vehicule.getMarque());
        }
    }
}
