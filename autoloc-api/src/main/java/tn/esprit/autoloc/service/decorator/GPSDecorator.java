package tn.esprit.autoloc.service.decorator;

public class GPSDecorator extends DecorateurTarif {

    private static final double SURCOUT_JOURNALIER = 15.0;

    public GPSDecorator(LocationTarif composant) {
        super(composant);
    }

    @Override
    public double calculerPrixTotal(int nbJours) {
        return super.calculerPrixTotal(nbJours) + (SURCOUT_JOURNALIER * nbJours);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + Option GPS (15 DT/j)";
    }
}
