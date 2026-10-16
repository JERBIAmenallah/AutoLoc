# Note de Synthèse — Atelier 4 : Couche Service, IoC et Injection de Dépendances

**Projet :** AutoLoc (`autoloc-api`)  
**Module :** UP Architecture des Systèmes d'Information (ASI)  
**Étudiant :** JERBI Amenallah (Groupe 1 - 4CC14)  

---

## Section A — Modes d'injection comparés

| Critère d'évaluation | Injection par Constructeur (Recommandée) | Injection par Attribut (`@Autowired` direct) |
| :--- | :--- | :--- |
| **Champ `final` possible ?** | **Oui**, le champ est initialisé à l'instanciation | **Non**, le champ doit être mutable pour l'injection par réflexion |
| **Dépendance visible dans la signature ?** | **Oui**, explicitée dans les paramètres du constructeur | **Non**, dépendances cachées à l'intérieur de la classe |
| **Utilisable hors conteneur sans dépendance `null` ?** | **Oui**, instanciable directement via `new Service(mockRepo)` dans les tests | **Non**, les dépendances restent `null` sauf si instanciées via réflexion |
| **Signalé par SonarQube for IDE ?** | **Non**, respecte les standards Clean Architecture | **Oui**, règle `java:S6813` (*"Field dependency injection should be avoided"*) |

---

## Section B — Services et dépendances injectées

| Service | Dépendances injectées | Mode d'injection | Justification |
| :--- | :--- | :--- | :--- |
| `AgenceServiceImpl` | `IAgenceRepository` | Constructeur (`@RequiredArgsConstructor`) | Garantit l'accès à la persistance des agences et la validation du nom obligatoire dès l'instanciation. |
| `ClientServiceImpl` | `IClientRepository` | Constructeur (`@RequiredArgsConstructor`) | IClientRepository requis pour vérifier et persister les clients avec validation d'unicité et de présence d'identifiants. |
| `ContratServiceImpl` | `IContratRepository` | Constructeur (`@RequiredArgsConstructor`) | IContratRepository requis pour manipuler les contrats, valider les montants et gérer la cascade sur les paiements. |
| `EmployeServiceImpl` | `IEmployeRepository` | Constructeur (`@RequiredArgsConstructor`) | IEmployeRepository requis pour persister les employés et valider la présence du rôle et du nom. |
| `EquipementServiceImpl` | `IEquipementRepository` | Constructeur (`@RequiredArgsConstructor`) | IEquipementRepository requis pour gérer les équipements et vérifier la non-vacuité du libellé. |
| `MaintenanceServiceImpl` | `IMaintenanceRepository` | Constructeur (`@RequiredArgsConstructor`) | IMaintenanceRepository requis pour planifier les entretiens et vérifier la cohérence des dates début/fin. |
| `PaiementServiceImpl` | `IPaiementRepository` | Constructeur (`@RequiredArgsConstructor`) | IPaiementRepository requis en lecture seule (`findById`, `findAll`) ; les écritures sont déléguées au Contrat. |
| `ReservationServiceImpl` | `IReservationRepository` | Constructeur (`@RequiredArgsConstructor`) | IReservationRepository requis pour persister les réservations et contrôler que `dateFin >= dateDebut`. |
| `VehiculeServiceImpl` | `IVehiculeRepository` | Constructeur (`@RequiredArgsConstructor`) | IVehiculeRepository requis pour persister les véhicules et valider le tarif journalier positif. |

---

## Section C — Analyse des messages d'erreur du conteneur

### Message 1 : Bean `IContratService` introuvable
* **Description :** `Parameter 0 of constructor in ContratController required a bean of type 'IContratService' that could not be found.`
* **Causes les plus probables côté `ContratServiceImpl` :**
  1. *Oubli d'annotation :* La classe d'implémentation n'a pas été annotée avec `@Service` (ou `@Component`), donc Spring ne l'a pas enregistrée comme bean.
  2. *Package hors scan :* La classe est placée dans un package qui n'est pas scanné par `@SpringBootApplication` (en dehors de `tn.esprit.autoloc` et ses sous-packages).
* **Correction :** Ajouter l'annotation `@Service` sur `ContratServiceImpl` et vérifier son emplacement dans `tn.esprit.autoloc.service`.

### Message 2 : Conflit d'ambiguïté (2 beans trouvés)
* **Description :** `required a single bean, but 2 were found: emailNotificateur, smsNotificateur`
* **Deux corrections possibles :**
  1. *Correction 1 (`@Primary`) :* Poser l'annotation `@Primary` sur la classe d'implémentation prioritaire (ex. sur `EmailNotificateur`) pour en faire le choix par défaut.
  2. *Correction 2 (`@Qualifier`) :* Préciser explicitement le nom du bean attendu au point d'injection avec `@Qualifier("emailNotificateur")` au niveau du paramètre du constructeur de `NotificationController`.

