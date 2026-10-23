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
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.service.IContratService;

@RestController
@RequestMapping("/contrats")
@RequiredArgsConstructor
@Tag(name = "Contrats", description = "Endpoints de gestion des contrats de location")
public class ContratController {

    private final IContratService contratService;

    @Operation(summary = "Créer un nouveau contrat", description = "Enregistre un contrat de location, avec gestion des paiements associés en cascade")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Contrat créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides fournies")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Contrat> create(@RequestBody Contrat contrat) {
        return new ResponseEntity<>(contratService.create(contrat), HttpStatus.CREATED);
    }

    @Operation(summary = "Obtenir un contrat par son identifiant", description = "Retourne les détails d'un contrat avec ses paiements associés")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contrat trouvé"),
            @ApiResponse(responseCode = "404", description = "Contrat introuvable")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Contrat> findById(
            @Parameter(description = "Identifiant unique du contrat", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(contratService.findById(id));
    }

    @Operation(summary = "Lister tous les contrats", description = "Retourne la liste de tous les contrats de location")
    @ApiResponse(responseCode = "200", description = "Liste des contrats récupérée avec succès")
    @GetMapping
    public ResponseEntity<List<Contrat>> findAll() {
        return ResponseEntity.ok(contratService.findAll());
    }

    @Operation(summary = "Mettre à jour un contrat", description = "Modifie les caractéristiques d'un contrat existant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contrat mis à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Contrat introuvable"),
            @ApiResponse(responseCode = "400", description = "Données de mise à jour invalides")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Contrat> update(
            @Parameter(description = "Identifiant du contrat à modifier", required = true)
            @PathVariable Long id,
            @RequestBody Contrat contrat) {
        return ResponseEntity.ok(contratService.update(id, contrat));
    }

    @Operation(summary = "Supprimer un contrat", description = "Supprime un contrat de location")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Contrat supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Contrat introuvable")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteById(
            @Parameter(description = "Identifiant du contrat à supprimer", required = true)
            @PathVariable Long id) {
        contratService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
