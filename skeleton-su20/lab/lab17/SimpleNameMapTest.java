import static org.junit.jupiter.api.Assertions.*;

class SimpleNameMapTest {

    @org.junit.jupiter.api.Test
    void put() {
        SimpleNameMap snm = new SimpleNameMap();
        snm.put("Alex", "Schedel");
        assertTrue(snm.containsKey("Alex"));
        assertEquals("Schedel", snm.get("Alex"));
        assertEquals(20,snm.length());

        assertEquals("Schedel", snm.remove("Alex"));
        assertFalse(snm.containsKey("Alex"));

        snm.put("Alex", "Schedel");
        assertEquals("Schedel", snm.get("Alex"));
        snm.put("Ale", "Sch");
        assertEquals("Sch", snm.get("Ale"));

            /* assertTrue(snm.sNM[6] == null);
            snm.put("B", "Bob");
            assertFalse(snm.sNM[6] == null); */
    }

}