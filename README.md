# Simulateur de Bourse - Instructions d'exécution sous Eclipse

Ce document explique comment configurer, exécuter et utiliser le projet "Simulateur de Bourse" sous Eclipse.

## Prérequis
- **Eclipse IDE** : Version compatible avec Java (ex. Eclipse IDE for Java Developers).
- **JDK** : Java Development Kit (version 8 ou supérieure) configuré dans Eclipse.
- **Fichiers CSV** : Les fichiers `entreprises_bourse.csv` et `events.csv` doivent être présents dans le dossier `src`.
- **Bibliothèques externes** : Les fichiers JAR suivants doivent être inclus dans le projet :
  - jfreechart-1.0.19.jar
  - jcommon-1.0.23.jar
  - jfreesvg-2.0.jar
  - jfreechart-1.0.19-experimental.jar
  - jfreechart-1.0.19-swt.jar
  - orsoncharts-1.4-eval-nofx.jar
  - orsonpdf-1.6-eval.jar
  - swtgraphics2d.jar
  - servlet.jar
  - log4j-1.2.17.jar
  - junit-4.11.jar
  - hamcrest-core-1.3.jar

## Structure du projet
Le projet est organisé en plusieurs packages :
- `data` : Classes de base (`Actif`, `Action`, `Obligation`, `Entreprise`, `Evenement`, `Portfeuille`, `Transaction`, `Utilisateur`).
- `gestion` : Classes de traitement (`Actualite`, `Bourse`, `GestionPortfeuille`).
- `gui` : Classes de l'interface graphique (`EvenementsPanel`, `GraphiquePanel`, `InfoPanel`, `ListeActionsPanel`, `MainWindow`, `PersonalisationTable`, `PersoTableauPortfeuille`, `PortfeuillePanel`, `Tableaudedonnes`, `TableauHistorique`, `TableauPortefeuille`).
- `test` : Classe principale (`Test`) pour lancer l'application.
- Fichiers CSV :
  - `src/entreprises_bourse.csv` : Liste des entreprises.
  - `src/events.csv` : Liste des événements impactant les actions.

## Étapes pour exécuter le programme

1. **Importer le projet** :
   - Ouvrez Eclipse.
   - Allez à `File > Import > General > Existing Projects into Workspace`.
   - Sélectionnez le dossier racine du projet (contenant `data`, `gestion`, `gui`, `test`, `src`).
   - Cliquez sur `Finish`.

2. **Configurer les bibliothèques externes** :
   - Copiez les fichiers JAR listés ci-dessus dans un dossier (par exemple, `lib`) à la racine de votre projet.
   - Dans Eclipse : Clic droit sur le projet > `Properties` > `Java Build Path` > `Libraries` > `Add External JARs`.
   - Naviguez vers le dossier `lib`, sélectionnez tous les JARs mentionnés, puis cliquez sur `Apply and Close`.

3. **Vérifier les fichiers CSV** :
   - Placez `entreprises_bourse.csv` et `events.csv` dans le dossier `src`.
   - Vérifiez que les chemins dans le code (`src/entreprises_bourse.csv` dans `Bourse`, `src/events.csv` dans `Actualite`) sont corrects. Si nécessaire, ajustez les chemins ou déplacez les fichiers à la racine du projet et mettez à jour le code.

4. **Exécuter le programme** :
   - Ouvrez le package `test` dans l'Explorateur de projets.
   - Clic droit sur `Test.java` > `Run As` > `Java Application`.
   - La fenêtre "Simulateur de Bourse" s'ouvre avec l'interface graphique.

## Fonctionnalités du Simulateur de Bourse
Le simulateur permet de gérer un portefeuille boursier virtuel avec un solde initial de 50 000 €. Voici les principales fonctionnalités accessibles via l'interface graphique :

1. **Gestion des actifs (achat et vente)** :
   - **Acheter des actions** :
     - Dans l'onglet "Liste des Actions", sélectionnez une entreprise dans le tableau des actions.
     - Cliquez sur la ligne ou utilisez le menu contextuel ("Acheter") pour ouvrir une boîte de dialogue.
     - Entrez la quantité d'actions à acheter. Le solde disponible est vérifié avant validation.
   - **Acheter des obligations** :
     - Dans l'onglet "Liste des Actions", allez à l'onglet "Obligations".
     - Sélectionnez une obligation et cliquez pour acheter (une seule obligation à la fois).
     - Le prix, le taux d'intérêt et l'échéance sont affichés.
   - **Vendre des actions** :
     - Dans l'onglet "Mon Portefeuille", sélectionnez une action dans le tableau des actifs détenus.
     - Choisissez de vendre tout ou une partie via une boîte de dialogue. Le solde est mis à jour après la vente.
   - **Vendre des obligations** :
     - Dans l'onglet "Mon Portefeuille", sélectionnez une obligation et vendez-la (vente unitaire).
     - Le solde est crédité du prix actuel.

