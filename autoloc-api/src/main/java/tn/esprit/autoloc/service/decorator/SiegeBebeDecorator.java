package tn.esprit.autoloc.service.decorator;

public class SiegeBebeDecorator extends DecorateurTarif {

    private static final double SURCOUT_JOURNALIER = 10.0;

    public SiegeBebeDecorator(LocationTarif composant) {
        super(composant);
    }

    @Override
    public double calculerPrixTotal(int nbJours) {
        return super.calculerPrixTotal(nbJours) + (SURCOUT_JOURNALIER * nbJours);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + Siège Bébé (10 DT/j)";
    }
}
