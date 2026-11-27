package tn.esprit.autoloc.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.StatutReservation;

public interface IContratRepository extends JpaRepository<Contrat, Long> {
    List<Contrat> findByDateSignature(LocalDate dateSignature);

    @Query("SELECT c FROM Contrat c JOIN FETCH c.reservation r "
            + "WHERE c.valide = true "
            + "AND r.dateFin BETWEEN :maintenant AND :dans48h "
            + "AND r.statut NOT IN (:statutsExclus)")
    List<Contrat> findContratsProchesEcheance(
            @Param("maintenant") LocalDate maintenant,
            @Param("dans48h") LocalDate dans48h,
            @Param("statutsExclus") List<StatutReservation> statutsExclus);
}
