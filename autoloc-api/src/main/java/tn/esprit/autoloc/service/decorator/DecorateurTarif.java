package tn.esprit.autoloc.service.decorator;

public abstract class DecorateurTarif implements LocationTarif {

    protected final LocationTarif composant;

    public DecorateurTarif(LocationTarif composant) {
        this.composant = composant;
    }

    @Override
    public double calculerPrixTotal(int nbJours) {
        return composant.calculerPrixTotal(nbJours);
    }

    @Override
    public String getDescription() {
        return composant.getDescription();
    }
}
