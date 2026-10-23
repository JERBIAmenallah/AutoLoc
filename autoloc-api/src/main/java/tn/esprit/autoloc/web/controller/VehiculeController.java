package tn.esprit.autoloc.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.service.IVehiculeService;

@RestController
@RequestMapping("/vehicules")
@RequiredArgsConstructor
@Tag(name = "Véhicules", description = "Endpoints de gestion des véhicules")
public class VehiculeController {

    private final IVehiculeService vehiculeService;

    @Operation(summary = "Créer un nouveau véhicule", description = "Enregistre un véhicule dans le parc automobile")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Véhicule créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides fournies")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Vehicule> create(@RequestBody Vehicule vehicule) {
        return new ResponseEntity<>(vehiculeService.create(vehicule), HttpStatus.CREATED);
    }

    @Operation(summary = "Obtenir un véhicule par son identifiant", description = "Retourne les détails d'un véhicule")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Véhicule trouvé"),
            @ApiResponse(responseCode = "404", description = "Véhicule introuvable")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Vehicule> findById(
            @Parameter(description = "Identifiant unique du véhicule", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(vehiculeService.findById(id));
    }

    @Operation(summary = "Lister tous les véhicules", description = "Retourne la liste de tous les véhicules du parc")
    @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    @GetMapping
    public ResponseEntity<List<Vehicule>> findAll() {
        return ResponseEntity.ok(vehiculeService.findAll());
    }

    @Operation(summary = "Mettre à jour un véhicule", description = "Modifie les caractéristiques d'un véhicule existant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Véhicule mis à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Véhicule introuvable"),
            @ApiResponse(responseCode = "400", description = "Données de mise à jour invalides")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Vehicule> update(
            @Parameter(description = "Identifiant du véhicule à modifier", required = true)
            @PathVariable Long id,
            @RequestBody Vehicule vehicule) {
        return ResponseEntity.ok(vehiculeService.update(id, vehicule));
    }

    @Operation(summary = "Supprimer un véhicule", description = "Supprime définitivement un véhicule du parc")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Véhicule supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Véhicule introuvable")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteById(
            @Parameter(description = "Identifiant du véhicule à supprimer", required = true)
            @PathVariable Long id) {
        vehiculeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
