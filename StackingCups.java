import java.util.ArrayList;

/**
 * Coordinador principal del simulador de Stacking Cups.
 * Orquesta la creación de objetos (Cups y Lids) y delega a Tower
 * toda la lógica de apilamiento y posicionamiento.
 *
 * PRINCIPIO SRP: StackingCups tiene una sola responsabilidad: coordinar
 * el estado de la simulación. No muestra diálogos (eso es de SimulationUI)
 * y no calcula posiciones físicas (eso es de Tower).
 *
 * @author Juan Castellanos - Juan Uribe
 * @version 7.2
 */
public class StackingCups {

    private ArrayList<Cup> cups;
    private ArrayList<Lid> lids;
    private Tower tower;
    private SimulationUI ui;
    private int towerWidth;
    private int towerHeight;

    private final String[] COLORS = {
        "blue", "red", "green", "yellow", "magenta", "black", "pink"
    };

    /**
     * Constructor de la simulación.
     * Canvas se inicializa aquí con las medidas correctas antes de crear
     * Tower o cualquier Cup, ya que Canvas es un Singleton y solo acepta
     * dimensiones en su primera llamada.
     * @param towerWidth  Ancho de la ventana.
     * @param towerHeight Alto de la ventana.
     */
    public StackingCups(int towerWidth, int towerHeight) {
        this.cups = new ArrayList<>();
        this.lids = new ArrayList<>();
        this.towerWidth = towerWidth;
        this.towerHeight = towerHeight;
        Canvas.getCanvas(towerWidth, towerHeight);
        this.tower = new Tower(towerWidth, towerHeight);
        this.ui = new SimulationUI();
    }

    /**
     * Crea una torre automáticamente con N tazas de IDs impares.
     * Limpia cualquier simulación previa antes de crear la nueva.
     * @param cupsCount Cantidad de tazas a generar.
     */
    public void tower(int cupsCount) {
        resetInternalData();
        for (int n = 1; n <= cupsCount; n++) {
            pushCup((2 * n) - 1);
        }
        tower.reorganizeTower();
        syncLids();
    }

    /**
     * Crea una nueva taza y la intenta añadir a la torre.
     * La posición inicial es temporal ya que Tower la sobreescribe con el Y correcto.
     * @param i ID de la taza, determina el tamaño según la fórmula ICPC.
     */
    public void pushCup(int i) {
        if (tower.findCupById(i) != null) {
            ui.showWarning("La taza con ID " + i + " ya existe.");
            return;
        }

        int heightPx = ((2 * i) - 1) * 20;
        int widthPx = i * 20;
        String color = COLORS[cups.size() % COLORS.length];
        Cup newCup = new Cup(i, heightPx, widthPx, color, towerWidth / 2, towerHeight);

        if (tower.canAddCup(newCup)) {
            tower.addCup(newCup);
            cups.add(newCup);
            newCup.makeVisible();
        } else {
            ui.showWarning("No hay espacio suficiente para la taza " + i);
        }
    }

    /**
     * Coloca una tapa sobre una taza existente.
     * No hace nada si la taza no existe o ya tiene tapa.
     * La tapa se posiciona en el tope de la taza usando Tower.getLidY(),
     * lo que garantiza que si la taza está anidada la tapa quede dentro
     * de la taza exterior.
     * @param cupId ID de la taza que recibe la tapa.
     */
    public void pushLid(int cupId) {
        Cup cup = tower.findCupById(cupId);
        if (cup == null) {
            return;
        }
        if (findLidByCupId(cupId) != null) {
            return;
        }

        Lid lid = new Lid(cupId, cup.getWidth(), 20, cup.getColor());
        lid.setPosition(cup.getX(), tower.getLidY(cup));
        lid.makeVisible();
        lids.add(lid);
        cup.changeColor("black");
    }

    /**
     * Intercambia la posición de dos objetos en la simulación.
     * Si el tipo es "cup" intercambia tazas en la torre y sincroniza tapas.
     * Si el tipo es "lid" intercambia los dueños de las tapas.
     * Cualquier otro tipo es ignorado.
     * @param type "cup" para tazas, "lid" para tapas.
     * @param id1  ID del primer objeto.
     * @param id2  ID del segundo objeto.
     */
    public void swap(String type, int id1, int id2) {
        if (type.equalsIgnoreCase("cup")) {
            tower.swapCups(id1, id2);
            syncLids();
        } else if (type.equalsIgnoreCase("lid")) {
            Lid lid1 = findLidByCupId(id1);
            Lid lid2 = findLidByCupId(id2);

            if (lid1 != null && lid2 != null) {
                int oldId1 = lid1.getCupId();
                int oldId2 = lid2.getCupId();

                lid1.setCupId(oldId2);
                lid2.setCupId(oldId1);

                syncLids();

                Cup c1 = tower.findCupById(oldId1);
                Cup c2 = tower.findCupById(oldId2);

                if (c1 != null) {
                    c1.changeColor("black");
                }
                if (c2 != null) {
                    c2.changeColor("black");
                }
            }
        }
    }

