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
import tn.esprit.autoloc.domain.Maintenance;
import tn.esprit.autoloc.service.IMaintenanceService;

@RestController
@RequestMapping("/maintenances")
@RequiredArgsConstructor
@Tag(name = "Maintenances", description = "Endpoints de gestion des entretiens et maintenances de véhicules")
public class MaintenanceController {

    private final IMaintenanceService maintenanceService;

    @Operation(summary = "Planifier une maintenance", description = "Enregistre une nouvelle intervention sur un véhicule")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Maintenance créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides fournies")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Maintenance> create(@RequestBody Maintenance maintenance) {
        return new ResponseEntity<>(maintenanceService.create(maintenance), HttpStatus.CREATED);
    }

    @Operation(summary = "Obtenir une maintenance par ID", description = "Retourne les détails d'une maintenance")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Maintenance trouvée"),
            @ApiResponse(responseCode = "404", description = "Maintenance introuvable")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Maintenance> findById(
            @Parameter(description = "Identifiant de la maintenance", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(maintenanceService.findById(id));
    }

    @Operation(summary = "Lister toutes les maintenances", description = "Retourne la liste des interventions")
    @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    @GetMapping
    public ResponseEntity<List<Maintenance>> findAll() {
        return ResponseEntity.ok(maintenanceService.findAll());
    }

    @Operation(summary = "Mettre à jour une maintenance", description = "Modifie une intervention existante")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Maintenance mise à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Maintenance introuvable"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Maintenance> update(
            @Parameter(description = "Identifiant de la maintenance", required = true)
            @PathVariable Long id,
            @RequestBody Maintenance maintenance) {
        return ResponseEntity.ok(maintenanceService.update(id, maintenance));
    }

    @Operation(summary = "Supprimer une maintenance", description = "Supprime une maintenance")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Maintenance supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Maintenance introuvable")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteById(
            @Parameter(description = "Identifiant de la maintenance à supprimer", required = true)
            @PathVariable Long id) {
        maintenanceService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
