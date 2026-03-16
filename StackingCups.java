import java.util.ArrayList;
 
/**
 * Coordinador principal del simulador de Stacking Cups.
 * Orquesta la creación de objetos (Cups y Lids) y delega a Tower
 * toda la lógica de apilamiento y posicionamiento.
 *
 * PRINCIPIO SRP: StackingCups tiene UNA sola responsabilidad: coordinar
 * el estado de la simulación. NO muestra diálogos (eso es de SimulationUI)
 * y NO calcula posiciones físicas (eso es de Tower).
 *
 * @author Juan Castellanos - Juan Uribe
 * @version 7.1
 */
public class StackingCups {
 
    private ArrayList<Cup> cups;
    private ArrayList<Lid> lids;
 
    private Tower tower;
    private SimulationUI ui;
 
    private int towerWidth;
    private int towerHeight;
 
    private final int MARGIN_BOTTOM = 80;
 
    private final String[] COLORS = {
        "blue", "red", "green", "yellow", "magenta", "black", "pink"
    };
 
    // =========================================================================
    //  CONSTRUCTOR
    // =========================================================================
 
    /**
     * Constructor de la simulación.
     * @param towerWidth  Ancho de la ventana.
     * @param towerHeight Alto de la ventana.
     */
    public StackingCups(int towerWidth, int towerHeight) {
        this.cups        = new ArrayList<>();
        this.lids        = new ArrayList<>();
        this.towerWidth  = towerWidth;
        this.towerHeight = towerHeight;
 
        // IMPORTANTE: Canvas es un Singleton. Se inicializa aquí con las medidas
        // correctas ANTES de crear Tower o cualquier Cup/Rectangle. Si se omite
        // este paso, la primera llamada interna a Canvas.getCanvas() usaría el
        // default de 800x800 ignorando las dimensiones del usuario.
        Canvas.getCanvas(towerWidth, towerHeight);
 
        this.tower = new Tower(towerWidth, towerHeight);
        this.ui    = new SimulationUI();
    }
 
    // =========================================================================
    //  REQUERIMIENTOS PRINCIPALES
    // =========================================================================
 
    /**
     * Requerimiento 10: Crea una torre automáticamente con N tazas de IDs impares.
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
     * @param i ID de la taza (determina el tamaño según la fórmula ICPC).
     */
    public void pushCup(int i) {
        if (tower.findCupById(i) != null) {
            ui.showWarning("La taza con ID " + i + " ya existe.");
            return;
        }
 
        int heightPx = ((2 * i) - 1) * 20;
        int widthPx  = i * 20;
        String color = COLORS[cups.size() % COLORS.length];
        int groundLevel = towerHeight - MARGIN_BOTTOM;
 
        Cup newCup = new Cup(i, heightPx, widthPx, color, towerWidth / 2, groundLevel);
 
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
     * @param cupId ID de la taza que recibe la tapa.
     */
    public void pushLid(int cupId) {
        Cup cup = tower.findCupById(cupId);
        if (cup == null) return;
        if (findLidByCupId(cupId) != null) return;
 
        Lid lid = new Lid(cupId, cup.getWidth(), 20, cup.getColor());
        lid.setPosition(cup.getX(), cup.getY() - cup.getHeight());
        lid.makeVisible();
        lids.add(lid);
 
        cup.changeColor("black");
    }
 
    /**
     * Requerimiento 11: Intercambia la posición de dos objetos.
     * @param type "cup" para tazas, "lid" para tapas. Otros valores son ignorados.
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
                if (c1 != null) c1.changeColor("black");
                if (c2 != null) c2.changeColor("black");
            }
        }
        // Tipos inválidos se ignoran silenciosamente
    }
 
    /**
     * Elimina una taza por ID. Su tapa asociada también se elimina.
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
     * Elimina la tapa de una taza y restaura su color original.
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
 
    // =========================================================================
    //  ORDENAMIENTO
    // =========================================================================
 
    /**
     * Ordena las tazas de menor a mayor ID.
     */
    public void reorganizeTower() {
        tower.reorganizeTower();
        syncLids();
    }
 
    /**
     * Ordena las tazas de mayor a menor ID (modo anidamiento).
     */
    public void reorganizeTowerReverse() {
        tower.reorganizeTowerReverse();
        syncLids();
    }
 
    // =========================================================================
    //  CONSULTAS
    // =========================================================================
 
    /**
     * Muestra información detallada del estado actual de la simulación.
     */
    public void consultInformation() {
        int lidedCount   = 0;
        int unlidedCount = 0;
        for (Cup cup : cups) {
            if (findLidByCupId(cup.getId()) != null) lidedCount++;
            else unlidedCount++;
        }
        ui.showSimulationReport(cups.size(), lidedCount, unlidedCount, lids.size());
    }
 
    /**
     * Muestra el mejor movimiento de intercambio para optimizar la altura.
     */
    public void consultSwap() {
        ui.showSwapSuggestion(tower.consultSwapToReduce());
    }
 
    /**
     * Finaliza la simulación por completo.
     */
    public void finishSimulation() {
        resetInternalData();
        ui.showFinishMessage();
    }
 
    // =========================================================================
    //  MÉTODOS PRIVADOS
    // =========================================================================
 
    /**
     * Sincroniza la posición de las tapas con sus respectivas tazas.
     * Debe llamarse después de cualquier movimiento masivo de la torre.
     */
    private void syncLids() {
        for (Lid lid : lids) {
            Cup cup = tower.findCupById(lid.getCupId());
            if (cup != null) {
                lid.setPosition(cup.getX(), cup.getY() - cup.getHeight());
            }
        }
    }
 
    /**
     * Limpia todos los datos internos sin mostrar mensajes.
     */
    private void resetInternalData() {
        tower.clear();
        for (Cup c : cups) c.makeInvisible();
        for (Lid l : lids) l.makeInvisible();
        cups.clear();
        lids.clear();
    }
 
    /**
     * Busca una tapa por el ID de su taza asociada.
     */
    private Lid findLidByCupId(int id) {
        for (Lid l : lids) {
            if (l.getCupId() == id) return l;
        }
        return null;
    }
 
    /** @return Cantidad actual de tazas en la simulación. */
    public int getCupCount() { return cups.size(); }
 
    /** @return Cantidad actual de tapas en la simulación. */
    public int getLidCount() { return lids.size(); }
 
    /** @return Objeto Lid asociado a un ID, o null si no existe. */
    public Lid getLid(int id) { return findLidByCupId(id); }
 
    /** @return El objeto Cup con el ID dado, o null si no existe. */
    public Cup getCup(int id) { return tower.findCupById(id); }
}
