import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Pruebas de unidad para la clase TowerContest.
 * Verifica que solve() y calculateHeight() funcionen correctamente.
 *
 * @author Juan Castellanos - Juan Uribe
 */
public class TowerContestTest {

    private TowerContest contest;

    @Before
    public void setUp() {
        contest = new TowerContest();
    }

    // ----------------------------------------------------------------
    //  calculateHeight
    // ----------------------------------------------------------------

    @Test
    public void accordingCUShouldReturnZeroForEmptyOrder() {
        assertEquals(0, contest.calculateHeight(new int[]{}));
    }

    @Test
    public void accordingCUShouldReturnCupHeightForSingleCup() {
        assertEquals(1, contest.calculateHeight(new int[]{1}));
        assertEquals(3, contest.calculateHeight(new int[]{2}));
        assertEquals(7, contest.calculateHeight(new int[]{4}));
    }

    @Test
    public void accordingCUShouldCalculateSampleFromProblem() {
        // Sample 1: n=4 h=9, order [4,2,3,1] -> heights [7,3,5,1]
        assertEquals(9, contest.calculateHeight(new int[]{4, 2, 3, 1}));
    }

    @Test
    public void accordingCUShouldCalculateAllStackedOrder() {
        // [1,2,3,4] all stacked: 1+3+5+7=16
        assertEquals(16, contest.calculateHeight(new int[]{1, 2, 3, 4}));
    }

    @Test
    public void accordingCUShouldCalculateAllNestedOrder() {
        // [4,3,2,1] all nested: height = 7 (just cup4)
        assertEquals(7, contest.calculateHeight(new int[]{4, 3, 2, 1}));
    }

    @Test
    public void accordingCUShouldCalculateNestedInsideAscending() {
        // [2,1,4,3]: height=9
        assertEquals(9, contest.calculateHeight(new int[]{2, 1, 4, 3}));
    }

    // ----------------------------------------------------------------
    //  solve - casos imposibles
    // ----------------------------------------------------------------

    @Test
    public void accordingCUShouldReturnImpossibleWhenHeightTooSmall() {
        assertEquals("impossible", contest.solve(4, 6));
    }

    @Test
    public void accordingCUShouldReturnImpossibleWhenHeightTooLarge() {
        assertEquals("impossible", contest.solve(4, 17));
    }

    @Test
    public void accordingCUShouldReturnImpossibleWhenHeightZero() {
        assertEquals("impossible", contest.solve(4, 0));
    }

    @Test
    public void accordingCUShouldReturnImpossibleWhenHeightBelowMinN5() {
        // min for n=5 is 2*5-1=9
        assertEquals("impossible", contest.solve(5, 8));
    }

    // ----------------------------------------------------------------
    //  solve - casos del enunciado
    // ----------------------------------------------------------------

    @Test
    public void accordingCUShouldSolveSampleInput1() {
        // n=4, h=9 -> possible (sample output: 7 3 5 1)
        String result = contest.solve(4, 9);
        assertNotEquals("impossible", result);
        int[] order = parseOrder(result);
        assertEquals(9, contest.calculateHeight(order));
        assertEquals(4, order.length);
    }

    @Test
    public void accordingCUShouldReturnImpossibleForSampleInput2() {
        // n=4, h=100 -> impossible
        assertEquals("impossible", contest.solve(4, 100));
    }

    // ----------------------------------------------------------------
    //  solve - altura mínima y máxima
    // ----------------------------------------------------------------

    @Test
    public void accordingCUShouldSolveMinimumHeight() {
        // n=4, h=7 (min=2*4-1=7)
        String result = contest.solve(4, 7);
        assertNotEquals("impossible", result);
        assertEquals(7, contest.calculateHeight(parseOrder(result)));
    }

    @Test
    public void accordingCUShouldSolveMaximumHeight() {
        // n=4, h=16 (max=4^2=16)
        String result = contest.solve(4, 16);
        assertNotEquals("impossible", result);
        assertEquals(16, contest.calculateHeight(parseOrder(result)));
    }

    @Test
    public void accordingCUShouldSolveMinHeightN1() {
        String result = contest.solve(1, 1);
        assertNotEquals("impossible", result);
        assertEquals(1, contest.calculateHeight(parseOrder(result)));
    }

