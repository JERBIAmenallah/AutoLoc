package tn.esprit.autoloc.service.decorator;

public interface LocationTarif {
    double calculerPrixTotal(int nbJours);
    String getDescription();
}
