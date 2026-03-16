import java.util.ArrayList;
 
/**
 * Motor lógico de apilamiento para el simulador de Stacking Cups.
 * Gestiona la colección de tazas, valida las reglas físicas de la ICPC
 * y calcula las coordenadas espaciales (vectores) para el renderizado.
 *
 * PRINCIPIO SRP: Tower es responsable ÚNICAMENTE de:
 *   1. Gestionar la colección de tazas.
 *   2. Calcular posiciones físicas (vectores Y).
 *   3. Validar reglas de apilamiento.
 * NO es responsable de la UI ni de coordinar tapas (eso es de StackingCups).
 *
 * @author Juan Castellanos - Juan Uribe
 * @version 7.1
 */
public class Tower {
 
    private ArrayList<Cup> cups;
 
    private int maxHeight;
    private int width;
 
    private final int SCALE         = 20;  // 1 cm = 20 px
    private final int BOTTOM_OFFSET = 160; // Margen inferior sobre el suelo
 
    /**
     * Constructor de la torre.
     * @param width  Ancho total del área de simulación.
     * @param height Alto total del área de simulación.
     */
    public Tower(int width, int height) {
        this.cups      = new ArrayList<>();
        this.width     = width;
        this.maxHeight = height - BOTTOM_OFFSET;
    }
 
    // =========================================================================
    //  GESTIÓN DE TAZAS
    // =========================================================================
 
    /**
     * Busca una taza por su ID.
     * @param id Identificador único.
     * @return El objeto Cup si existe, null en caso contrario.
     */
    public Cup findCupById(int id) {
        for (Cup c : cups) {
            if (c.getId() == id) return c;
        }
        return null;
    }
 
    /**
     * Añade una taza a la torre y calcula su posición vectorial.
     * Reglas:
     *  - ID menor al anterior → anidada (sube solo SCALE px).
     *  - ID mayor o igual     → apilada (sube la altura completa de la anterior).
     * @param cup Objeto Cup a posicionar.
     */
    public void addCup(Cup cup) {
        int targetX = this.width / 2;
        int targetY;
 
        if (cups.isEmpty()) {
            targetY = this.maxHeight;
        } else {
            Cup last = cups.get(cups.size() - 1);
            if (cup.getId() < last.getId()) {
                targetY = last.getY() - SCALE;
            } else {
                targetY = last.getY() - last.getHeight();
            }
        }
 
        cup.setPosition(targetX, targetY);
        cups.add(cup);
    }
 
    /**
     * Remueve una taza de la colección.
     * El recálculo de posiciones debe ejecutarse desde StackingCups.
     * @param cup Referencia del objeto a remover.
     */
    public void removeCup(Cup cup) {
        cups.remove(cup);
    }
 
    /**
     * Elimina todas las tazas de la torre.
     */
    public void clear() {
        cups.clear();
    }
 
    // =========================================================================
    //  ORDENAMIENTO
    // =========================================================================
 
    /**
     * Ordena las tazas de menor a mayor ID (Bubble Sort ascendente)
     * y recalcula sus posiciones.
     */
    public void reorganizeTower() {
        if (cups.isEmpty()) return;
        bubbleSort(true);
        applyNewOrder();
    }
 
    /**
     * Ordena las tazas de mayor a menor ID (Bubble Sort descendente)
     * y recalcula sus posiciones. Útil para el modo de anidamiento.
     */
    public void reorganizeTowerReverse() {
        if (cups.isEmpty()) return;
        bubbleSort(false);
        applyNewOrder();
    }
 
    // =========================================================================
    //  INTERCAMBIO
    // =========================================================================
 
    /**
     * Intercambia la posición de dos tazas en la torre.
     * @param id1 ID de la primera taza.
     * @param id2 ID de la segunda taza.
     */
    public void swapCups(int id1, int id2) {
        int i1 = indexOfId(id1);
        int i2 = indexOfId(id2);
 
        if (i1 != -1 && i2 != -1) {
            Cup temp = cups.get(i1);
            cups.set(i1, cups.get(i2));
            cups.set(i2, temp);
            applyNewOrder();
        }
    }
 