    @Test
    public void accordingCUShouldSolveMaxHeightN1() {
        String result = contest.solve(1, 1);
        assertNotEquals("impossible", result);
        assertEquals(1, contest.calculateHeight(parseOrder(result)));
    }

    // ----------------------------------------------------------------
    //  solve - todos los rangos para n pequeño
    // ----------------------------------------------------------------

    @Test
    public void accordingCUShouldSolveAllHeightsForN4() {
        int n = 4;
        for (int h = 2 * n - 1; h <= n * n; h++) {
            String result = contest.solve(n, h);
            assertNotEquals("Altura " + h + " debería ser posible", "impossible", result);
            int[] order = parseOrder(result);
            assertEquals("Altura calculada incorrecta para h=" + h,
                    h, contest.calculateHeight(order));
            assertEquals("Debe usar exactamente " + n + " tazas",
                    n, order.length);
        }
    }

    @Test
    public void accordingCUShouldSolveAllHeightsForN5() {
        int n = 5;
        for (int h = 2 * n - 1; h <= n * n; h++) {
            String result = contest.solve(n, h);
            assertNotEquals("Altura " + h + " debería ser posible", "impossible", result);
            int[] order = parseOrder(result);
            assertEquals("Altura calculada incorrecta para h=" + h,
                    h, contest.calculateHeight(order));
            assertEquals("Debe usar exactamente " + n + " tazas",
                    n, order.length);
        }
    }

    @Test
    public void accordingCUShouldSolveAllHeightsForN10() {
        int n = 10;
        for (int h = 2 * n - 1; h <= n * n; h++) {
            String result = contest.solve(n, h);
            assertNotEquals("Altura " + h + " debería ser posible", "impossible", result);
            int[] order = parseOrder(result);
            assertEquals("Altura calculada incorrecta para h=" + h,
                    h, contest.calculateHeight(order));
        }
    }

    // ----------------------------------------------------------------
    //  solve - n=2 casos especiales
    // ----------------------------------------------------------------

    @Test
    public void accordingCUShouldSolveN2H3() {
        // n=2, min=3
        String result = contest.solve(2, 3);
        assertNotEquals("impossible", result);
        assertEquals(3, contest.calculateHeight(parseOrder(result)));
    }

    @Test
    public void accordingCUShouldSolveN2H4() {
        // n=2, max=4
        String result = contest.solve(2, 4);
        assertNotEquals("impossible", result);
        assertEquals(4, contest.calculateHeight(parseOrder(result)));
    }

    // ----------------------------------------------------------------
    //  solve - usa todas las tazas
    // ----------------------------------------------------------------

    @Test
    public void accordingCUShouldUseAllCupsInResult() {
        int n = 6;
        int h = 20;
        String result = contest.solve(n, h);
        assertNotEquals("impossible", result);
        int[] order = parseOrder(result);
        assertEquals("Debe usar exactamente " + n + " tazas", n, order.length);

        boolean[] seen = new boolean[n + 1];
        for (int cupId : order) {
            assertFalse("ID " + cupId + " duplicado", seen[cupId]);
            seen[cupId] = true;
        }
        for (int i = 1; i <= n; i++) {
            assertTrue("Falta la taza con ID " + i, seen[i]);
        }
    }
    //  solve - valores en límite del tipo long
    
    @Test
    public void accordingCUShouldHandleLargeN() {
        // n=100, h=min=199
        int n = 100;
        long minH = 2L * n - 1;
        String result = contest.solve(n, minH);
        assertNotEquals("impossible", result);
        int[] order = parseOrder(result);
        assertEquals(n, order.length);
    }

    @Test
    public void accordingCUShouldHandleLargeNMaxHeight() {
        int n = 100;
        long maxH = (long) n * n;
        String result = contest.solve(n, maxH);
        assertNotEquals("impossible", result);
        int[] order = parseOrder(result);
        assertEquals(n, order.length);
    }

    private int[] parseOrder(String solution) {
        String[] parts = solution.split(" ");
        int[] order = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            int height = Integer.parseInt(parts[i]);
            order[i] = (height + 1) / 2;
        }
        return order;
    }
}