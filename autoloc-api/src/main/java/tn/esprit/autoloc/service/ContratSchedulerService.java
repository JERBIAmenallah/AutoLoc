package tn.esprit.autoloc.service;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.StatutReservation;
import tn.esprit.autoloc.repository.IContratRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class ContratSchedulerService {

    private static final List<StatutReservation> STATUTS_EXCLUS = List.of(
            StatutReservation.TERMINEE,
            StatutReservation.ANNULEE
    );

    private final IContratRepository contratRepository;

    @Scheduled(cron = "0 0 7 ? * MON-FRI")
    @Transactional(readOnly = true)
    public void identifierContratsProchesEcheance() {
        LocalDate aujourdhui = LocalDate.now();
        LocalDate dans48h = aujourdhui.plusDays(2);

        List<Contrat> contrats = contratRepository.findContratsProchesEcheance(
                aujourdhui, dans48h, STATUTS_EXCLUS);

        if (contrats.isEmpty()) {
            log.info("Aucun contrat arrivant à échéance dans les 48 heures ({} -> {}).", aujourdhui, dans48h);
            return;
        }

        log.info("{} contrat(s) arrivant à échéance dans les 48 heures ({} -> {}) :",
                contrats.size(), aujourdhui, dans48h);

        for (Contrat contrat : contrats) {
            Reservation reservation = contrat.getReservation();
            Long idReservation = reservation != null ? reservation.getIdReservation() : null;
            LocalDate dateFin = reservation != null ? reservation.getDateFin() : null;

            log.info("Contrat #{} | Réservation #{} | Fin le {} | Montant : {} DT",
                    contrat.getIdContrat(), idReservation, dateFin, contrat.getMontantTotal());
        }
    }
}
