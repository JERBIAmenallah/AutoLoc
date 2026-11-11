package tn.esprit.autoloc.service.decorator;

public class TarifDeBase implements LocationTarif {

    private final String modeleVehicule;
    private final double tarifJournalier;

    public TarifDeBase(String modeleVehicule, double tarifJournalier) {
        this.modeleVehicule = modeleVehicule;
        this.tarifJournalier = tarifJournalier;
    }

    @Override
    public double calculerPrixTotal(int nbJours) {
        return this.tarifJournalier * nbJours;
    }

    @Override
    public String getDescription() {
        return "Location de base (" + modeleVehicule + " @ " + tarifJournalier + " DT/j)";
    }
}
