package bgu.spl.mics.application.passiveObjects;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EwokTest {

    private Ewok e1;

    @BeforeEach
    void setUp() {
        e1 = new Ewok(1);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void acquire() {
        try {
            assertTrue(e1.isAvailable());
            e1.acquire();
            assertFalse(e1.isAvailable());
        } catch (Exception e) {
            System.out.println("FAIL: " + e);
        }
    }

    @Test
    void release() {
        try {
            e1.acquire();
            assertFalse(e1.isAvailable());
            e1.release();
            assertTrue(e1.isAvailable());
        } catch (Exception e) {
            System.out.println("FAIL: " + e);
        }
    }

    @Test
    void isAvailable() {
        try {
            assertTrue(e1.available);
            assertTrue(e1.isAvailable());
            e1.acquire();
            assertFalse(e1.isAvailable());
        } catch (Exception e) {
            System.out.println("FAIL: " + e);
        }
    }
}