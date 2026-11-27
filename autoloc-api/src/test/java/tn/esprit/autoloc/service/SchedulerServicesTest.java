package tn.esprit.autoloc.service;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronExpression;
import tn.esprit.autoloc.AutolocApiApplication;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.StatutReservation;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.repository.IContratRepository;
import tn.esprit.autoloc.repository.IReservationRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SchedulerServicesTest {

    @Mock
    private IContratRepository contratRepository;
    @Mock
    private IReservationRepository reservationRepository;
    @Mock
    private IVehiculeRepository vehiculeRepository;

    private ContratSchedulerService contratSchedulerService;
    private VehiculeSchedulerService vehiculeSchedulerService;
    private ReservationSchedulerService reservationSchedulerService;

    @BeforeEach
    void setUp() {
        contratSchedulerService = new ContratSchedulerService(contratRepository);
        vehiculeSchedulerService = new VehiculeSchedulerService(reservationRepository, vehiculeRepository);
        reservationSchedulerService = new ReservationSchedulerService(reservationRepository);
    }

    @Test
    void testEnableSchedulingPresentSurApplication() {
        assertTrue(AutolocApiApplication.class.isAnnotationPresent(EnableScheduling.class));
    }

    @Test
    void testCronExpressionContratScheduler() throws NoSuchMethodException {
        Method method = ContratSchedulerService.class.getMethod("identifierContratsProchesEcheance");
        Scheduled scheduled = method.getAnnotation(Scheduled.class);

        assertNotNull(scheduled);
        assertEquals("0 0 7 ? * MON-FRI", scheduled.cron());
        assertTrue(CronExpression.isValidExpression(scheduled.cron()));
    }

    @Test
    void testCronExpressionVehiculeScheduler() throws NoSuchMethodException {
        Method method = VehiculeSchedulerService.class.getMethod("libererVehiculesFinLocation");
        Scheduled scheduled = method.getAnnotation(Scheduled.class);

        assertNotNull(scheduled);
        assertEquals("0 0 1 * * ?", scheduled.cron());
        assertTrue(CronExpression.isValidExpression(scheduled.cron()));
    }

    @Test
    void testIdentifierContratsProchesEcheance() {
        LocalDate aujourdhui = LocalDate.now();
        LocalDate dans48h = aujourdhui.plusDays(2);
        List<StatutReservation> statutsExclus = List.of(StatutReservation.TERMINEE, StatutReservation.ANNULEE);

        Reservation r1 = new Reservation();
        r1.setIdReservation(10L);
        r1.setDateDebut(aujourdhui.minusDays(3));
        r1.setDateFin(aujourdhui.plusDays(1));
        r1.setStatut(StatutReservation.CONFIRMEE);

        Contrat c1 = new Contrat();
        c1.setIdContrat(1L);
        c1.setDateSignature(aujourdhui.minusDays(4));
        c1.setMontantTotal(new BigDecimal("420.00"));
        c1.setValide(true);
        c1.setReservation(r1);

        Reservation r2 = new Reservation();
        r2.setIdReservation(11L);
        r2.setDateDebut(aujourdhui.minusDays(2));
        r2.setDateFin(dans48h);
        r2.setStatut(StatutReservation.CONFIRMEE);

        Contrat c2 = new Contrat();
        c2.setIdContrat(2L);
        c2.setDateSignature(aujourdhui.minusDays(2));
        c2.setMontantTotal(new BigDecimal("280.00"));
        c2.setValide(true);
        c2.setReservation(r2);

        when(contratRepository.findContratsProchesEcheance(aujourdhui, dans48h, statutsExclus))
                .thenReturn(List.of(c1, c2));

        contratSchedulerService.identifierContratsProchesEcheance();

        verify(contratRepository).findContratsProchesEcheance(aujourdhui, dans48h, statutsExclus);
    }

    @Test
    void testIdentifierContratsProchesEcheanceAucunResultat() {
        LocalDate aujourdhui = LocalDate.now();
        LocalDate dans48h = aujourdhui.plusDays(2);
        List<StatutReservation> statutsExclus = List.of(StatutReservation.TERMINEE, StatutReservation.ANNULEE);

        when(contratRepository.findContratsProchesEcheance(aujourdhui, dans48h, statutsExclus))
                .thenReturn(Collections.emptyList());

        contratSchedulerService.identifierContratsProchesEcheance();

        verify(contratRepository).findContratsProchesEcheance(aujourdhui, dans48h, statutsExclus);
    }

    @Test
    void testLibererVehiculesFinLocation() {
        LocalDate aujourdhui = LocalDate.now();
        List<StatutReservation> statutsExclus = List.of(StatutReservation.TERMINEE, StatutReservation.ANNULEE);

        Vehicule vehicule = new Vehicule();
        vehicule.setIdVehicule(5L);
        vehicule.setImmatriculation("123-TN-4567");
        vehicule.setStatut(StatutVehicule.LOUE);

        Reservation reservation = new Reservation();
        reservation.setIdReservation(20L);
        reservation.setDateDebut(aujourdhui.minusDays(5));
        reservation.setDateFin(aujourdhui.minusDays(1));
        reservation.setStatut(StatutReservation.CONFIRMEE);
        reservation.setVehicule(vehicule);

        when(reservationRepository.findByDateFinBeforeAndStatutNotIn(aujourdhui, statutsExclus))
                .thenReturn(List.of(reservation));
        when(vehiculeRepository.save(any(Vehicule.class))).thenAnswer(inv -> inv.getArgument(0));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(inv -> inv.getArgument(0));

        vehiculeSchedulerService.libererVehiculesFinLocation();

        assertEquals(StatutVehicule.DISPONIBLE, vehicule.getStatut());
        assertEquals(StatutReservation.TERMINEE, reservation.getStatut());
        verify(vehiculeRepository).save(vehicule);
        verify(reservationRepository).save(reservation);
    }

    @Test
    void testLibererVehiculesFinLocationSansVehicule() {
        LocalDate aujourdhui = LocalDate.now();
        List<StatutReservation> statutsExclus = List.of(StatutReservation.TERMINEE, StatutReservation.ANNULEE);

        Reservation reservation = new Reservation();
        reservation.setIdReservation(21L);
        reservation.setDateDebut(aujourdhui.minusDays(4));
        reservation.setDateFin(aujourdhui.minusDays(1));
        reservation.setStatut(StatutReservation.CONFIRMEE);
        reservation.setVehicule(null);

        when(reservationRepository.findByDateFinBeforeAndStatutNotIn(aujourdhui, statutsExclus))
                .thenReturn(List.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(inv -> inv.getArgument(0));

        vehiculeSchedulerService.libererVehiculesFinLocation();

        assertEquals(StatutReservation.TERMINEE, reservation.getStatut());
        verify(vehiculeRepository, never()).save(any(Vehicule.class));
        verify(reservationRepository).save(reservation);
    }

    @Test
    void testLibererVehiculesFinLocationListeVide() {
        LocalDate aujourdhui = LocalDate.now();
        List<StatutReservation> statutsExclus = List.of(StatutReservation.TERMINEE, StatutReservation.ANNULEE);

        when(reservationRepository.findByDateFinBeforeAndStatutNotIn(aujourdhui, statutsExclus))
                .thenReturn(Collections.emptyList());

        vehiculeSchedulerService.libererVehiculesFinLocation();

        verify(reservationRepository).findByDateFinBeforeAndStatutNotIn(aujourdhui, statutsExclus);
        verify(vehiculeRepository, never()).save(any(Vehicule.class));
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void testCronExpressionAfficherVehicules() throws NoSuchMethodException {
        Method method = VehiculeSchedulerService.class.getMethod("afficherVehicules");
        Scheduled scheduled = method.getAnnotation(Scheduled.class);

        assertNotNull(scheduled);
        assertEquals("0 0 9 ? * MON", scheduled.cron());
        assertTrue(CronExpression.isValidExpression(scheduled.cron()));
    }

    @Test
    void testAfficherVehiculesAnneeCourante() {
        int annee = LocalDate.now().getYear();
        LocalDate debut = LocalDate.of(annee, 1, 1);
        LocalDate fin = LocalDate.of(annee, 12, 31);

        Vehicule vehicule = new Vehicule();
        vehicule.setIdVehicule(1L);
        vehicule.setImmatriculation("123-TN-4567");
        vehicule.setMarque("Toyota");
        vehicule.setDateCreation(LocalDate.now());

        when(vehiculeRepository.findByDateCreationBetween(debut, fin))
                .thenReturn(List.of(vehicule));

        vehiculeSchedulerService.afficherVehicules();

        verify(vehiculeRepository).findByDateCreationBetween(debut, fin);
    }

    @Test
    void testCronExpressionVerifierReservations() throws NoSuchMethodException {
        Method method = ReservationSchedulerService.class.getMethod("verifierReservations");
        Scheduled scheduled = method.getAnnotation(Scheduled.class);

        assertNotNull(scheduled);
        assertEquals("0 0 9 * * ?", scheduled.cron());
        assertTrue(CronExpression.isValidExpression(scheduled.cron()));
    }

    @Test
    void testVerifierReservationsDateFinAujourdhui() {
        LocalDate aujourdhui = LocalDate.now();
        Reservation reservation = new Reservation();
        reservation.setIdReservation(30L);
        reservation.setDateDebut(aujourdhui.minusDays(2));
        reservation.setDateFin(aujourdhui);
        reservation.setStatut(StatutReservation.CONFIRMEE);

        when(reservationRepository.findByDateFinAndStatutNot(aujourdhui, StatutReservation.TERMINEE))
                .thenReturn(List.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(inv -> inv.getArgument(0));

        reservationSchedulerService.verifierReservations();

        assertEquals(StatutReservation.TERMINEE, reservation.getStatut());
        verify(reservationRepository).save(reservation);
    }
}
