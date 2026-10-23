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
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.service.IEmployeService;

@RestController
@RequestMapping("/employes")
@RequiredArgsConstructor
@Tag(name = "Employés", description = "Endpoints de gestion des employés")
public class EmployeController {

    private final IEmployeService employeService;

    @Operation(summary = "Créer un employé", description = "Enregistre un nouvel employé")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Employé créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides fournies")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Employe> create(@RequestBody Employe employe) {
        return new ResponseEntity<>(employeService.create(employe), HttpStatus.CREATED);
    }

    @Operation(summary = "Obtenir un employé par ID", description = "Retourne les détails d'un employé")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employé trouvé"),
            @ApiResponse(responseCode = "404", description = "Employé introuvable")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Employe> findById(
            @Parameter(description = "Identifiant de l'employé", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(employeService.findById(id));
    }

    @Operation(summary = "Lister tous les employés", description = "Retourne la liste des employés")
    @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    @GetMapping
    public ResponseEntity<List<Employe>> findAll() {
        return ResponseEntity.ok(employeService.findAll());
    }

    @Operation(summary = "Mettre à jour un employé", description = "Modifie les informations d'un employé")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employé mis à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Employé introuvable"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Employe> update(
            @Parameter(description = "Identifiant de l'employé", required = true)
            @PathVariable Long id,
            @RequestBody Employe employe) {
        return ResponseEntity.ok(employeService.update(id, employe));
    }

    @Operation(summary = "Supprimer un employé", description = "Supprime un employé")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Employé supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Employé introuvable")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteById(
            @Parameter(description = "Identifiant de l'employé", required = true)
            @PathVariable Long id) {
        employeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