### Message 3 : Dépendance circulaire (`ClientServiceImpl` ↔ `ReservationServiceImpl`)
* **Description :** `The dependencies of some of the beans form a cycle: clientServiceImpl <-> reservationServiceImpl`
* **Pourquoi l'injection par constructeur rend le cycle impossible à contourner ?**  
  Spring doit instancier toutes les dépendances d'un constructeur avant d'appeler ce constructeur. Si A a besoin de B pour exister, et B a besoin de A pour exister, aucun des deux ne peut être instancié en premier.
* **Piste de conception :**  
  Appliquer le principe de responsabilité unique (SRP) : extraire la logique commune ou croisée dans un troisième service médiateur (ex. `ReservationValidationService`) ou utiliser un mécanisme d'événements applicatifs (`ApplicationEventPublisher`).

---

## Section D — Anomalies SonarQube for IDE relevées et corrigées

| Règle SonarQube | Type / Sévérité | Explication | Correction apportée |
| :--- | :--- | :--- | :--- |
| **`java:S6813`** | Code Smell (Major) | *Field dependency injection should be avoided.* L'injection sur attribut via `@Autowired` rend les tests unitaires complexes et masque les dépendances. | Remplacement par une injection via constructeur avec champs `final`, généré proprement par `@RequiredArgsConstructor`. |
| **`java:S112`** | Code Smell (Major) | *Generic exceptions should never be thrown.* Lever `RuntimeException` générique empêche un traitement ciblé des erreurs. | Création et utilisation d'une exception métier dédiée `ResourceNotFoundException`. |
| **`java:S1128`** | Code Smell (Minor) | *Unused imports should be removed.* Présence d'imports superflus dans les fichiers source. | Nettoyage systématique de tous les imports inutilisés dans les packages `service` et `web.controller`. |

---

## Section E — Questions de compréhension

### 1. Où est le `new` de `ContratServiceImpl` et qui l'exécute ?
Il n'y a aucun `new ContratServiceImpl` dans notre code applicatif. C'est le conteneur IoC de Spring (`ApplicationContext`) qui l'exécute par réflexion au démarrage lors de la phase d'instanciation des beans, après avoir détecté la classe grâce à l'annotation `@Service`.

### 2. Pourquoi un contrôleur dépendra-t-il de `IContratService` et non de `ContratServiceImpl` ?
Pour respecter le principe d'Inversion des Dépendances (DIP - SOLID) et assurer un faible couplage. Le contrôleur dépend d'une abstraction (contrat d'interface) et non d'une implémentation concrète, ce qui permet de remplacer ou mocker le service facilement lors des tests sans modifier le contrôleur.

### 3. Pourquoi un service singleton doit-il rester sans état ? Donner un exemple de bug.
Un service de scope singleton est partagé par tous les threads et toutes les requêtes de l'application. S'il conserve un état mutable dans un attribut d'instance (ex. `private Contrat contratCourant;`), une requête HTTP peut écraser les données d'une autre requête concurrente, provoquant des corruptions de données critiques (*Race Condition*).

### 4. Pourquoi `update` charge-t-il l'entité existante avant d'appeler `save` au lieu de lui passer directement l'objet reçu ?
Passer directement l'objet reçu à `save` risquerait d'écraser des champs non renseignés par des valeurs `null`, d'altérer des identifiants non modifiables ou de rompre des associations en cascade (ex. vider la liste des paiements d'un contrat si elle n'est pas transmise dans la requête). Le chargement préalable garantit une mise à jour ciblée et sécurisée.

### 5. Différences `@Component` / `@Bean`, `@Primary` / `@Qualifier` et lecture seule de `IPaiementService` :
* **`@Component` vs `@Bean` :** `@Component` est une annotation de classe scannée automatiquement ; `@Bean` est posée sur une méthode dans une classe `@Configuration` pour enregistrer manuellement un bean (souvent issu d'une bibliothèque tierce).
* **`@Primary` vs `@Qualifier` :** `@Primary` définit un bean favori par défaut au niveau de la classe ; `@Qualifier("nom")` force la sélection d'un bean spécifique au niveau du point d'injection.
* **Lecture seule de `IPaiementService` :** Un paiement est lié de manière indissociable à un contrat. Selon les règles métier d'AutoLoc, les paiements ne peuvent être créés, modifiés ou supprimés qu'à travers le cycle de vie du contrat auquel ils sont rattachés.
