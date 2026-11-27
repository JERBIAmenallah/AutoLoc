# Note de Synthèse — Atelier 8 : Tâches Planifiées (Spring Scheduling)

**Projet :** AutoLoc (`autoloc-api`)  
**Module :** UP Architecture des Systèmes d'Information (ASI)  
**Étudiant :** JERBI Amenallah (Groupe 1 - 4CC14)  

---

## 1. Activation de la Planification

L'annotation `@EnableScheduling` a été ajoutée sur la classe principale `AutolocApiApplication` (`tn.esprit.autoloc.AutolocApiApplication`) afin d'activer la détection et l'exécution automatique des méthodes annotées avec `@Scheduled` dans le contexte Spring Boot.

---

## 2. Services Planifiés Implémentés

| Service | Méthode | Expression Cron | Fréquence | Rôle métier |
| :--- | :--- | :--- | :--- | :--- |
| `ContratSchedulerService` | `identifierContratsProchesEcheance()` | `0 0 7 ? * MON-FRI` | Lundi au vendredi à 07h00 | Identifie et journalise les contrats valides dont la réservation expire dans les 48 heures (`aujourd'hui` à `J+2`). |
| `VehiculeSchedulerService` | `libererVehiculesFinLocation()` | `0 0 1 * * ?` | Tous les jours à 01h00 | Clôture les réservations échues (`TERMINEE`) et remet automatiquement leurs véhicules à `DISPONIBLE`. |

### Détail des requêtes et traitements

1. **Suivi des contrats arrivant à échéance (`ContratSchedulerService`)**
   - Requête JPQL ajoutée dans `IContratRepository` : `findContratsProchesEcheance(LocalDate maintenant, LocalDate dans48h, List<StatutReservation> statutsExclus)`.
   - Utilise `JOIN FETCH c.reservation r` pour éviter tout problème de chargement paresseux (`LazyInitializationException`), en filtrant sur `c.valide = true`, `r.dateFin BETWEEN :maintenant AND :dans48h` et `r.statut NOT IN (TERMINEE, ANNULEE)`.
   - Exécutée en `@Transactional(readOnly = true)` avec journalisation SLF4J (`idContrat`, `idReservation`, `dateFin`, `montantTotal`).

2. **Libération automatique des véhicules (`VehiculeSchedulerService`)**
   - Requête dérivée Spring Data JPA ajoutée dans `IReservationRepository` : `findByDateFinBeforeAndStatutNotIn(LocalDate date, List<StatutReservation> statutsExclus)`.
   - Exécutée en `@Transactional` : pour chaque réservation dont `dateFin < LocalDate.now()` (hors `TERMINEE` et `ANNULEE`), passe `reservation.statut` à `StatutReservation.TERMINEE`, remet `vehicule.statut` à `StatutVehicule.DISPONIBLE` si un véhicule est associé, enregistre les deux entités et journalise l'opération via SLF4J.

---

## 3. Rappel sur le Format Cron Spring (6 champs)

Format utilisé par Spring `@Scheduled` : `seconde minute heure jourDuMois mois jourDeLaSemaine`

- `0 0 7 ? * MON-FRI` : à `07:00:00`, sans contrainte sur le jour du mois (`?`), tous les mois (`*`), du lundi au vendredi (`MON-FRI`).
- `0 0 1 * * ?` : à `01:00:00`, tous les jours du mois (`*`), tous les mois (`*`), quel que soit le jour de la semaine (`?`).

---

## 4. Vérification par Tests Unitaires

Les tests ont été ajoutés dans `src/test/java/tn/esprit/autoloc/service/SchedulerServicesTest.java` (JUnit 5 + Mockito) et exécutés avec les suites existantes :

```powershell
.\mvnw.cmd test "-Dtest=SchedulerServicesTest,AffectationServicesTest,DecoratorPatternTest"
```

### Couverture de `SchedulerServicesTest` (8 tests)
- Présence de `@EnableScheduling` sur `AutolocApiApplication`.
- Vérification par réflexion et `CronExpression.isValidExpression(...)` des expressions `"0 0 7 ? * MON-FRI"` et `"0 0 1 * * ?"`.
- `ContratSchedulerService` : détection des contrats dans la fenêtre `[LocalDate.now(), LocalDate.now().plusDays(2)]` et gestion du cas sans résultat.
- `VehiculeSchedulerService` : passage à `DISPONIBLE` + `TERMINEE` avec sauvegarde des deux repositories, gestion d'une réservation sans véhicule associé, et gestion d'une liste vide.

### Résultats Maven Surefire
- `DecoratorPatternTest` : **4 tests**, 0 échec, 0 erreur, 0 ignoré
- `AffectationServicesTest` : **6 tests**, 0 échec, 0 erreur, 0 ignoré
- `SchedulerServicesTest` : **8 tests**, 0 échec, 0 erreur, 0 ignoré
- **Total : 18 tests exécutés, 0 échec, 0 erreur, 0 ignoré (`BUILD SUCCESS`)**

Les captures de preuve (code source des schedulers et exécution Maven + logs SLF4J) sont archivées dans `docs/captures_atelier8/`.
