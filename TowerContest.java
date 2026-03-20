import java.util.ArrayList;

/**
 * Resuelve el Problema J de la ICPC 2025 - Stacking Cups.
 * Dado n tazas y una altura objetivo h, determina el orden en que
 * deben apilarse para alcanzar exactamente esa altura.
 *
 * La clase Tower se usa ÚNICAMENTE para simular la solución visualmente,
 * NO para resolver el problema matemático.
 *
 * PRINCIPIO SRP: TowerContest es responsable únicamente de resolver
 * y simular el problema de la maratón. No gestiona la UI ni la lógica
 * general del simulador.
 *
 * @author Juan Castellanos - Juan Uribe
 * @version 1.0
 */
public class TowerContest {

    /**
     * Resuelve el problema de la maratón ICPC 2025 Problema J.
     *
     * Dado n tazas (la taza i tiene altura 2i-1 cm) y una altura objetivo h,
     * devuelve el orden en que deben apilarse para que la torre alcance
     * exactamente h cm.
     *
     * Reglas de apilamiento:
     * - La taza i cabe dentro de la taza j si y solo si i < j.
     * - Si la taza colocada tiene ID mayor al anterior: se apila encima (contribuye 2i-1).
     * - Si la taza colocada tiene ID menor al anterior: se anida dentro (contribuye 0).
     * - La altura de la torre es la distancia desde el punto más bajo al más alto.
     *
     * Rango de alturas alcanzables con n tazas: [2n-1, n^2].
     *
     * Algoritmo:
     * 1. La taza n siempre es el "techo" del sistema (última en la cadena apilada).
     * 2. Se construye un "pedestal" de altura T = h - (2n-1) usando un subconjunto
     *    de las tazas 1..n-1 con la siguiente lógica:
     *    - Si T es impar: usar solo la taza (T+1)/2.
     *    - Si T es par:   usar las tazas T/2+1 (primera) y T/2 (anidada dentro).
     *    - Para T grande: combinar recursivamente hasta agotar el objetivo.
     * 3. Las tazas restantes se anidan dentro de la taza n.
     *
     * @param n Número de tazas (1 <= n <= 200000).
     * @param h Altura objetivo en cm (1 <= h <= 4*10^10).
     * @return String con las alturas de las tazas en el orden de apilamiento,
     *         o "impossible" si no es posible alcanzar esa altura.
     */
    public String solve(int n, long h) {
        long minHeight = 2L * n - 1;
        long maxHeight = (long) n * n;

        if (h < minHeight || h > maxHeight) {
            return "impossible";
        }

        long T = h - minHeight;
        ArrayList<Integer> pedestal = buildPedestal(n - 1, T);

        if (T > 0 && pedestal == null) {
            return "impossible";
        }

        if (pedestal == null) {
            pedestal = new ArrayList<>();
        }

        boolean[] used = new boolean[n + 1];
        for (int cup : pedestal) {
            used[cup] = true;
        }

        pedestal.add(n);
        used[n] = true;

        for (int i = n - 1; i >= 1; i--) {
            if (!used[i]) {
                pedestal.add(i);
            }
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < pedestal.size(); i++) {
            if (i > 0) {
                result.append(" ");
            }
            result.append(2 * pedestal.get(i) - 1);
        }

        return result.toString();
    }

