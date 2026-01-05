package gle.game2d.player;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests unitaires pour PlayerDirection.
 * Vérifie le pattern Singleton et les conversions.
 */
public class PlayerDirectionTest {

    // Vérifie le pattern Singleton pour une direction donnée (DOWN) : deux créations renvoient la même instance.
    @Test
    public void testSingletonPattern() {
        PlayerDirection down1 = PlayerDirection.createDown();
        PlayerDirection down2 = PlayerDirection.createDown();

        assertSame("Les instances DOWN devraient être identiques (Singleton)", down1, down2);
    }

    // Vérifie que toutes les directions (UP/DOWN/LEFT/RIGHT) sont des singletons (même instance à chaque appel).
    @Test
    public void testAllDirectionsSingleton() {
        assertSame("UP Singleton",
            PlayerDirection.createUp(), PlayerDirection.createUp());
        assertSame("DOWN Singleton",
            PlayerDirection.createDown(), PlayerDirection.createDown());
        assertSame("LEFT Singleton",
            PlayerDirection.createLeft(), PlayerDirection.createLeft());
        assertSame("RIGHT Singleton",
            PlayerDirection.createRight(), PlayerDirection.createRight());
    }

    // Vérifie les méthodes de test de direction (isUp/isDown/isLeft/isRight) pour chaque instance.
    @Test
    public void testDirectionChecks() {
        PlayerDirection down = PlayerDirection.createDown();
        PlayerDirection up = PlayerDirection.createUp();
        PlayerDirection left = PlayerDirection.createLeft();
        PlayerDirection right = PlayerDirection.createRight();

        assertTrue("DOWN devrait être DOWN", down.isDown());
        assertFalse("DOWN ne devrait pas être UP", down.isUp());
        assertFalse("DOWN ne devrait pas être LEFT", down.isLeft());
        assertFalse("DOWN ne devrait pas être RIGHT", down.isRight());

        assertTrue("UP devrait être UP", up.isUp());
        assertTrue("LEFT devrait être LEFT", left.isLeft());
        assertTrue("RIGHT devrait être RIGHT", right.isRight());
    }

    // Vérifie la conversion de chaque direction en clé d'animation (toAnimationKey()).
    @Test
    public void testToAnimationKey() {
        assertEquals("down", PlayerDirection.createDown().toAnimationKey());
        assertEquals("up", PlayerDirection.createUp().toAnimationKey());
        assertEquals("left", PlayerDirection.createLeft().toAnimationKey());
        assertEquals("right", PlayerDirection.createRight().toAnimationKey());
    }

    // Vérifie la représentation texte (toString()) de chaque direction.
    @Test
    public void testToString() {
        assertEquals("DOWN", PlayerDirection.createDown().toString());
        assertEquals("UP", PlayerDirection.createUp().toString());
        assertEquals("LEFT", PlayerDirection.createLeft().toString());
        assertEquals("RIGHT", PlayerDirection.createRight().toString());
    }

    // Vérifie la logique de equals() : même direction = true, direction différente = false, réflexivité, null.
    @Test
    public void testEquals() {
        PlayerDirection down1 = PlayerDirection.createDown();
        PlayerDirection down2 = PlayerDirection.createDown();
        PlayerDirection up = PlayerDirection.createUp();

        assertTrue("DOWN devrait être égal à DOWN", down1.equals(down2));
        assertFalse("DOWN ne devrait pas être égal à UP", down1.equals(up));
        assertTrue("DOWN devrait être égal à lui-même", down1.equals(down1));
        assertFalse("DOWN ne devrait pas être égal à null", down1.equals(null));
    }

    // Vérifie la cohérence des hashCode() : deux instances (singleton) de DOWN doivent avoir le même hash code.
    @Test
    public void testHashCode() {
        PlayerDirection down1 = PlayerDirection.createDown();
        PlayerDirection down2 = PlayerDirection.createDown();

        assertEquals("Les hash codes de DOWN devraient être identiques",
            down1.hashCode(), down2.hashCode());
    }

    // Vérifie que les codes internes de direction (getDirectionCode()) sont différents entre directions distinctes.
    @Test
    public void testDirectionCodes() {
        PlayerDirection down = PlayerDirection.createDown();
        PlayerDirection right = PlayerDirection.createRight();
        PlayerDirection up = PlayerDirection.createUp();
        PlayerDirection left = PlayerDirection.createLeft();

        // Les codes devraient être différents
        assertNotEquals("DOWN et RIGHT devraient avoir des codes différents",
            down.getDirectionCode(), right.getDirectionCode());
        assertNotEquals("UP et DOWN devraient avoir des codes différents",
            up.getDirectionCode(), down.getDirectionCode());
        assertNotEquals("LEFT et RIGHT devraient avoir des codes différents",
            left.getDirectionCode(), right.getDirectionCode());
    }

    // Vérifie que toutes les directions sont uniques : aucune paire ne doit être égale.
    @Test
    public void testAllDirectionsUnique() {
        PlayerDirection[] directions = {
            PlayerDirection.createDown(),
            PlayerDirection.createUp(),
            PlayerDirection.createLeft(),
            PlayerDirection.createRight()
        };

        for (int i = 0; i < directions.length; i++) {
            for (int j = i + 1; j < directions.length; j++) {
                assertNotEquals("Toutes les directions devraient être différentes",
                    directions[i], directions[j]);
            }
        }
    }
}
