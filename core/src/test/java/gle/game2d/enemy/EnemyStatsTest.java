package gle.game2d.enemy;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests unitaires pour EnemyStats et son Builder.
 * Vérifie la construction correcte des statistiques d'ennemis.
 */
public class EnemyStatsTest {

    // Vérifie que le Builder sans configuration applique bien les valeurs par défaut (dimensions, vitesse, santé, dégâts).
    @Test
    public void testBuilderDefaultValues() {
        EnemyStats stats = new EnemyStats.Builder().build();

        assertEquals("Largeur par défaut", 32, stats.getWidth());
        assertEquals("Hauteur par défaut", 32, stats.getHeight());
        assertEquals("Vitesse par défaut", 50f, stats.getSpeed(), 0.001f);
        assertEquals("Santé par défaut", 50, stats.getMaxHealth());
        assertEquals("Dégâts par défaut", 10, stats.getDamage());
    }

    // Vérifie que le Builder applique correctement des valeurs personnalisées via les méthodes withX().
    @Test
    public void testBuilderCustomValues() {
        EnemyStats stats = new EnemyStats.Builder()
            .withDimensions(64, 64)
            .withSpeed(100f)
            .withHealth(100)
            .withDamage(20)
            .build();

        assertEquals("Largeur personnalisée", 64, stats.getWidth());
        assertEquals("Hauteur personnalisée", 64, stats.getHeight());
        assertEquals("Vitesse personnalisée", 100f, stats.getSpeed(), 0.001f);
        assertEquals("Santé personnalisée", 100, stats.getMaxHealth());
        assertEquals("Dégâts personnalisés", 20, stats.getDamage());
    }

    // Vérifie que des dimensions invalides (largeur = 0) déclenchent une exception.
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidDimensions_Zero() {
        new EnemyStats.Builder()
            .withDimensions(0, 32)
            .build();
    }

    // Vérifie que des dimensions invalides (hauteur négative) déclenchent une exception.
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidDimensions_Negative() {
        new EnemyStats.Builder()
            .withDimensions(32, -10)
            .build();
    }

    // Vérifie qu'une vitesse négative est refusée par validation (exception).
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidSpeed_Negative() {
        new EnemyStats.Builder()
            .withSpeed(-10f)
            .build();
    }

    // Vérifie qu'une vitesse égale à 0 est acceptée et correctement stockée.
    @Test
    public void testValidSpeed_Zero() {
        EnemyStats stats = new EnemyStats.Builder()
            .withSpeed(0f)
            .build();

        assertEquals("Une vitesse de 0 devrait être valide", 0f, stats.getSpeed(), 0.001f);
    }

    // Vérifie qu'une santé max égale à 0 est refusée (exception).
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidHealth_Zero() {
        new EnemyStats.Builder()
            .withHealth(0)
            .build();
    }

    // Vérifie qu'une santé max négative est refusée (exception).
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidHealth_Negative() {
        new EnemyStats.Builder()
            .withHealth(-10)
            .build();
    }

    // Vérifie que des dégâts négatifs sont refusés (exception).
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidDamage_Negative() {
        new EnemyStats.Builder()
            .withDamage(-5)
            .build();
    }

    // Vérifie que des dégâts égaux à 0 sont acceptés et correctement stockés.
    @Test
    public void testValidDamage_Zero() {
        EnemyStats stats = new EnemyStats.Builder()
            .withDamage(0)
            .build();

        assertEquals("Des dégâts de 0 devraient être valides", 0, stats.getDamage());
    }

    // Vérifie que le chaînage des méthodes du Builder (fluent API) fonctionne et crée un objet non nul.
    @Test
    public void testBuilderChaining() {
        EnemyStats stats = new EnemyStats.Builder()
            .withDimensions(48, 48)
            .withSpeed(75f)
            .withHealth(75)
            .withDamage(15)
            .build();

        assertNotNull("Le builder devrait créer un objet valide", stats);
        assertEquals("Toutes les valeurs devraient être définies", 48, stats.getWidth());
    }

    // Vérifie que toString() renvoie une chaîne non nulle et contenant un identifiant attendu ("EnemyStats").
    @Test
    public void testToString() {
        EnemyStats stats = new EnemyStats.Builder()
            .withDimensions(64, 64)
            .withSpeed(100f)
            .withHealth(100)
            .withDamage(20)
            .build();

        String str = stats.toString();

        assertNotNull("ToString ne devrait pas être null", str);
        assertTrue("ToString devrait contenir 'EnemyStats'", str.contains("EnemyStats"));
    }
}
