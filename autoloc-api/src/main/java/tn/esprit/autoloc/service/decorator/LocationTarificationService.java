package tn.esprit.autoloc.service.decorator;

import org.springframework.stereotype.Service;

@Service
public class LocationTarificationService {

    public LocationTarif composerTarification(String modele, double tarifJournalier, boolean avecGps, boolean avecAssurance, boolean avecSiegeBebe) {
        LocationTarif tarif = new TarifDeBase(modele, tarifJournalier);

        if (avecGps) {
            tarif = new GPSDecorator(tarif);
        }
        if (avecAssurance) {
            tarif = new AssuranceDecorator(tarif);
        }
        if (avecSiegeBebe) {
            tarif = new SiegeBebeDecorator(tarif);
        }

        return tarif;
    }

    public double calculerPrixTotal(String modele, double tarifJournalier, int nbJours, boolean avecGps, boolean avecAssurance, boolean avecSiegeBebe) {
        LocationTarif tarif = composerTarification(modele, tarifJournalier, avecGps, avecAssurance, avecSiegeBebe);
        return tarif.calculerPrixTotal(nbJours);
    }
}
