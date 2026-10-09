# Notes Atelier 3 — Couche Repository et Analyse SonarQube

## 1. Choix des interfaces Repository

Toutes les interfaces de la couche repository étendent `JpaRepository<T, ID>` afin de bénéficier du CRUD complet, du retour en `List` (au lieu de simple `Iterable`), des fonctionnalités de pagination/tri et des méthodes spécifiques JPA (`saveAndFlush`, `deleteAllInBatch`, etc.).

| Interface | Étend | Justification |
| :--- | :--- | :--- |
| `IAgenceRepository` | `JpaRepository<Agence, Long>` | CRUD complet, gestion des listes d'agences, tri et pagination. |
| `IClientRepository` | `JpaRepository<Client, Long>` | CRUD complet, recherche de clients et manipulation sous forme de List. |
| `IContratRepository` | `JpaRepository<Contrat, Long>` | CRUD complet, findAll renvoie une List, saveAndFlush disponible. |
| `IEmployeRepository` | `JpaRepository<Employe, Long>` | CRUD complet, gestion du personnel et accès direct par identifiant. |
| `IEquipementRepository` | `JpaRepository<Equipement, Long>` | CRUD complet pour les équipements disponibles sur le parc. |
| `IMaintenanceRepository` | `JpaRepository<Maintenance, Long>` | CRUD complet pour le suivi des interventions de maintenance. |
| `IPaiementRepository` | `JpaRepository<Paiement, Long>` | Lecture et consultation des paiements associés aux contrats. |
| `IReservationRepository` | `JpaRepository<Reservation, Long>` | CRUD complet, suivi du cycle de vie des réservations de véhicules. |
| `IVehiculeRepository` | `JpaRepository<Vehicule, Long>` | CRUD complet, recherche par critères, gestion du parc et pagination. |

---

## 2. Anomalies SonarQube for IDE (SonarLint) et Corrections

| Anomalie SonarQube for IDE | Règle / explication | Correction apportée |
| :--- | :--- | :--- |
| Utilisation de `@Data` sur les entités avec relations | **java:S2160** : Risque de boucle infinie (`StackOverflowError`) dans les méthodes générées `equals`, `hashCode` et `toString` sur les associations bidirectionnelles. | Remplacement par des annotations ciblées Lombok : `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`. |
| Imports inutilisés dans certaines classes | **java:S1128** : Présence d'imports inutilisés polluant le code source. | Suppression et nettoyage de tous les imports superflus dans l'ensemble des fichiers du package `domain`. |
| Respect de la convention de nommage des interfaces Repository | Convention de nommage projet AutoLoc (`I...Repository`). | Préfixage systématique de chaque interface par `I` (`IVehiculeRepository`, `IContratRepository`...) dans le package `repository`. |
| Absence éventuelle de constructeur sans argument exigé par JPA | Spécification Jakarta Persistence : toute entité persistante doit posséder un constructeur par défaut. | Ajout de `@NoArgsConstructor` sur toutes les entités du domaine. |