2. **Consultation du marché** :
   - **Tableau des actions** : Affiche les entreprises avec leur ID, nom, prix actuel, variation (%), secteur et capitalisation (M€).
   - **Tableau des obligations** : Affiche les obligations avec leur ID, nom, prix, taux d'intérêt, échéance, secteur et capitalisation.
   - **Filtres et recherche** :
     - Utilisez la barre de recherche pour trouver une entreprise par nom ou secteur.
     - Filtrez par secteur via une liste déroulante pour limiter l'affichage.

3. **Gestion du portefeuille** :
   - **Vue des actifs détenus** : Dans l'onglet "Mon Portefeuille", consultez vos actions et obligations avec leur type, quantité, prix d'achat, prix actuel, variation (%) et valeur totale.
   - **Historique des transactions** : Consultez toutes les transactions (achats/ventes) avec le type, l'action, l'entreprise, la quantité, le prix unitaire, le total et la date.
   - **Statistiques** : Un graphique en secteurs montre la répartition des investissements historiques par secteur.

4. **Analyse graphique** :
   - Dans l'onglet "Graphiques", deux graphiques sont disponibles :
     - **Évolution des prix** : Courbes montrant l'évolution des prix des actions par tour, filtrables par secteur.
     - **Performance par secteur** : Histogramme affichant la variation moyenne (%) des entreprises par secteur.
   - Filtrez les données par secteur via une liste déroulante.

5. **Suivi des événements** :
   - Le panneau "Événements" (en bas de l'interface) affiche les événements boursiers (ex. crises, innovations).
   - Cliquez sur un événement pour voir ses détails : nom, catégorie, secteur, sous-secteur, description, impact et durée.
   - Les événements influencent les prix des actions selon leur secteur et leur degré d'impact.

6. **Simulation du marché** :
   - **Tour par tour** : Cliquez sur "Tour Suivant" pour avancer d'un tour. Les prix, événements et portefeuille sont mis à jour.
   - **Lecture automatique** : Activez "Lecture Auto" pour lancer une simulation continue (tours toutes les 1,2 secondes). Cliquez à nouveau pour mettre en pause.
   - Les prix évoluent selon un modèle incluant volatilité, tendances sectorielles, événements et momentum.

7. **Création d'entreprises** :
   - Cliquez sur "Créer Entreprise" pour ajouter une nouvelle entreprise à la bourse.
   - Remplissez un formulaire avec : nom, secteur, sous-secteur, devise, bourse, capital initial, prix par action, volume moyen, volatilité et variation initiale.
   - L'entreprise est ajoutée au marché et apparaît dans le tableau des actions.

8. **Suivi financier** :
   - Le panneau supérieur affiche :
     - **Solde disponible** : Argent restant pour investir.
     - **Montant investi** : Total investi dans les actifs.
     - **Bénéfice/Perte** : Gain ou perte par rapport au solde initial (50 000 €).
     - **Santé du portefeuille** : Barre de progression (0-100%) basée sur les performances des investissements.

## Résolution des problèmes
- **Erreur "File not found" (CSV)** :
  - Vérifiez que les fichiers CSV sont dans `src`.
  - Corrigez les chemins dans le code si nécessaire.
- **Erreur de dépendance (ClassNotFoundException)** :
  - Assurez-vous que tous les JARs listés sont ajoutés au Build Path.
  - Vérifiez la compatibilité des versions avec votre JDK.
- **Problèmes d'affichage graphique** :
  - Vérifiez la compatibilité du JDK (8+).
  - Résolution d'écran recommandée : 1200x800.

## Remarques
- Solde initial : 50 000 €.
- Les prix évoluent à chaque tour selon les événements, la volatilité et les tendances.
- Les fichiers CSV doivent respecter le format attendu (voir colonnes dans `Bourse` et `Actualite`).
- L'interface est intuitive, avec des tableaux, graphiques et boîtes de dialogue pour faciliter la gestion.
