# Note de Synthèse — Atelier 5 : API REST, Documentation OpenAPI/Swagger et Tests Postman

**Projet :** AutoLoc (`autoloc-api`)  
**Module :** UP Architecture des Systèmes d'Information (ASI)  
**Étudiant :** JERBI Amenallah (Groupe 1 - 4CC14)  

---

## Section A — Dépendances et Configuration

### 1. Dépendance SpringDoc OpenAPI
Pour générer automatiquement la documentation OpenAPI 3 et exposer l'interface graphique Swagger UI sous **Spring Boot 3.2.5** (Java 17), la dépendance standard suivante a été intégrée dans le fichier `pom.xml` :

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.5.0</version>
</dependency>
```

### 2. Configuration du préfixe d'API et du serveur (`application.properties`)
Afin de centraliser l'accès aux ressources REST sous un préfixe commun et d'éviter les conflits de ports avec les services locaux (notamment Oracle XE sur le port 8080), la configuration suivante a été appliquée :

```properties
server.port=8089
server.servlet.context-path=/api

# Jackson
spring.jackson.serialization.fail-on-empty-beans=false

# Swagger / OpenAPI
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.api-docs.path=/v3/api-docs
```

* **Conséquence sur les Contrôleurs :** Avec `server.servlet.context-path=/api`, le préfixe `/api` est géré globalement par le conteneur servlet Tomcat. Les annotations `@RequestMapping` des contrôleurs déclarent donc directement la ressource métier (ex. `@RequestMapping("/vehicules")`), ce qui résout automatiquement vers `http://localhost:8089/api/vehicules`.
* **Points d'accès Swagger :**
  - Interface interactive Swagger UI : `http://localhost:8089/api/swagger-ui.html`
  - Spécification OpenAPI au format JSON : `http://localhost:8089/api/v3/api-docs`

---

## Section B — Architecture des Contrôleurs REST et Documentation OpenAPI

### 1. Synthèse des Contrôleurs CRUD
Chaque entité principale est exposée via un `@RestController` injectant son service métier via constructeur (`@RequiredArgsConstructor` de Lombok) :

| Contrôleur | Préfixe URI | Tag Swagger | Opérations exposées |
| :--- | :--- | :--- | :--- |
| `VehiculeController` | `/vehicules` | `Véhicules` | `POST` (201), `GET` all (200), `GET` {id} (200/404), `PUT` {id} (200/404), `DELETE` {id} (204/404) |
| `ClientController` | `/clients` | `Clients` | `POST` (201), `GET` all (200), `GET` {id} (200/404), `PUT` {id} (200/404), `DELETE` {id} (204/404) |
| `AgenceController` | `/agences` | `Agences` | `POST` (201), `GET` all (200), `GET` {id} (200/404), `PUT` {id} (200/404), `DELETE` {id} (204/404) |
| `ContratController` | `/contrats` | `Contrats` | `POST` (201 avec cascade), `GET` all (200), `GET` {id} (200/404), `PUT` {id} (200/404), `DELETE` {id} (204/404) |
| `EmployeController` | `/employes` | `Employés` | `POST` (201), `GET` all (200), `GET` {id} (200/404), `PUT` {id} (200/404), `DELETE` {id} (204/404) |
| `EquipementController` | `/equipements` | `Équipements` | `POST` (201), `GET` all (200), `GET` {id} (200/404), `PUT` {id} (200/404), `DELETE` {id} (204/404) |
| `MaintenanceController` | `/maintenances` | `Maintenances` | `POST` (201), `GET` all (200), `GET` {id} (200/404), `PUT` {id} (200/404), `DELETE` {id} (204/404) |
| `ReservationController` | `/reservations` | `Réservations` | `POST` (201), `GET` all (200), `GET` {id} (200/404), `PUT` {id} (200/404), `DELETE` {id} (204/404) |
| `PaiementController` | `/paiements` | `Paiements` | `GET` all (200), `GET` {id} (200/404) *(écriture déléguée au contrat)* |

### 2. Annotations SpringDoc mises en œuvre
* `@Tag(name, description)` : Regroupe logiquement les opérations d'une même ressource dans Swagger UI.
* `@Operation(summary, description)` : Décrit l'intention métier de l'endpoint et ses détails de fonctionnement.
* `@ApiResponses` et `@ApiResponse(responseCode, description)` : Documente de façon exhaustive les codes retours HTTP possibles (ex. 201 Created, 204 No Content, 400 Bad Request, 404 Not Found).
* `@Parameter(description, required)` : Documente les paramètres d'URI (`@PathVariable Long id`).
* `@ResponseStatus` : Déclare explicitement le statut HTTP de succès sur la méthode pour la documentation OpenAPI (`HttpStatus.CREATED` ou `HttpStatus.NO_CONTENT`).