    /**
     * Construye el pedestal: una secuencia de tazas cuya ÚLTIMA taza
     * tiene exactamente top = T.
     *
     * Garantiza: la última taza en la secuencia tiene su parte superior
     * exactamente a T cm del suelo.
     *
     * Casos base:
     * - T == 0: pedestal vacío (la taza n va al suelo directamente).
     * - T impar: una sola taza con ID = (T+1)/2, cuya altura es 2*(T+1)/2-1 = T.
     * - T par: dos tazas [T/2+1, T/2]. La primera crea una base, la segunda
     *   se anida dentro con top = T.
     *
     * Caso recursivo para T grande: elegir la taza más grande posible c
     * con 2c-1 <= T, recurrir con T' = T-(2c-1) y máximo c-1.
     *
     * @param maxCup ID máximo de taza disponible para el pedestal.
     * @param T      Altura objetivo para el top de la última taza del pedestal.
     * @return Lista de IDs de tazas en orden, o null si no es posible.
     */
    private ArrayList<Integer> buildPedestal(int maxCup, long T) {
        if (T == 0) {
            return new ArrayList<>();
        }
        if (T < 0 || maxCup < 1) {
            return null;
        }

        if (T % 2 == 1) {
            int c = (int) ((T + 1) / 2);
            if (c <= maxCup) {
                ArrayList<Integer> result = new ArrayList<>();
                result.add(c);
                return result;
            }
        } else {
            int c = (int) (T / 2);
            int c1 = c + 1;
            if (c >= 1 && c1 <= maxCup) {
                ArrayList<Integer> result = new ArrayList<>();
                result.add(c1);
                result.add(c);
                return result;
            }
        }

        int c = (int) Math.min(maxCup, (T + 1) / 2);
        while (c >= 1) {
            long cupHeight = 2L * c - 1;
            if (cupHeight <= T) {
                long T2 = T - cupHeight;
                ArrayList<Integer> sub = buildPedestal(c - 1, T2);
                if (sub != null) {
                    sub.add(c);
                    return sub;
                }
            }
            c--;
        }

        return null;
    }

    /**
     * Simula visualmente la solución del problema de la maratón.
     * Usa la clase Tower para construir y mostrar la torre resultante.
     *
     * Si la solución existe (solve retorna un orden válido):
     * - Crea una simulación StackingCups.
     * - Agrega las tazas en el orden calculado por solve.
     * - La torre queda visible en el canvas.
     *
     * Si la solución no existe: muestra un mensaje indicándolo.
     *
     * @param n Número de tazas.
     * @param h Altura objetivo en cm.
     */
    public void simulate(int n, long h) {
        String solution = solve(n, h);

        if (solution.equals("impossible")) {
            SimulationUI ui = new SimulationUI();
            ui.showWarning("No es posible construir una torre de "
                    + h + " cm con " + n + " tazas.\n"
                    + "Rango válido: [" + (2 * n - 1) + ", " + ((long) n * n) + "] cm.");
            return;
        }

        String[] parts = solution.split(" ");

        // El canvas debe ser lo suficientemente alto para mostrar la torre.
        // h cm * 20 px/cm = altura en píxeles, más un margen de 100px.
        int canvasHeight = (int) (h * 20) + 100;
        int canvasWidth  = Math.max(400, n * 30);
        int canvasSize   = Math.max(canvasHeight, canvasWidth);
        canvasSize       = Math.max(canvasSize, 600);

        // Reinicia el singleton de Canvas para que las nuevas dimensiones
        // se apliquen correctamente y el canvas anterior no interfiera.
        Canvas.resetCanvas();

        StackingCups simulator = new StackingCups(canvasSize, canvasSize);

        for (String part : parts) {
            int cupHeight = Integer.parseInt(part);
            int cupId = (cupHeight + 1) / 2;
            simulator.pushCup(cupId);
            // Pequeña pausa para que el canvas registre cada taza
            Canvas.getCanvas().wait(30);
        }

        // Pausa final para que el canvas muestre la torre completa
        Canvas.getCanvas().wait(200);
    }

    /**
     * Calcula la altura de una torre dado el orden de apilamiento de las tazas.
     * Útil para verificar que solve produce resultados correctos.
     *
     * @param order Array con los IDs de las tazas en orden de apilamiento.
     * @return Altura total de la torre en cm.
     */
    public int calculateHeight(int[] order) {
        if (order == null || order.length == 0) {
            return 0;
        }

        int[] bases = new int[order.length];
        int[] tops  = new int[order.length];

        tops[0] = 2 * order[0] - 1;

        for (int i = 1; i < order.length; i++) {
            int cupHeight = 2 * order[i] - 1;
            if (order[i] < order[i - 1]) {
                bases[i] = bases[i - 1] + 1;
            } else {
                bases[i] = tops[i - 1];
            }
            tops[i] = bases[i] + cupHeight;
        }

        int maxTop = 0;
        for (int top : tops) {
            if (top > maxTop) {
                maxTop = top;
            }
        }
        return maxTop;
    }
}