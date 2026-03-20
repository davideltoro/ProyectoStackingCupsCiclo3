import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
 
/**
 * Pruebas de unidad para la clase StackingCups (Coordinador).
 * Cubre todos los métodos públicos.
 *
 * Nomenclatura: accordingCU... (Castellanos-Uribe)
 *
 * CATEGORÍAS:
 *  - pushCup      : creación de tazas
 *  - pushLid      : creación de tapas
 *  - swap         : intercambios (cup y lid)
 *  - removeCup    : eliminación de tazas
 *  - removeLid    : eliminación de tapas
 *  - tower        : creación automática
 *  - reorganize   : ordenamientos
 *  - consult*     : consultas informativas
 *  - finish       : finalización de simulación
 *  - Robustez     : casos de error e IDs inválidos
 *
 * @author Juan Castellanos - Juan Uribe
 */
public class TowerCC2Test {
 
    private StackingCups simulator;
 
    @Before
    public void setUp() {
        simulator = new StackingCups(800, 600);
    }
 
    @After
    public void tearDown() {
        simulator.finishSimulation();
    }
    //  pushCup
 
    /**
     * Verifica que se cree una taza correctamente.
     */
    @Test
    public void accordingCUShouldAddSingleCup() {
        simulator.pushCup(3);
        assertEquals("Debería haber exactamente 1 taza", 1, simulator.getCupCount());
    }
 
    /**
     * Verifica que se puedan apilar múltiples tazas distintas.
     */
    @Test
    public void accordingCUShouldAddMultipleCups() {
        simulator.pushCup(1);
        simulator.pushCup(3);
        simulator.pushCup(5);
        assertEquals("Debería haber 3 tazas", 3, simulator.getCupCount());
    }
 
    /**
     * Verifica que no se permitan tazas con ID duplicado.
     */
    @Test
    public void accordingCUShouldIgnoreDuplicateCupId() {
        simulator.pushCup(2);
        simulator.pushCup(2);
        assertEquals("Solo debe existir 1 taza con ese ID", 1, simulator.getCupCount());
    }
 
    /**
     * Verifica que la taza creada sea recuperable por su ID.
     */
    @Test
    public void accordingCUShouldReturnCupById() {
        simulator.pushCup(4);
        assertNotNull("La taza debe ser recuperable por ID", simulator.getCup(4));
    }
 
    /**
     * Verifica que un ID inexistente retorne null.
     */
    @Test
    public void accordingCUShouldReturnNullForNonExistentCup() {
        assertNull("No debe encontrar taza inexistente", simulator.getCup(99));
    }
 
    //  pushLid
    
    /**
     * Verifica que se cree una tapa para una taza existente.
     */
    @Test
    public void accordingCUShouldAddLidToExistingCup() {
        simulator.pushCup(5);
        simulator.pushLid(5);
        assertEquals("Debe haber 1 tapa", 1, simulator.getLidCount());
    }
 
    /**
     * Verifica que no se cree tapa para una taza inexistente.
     */
    @Test
    public void accordingCUShouldNotAddLidToNonExistentCup() {
        simulator.pushLid(99);
        assertEquals("No debe haberse creado ninguna tapa", 0, simulator.getLidCount());
    }
 
    /**
     * Verifica que no se duplique una tapa en la misma taza.
     */
    @Test
    public void accordingCUShouldNotDuplicateLidOnSameCup() {
        simulator.pushCup(3);
        simulator.pushLid(3);
        simulator.pushLid(3);
        assertEquals("Solo debe existir 1 tapa por taza", 1, simulator.getLidCount());
    }
 
    /**
     * Verifica que la tapa creada sea recuperable por el ID de su taza.
     */
    @Test
    public void accordingCUShouldReturnLidByCupId() {
        simulator.pushCup(2);
        simulator.pushLid(2);
        assertNotNull("La tapa debe ser recuperable", simulator.getLid(2));
    }
 
    //  swap — tipo "cup"
 
    /**
     * Verifica que la cantidad de tazas no cambie después de un swap de tazas.
     */
    @Test
    public void accordingCUShouldPreserveCupCountAfterCupSwap() {
        simulator.pushCup(1);
        simulator.pushCup(5);
        simulator.swap("cup", 1, 5);
        assertEquals("La cantidad de tazas no debe cambiar", 2, simulator.getCupCount());
    }
 
    /**
     * Verifica que las tapas sigan a sus tazas después de un swap de tazas.
     */
    @Test
    public void accordingCUShouldSyncLidsAfterCupSwap() {
        simulator.pushCup(10);
        simulator.pushCup(1);
        simulator.pushLid(1);
 
        int yOriginal = getLidY(1);
        simulator.swap("cup", 10, 1);
        int yNuevo = getLidY(1);
 
        assertNotEquals("La tapa debe moverse junto con la taza", yOriginal, yNuevo);
    }
 