---

## Section C — Gestion des Relations Bidirectionnelles et Sérialisation JSON

### 1. La problématique du cycle infini Jackson
Dans un modèle JPA relationnel comportant des associations bidirectionnelles (par exemple `Contrat ↔ Paiement` ou `Client ↔ Reservation`), le sérialiseur JSON de Jackson inspecte les getters de chaque objet de manière récursive. Sans garde-fou, `Contrat` sérialise sa liste de `Paiement`, chaque `Paiement` sérialise son `Contrat`, entraînant une boucle infinie et une exception fatale `StackOverflowError` (ou `JsonMappingException`).

### 2. Solutions architecturales appliquées

#### A. Le couple `@JsonManagedReference` et `@JsonBackReference` (Relation forte avec cascade)
Appliqué sur l'agrégation métier `Contrat ↔ Paiement` :
* Dans `Contrat.java` :
  ```java
  @OneToMany(mappedBy = "contrat", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("contrat-paiements")
  private List<Paiement> paiements;
  ```
* Dans `Paiement.java` :
  ```java
  @ManyToOne
  @JsonBackReference("contrat-paiements")
  private Contrat contrat;
  ```
* **Bénéfice :** Lors de la récupération d'un contrat (`GET /api/contrats/1`), la liste des paiements associés est parfaitement sérialisée dans le JSON, mais le lien inverse vers le contrat parent est omis, rompant proprement le cycle sans perdre l'information fille. Grâce à `cascade = CascadeType.ALL`, la création d'un contrat via un unique appel `POST` persiste simultanément le contrat et tous ses paiements.

#### B. L'annotation `@JsonIgnore` (Relations inverses et collections parentes)
Pour les autres entités où la navigation inverse n'est pas requise dans le payload principal du parent, `@JsonIgnore` a été apposé sur les collections :
* `Client.reservations`
* `Agence.employes` et `Agence.vehicules`
* `Vehicule.reservations` et `Vehicule.maintenances`
* `Equipement.vehicules`
* `Reservation.contrat`

---

## Section D — Gestion Centralisée des Exceptions (`GlobalExceptionHandler`)

Pour respecter les standards REST et fournir des réponses d'erreur exploitables aux clients HTTP, une gestion globale a été mise en place avec `@RestControllerAdvice` dans la classe `GlobalExceptionHandler` :

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
```

### Format standardisé du corps d'erreur JSON (`ErrorResponse`)
Toutes les erreurs renvoient une structure uniforme :
```json
{
  "statut": 404,
  "message": "Vehicule introuvable avec l'identifiant 999",
  "horodatage": "2026-10-23T16:15:30"
}
```

---

## Section E — Stratégie et Validation par Tests Postman

La collection `AutoLoc_Atelier5.postman_collection.json` a été conçue pour valider l'ensemble des exigences de l'atelier via la variable globale `{{baseUrl}} = http://localhost:8089/api` :

1. **Dossier 1 — Documentation OpenAPI :**
   - Vérification de la disponibilité du schéma JSON OpenAPI sur `/v3/api-docs`.
   - Vérification du chargement de Swagger UI sur `/swagger-ui.html`.
2. **Dossiers 2, 3 et 4 — CRUD Véhicules, Clients et Agences :**
   - Scénarios nominaux : Création (201 Created), Consultation de la liste (200 OK), Consultation par ID (200 OK), Mise à jour (200 OK), Suppression (204 No Content).
   - Scénarios d'erreur : Requête avec identifiant inexistant (404 Not Found avec payload `ErrorResponse`), Requête avec données invalides (400 Bad Request avec motif d'invalidation).
3. **Dossier 5 — Cascade et Intégrité Contrat / Paiements :**
   - Envoi d'un payload `Contrat` contenant deux objets `Paiement` imbriqués.
   - Validation que le contrat est persisté avec statut 201 et que les deux paiements sont générés avec leurs identifiants en base.
   - Relecture du contrat créé pour prouver l'absence de récursion infinie Jackson.
   - Test de validation métier : création d'un contrat avec un montant négatif rejetée en 400 Bad Request.
