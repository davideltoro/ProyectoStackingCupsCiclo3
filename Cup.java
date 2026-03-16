import java.util.ArrayList;
import java.awt.Shape;
import java.awt.Point;

 
/**
 * Representa una taza compuesta por tres objetos Rectangle en forma de "U".
 * Utiliza un vector de posición (Point) para centralizar el movimiento
 * y la lógica de sus componentes internos.
 *
 * REFACTORIZACIÓN SOLID:
 * - SRP: Cup solo es responsable de representar y posicionar una taza.
 *   No contiene lógica de negocio de la torre ni de la UI.
 * - OCP: Implementa la interfaz Stackable, lo que permite que Tower
 *   y StackingCups trabajen con el contrato sin depender de la clase concreta.
 *
 * @author Juan Castellanos - Juan Uribe
 * @version 7.0 (SOLID Refactor)
 */
public class Cup implements Stackable {
 
    /** Lista que contiene las partes físicas (rectángulos) de la taza */
    private ArrayList<Rectangle> style;
    
    private Rectangle rectangle;
    private boolean isVisible;
    private int id;
    private int width;
    private int height;
    private String color;
 
    /**
     * Vector de posición central.
     * Representa el punto medio de la base de la taza (su eje de gravedad).
     */
    private Point position;
 
    // =========================================================================
    //  GETTERS (Stackable + propios)
    // =========================================================================
 
    @Override public int getX()      { return (int) position.getX(); }
    @Override public int getY()      { return (int) position.getY(); }
    @Override public int getWidth()  { return width; }
    @Override public int getHeight() { return height; }
 
    public int getId()       { return id; }
    public String getColor() { return color; }
 
    // =========================================================================
    //  CONSTRUCTOR
    // =========================================================================
 
    /**
     * Constructor de la taza. Configura dimensiones, color y posición inicial.
     * @param id          Identificador único.
     * @param totalHeight Altura total en píxeles.
     * @param borderWidth Ancho total en píxeles.
     * @param color       Color de la taza.
     * @param posX        Coordenada X del vector inicial.
     * @param posY        Coordenada Y del vector inicial.
     */
    public Cup(int id, int totalHeight, int borderWidth, String color, int posX, int posY) {
        this.id        = id;
        this.height    = totalHeight;
        this.width     = borderWidth;
        this.color     = color;
        this.isVisible = false;
        this.style     = new ArrayList<>();
        this.position  = new Point(posX, posY);
 
        // Pared izquierda, pared derecha, base
        style.add(new Rectangle());
        style.add(new Rectangle());
        style.add(new Rectangle());
 
        // Paredes de 1/5 del ancho total
        style.get(0).changeSize(totalHeight, borderWidth / 5);
        style.get(1).changeSize(totalHeight, borderWidth / 5);
        style.get(2).changeSize(totalHeight / 5, borderWidth);
 
        changeColor(color);
        updateComponents();
    }
 
    // =========================================================================
    //  STACKABLE — implementación de la interfaz
    // =========================================================================
 
    /**
     * Modifica la ubicación de la taza a una coordenada específica.
     * @param x Nueva coordenada X.
     * @param y Nueva coordenada Y.
     */
    @Override
    public void setPosition(int x, int y) {
        this.position.setLocation(x, y);
        updateComponents();
    }
 
    /** Hace que todos los componentes de la taza se dibujen en el canvas. */
    @Override
    public void makeVisible() {
        if (!isVisible) {
            for (Rectangle r : style) r.makeVisible();
            isVisible = true;
        }
    }
 
    /** Borra la representación gráfica de todos los componentes. */
    @Override
    public void makeInvisible() {
        if (isVisible) {
            for (Rectangle r : style) r.makeInvisible();
            isVisible = false;
        }
    }
 
    // =========================================================================
    //  MÉTODOS PROPIOS DE CUP
    // =========================================================================
 
    /**
     * Actualiza el color de todos los rectángulos que componen la taza.
     * @param color Nombre del color (ej: "red", "blue").
     */
    public void changeColor(String color) {
        this.color = color;
        for (Rectangle r : style) r.changeColor(color);
    }
 
    /**
     * Retorna el área total que ocupa la taza (Bounding Box).
     * @return Shape que representa el área contenedora de la taza.
     */
    public Shape getShape() {
        return new java.awt.Rectangle(getX() - width / 2, getY() - height, width, height);
    }
 
    // =========================================================================
    //  MÉTODO PRIVADO
    // =========================================================================
 
    /**
     * Lógica de posicionamiento relativo:
     * Calcula dónde debe estar cada rectángulo para que juntos formen la "U".
     * Se basa en el vector 'position' para un reset absoluto.
     */
    private void updateComponents() {
        int x              = (int) position.getX();
        int y              = (int) position.getY();
        int wallThickness  = width / 5;
        int floorThickness = height / 5;
 
        Rectangle left  = style.get(0);
        Rectangle right = style.get(1);
        Rectangle floor = style.get(2);
 
        // Pared izquierda: borde izquierdo (x - width/2)
        left.moveHorizontal(x - (width / 2) - left.getX());
        left.moveVertical(y - height - left.getY());
 
        // Pared derecha: borde derecho restando su grosor
        right.moveHorizontal((x + width / 2 - wallThickness) - right.getX());
        right.moveVertical(y - height - right.getY());
 
        // Base: centro, subiendo su propio grosor desde el suelo
        floor.moveHorizontal(x - (width / 2) - floor.getX());
        floor.moveVertical(y - floorThickness - floor.getY());
    }
}