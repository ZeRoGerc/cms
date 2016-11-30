package logic;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CoreTest {

    @Test
    public void testFindRoots_shouldProduceOneRootForTwoAsParameter() throws Exception {
        List<Double> result = Core.findRoots(2);
        assertEquals(result.size(), 1);
    }

    @Test
    public void testFindRoots_shouldProduceTwoRootsForThreePointTwoAsParameter() throws Exception {
        List<Double> result = Core.findRoots(3.2);
        assertEquals(result.size(), 2);
    }

    @Test
    public void testFindRoots_shouldProduceManyRootsForFourAsParameter() throws Exception {
        List<Double> result = Core.findRoots(4);
        assertTrue(result.size() > 50);
    }
}