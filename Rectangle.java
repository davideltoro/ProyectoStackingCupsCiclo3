import java.awt.*;
 
/**
 * Un rectángulo que puede ser manipulado y que se dibuja a sí mismo en un canvas.
 * @author Juan Castellanos - Juan Uribe
 * @version 3.0
 */
public class Rectangle {
    public static int EDGES = 4;
 
    private int height;
    private int width;
    private int xPosition;
    private int yPosition;
    private String color;
    private boolean isVisible;
 
    /**
     * Crea un nuevo rectángulo con valores por defecto.
     */
    public Rectangle() {
        height    = 50;
        width     = 40;
        xPosition = 70;
        yPosition = 25;
        color     = "magenta";
        isVisible = false;
    }
 
    /**
     * Hace visible este rectángulo.
     */
    public void makeVisible() {
        isVisible = true;
        draw();
    }
 
    /**
     * Hace invisible este rectángulo.
     */
    public void makeInvisible() {
        erase();
        isVisible = false;
    }
 
    // --- Métodos de movimiento ---
    public void moveRight()  { moveHorizontal(20);  }
    public void moveLeft()   { moveHorizontal(-20); }
    public void moveUp()     { moveVertical(-20);   }
    public void moveDown()   { moveVertical(20);    }
 
    public void moveHorizontal(int distance) {
        erase();
        xPosition += distance;
        draw();
    }
 
    public void moveVertical(int distance) {
        erase();
        yPosition += distance;
        draw();
    }
 
    /**
     * Movimiento suave horizontal.
     */
    public void slowMoveHorizontal(int distance) {
        int delta       = (distance < 0) ? -1 : 1;
        int absDistance = Math.abs(distance);
        for (int i = 0; i < absDistance; i++) {
            xPosition += delta;
            draw();
            Canvas.getCanvas().wait(5);
        }
    }
 
    /**
     * Movimiento suave vertical.
     */
    public void slowMoveVertical(int distance) {
        int delta       = (distance < 0) ? -1 : 1;
        int absDistance = Math.abs(distance);
        for (int i = 0; i < absDistance; i++) {
            yPosition += delta;
            draw();
            Canvas.getCanvas().wait(5);
        }
    }
 
    /**
     * Cambia el tamaño del rectángulo.
     */
    public void changeSize(int newHeight, int newWidth) {
        erase();
        height = newHeight;
        width  = newWidth;
        draw();
    }
 
    /**
     * Cambia el color del rectángulo.
     */
    public void changeColor(String newColor) {
        color = newColor;
        draw();
    }
 
    // --- Métodos de dibujo ---
 
    private void draw() {
        if (isVisible) {
            Canvas board = Canvas.getCanvas();
            board.draw(this, color,
                new java.awt.Rectangle(xPosition, yPosition, width, height));
        }
    }
 
    private void erase() {
        if (isVisible) {
            Canvas board = Canvas.getCanvas();
            board.erase(this);
        }
    }
 
    // --- Getters ---
    public int getX()      { return xPosition; }
    public int getY()      { return yPosition; }
    public int getWidth()  { return width;     }
    public int getHeight() { return height;    }
}