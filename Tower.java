import java.util.ArrayList;

/**
 * Motor lógico de apilamiento para el simulador de Stacking Cups.
 * Gestiona la colección de tazas, valida las reglas físicas de la ICPC
 * y calcula las coordenadas espaciales para el renderizado.
 *
 * PRINCIPIO SRP: Tower es responsable únicamente de gestionar la colección
 * de tazas, calcular posiciones físicas y validar reglas de apilamiento.
 *
 * @author Juan Castellanos - Juan Uribe
 * @version 7.2
 */
public class Tower {

    private ArrayList<Cup> cups;
    private int maxHeight;
    private int width;
    private final int SCALE = 20;

    /**
     * Constructor de la torre.
     * @param width  Ancho total del área de simulación.
     * @param height Alto total del área de simulación.
     */
    public Tower(int width, int height) {
        this.cups = new ArrayList<>();
        this.width = width;
        this.maxHeight = height;
    }

    /**
     * Busca una taza por su ID.
     * @param id Identificador único.
     * @return El objeto Cup si existe, null en caso contrario.
     */
    public Cup findCupById(int id) {
        Cup found = null;
        for (int i = 0; i < cups.size(); i++) {
            Cup c = cups.get(i);
            if (c.getId() == id) {
                found = c;
            }
        }
        return found;
    }

