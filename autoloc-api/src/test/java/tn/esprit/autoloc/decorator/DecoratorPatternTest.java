package tn.esprit.autoloc.decorator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tn.esprit.autoloc.service.decorator.AssuranceDecorator;
import tn.esprit.autoloc.service.decorator.GPSDecorator;
import tn.esprit.autoloc.service.decorator.LocationTarif;
import tn.esprit.autoloc.service.decorator.LocationTarificationService;
import tn.esprit.autoloc.service.decorator.SiegeBebeDecorator;
import tn.esprit.autoloc.service.decorator.TarifDeBase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DecoratorPatternTest {

    @Test
    @DisplayName("Test 1 : Location de base sans option")
    void testLocationDeBase() {
        LocationTarif tarif = new TarifDeBase("Toyota Yaris", 75.0);

        double prix = tarif.calculerPrixTotal(5);

        assertEquals(375.0, prix, 0.001);
        assertTrue(tarif.getDescription().contains("Location de base"));
    }

    @Test
    @DisplayName("Test 2 : Location avec option GPS (+15 DT/j)")
    void testLocationAvecGps() {
        LocationTarif tarif = new GPSDecorator(new TarifDeBase("Toyota Yaris", 75.0));

        // (75 + 15) * 5 = 450 DT
        double prix = tarif.calculerPrixTotal(5);

        assertEquals(450.0, prix, 0.001);
        assertTrue(tarif.getDescription().contains("Option GPS"));
    }

    @Test
    @DisplayName("Test 3 : Location cumulée avec GPS + Assurance + Siège Bébé")
    void testLocationCumuleeToutesOptions() {
        LocationTarif tarif = new SiegeBebeDecorator(
                new AssuranceDecorator(
                        new GPSDecorator(
                                new TarifDeBase("Toyota Yaris", 75.0)
                        )
                )
        );

        // (75 + 15 [GPS] + 35 [Assurance] + 10 [Siège]) * 4 = 135 * 4 = 540 DT
        double prix = tarif.calculerPrixTotal(4);

        assertEquals(540.0, prix, 0.001);
        assertTrue(tarif.getDescription().contains("Option GPS"));
        assertTrue(tarif.getDescription().contains("Assurance Tous Risques"));
        assertTrue(tarif.getDescription().contains("Siège Bébé"));
    }

    @Test
    @DisplayName("Test 4 : Service Spring LocationTarificationService")
    void testServiceSpring() {
        LocationTarificationService service = new LocationTarificationService();

        // 3 jours avec GPS et Assurance
        // (80 + 15 + 35) * 3 = 130 * 3 = 390 DT
        double prix = service.calculerPrixTotal("Clio 5", 80.0, 3, true, true, false);

        assertEquals(390.0, prix, 0.001);
    }
}
