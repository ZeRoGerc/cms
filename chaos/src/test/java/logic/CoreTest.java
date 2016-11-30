package logic;

import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CoreTest {

    @Test
    public void testFindRoots_shouldProduceOneRootForTwoAsParameter() throws Exception {
        List<Double> result = Core.findRoots(2);

        for (int i = 1; i < result.size(); i++) {
            assertTrue(Core.equals(result.get(i), result.get(i - 1)));
        }
    }

    @Test
    public void testFindRoots_shouldProduceTwoRootsForThreePointTwoAsParameter() throws Exception {
        List<Double> result = Core.findRoots(3.2);

        Collections.sort(result);
        int different = 0;

        for (int i = 1; i < result.size(); i++) {
            if (!Core.equals(result.get(i), result.get(i - 1))) {
                different++;
            }
        }

        assertEquals(different, 1);
    }

    @Test
    public void testFindRoots_shouldProduceChaosForFourAsParameter() throws Exception {
        List<Double> result = Core.findRoots(4);

        Collections.sort(result);
        int different = 0;

        for (int i = 1; i < result.size(); i++) {
            if (!Core.equals(result.get(i), result.get(i - 1))) {
                different++;
            }
        }

        assertEquals(different, result.size() - 1);
    }
}