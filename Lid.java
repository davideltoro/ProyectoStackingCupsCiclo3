import java.awt.Shape;
import java.awt.Point;
 
/**
 * Representa la tapa de una taza en el simulador.
 * Se vincula a una taza mediante su cupId y utiliza un vector
 * de posición central (Point) para su renderizado.
 *
 * REFACTORIZACIÓN SOLID:
 * - SRP: Lid solo es responsable de representar y posicionar una tapa.
 *   No contiene lógica de negocio ni de la UI.
 * - OCP: Implementa Stackable, permitiendo que StackingCups trabaje
 *   con el contrato sin depender de la clase concreta.
 *
 * @author Juan Castellanos - Juan Uribe
 * @version 5.0 (SOLID Refactor)
 */
public class Lid implements Stackable {
 
    private Rectangle cover;
    private boolean isVisible;
    private int cupId;
    private int width;
    private int height;
    private String color;
 
    /** Vector de posición central en la base de la tapa */
    private Point position;
 
    private final int SCALE = 20; // 1 cm = 20 px
 
    // =========================================================================
    //  CONSTRUCTOR
    // =========================================================================
 
    /**
     * Constructor de la tapa.
     * @param cupId    ID de la taza que la contiene.
     * @param widthPx  Ancho de la tapa en píxeles.
     * @param heightPx Alto de la tapa en píxeles.
     * @param color    Color de la tapa.
     */
    public Lid(int cupId, int widthPx, int heightPx, String color) {
        this.cupId     = cupId;
        this.width     = widthPx;
        this.height    = heightPx;
        this.color     = color;
        this.isVisible = false;
        this.position  = new Point(0, 0);
 
        cover = new Rectangle();
        cover.changeSize(this.height, this.width);
        cover.changeColor(color);
    }
 
    // =========================================================================
    //  STACKABLE — implementación de la interfaz
    // =========================================================================
 
    /**
     * Establece la posición absoluta de la tapa.
     * @param x Centro horizontal.
     * @param y Base vertical (punto de contacto con la taza).
     */
    @Override
    public void setPosition(int x, int y) {
        this.position.setLocation(x, y);
        updateComponents();
    }
 
    /** Muestra la tapa en el canvas. */
    @Override
    public void makeVisible() {
        if (!isVisible) {
            cover.makeVisible();
            isVisible = true;
        }
    }
 
    /** Oculta la tapa del canvas. */
    @Override
    public void makeInvisible() {
        if (isVisible) {
            cover.makeInvisible();
            isVisible = false;
        }
    }
 
    @Override public int getX()      { return (int) position.getX(); }
    @Override public int getY()      { return (int) position.getY(); }
    @Override public int getWidth()  { return width / SCALE; }
    @Override public int getHeight() { return height / SCALE; }
 
    // =========================================================================
    //  MÉTODOS PROPIOS DE LID
    // =========================================================================
 
    /**
     * Permite reasignar la tapa a una nueva taza (usado en swap de tapas).
     * @param newCupId El nuevo ID de la taza propietaria.
     */
    public void setCupId(int newCupId) {
        this.cupId = newCupId;
    }
 
    public int    getCupId() { return cupId; }
    public String getColor() { return color; }
 
    /**
     * Retorna el área ocupada por la tapa para colisiones o dibujo.
     * @return Shape rectangular de la tapa.
     */
    public Shape getShape() {
        return new java.awt.Rectangle(cover.getX(), cover.getY(),
                                      cover.getWidth(), cover.getHeight());
    }
 
    // =========================================================================
    //  MÉTODO PRIVADO
    // =========================================================================
 
    /**
     * Actualiza la posición del rectángulo interno basándose en el vector central.
     * X: se desplaza a x - (ancho/2) para que el punto central sea el eje.
     * Y: se desplaza a y - alto para que la base descanse sobre el tope de la taza.
     */
    private void updateComponents() {
        int x = (int) position.getX();
        int y = (int) position.getY();
        cover.moveHorizontal(x - (width / 2) - cover.getX());
        cover.moveVertical(y - height - cover.getY());
    }
}