    /**
     * Elimina una taza por ID junto con su tapa asociada si tiene una.
     * Tras la eliminación reorganiza la torre para colapsar el espacio vacío.
     * @param id ID de la taza a eliminar.
     */
    public void removeCup(int id) {
        Cup cup = tower.findCupById(id);
        if (cup != null) {
            removeLid(id);
            tower.removeCup(cup);
            cups.remove(cup);
            cup.makeInvisible();
            reorganizeTower();
        }
    }

    /**
     * Elimina la tapa de una taza y restaura el color original de la taza.
     * @param cupId ID de la taza cuya tapa se eliminará.
     */
    public void removeLid(int cupId) {
        Lid lid = findLidByCupId(cupId);
        if (lid != null) {
            lid.makeInvisible();
            lids.remove(lid);

            Cup cup = tower.findCupById(cupId);
            if (cup != null) {
                int pos = cups.indexOf(cup);
                if (pos != -1) {
                    cup.changeColor(COLORS[pos % COLORS.length]);
                }
            }
        }
    }

    /**
     * Ordena las tazas de menor a mayor ID y sincroniza las tapas.
     */
    public void reorganizeTower() {
        tower.reorganizeTower();
        syncLids();
    }

    /**
     * Ordena las tazas de mayor a menor ID y sincroniza las tapas.
     */
    public void reorganizeTowerReverse() {
        tower.reorganizeTowerReverse();
        syncLids();
    }

    /**
     * Muestra información detallada del estado actual de la simulación:
     * total de tazas, tazas con tapa, tazas sin tapa y total de tapas.
     */
    public void consultInformation() {
        int lidedCount = 0;
        int unlidedCount = 0;

        for (int i = 0; i < cups.size(); i++) {
            Cup cup = cups.get(i);
            if (findLidByCupId(cup.getId()) != null) {
                lidedCount = lidedCount + 1;
            } else {
                unlidedCount = unlidedCount + 1;
            }
        }

        ui.showSimulationReport(cups.size(), lidedCount, unlidedCount, lids.size());
    }

    /**
     * Muestra el mejor movimiento de intercambio para reducir la altura de la torre.
     */
    public void consultSwap() {
        ui.showSwapSuggestion(tower.consultSwapToReduce());
    }

    /**
     * Finaliza la simulación limpiando todos los objetos y mostrando confirmación.
     */
    public void finishSimulation() {
        resetInternalData();
        ui.showFinishMessage();
    }

    /**
     * Sincroniza la posición de todas las tapas con sus tazas actuales.
     * Debe llamarse después de cualquier movimiento masivo de la torre.
     */
    private void syncLids() {
        for (int i = 0; i < lids.size(); i++) {
            Lid lid = lids.get(i);
            Cup cup = tower.findCupById(lid.getCupId());
            if (cup != null) {
                lid.setPosition(cup.getX(), tower.getLidY(cup));
            }
        }
    }

    /**
     * Limpia todos los datos internos sin mostrar mensajes al usuario.
     */
    private void resetInternalData() {
        tower.clear();
        for (int i = 0; i < cups.size(); i++) {
            cups.get(i).makeInvisible();
        }
        for (int i = 0; i < lids.size(); i++) {
            lids.get(i).makeInvisible();
        }
        cups.clear();
        lids.clear();
    }

    /**
     * Busca una tapa por el ID de su taza asociada.
     * @param id ID de la taza.
     * @return El objeto Lid si existe, null en caso contrario.
     */
    private Lid findLidByCupId(int id) {
        Lid found = null;
        for (int i = 0; i < lids.size(); i++) {
            if (lids.get(i).getCupId() == id) {
                found = lids.get(i);
            }
        }
        return found;
    }

    /**
     * @return Cantidad actual de tazas en la simulación.
     */
    public int getCupCount() {
        return cups.size();
    }

    /**
     * @return Cantidad actual de tapas en la simulación.
     */
    public int getLidCount() {
        return lids.size();
    }

    /**
     * @return Objeto Lid asociado a un ID, o null si no existe.
     */
    public Lid getLid(int id) {
        return findLidByCupId(id);
    }

    /**
     * @return El objeto Cup con el ID dado, o null si no existe.
     */
    public Cup getCup(int id) {
        return tower.findCupById(id);
    }
}
