package tn.esprit.autoloc.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.autoloc.domain.Reservation;

public interface IReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByDateDebutAndDateFin(LocalDate dateDebut, LocalDate dateFin);
}