    // =========================================================================
    //  CONSULTAS
    // =========================================================================
 
    /**
     * Busca el par de tazas cuyo intercambio reduzca más la altura total.
     * @return Mensaje con la sugerencia, o aviso si no hay mejora posible.
     */
    public String consultSwapToReduce() {
        if (cups.size() < 2) return "Se necesitan al menos 2 tazas para un intercambio.";
 
        int currentHeight = calculateHeightForList(cups);
        int bestReduction = 0;
        String bestSwap   = "No se encontró un intercambio que reduzca la altura actual.";
 
        for (int i = 0; i < cups.size(); i++) {
            for (int j = i + 1; j < cups.size(); j++) {
                ArrayList<Cup> sim = new ArrayList<>(cups);
                Cup temp = sim.get(i);
                sim.set(i, sim.get(j));
                sim.set(j, temp);
 
                int reduction = currentHeight - calculateHeightForList(sim);
                if (reduction > bestReduction) {
                    bestReduction = reduction;
                    bestSwap = "Intercambiar Taza " + cups.get(i).getId()
                             + " con Taza " + cups.get(j).getId()
                             + " reduciría la altura en " + (reduction / SCALE) + " cm.";
                }
            }
        }
        return bestSwap;
    }
 
    /**
     * Determina si el tope de una taza está libre (sin otra anidada encima).
     * @param cup Taza a evaluar.
     * @return true si el tope está libre.
     */
    public boolean isCupTopFree(Cup cup) {
        int index = cups.indexOf(cup);
        if (index == cups.size() - 1) return true;
        return !(cups.get(index + 1).getId() < cup.getId());
    }
 
    /**
     * Valida si hay espacio en la torre para añadir una nueva taza.
     * @param cup Taza candidata.
     * @return true si se puede añadir.
     */
    public boolean canAddCup(Cup cup) {
        return true;
    }
 
    // =========================================================================
    //  MÉTODOS PRIVADOS
    // =========================================================================
 
    /**
     * Bubble Sort in-place sobre la lista de tazas.
     * @param ascending true → menor a mayor ID; false → mayor a menor ID.
     */
    private void bubbleSort(boolean ascending) {
        int n = cups.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                boolean shouldSwap = ascending
                        ? cups.get(j).getId() > cups.get(j + 1).getId()
                        : cups.get(j).getId() < cups.get(j + 1).getId();
                if (shouldSwap) {
                    Cup temp = cups.get(j);
                    cups.set(j, cups.get(j + 1));
                    cups.set(j + 1, temp);
                }
            }
        }
    }
 
    /**
     * Sincroniza el orden lógico del ArrayList con las coordenadas visuales.
     * Vacía la lista y re-apila cada taza para que addCup recalcule los Y.
     */
    private void applyNewOrder() {
        ArrayList<Cup> temp = new ArrayList<>(cups);
        cups.clear();
        for (Cup cup : temp) {
            addCup(cup);
        }
    }
 
    /**
     * Calcula la altura total de una lista de tazas aplicando las reglas de apilamiento.
     * @param list Lista a evaluar.
     * @return Altura total en píxeles.
     */
    private int calculateHeightForList(ArrayList<Cup> list) {
        if (list.isEmpty()) return 0;
        int total = list.get(0).getHeight();
        for (int i = 1; i < list.size(); i++) {
            Cup current  = list.get(i);
            Cup previous = list.get(i - 1);
            total += (current.getId() < previous.getId()) ? SCALE : current.getHeight();
        }
        return total;
    }
 
    /**
     * Retorna el índice en la lista del Cup con el ID dado, o -1 si no existe.
     */
    private int indexOfId(int id) {
        for (int i = 0; i < cups.size(); i++) {
            if (cups.get(i).getId() == id) return i;
        }
        return -1;
    }
}