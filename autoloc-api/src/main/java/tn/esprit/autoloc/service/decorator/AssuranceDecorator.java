package tn.esprit.autoloc.service.decorator;

public class AssuranceDecorator extends DecorateurTarif {

    private static final double SURCOUT_JOURNALIER = 35.0;

    public AssuranceDecorator(LocationTarif composant) {
        super(composant);
    }

    @Override
    public double calculerPrixTotal(int nbJours) {
        return super.calculerPrixTotal(nbJours) + (SURCOUT_JOURNALIER * nbJours);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + Assurance Tous Risques (35 DT/j)";
    }
}
