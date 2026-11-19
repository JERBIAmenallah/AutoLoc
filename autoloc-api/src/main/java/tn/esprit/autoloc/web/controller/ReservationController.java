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
import tn.esprit.autoloc.service.IReservationService;
import tn.esprit.autoloc.web.dto.ReservationRequestDTO;
import tn.esprit.autoloc.web.dto.ReservationResponseDTO;
import tn.esprit.autoloc.web.dto.ReservationWithContratRequestDTO;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
@Tag(name = "Réservations", description = "Endpoints de gestion des réservations et affectations associées")
public class ReservationController {

    private final IReservationService reservationService;

    @Operation(summary = "Créer une réservation", description = "Enregistre une nouvelle réservation de véhicule")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Réservation créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides fournies")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ReservationResponseDTO> create(@Valid @RequestBody ReservationRequestDTO dto) {
        return new ResponseEntity<>(reservationService.create(dto), HttpStatus.CREATED);
    }

    @Operation(summary = "Obtenir une réservation par ID", description = "Retourne les détails d'une réservation sous forme de DTO")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Réservation trouvée"),
            @ApiResponse(responseCode = "404", description = "Réservation introuvable")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponseDTO> findById(
            @Parameter(description = "Identifiant de la réservation", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(reservationService.findById(id));
    }

    @Operation(summary = "Lister toutes les réservations", description = "Retourne la liste des réservations sous forme de DTOs")
    @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    @GetMapping
    public ResponseEntity<List<ReservationResponseDTO>> findAll() {
        return ResponseEntity.ok(reservationService.findAll());
    }

    @Operation(summary = "Mettre à jour une réservation", description = "Modifie les dates ou le statut d'une réservation")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Réservation mise à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Réservation introuvable"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ReservationResponseDTO> update(
            @Parameter(description = "Identifiant de la réservation à modifier", required = true)
            @PathVariable Long id,
            @Valid @RequestBody ReservationRequestDTO dto) {
        return ResponseEntity.ok(reservationService.update(id, dto));
    }

    @Operation(summary = "Supprimer une réservation", description = "Supprime une réservation")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Réservation supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Réservation introuvable")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteById(
            @Parameter(description = "Identifiant de la réservation à supprimer", required = true)
            @PathVariable Long id) {
        reservationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Affecter une réservation à un client", description = "Associe une réservation existante à un client recherché par son nom et prénom")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Réservation affectée au client avec succès"),
            @ApiResponse(responseCode = "404", description = "Réservation ou client introuvable"),
            @ApiResponse(responseCode = "400", description = "Paramètres invalides")
    })
    @PutMapping("/{idReservation}/affecter-client")
    public ResponseEntity<ReservationResponseDTO> affecterReservationAClient(
            @Parameter(description = "Identifiant de la réservation", required = true)
            @PathVariable Long idReservation,
            @Parameter(description = "Nom du client", required = true)
            @RequestParam String nom,
            @Parameter(description = "Prénom du client", required = true)
            @RequestParam String prenom) {
        return ResponseEntity.ok(reservationService.affecterReservationAClient(idReservation, nom, prenom));
    }

    @Operation(summary = "Créer une réservation et son contrat associé", description = "Crée simultanément une réservation et le contrat lié dans une même transaction")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Réservation et contrat créés avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Client ou véhicule introuvable")
    })
    @PostMapping("/avec-contrat")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ReservationResponseDTO> creerReservationEtContratAssocie(
            @Valid @RequestBody ReservationWithContratRequestDTO requestDTO) {
        return new ResponseEntity<>(reservationService.creerReservationEtContratAssocie(requestDTO), HttpStatus.CREATED);
    }
}
