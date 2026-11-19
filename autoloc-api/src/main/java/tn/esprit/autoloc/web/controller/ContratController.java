package tn.esprit.autoloc.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.service.IContratService;
import tn.esprit.autoloc.web.dto.ContratDTO;

@RestController
@RequestMapping("/contrats")
@RequiredArgsConstructor
@Tag(name = "Contrats", description = "Endpoints de gestion des contrats de location avec DTOs et affectations")
public class ContratController {

    private final IContratService contratService;

    @Operation(summary = "Créer un nouveau contrat", description = "Enregistre un contrat de location avec ses paiements en cascade via DTO validé")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Contrat créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides fournies (échec de validation)")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ContratDTO> create(@Valid @RequestBody ContratDTO dto) {
        return new ResponseEntity<>(contratService.create(dto), HttpStatus.CREATED);
    }

    @Operation(summary = "Obtenir un contrat par son identifiant", description = "Retourne les détails d'un contrat sous forme de DTO avec ses paiements")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contrat trouvé"),
            @ApiResponse(responseCode = "404", description = "Contrat introuvable")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ContratDTO> findById(
            @Parameter(description = "Identifiant unique du contrat", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(contratService.findById(id));
    }

    @Operation(summary = "Lister tous les contrats", description = "Retourne la liste de tous les contrats de location sous forme de DTOs")
    @ApiResponse(responseCode = "200", description = "Liste des contrats récupérée avec succès")
    @GetMapping
    public ResponseEntity<List<ContratDTO>> findAll() {
        return ResponseEntity.ok(contratService.findAll());
    }

    @Operation(summary = "Mettre à jour un contrat", description = "Modifie les caractéristiques d'un contrat existant avec validation")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contrat mis à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Contrat introuvable"),
            @ApiResponse(responseCode = "400", description = "Données de mise à jour invalides")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ContratDTO> update(
            @Parameter(description = "Identifiant du contrat à modifier", required = true)
            @PathVariable Long id,
            @Valid @RequestBody ContratDTO dto) {
        return ResponseEntity.ok(contratService.update(id, dto));
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

    @Operation(summary = "Affecter un contrat à une réservation", description = "Associe un contrat (recherché par date de signature) à une réservation (recherchée par dates de début et fin)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contrat affecté à la réservation avec succès"),
            @ApiResponse(responseCode = "404", description = "Contrat ou réservation introuvable"),
            @ApiResponse(responseCode = "400", description = "Dates invalides ou réservation déjà associée")
    })
    @PutMapping("/affecter-reservation")
    public ResponseEntity<ContratDTO> affecterContratAReservation(
            @Parameter(description = "Date de signature du contrat (YYYY-MM-DD)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateSignature,
            @Parameter(description = "Date de début de la réservation (YYYY-MM-DD)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @Parameter(description = "Date de fin de la réservation (YYYY-MM-DD)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        return ResponseEntity.ok(contratService.affecterContratAReservation(dateSignature, dateDebut, dateFin));
    }
}
