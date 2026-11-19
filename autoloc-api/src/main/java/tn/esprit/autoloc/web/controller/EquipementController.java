package tn.esprit.autoloc.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.service.IEquipementService;
import tn.esprit.autoloc.web.dto.EquipementDTO;

@RestController
@RequestMapping("/equipements")
@RequiredArgsConstructor
@Tag(name = "Équipements", description = "Endpoints de gestion des équipements pour véhicules")
public class EquipementController {

    private final IEquipementService equipementService;

    @Operation(summary = "Créer un équipement", description = "Enregistre un nouvel équipement via DTO validé")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Équipement créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides fournies")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<EquipementDTO> create(@Valid @RequestBody EquipementDTO dto) {
        return new ResponseEntity<>(equipementService.create(dto), HttpStatus.CREATED);
    }

    @Operation(summary = "Obtenir un équipement par ID", description = "Retourne les détails d'un équipement sous forme de DTO")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Équipement trouvé"),
            @ApiResponse(responseCode = "404", description = "Équipement introuvable")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EquipementDTO> findById(
            @Parameter(description = "Identifiant de l'équipement", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(equipementService.findById(id));
    }

    @Operation(summary = "Lister tous les équipements", description = "Retourne la liste complète des équipements sous forme de DTOs")
    @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    @GetMapping
    public ResponseEntity<List<EquipementDTO>> findAll() {
        return ResponseEntity.ok(equipementService.findAll());
    }

    @Operation(summary = "Mettre à jour un équipement", description = "Modifie les caractéristiques d'un équipement")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Équipement mis à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Équipement introuvable"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EquipementDTO> update(
            @Parameter(description = "Identifiant de l'équipement", required = true)
            @PathVariable Long id,
            @Valid @RequestBody EquipementDTO dto) {
        return ResponseEntity.ok(equipementService.update(id, dto));
    }

    @Operation(summary = "Supprimer un équipement", description = "Supprime un équipement")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Équipement supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Équipement introuvable")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteById(
            @Parameter(description = "Identifiant de l'équipement à supprimer", required = true)
            @PathVariable Long id) {
        equipementService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
