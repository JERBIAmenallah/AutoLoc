package tn.esprit.autoloc.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.StatutReservation;

public interface IReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByDateDebutAndDateFin(LocalDate dateDebut, LocalDate dateFin);

    List<Reservation> findByDateFinBeforeAndStatutNotIn(LocalDate date, List<StatutReservation> statutsExclus);

    List<Reservation> findByDateFinAndStatutNot(LocalDate dateFin, StatutReservation statut);
}
