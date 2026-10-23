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
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.service.IClientService;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
@Tag(name = "Clients", description = "Endpoints de gestion des clients")
public class ClientController {

    private final IClientService clientService;

    @Operation(summary = "Créer un nouveau client", description = "Enregistre un nouveau client dans le système")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Client créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides fournies")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Client> create(@RequestBody Client client) {
        return new ResponseEntity<>(clientService.create(client), HttpStatus.CREATED);
    }

    @Operation(summary = "Obtenir un client par son identifiant", description = "Retourne les détails d'un client")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Client trouvé"),
            @ApiResponse(responseCode = "404", description = "Client introuvable")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Client> findById(
            @Parameter(description = "Identifiant unique du client", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(clientService.findById(id));
    }

    @Operation(summary = "Lister tous les clients", description = "Retourne la liste complète des clients enregistrés")
    @ApiResponse(responseCode = "200", description = "Liste des clients récupérée avec succès")
    @GetMapping
    public ResponseEntity<List<Client>> findAll() {
        return ResponseEntity.ok(clientService.findAll());
    }

    @Operation(summary = "Mettre à jour un client", description = "Modifie les informations d'un client existant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Client mis à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Client introuvable"),
            @ApiResponse(responseCode = "400", description = "Données de mise à jour invalides")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Client> update(
            @Parameter(description = "Identifiant du client à modifier", required = true)
            @PathVariable Long id,
            @RequestBody Client client) {
        return ResponseEntity.ok(clientService.update(id, client));
    }

    @Operation(summary = "Supprimer un client", description = "Supprime un client du système")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Client supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Client introuvable")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteById(
            @Parameter(description = "Identifiant du client à supprimer", required = true)
            @PathVariable Long id) {
        clientService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