    //  swap — tipo "lid"
    
    /**
     * Verifica que el swap de tapas reasigne los dueños correctamente.
     */
    @Test
    public void accordingCUShouldTransferOwnershipOnLidSwap() {
        simulator.pushCup(1);
        simulator.pushCup(5);
        simulator.pushLid(1);
        simulator.pushLid(5);
 
        simulator.swap("lid", 1, 5);
 
        assertNotNull("La tapa con ID reasignado debe existir", simulator.getLid(5));
    }
 
    /**
     * Verifica que el swap de tapas no cambie la cantidad total de tapas.
     */
    @Test
    public void accordingCUShouldPreserveLidCountAfterLidSwap() {
        simulator.pushCup(1);
        simulator.pushCup(3);
        simulator.pushLid(1);
        simulator.pushLid(3);
 
        simulator.swap("lid", 1, 3);
 
        assertEquals("La cantidad de tapas no debe cambiar", 2, simulator.getLidCount());
    }
 
    /**
     * Verifica que un swap de tapas con tapas inexistentes no cause error.
     */
    @Test
    public void accordingCUShouldIgnoreLidSwapWhenLidsMissing() {
        simulator.pushCup(1);
        simulator.pushCup(3);
        // Sin tapas creadas
        simulator.swap("lid", 1, 3);
        assertEquals("La cantidad de tapas debe seguir en 0", 0, simulator.getLidCount());
    }
 
    /**
     * Verifica que un tipo inválido en swap no altere el estado.
     */
    @Test
    public void accordingCUShouldIgnoreInvalidSwapType() {
        simulator.pushCup(1);
        simulator.pushCup(2);
        simulator.swap("box", 1, 2);
        assertEquals("El conteo de tazas no debe cambiar", 2, simulator.getCupCount());
    }
    //  removeCup
 
    /**
     * Verifica que al eliminar una taza, el conteo disminuya.
     */
    @Test
    public void accordingCUShouldDecreaseCupCountOnRemove() {
        simulator.pushCup(3);
        simulator.removeCup(3);
        assertEquals("Debe haber 0 tazas tras eliminar la única", 0, simulator.getCupCount());
    }
 
    /**
     * Verifica que al eliminar una taza, su tapa también desaparezca.
     */
    @Test
    public void accordingCUShouldRemoveLidWhenCupIsRemoved() {
        simulator.pushCup(5);
        simulator.pushLid(5);
        simulator.removeCup(5);
        assertNull("La tapa debe haberse eliminado con la taza", simulator.getLid(5));
    }
 
    /**
     * Verifica que eliminar un ID inexistente no cause error.
     */
    @Test
    public void accordingCUShouldNotFailWhenRemovingNonExistentCup() {
        simulator.pushCup(2);
        simulator.removeCup(99); // No existe
        assertEquals("La taza original debe seguir intacta", 1, simulator.getCupCount());
    }
    //  removeLid
 
    /**
     * Verifica que se elimine la tapa correctamente.
     */
    @Test
    public void accordingCUShouldRemoveLidSuccessfully() {
        simulator.pushCup(4);
        simulator.pushLid(4);
        simulator.removeLid(4);
        assertEquals("Debe haber 0 tapas", 0, simulator.getLidCount());
    }
 
    /**
     * Verifica que eliminar una tapa inexistente no cause error.
     */
    @Test
    public void accordingCUShouldNotFailWhenRemovingNonExistentLid() {
        simulator.pushCup(4);
        simulator.removeLid(4); // Sin tapa creada
        assertEquals("La taza debe seguir existiendo", 1, simulator.getCupCount());
    }
 
    /**
     * Verifica que la taza recupere su color original tras quitar la tapa.
     */
    @Test
    public void accordingCUShouldRestoreCupColorAfterLidRemoval() {
        simulator.pushCup(2);
        Cup cup = simulator.getCup(2);
        String colorOriginal = cup.getColor();
 
        simulator.pushLid(2);
        // Tras pushLid el color cambia a "black"
        assertEquals("El color tras la tapa debe ser black", "black", cup.getColor());
 
        simulator.removeLid(2);
        assertEquals("El color debe restaurarse al original", colorOriginal, cup.getColor());
    }
 
    //  tower (creación automática)
 
