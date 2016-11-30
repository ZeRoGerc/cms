package logic;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class RootFinderTest {

    @Test
    public void testPerformance() throws Exception {
        RootFinder finder = new RootFinder();
        long start = System.nanoTime();
        finder.getRootsForInterval(-1, 1, 1e-3);
        long end = System.nanoTime();

        assertTrue(end - start < 1e9); // less than second
    }
}