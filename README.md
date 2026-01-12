# Moteur de Jeu 2D avec LibGDX - Projet POO

**Auteur:** Gaëtan Rogeron, Lucas Bertin, Emma Doutres  
**Dépôt GitHub:** https://github.com/gaetan-rogeron/poo-2d.git

## Table des matières

- [Description](#description)
- [Prérequis](#prérequis)
- [Compilation et exécution](#compilation-et-exécution)
- [Commandes](#commandes)
- [Architecture](#architecture)
- [Utilisation de Tiled](#utilisation-de-tiled)
- [Tests](#tests)
- [Design Patterns](#design-patterns)
- [Extension du moteur](#extension-du-moteur)

## Description

Moteur de jeu 2D type RPG développé avec LibGDX. Le contenu (cartes, ennemis, objets) est entièrement configurable via Tiled sans modifier le code Java. Le jeu implémente un système de combat, des zones avec transitions fluides, et des objets collectables.

**Technologies:** LibGDX, Java 11+, Gradle, Tiled, JUnit 4

## Prérequis

- JDK 11 ou supérieur (`java -version`)
- Gradle (inclus via wrapper)
- Tiled Map Editor (optionnel, pour éditer les cartes)

## Compilation et exécution

### Compilation

Linux/Mac:
```bash
./gradlew lwjgl3:build
```

Windows:
```bash
gradlew.bat lwjgl3:build
```

### Exécution

Linux/Mac:
```bash
./gradlew lwjgl3:run
```

Windows:
```bash
gradlew.bat lwjgl3:run
```

Ou via JAR:
```bash
java -jar lwjgl3/build/libs/lwjgl3-1.0.jar
```

### Scripts d'exécution rapide

`run.sh` (Linux/Mac):
```bash
#!/bin/bash
./gradlew lwjgl3:run
```

`run.bat` (Windows):
```batch
@echo off
gradlew.bat lwjgl3:run
pause
```

## Commandes

**Déplacement:** Flèches directionnelles  
**Attaque:** Espace ou X  
**Debug:** T (changer transition), +/- (vitesse transition)

## Architecture

### Structure des packages

```
core/src/main/java/gle/game2d/
├── behavior/              # Strategy Pattern
├── collision/             # Gestion collisions
├── enemy/                 # Factory, Template Method, Builder
├── object/                # Objets collectables
├── player/                # Composition
├── ui/                    # Interface utilisateur
├── zone/                  # Strategy + Observer
└── Main.java
```

### Séparation MVC

- **Modèle:** `enemy/`, `player/`, `collision/`, `object/`
- **Vue:** `ui/`, composants d'animation
- **Contrôleur:** `behavior/`, `zone/`, managers

## Utilisation de Tiled

### Ouvrir la carte

1. Ouvrir Tiled
2. Fichier → Ouvrir → `assets/maps/map.tmx`

### Couches (Layers)

- **Background / Background2:** Décor (non bloquant)
- **Collision:** Tuiles bloquantes (propriété `blocked = true`)
- **Object:** Objets de spawn

### Ajouter des éléments

**Ennemi:**
1. Couche "Object" → Insérer objet
2. Nom: `Slime`, `Skeleton` ou `King_Slime`

**Objet collectable:**
1. Couche "Object" → Insérer objet
2. Nom: `Potion` ou `Sword`

**Position joueur:**
1. Déplacer l'objet nommé `Player`

## Tests

Le projet inclut 65 tests unitaires.

**Exécuter tous les tests:**
```bash
./gradlew test
```

**Par catégorie:**
```bash
./gradlew test --tests gle.game2d.player.*
./gradlew test --tests gle.game2d.enemy.*
./gradlew test --tests gle.game2d.behavior.*
./gradlew test --tests gle.game2d.zone.*
```

Rapport HTML: `core/build/reports/tests/test/index.html`

## Design Patterns

- **Strategy:** Comportements ennemis (`behavior/`), transitions caméra (`zone/`)
- **Observer:** Système de zones (`IZoneObserver`)
- **Factory + Registry:** Création d'ennemis (`EnemyFactory`)
- **Template Method:** Structure fixe avec hooks (`EnemyBase`, `Player`)
- **Builder:** Construction de stats (`EnemyStats.Builder`)
- **Singleton:** Directions du joueur (`PlayerDirection`)
- **Composition:** Composants du joueur (Health, Movement, Attack, Animation)
- **Facade:** Accès simplifié à Tiled (`CollisionMap`)

## Extension du moteur

### Ajouter un ennemi

**1. Créer la classe:**
```java
class GoblinEnemy extends EnemyBase {
    public GoblinEnemy(float x, float y, EnemyStats stats, 
                       IEnemyBehavior behavior, CollisionMap collisionMap) {
        super(x, y, stats, behavior, collisionMap);
    }
    
    @Override
    protected void initializeAnimations() {
        // Charger spritesheet
    }
}
```

**2. Enregistrer dans la Factory:**
```java
EnemyFactory.registerEnemyType("Goblin", (x, y, collisionMap) -> {
    EnemyStats stats = new EnemyStats.Builder()
        .withDimensions(32, 32)
        .withSpeed(60f)
        .withHealth(40)
        .withDamage(15)
        .build();
    return new GoblinEnemy(x, y, stats, new ChasePlayerBehavior(), collisionMap);
});
```

**3. Utiliser dans Tiled:** Créer un objet nommé `Goblin`

### Ajouter un comportement

```java
public class PatrolBehavior implements IEnemyBehavior {
    @Override
    public void execute(EnemyBase enemy, float deltaTime, Player player) {
        // Logique de patrouille
    }
}
```

### Ajouter un objet collectable

**1. Implémenter ICollectable:**
```java
public class Shield implements ICollectable {
    @Override
    public void onCollect(Player player) {
        // Effet sur le joueur
    }
    // Autres méthodes...
}
```

**2. Enregistrer dans ObjectManager:**
```java
// Dans loadObjectsFromMap()
else if ("Shield".equalsIgnoreCase(name)) {
    objects.add(new Shield(x, y));
}
```

---

**Auteur:** Gaëtan Rogeron, Lucas Bertin, Emma Doutres  
**Projet:** Devoir POO - Moteur de Jeu 2D  
**GitHub:** https://github.com/gaetan-rogeron/poo-2d.git

