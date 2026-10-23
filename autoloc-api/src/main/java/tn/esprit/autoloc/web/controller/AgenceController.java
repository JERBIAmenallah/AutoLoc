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
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.service.IAgenceService;

@RestController
@RequestMapping("/agences")
@RequiredArgsConstructor
@Tag(name = "Agences", description = "Endpoints de gestion des agences")
public class AgenceController {

    private final IAgenceService agenceService;

    @Operation(summary = "Créer une nouvelle agence", description = "Enregistre une nouvelle agence dans le réseau")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Agence créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides fournies")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Agence> create(@RequestBody Agence agence) {
        return new ResponseEntity<>(agenceService.create(agence), HttpStatus.CREATED);
    }

    @Operation(summary = "Obtenir une agence par son identifiant", description = "Retourne les détails d'une agence")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agence trouvée"),
            @ApiResponse(responseCode = "404", description = "Agence introuvable")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Agence> findById(
            @Parameter(description = "Identifiant unique de l'agence", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(agenceService.findById(id));
    }

    @Operation(summary = "Lister toutes les agences", description = "Retourne la liste complète des agences")
    @ApiResponse(responseCode = "200", description = "Liste des agences récupérée avec succès")
    @GetMapping
    public ResponseEntity<List<Agence>> findAll() {
        return ResponseEntity.ok(agenceService.findAll());
    }

    @Operation(summary = "Mettre à jour une agence", description = "Modifie les informations d'une agence existante")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agence mise à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Agence introuvable"),
            @ApiResponse(responseCode = "400", description = "Données de mise à jour invalides")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Agence> update(
            @Parameter(description = "Identifiant de l'agence à modifier", required = true)
            @PathVariable Long id,
            @RequestBody Agence agence) {
        return ResponseEntity.ok(agenceService.update(id, agence));
    }

    @Operation(summary = "Supprimer une agence", description = "Supprime une agence du réseau")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Agence supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Agence introuvable")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteById(
            @Parameter(description = "Identifiant de l'agence à supprimer", required = true)
            @PathVariable Long id) {
        agenceService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