    /**
     * Añade una taza a la torre y calcula su posición vectorial.
     * Si la torre está vacía la taza se apoya en el piso.
     * Si el ID de la nueva taza es menor al de la última se anida (sube SCALE px).
     * Si el ID es mayor o igual se apila encima (sube la altura completa).
     * @param cup Objeto Cup a posicionar.
     */
    public void addCup(Cup cup) {
        int targetX = this.width / 2;
        int targetY;

        if (cups.isEmpty()) {
            targetY = this.maxHeight;
        } else {
            Cup lastCup = cups.get(cups.size() - 1);
            if (cup.getId() < lastCup.getId()) {
                targetY = lastCup.getY() - SCALE;
            } else {
                targetY = lastCup.getY() - lastCup.getHeight();
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

    /**
     * Ordena las tazas de menor a mayor ID y recalcula sus posiciones.
     */
    public void reorganizeTower() {
        if (cups.isEmpty()) {
            return;
        }
        bubbleSortAscending();
        applyNewOrder();
    }

    /**
     * Ordena las tazas de mayor a menor ID y recalcula sus posiciones.
     */
    public void reorganizeTowerReverse() {
        if (cups.isEmpty()) {
            return;
        }
        bubbleSortDescending();
        applyNewOrder();
    }

    /**
     * Intercambia la posición de dos tazas en la torre.
     * @param id1 ID de la primera taza.
     * @param id2 ID de la segunda taza.
     */
    public void swapCups(int id1, int id2) {
        int index1 = -1;
        int index2 = -1;

        for (int i = 0; i < cups.size(); i++) {
            if (cups.get(i).getId() == id1) {
                index1 = i;
            }
            if (cups.get(i).getId() == id2) {
                index2 = i;
            }
        }

        if (index1 != -1 && index2 != -1) {
            Cup temp = cups.get(index1);
            cups.set(index1, cups.get(index2));
            cups.set(index2, temp);
            applyNewOrder();
        }
    }

    /**
     * Busca el par de tazas cuyo intercambio reduzca más la altura total.
     * @return Mensaje con la sugerencia, o aviso si no hay mejora posible.
     */
    public String consultSwapToReduce() {
        if (cups.size() < 2) {
            return "Se necesitan al menos 2 tazas para un intercambio.";
        }

        int currentHeight = calculateHeightForList(cups);
        int bestReduction = 0;
        int bestI = -1;
        int bestJ = -1;

        for (int i = 0; i < cups.size(); i++) {
            for (int j = i + 1; j < cups.size(); j++) {
                ArrayList<Cup> simulated = new ArrayList<Cup>(cups);
                Cup temp = simulated.get(i);
                simulated.set(i, simulated.get(j));
                simulated.set(j, temp);

                int simulatedHeight = calculateHeightForList(simulated);
                int reduction = currentHeight - simulatedHeight;

                if (reduction > bestReduction) {
                    bestReduction = reduction;
                    bestI = i;
                    bestJ = j;
                }
            }
        }

        if (bestI == -1) {
            return "No se encontró un intercambio que reduzca la altura actual.";
        }

        return "Intercambiar Taza " + cups.get(bestI).getId()
             + " con Taza " + cups.get(bestJ).getId()
             + " reduciría la altura en " + (bestReduction / SCALE) + " cm.";
    }

    /**
     * Verifica si la taza con id1 cabe dentro de la taza con id2.
     * Según las reglas ICPC la taza i cabe dentro de la taza j si i es menor que j,
     * ya que las tazas tienen diámetros crecientes.
     * Ambas tazas deben existir en la torre.
     * @param id1 ID de la taza que se quiere meter adentro.
     * @param id2 ID de la taza contenedora.
     * @return true si cup(id1) cabe dentro de cup(id2), false en caso contrario.
     */
    public boolean cover(int id1, int id2) {
        Cup cup1 = findCupById(id1);
        Cup cup2 = findCupById(id2);

        if (cup1 == null || cup2 == null) {
            return false;
        }

        if (cup1.getId() < cup2.getId()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Determina si el tope de una taza está libre, es decir,
     * sin otra taza anidada encima.
     * @param cup Taza a evaluar.
     * @return true si el tope está libre.
     */
    public boolean isCupTopFree(Cup cup) {
        int index = cups.indexOf(cup);

        if (index == cups.size() - 1) {
            return true;
        }

        Cup nextCup = cups.get(index + 1);
        if (nextCup.getId() < cup.getId()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Valida si hay espacio en la torre para añadir una nueva taza.
     * Calcula el Y proyectado y verifica que no supere el borde superior del canvas.
     * @param cup Taza candidata.
     * @return true si hay espacio suficiente, false si la taza no cabe.
     */
    public boolean canAddCup(Cup cup) {
        int projectedY;

        if (cups.isEmpty()) {
            projectedY = this.maxHeight;
        } else {
            Cup lastCup = cups.get(cups.size() - 1);
            if (cup.getId() < lastCup.getId()) {
                projectedY = lastCup.getY() - SCALE;
            } else {
                projectedY = lastCup.getY() - lastCup.getHeight();
            }
        }

        int topOfNewCup = projectedY - cup.getHeight();

        if (topOfNewCup >= 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Calcula el Y donde debe posicionarse la tapa de una taza.
     * La tapa se coloca en el tope de la taza.
     * Si la taza está anidada dentro de otra, la tapa queda visualmente
     * dentro de la taza exterior, que es el comportamiento correcto.
     * @param cup Taza a la que se le colocará la tapa.
     * @return Coordenada Y donde debe aparecer la tapa.
     */
    public int getLidY(Cup cup) {
        return cup.getY() - cup.getHeight();
    }

    /**
     * Bubble Sort ascendente: ordena las tazas de menor a mayor ID.
     */
    private void bubbleSortAscending() {
        int n = cups.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (cups.get(j).getId() > cups.get(j + 1).getId()) {
                    Cup temp = cups.get(j);
                    cups.set(j, cups.get(j + 1));
                    cups.set(j + 1, temp);
                }
            }
        }
    }

    /**
     * Bubble Sort descendente: ordena las tazas de mayor a menor ID.
     */
    private void bubbleSortDescending() {
        int n = cups.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (cups.get(j).getId() < cups.get(j + 1).getId()) {
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
        ArrayList<Cup> temp = new ArrayList<Cup>(cups);
        cups.clear();
        for (int i = 0; i < temp.size(); i++) {
            addCup(temp.get(i));
        }
    }

    /**
     * Calcula la altura total de una lista de tazas según las reglas de apilamiento.
     * @param list Lista a evaluar.
     * @return Altura total en píxeles.
     */
    private int calculateHeightForList(ArrayList<Cup> list) {
        if (list.isEmpty()) {
            return 0;
        }

        int totalHeight = list.get(0).getHeight();

        for (int i = 1; i < list.size(); i++) {
            Cup current = list.get(i);
            Cup previous = list.get(i - 1);

            if (current.getId() < previous.getId()) {
                totalHeight = totalHeight + SCALE;
            } else {
                totalHeight = totalHeight + current.getHeight();
            }
        }

        return totalHeight;
    }
}