    /**
     * Verifica que tower() cree la cantidad exacta de tazas con IDs impares.
     */
    @Test
    public void accordingCUShouldCreateCorrectNumberOfCupsInAutoTower() {
        simulator.tower(5);
        assertEquals("Deben crearse exactamente 5 tazas", 5, simulator.getCupCount());
    }
 
    /**
     * Verifica que tower() use IDs impares (1, 3, 5, 7, 9...).
     */
    @Test
    public void accordingCUShouldCreateCupsWithOddIds() {
        simulator.tower(3);
        assertNotNull("Debe existir la taza con ID 1", simulator.getCup(1));
        assertNotNull("Debe existir la taza con ID 3", simulator.getCup(3));
        assertNotNull("Debe existir la taza con ID 5", simulator.getCup(5));
    }
 
    /**
     * Verifica que tower() limpie la simulación previa antes de crear una nueva.
     */
    @Test
    public void accordingCUShouldResetBeforeCreatingNewTower() {
        simulator.tower(3);
        simulator.tower(2);
        assertEquals("Solo deben existir las tazas de la segunda llamada", 2, simulator.getCupCount());
    }
 
    //  reorganizeTower / reorganizeTowerReverse
 
    /**
     * Verifica que reorganizar no cambie la cantidad de tazas.
     */
    @Test
    public void accordingCUShouldPreserveCupCountAfterReorganize() {
        simulator.pushCup(3);
        simulator.pushCup(1);
        simulator.pushCup(5);
        simulator.reorganizeTower();
        assertEquals("La cantidad de tazas no debe cambiar", 3, simulator.getCupCount());
    }
 
    /**
     * Verifica que reorganizeTowerReverse no cambie la cantidad de tazas.
     */
    @Test
    public void accordingCUShouldPreserveCupCountAfterReverseReorganize() {
        simulator.pushCup(2);
        simulator.pushCup(4);
        simulator.reorganizeTowerReverse();
        assertEquals("La cantidad de tazas no debe cambiar", 2, simulator.getCupCount());
    }
 
    /**
     * Verifica que reorganizar sincronice las tapas existentes.
     */
    @Test
    public void accordingCUShouldSyncLidsAfterReorganize() {
        simulator.pushCup(1);
        simulator.pushCup(5);
        simulator.pushLid(1);
        int yAntes = getLidY(1);
 
        simulator.reorganizeTower();
        int yDespues = getLidY(1);
 
        // La posición puede cambiar o no según el orden, pero no debe ser -1 (null)
        assertNotEquals("La tapa no debe haberse desvinculado", -1, yDespues);
    }
 
    //  consultInformation
    /**
     * Verifica que consultInformation no lanza excepciones con tazas y tapas.
     */
    @Test
    public void accordingCUShouldRunConsultInformationWithoutException() {
        simulator.pushCup(1);
        simulator.pushCup(3);
        simulator.pushLid(1);
        simulator.consultInformation();
        assertTrue("consultInformation debe ejecutarse sin errores", true);
    }
 
    /**
     * Verifica que consultInformation funcione con la torre vacía.
     */
    @Test
    public void accordingCUShouldRunConsultInformationOnEmptyTower() {
        simulator.consultInformation();
        assertTrue("consultInformation en torre vacía no debe fallar", true);
    }
    /**
     * Verifica que consultSwap no lanza excepciones con múltiples tazas.
     */
    @Test
    public void accordingCUShouldRunConsultSwapWithoutException() {
        simulator.pushCup(5);
        simulator.pushCup(1);
        simulator.consultSwap();
        assertTrue("consultSwap debe ejecutarse sin errores", true);
    }
 
    /**
     * Verifica que consultSwap no falla con menos de 2 tazas.
     */
    @Test
    public void accordingCUShouldRunConsultSwapWithSingleCup() {
        simulator.pushCup(3);
        simulator.consultSwap();
        assertTrue("consultSwap con 1 taza no debe fallar", true);
    }
 
    // =========================================================================
    //  finishSimulation
    // =========================================================================
 
    /**
     * Verifica que finishSimulation vacíe por completo la simulación.
     */
    @Test
    public void accordingCUShouldClearAllDataOnFinish() {
        simulator.pushCup(1);
        simulator.pushCup(3);
        simulator.pushLid(1);
        simulator.finishSimulation();
        assertEquals("No deben quedar tazas", 0, simulator.getCupCount());
        assertEquals("No deben quedar tapas", 0, simulator.getLidCount());
    }
 
    /**
     * Retorna la coordenada Y de una tapa, o -1 si no existe.
     */
    private int getLidY(int cupId) {
        Lid l = simulator.getLid(cupId);
        return (l != null) ? l.getY() : -1;
    }
}