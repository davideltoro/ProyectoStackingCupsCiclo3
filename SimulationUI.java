import javax.swing.JOptionPane;
 
/**
 * Clase responsable EXCLUSIVAMENTE de la comunicación con el usuario
 * a través de diálogos gráficos (JOptionPane).
 *
 * PRINCIPIO SRP: Extrae la responsabilidad de mostrar mensajes de
 * StackingCups y Tower, quienes NO deben saber nada de la UI.
 *
 * PRINCIPIO OCP: Si en el futuro se cambia JOptionPane por una consola
 * o una interfaz web, solo se modifica esta clase.
 *
 * @author Juan Castellanos - Juan Uribe
 * @version 1.0
 */
public class SimulationUI {
 
    /**
     * Muestra un mensaje informativo genérico.
     * @param message Texto del mensaje.
     */
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(null, message);
    }
 
    /**
     * Muestra un mensaje con título personalizado de tipo informativo.
     * @param message Texto del mensaje.
     * @param title   Título de la ventana de diálogo.
     */
    public void showInfo(String message, String title) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
 
    /**
     * Muestra un mensaje de advertencia (para errores de validación).
     * @param message Texto del mensaje.
     */
    public void showWarning(String message) {
        JOptionPane.showMessageDialog(null, message, "Advertencia", JOptionPane.WARNING_MESSAGE);
    }
 
    /**
     * Construye y muestra el reporte de estado de la simulación.
     * @param totalCups   Total de tazas en la simulación.
     * @param lidsCount   Tazas con tapa.
     * @param noLidCount  Tazas sin tapa.
     * @param totalLids   Total de tapas existentes.
     */
    public void showSimulationReport(int totalCups, int lidsCount, int noLidCount, int totalLids) {
        String report = "Estado de la Simulación \n"
                + "Total de tazas: " + totalCups + "\n"
                + "Tazas con tapa: " + lidsCount + "\n"
                + "Tazas sin tapa: " + noLidCount + "\n"
                + "Total de tapas: " + totalLids;
        showInfo(report, "Consulta de Información");
    }
 
    /**
     * Muestra la sugerencia de optimización de la torre.
     * @param suggestion Texto con la sugerencia calculada por Tower.
     */
    public void showSwapSuggestion(String suggestion) {
        showInfo(suggestion, "Optimización de Torre");
    }
 
    /**
     * Muestra confirmación de finalización de la simulación.
     */
    public void showFinishMessage() {
        showMessage("Simulación finalizada.");
    }
}