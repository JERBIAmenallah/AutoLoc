package tn.esprit.autoloc.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.domain.Maintenance;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.StatutReservation;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IClientRepository;
import tn.esprit.autoloc.repository.IContratRepository;
import tn.esprit.autoloc.repository.IEquipementRepository;
import tn.esprit.autoloc.repository.IMaintenanceRepository;
import tn.esprit.autoloc.repository.IReservationRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;
import tn.esprit.autoloc.web.dto.ContratDTO;
import tn.esprit.autoloc.web.dto.ReservationResponseDTO;
import tn.esprit.autoloc.web.dto.ReservationWithContratRequestDTO;
import tn.esprit.autoloc.web.dto.VehiculeDTO;
import tn.esprit.autoloc.web.mapper.ContratMapper;
import tn.esprit.autoloc.web.mapper.EquipementMapper;
import tn.esprit.autoloc.web.mapper.MaintenanceMapper;
import tn.esprit.autoloc.web.mapper.PaiementMapper;
import tn.esprit.autoloc.web.mapper.ReservationMapper;
import tn.esprit.autoloc.web.mapper.VehiculeMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AffectationServicesTest {

    @Mock
    private IReservationRepository reservationRepository;
    @Mock
    private IClientRepository clientRepository;
    @Mock
    private IVehiculeRepository vehiculeRepository;
    @Mock
    private IEquipementRepository equipementRepository;
    @Mock
    private IMaintenanceRepository maintenanceRepository;
    @Mock
    private IContratRepository contratRepository;

    private ReservationServiceImpl reservationService;
    private VehiculeServiceImpl vehiculeService;
    private ContratServiceImpl contratService;

    @BeforeEach
    void setUp() {
        ContratMapper contratMapper = new ContratMapper(new PaiementMapper());
        ReservationMapper reservationMapper = new ReservationMapper(contratMapper);
        VehiculeMapper vehiculeMapper = new VehiculeMapper(new EquipementMapper(), new MaintenanceMapper());

        reservationService = new ReservationServiceImpl(
                reservationRepository,
                clientRepository,
                vehiculeRepository,
                contratRepository,
                reservationMapper,
                contratMapper
        );
        vehiculeService = new VehiculeServiceImpl(
                vehiculeRepository,
                equipementRepository,
                maintenanceRepository,
                vehiculeMapper
        );
        contratService = new ContratServiceImpl(
                contratRepository,
                reservationRepository,
                contratMapper
        );
    }

    @Test
    void testAffecterReservationAClient() {
        Reservation reservation = new Reservation();
        reservation.setIdReservation(1L);
        reservation.setDateDebut(LocalDate.of(2026, 11, 20));
        reservation.setDateFin(LocalDate.of(2026, 11, 24));
        reservation.setStatut(StatutReservation.EN_ATTENTE);

        Client client = new Client();
        client.setIdClient(10L);
        client.setNom("Jerbi");
        client.setPrenom("Amenallah");

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(clientRepository.findFirstByNomIgnoreCaseAndPrenomIgnoreCase("Jerbi", "Amenallah"))
                .thenReturn(Optional.of(client));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(inv -> inv.getArgument(0));

        ReservationResponseDTO result = reservationService.affecterReservationAClient(1L, "Jerbi", "Amenallah");

        assertNotNull(result);
        assertEquals(1L, result.getIdReservation());
        assertEquals(10L, result.getIdClient());
        assertEquals("Jerbi", result.getNomClient());
        assertEquals("Amenallah", result.getPrenomClient());
        verify(reservationRepository).save(reservation);
    }

    @Test
    void testAffecterReservationAClientIntrouvable() {
        Reservation reservation = new Reservation();
        reservation.setIdReservation(1L);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(clientRepository.findFirstByNomIgnoreCaseAndPrenomIgnoreCase("Inconnu", "Test"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> reservationService.affecterReservationAClient(1L, "Inconnu", "Test"));
    }

    @Test
    void testAffecterVehiculeAEquipement() {
        Vehicule vehicule = new Vehicule();
        vehicule.setIdVehicule(2L);
        vehicule.setImmatriculation("123-TN-4567");
        vehicule.setMarque("Toyota");
        vehicule.setModele("Yaris");
        vehicule.setCategorie(CategorieVehicule.CITADINE);
        vehicule.setTarifJournalier(new BigDecimal("75.00"));
        vehicule.setStatut(StatutVehicule.DISPONIBLE);
        vehicule.setEquipements(new ArrayList<>());

        Equipement equipement = new Equipement();
        equipement.setIdEquipement(5L);
        equipement.setLibelle("GPS");

        when(vehiculeRepository.findById(2L)).thenReturn(Optional.of(vehicule));
        when(equipementRepository.findById(5L)).thenReturn(Optional.of(equipement));
        when(vehiculeRepository.save(any(Vehicule.class))).thenAnswer(inv -> inv.getArgument(0));

        VehiculeDTO result = vehiculeService.affecterVehiculeAEquipement(2L, 5L);

        assertNotNull(result);
        assertEquals(1, result.getEquipements().size());
        assertEquals("GPS", result.getEquipements().get(0).getLibelle());
        verify(vehiculeRepository).save(vehicule);
    }

    @Test
    void testAffecterMaintenanceAvehicule() {
        Maintenance maintenance = new Maintenance();
        maintenance.setIdMaintenance(3L);
        maintenance.setDateDebut(LocalDate.of(2026, 11, 19));
        maintenance.setDescription("Vidange moteur");

        Vehicule vehicule = new Vehicule();
        vehicule.setIdVehicule(2L);
        vehicule.setImmatriculation("123-TN-4567");
        vehicule.setMarque("Toyota");
        vehicule.setModele("Yaris");
        vehicule.setCategorie(CategorieVehicule.CITADINE);
        vehicule.setTarifJournalier(new BigDecimal("75.00"));
        vehicule.setStatut(StatutVehicule.DISPONIBLE);
        vehicule.setMaintenances(new ArrayList<>());

        when(maintenanceRepository.findFirstByDescriptionIgnoreCase("Vidange moteur"))
                .thenReturn(Optional.of(maintenance));
        when(vehiculeRepository.findByImmatriculation("123-TN-4567"))
                .thenReturn(Optional.of(vehicule));
        when(maintenanceRepository.save(any(Maintenance.class))).thenAnswer(inv -> inv.getArgument(0));

        VehiculeDTO result = vehiculeService.affecterMaintenanceAvehicule("Vidange moteur", "123-TN-4567");

        assertNotNull(result);
        assertEquals(vehicule, maintenance.getVehicule());
        assertEquals(1, result.getMaintenances().size());
        assertEquals("Vidange moteur", result.getMaintenances().get(0).getDescription());
        verify(maintenanceRepository).save(maintenance);
    }

    @Test
    void testAffecterContratAReservation() {
        LocalDate dateSignature = LocalDate.of(2026, 11, 19);
        LocalDate dateDebut = LocalDate.of(2026, 11, 20);
        LocalDate dateFin = LocalDate.of(2026, 11, 25);

        Contrat contrat = new Contrat();
        contrat.setIdContrat(7L);
        contrat.setDateSignature(dateSignature);
        contrat.setMontantTotal(new BigDecimal("375.00"));
        contrat.setValide(true);

        Reservation reservation = new Reservation();
        reservation.setIdReservation(11L);
        reservation.setDateDebut(dateDebut);
        reservation.setDateFin(dateFin);
        reservation.setStatut(StatutReservation.CONFIRMEE);

        when(contratRepository.findByDateSignature(dateSignature))
                .thenReturn(java.util.List.of(contrat));
        when(reservationRepository.findByDateDebutAndDateFin(dateDebut, dateFin))
                .thenReturn(java.util.List.of(reservation));
        when(contratRepository.save(any(Contrat.class))).thenAnswer(inv -> inv.getArgument(0));

        ContratDTO result = contratService.affecterContratAReservation(dateSignature, dateDebut, dateFin);

        assertNotNull(result);
        assertEquals(7L, result.getIdContrat());
        assertEquals(11L, result.getIdReservation());
        verify(contratRepository).save(contrat);
    }

    @Test
    void testCreerReservationEtContratAssocie() {
        Client client = new Client();
        client.setIdClient(10L);
        client.setNom("Jerbi");
        client.setPrenom("Amenallah");

        Vehicule vehicule = new Vehicule();
        vehicule.setIdVehicule(2L);
        vehicule.setImmatriculation("123-TN-4567");

        ContratDTO contratDto = ContratDTO.builder()
                .dateSignature(LocalDate.of(2026, 11, 19))
                .montantTotal(new BigDecimal("300.00"))
                .valide(true)
                .build();

        ReservationWithContratRequestDTO requestDTO = ReservationWithContratRequestDTO.builder()
                .dateDebut(LocalDate.of(2026, 11, 20))
                .dateFin(LocalDate.of(2026, 11, 24))
                .statut(StatutReservation.CONFIRMEE)
                .idClient(10L)
                .idVehicule(2L)
                .contrat(contratDto)
                .build();

        when(clientRepository.findById(10L)).thenReturn(Optional.of(client));
        when(vehiculeRepository.findById(2L)).thenReturn(Optional.of(vehicule));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(inv -> {
            Reservation r = inv.getArgument(0);
            r.setIdReservation(15L);
            return r;
        });
        when(contratRepository.save(any(Contrat.class))).thenAnswer(inv -> {
            Contrat c = inv.getArgument(0);
            c.setIdContrat(20L);
            return c;
        });

        ReservationResponseDTO result = reservationService.creerReservationEtContratAssocie(requestDTO);

        assertNotNull(result);
        assertEquals(15L, result.getIdReservation());
        assertEquals(10L, result.getIdClient());
        assertEquals(2L, result.getIdVehicule());
        assertNotNull(result.getContrat());
        assertEquals(20L, result.getContrat().getIdContrat());
        assertEquals(15L, result.getContrat().getIdReservation());
        assertEquals(new BigDecimal("300.00"), result.getContrat().getMontantTotal());
    }
}
