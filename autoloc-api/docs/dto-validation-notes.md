# Note de Synthèse — Atelier 6 : Clean Code, DTOs, Mappers et Bean Validation

**Projet :** AutoLoc (`autoloc-api`)  
**Module :** UP Architecture des Systèmes d'Information (ASI)  
**Étudiant :** JERBI Amenallah (Groupe 1 - 4CC14)  

---

## 1. Problématique et Justification de l'Architecture DTO

Jusqu'à l'Atelier 5, les contrôleurs REST manipulaient directement les entités JPA (`Vehicule`, `Client`, `Contrat`). Cette pratique présente plusieurs risques majeurs en ingénierie logicielle :
1. **Sur-exposition et Sécurité :** Toutes les colonnes de la base de données sont exposées au client HTTP.
2. **Couplage fort :** Toute modification de la structure de la base de données casse immédiatement le contrat de l'API REST.
3. **Surcharge réseau (Over-fetching) :** Les relations bidirectionnelles imposaient des artifices Jackson (`@JsonIgnore`, `@JsonBackReference`) pour éviter les boucles infinies de sérialisation.

### Solution apportée par l'Atelier 6 :
L'introduction des **DTOs (Data Transfer Objects)** et des **Mappers** permet d'établir une frontière hermétique entre le modèle de persistance (Entités JPA) et le modèle d'échange (Payloads JSON REST).

```
[ Client HTTP / Postman ]
       │ (JSON DTO)
       ▼
[ @RestController ]  <--- Validation automatique via @Valid
       │ (DTO)
       ▼
[ I...Service ]
       │
       ├────> [ Mapper (@Component) ] : DTO <───> Entité JPA
       │
       ▼
[ I...Repository ] <───> [ Base de données MySQL ]
```

---

## 2. Inventaire des DTOs et Mappers créés

| DTO | Package | Rôle métier | Mapper associé |
| :--- | :--- | :--- | :--- |
| `VehiculeDTO` | `web.dto` | Payload d'échange pour les véhicules, découplé des maintenances et réservations. | `VehiculeMapper` |
| `ClientDTO` | `web.dto` | Payload d'échange pour les clients, avec validation stricte de l'email et du permis. | `ClientMapper` |
| `AgenceDTO` | `web.dto` | Payload allégé pour la gestion des agences du réseau. | `AgenceMapper` |
| `ContratDTO` | `web.dto` | Payload contenant la liste imbriquée de `PaiementDTO`, éliminant nativement toute boucle infinie. | `ContratMapper` |
| `PaiementDTO` | `web.dto` | Payload de consultation financière en lecture seule. | `PaiementMapper` |

---

## 3. Contrôles de Saisie et Bean Validation (Jakarta Validation)

La dépendance `spring-boot-starter-validation` est exploitée pour interdire tout état incohérent avant même l'exécution du service métier :

* **Sur `VehiculeDTO` :**
  - `@NotBlank(message = "L'immatriculation est obligatoire")` + `@Size(max = 20)`
  - `@NotBlank(message = "La marque est obligatoire")`
  - `@Positive(message = "Le tarif journalier doit être strictement positif")`
  - `@NotNull(message = "La catégorie est obligatoire")`
* **Sur `ClientDTO` :**
  - `@NotBlank(message = "Le nom est obligatoire")`
  - `@Email(message = "Format d'adresse email invalide")`
  - `@NotBlank(message = "Le numéro de permis est obligatoire")`
  - `@PastOrPresent(message = "La date d'inscription ne peut pas être dans le futur")`
* **Sur `ContratDTO` :**
  - `@PositiveOrZero(message = "Le montant total ne peut pas être négatif")`
  - `@Valid` sur la liste des `PaiementDTO` pour cascader la validation sur les règlements enfants.

---

## 4. Gestion Centralisée des Erreurs (`GlobalExceptionHandler`)

Lorsqu'une requête viole les contraintes de validation (`@Valid @RequestBody`), Spring Boot déclenche une `MethodArgumentNotValidException`.

Le composant `@RestControllerAdvice` intercepte cette exception et formate une réponse standardisée **HTTP 400 Bad Request** contenant une table associative (`Map<String, String>`) détaillant chaque champ en faute :

```json
{
  "statut": 400,
  "message": "Échec de validation des données transmises",
  "horodatage": "2026-10-30T22:46:27",
  "erreursValidation": {
    "immatriculation": "L'immatriculation est obligatoire",
    "tarifJournalier": "Le tarif journalier doit être strictement positif",
    "marque": "La marque est obligatoire"
  }
}
```

---

## 5. Guide Oral pour la Soutenance (Questions/Réponses)

1. **Pourquoi avoir créé des Mappers comme `@Component` plutôt qu'avec une librairie comme MapStruct ?**
   - *Réponse :* Les mappers manuels sous forme de composants Spring offrent une lisibilité totale du code, ne dépendent d'aucun processeur d'annotation tiers susceptible d'échouer lors des builds Maven, et permettent un contrôle explicite de la transformation d'agrégats (ex. Contrat vers Paiements).
2. **Où se situe la validation : dans le Controller ou dans le Service ?**
   - *Réponse :* La validation de format et de présence des champs (syntaxique) se fait à l'entrée du contrôleur grâce à `@Valid`. La validation des règles métier complexes (ex. vérification de doublon d'immatriculation ou de solde) reste dans la couche Service.
