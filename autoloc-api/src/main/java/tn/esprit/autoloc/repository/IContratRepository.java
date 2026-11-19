package tn.esprit.autoloc.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.autoloc.domain.Contrat;

public interface IContratRepository extends JpaRepository<Contrat, Long> {
    List<Contrat> findByDateSignature(LocalDate dateSignature);
}
