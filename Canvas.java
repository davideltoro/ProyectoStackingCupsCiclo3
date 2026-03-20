import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;

/**
 * Canvas 
 * Configurado con fondo blanco y marcas de altura laterales (regla).
 * @author Juan Castellanos - Juan Uribe
 * @version 2.0
 */
public class Canvas {
    private static Canvas canvasSingleton;

    /**
     * Método de factoría para obtener la instancia única del canvas con dimensiones específicas.
     * @param width Ancho deseado de la ventana.
     * @param height Alto deseado de la ventana.
     */
    public static Canvas getCanvas(int width, int height) {
        if (canvasSingleton == null) {
            canvasSingleton = new Canvas("Stacking Cups Board", width, height, Color.white);
        }
        canvasSingleton.setVisible(true);
        return canvasSingleton;
    }

    /**
     * Sobrecarga para obtener la instancia existente sin especificar medidas.
     */
    public static Canvas getCanvas() {
        return getCanvas(800, 800);
    }

    /**
     * Reinicia el singleton para permitir crear un nuevo canvas con dimensiones distintas.
     * Útil cuando simulate necesita un canvas de tamaño diferente al existente.
     */
    public static void resetCanvas() {
        if (canvasSingleton != null) {
            canvasSingleton.frame.dispose();
            canvasSingleton = null;
        }
    }

    private JFrame frame;
    private CanvasPane canvas;
    private Graphics2D graphic;
    private Color backgroundColour;
    private Image canvasImage;
    private List<Object> objects;
    private HashMap<Object, ShapeDescription> shapes;

    private Canvas(String title, int width, int height, Color bgColour) {
        frame = new JFrame();
        canvas = new CanvasPane();
        frame.setContentPane(canvas);
        frame.setTitle(title);
        canvas.setPreferredSize(new Dimension(width, height));
        backgroundColour = bgColour;
        frame.pack();
        objects = new ArrayList<Object>();
        shapes = new HashMap<Object, ShapeDescription>();
    }

    /**
     * Establece la visibilidad y prepara el entorno gráfico inicial.
     */
    public void setVisible(boolean visible) {
        if (graphic == null) {
            Dimension size = canvas.getSize();
            canvasImage = canvas.createImage(size.width, size.height);
            graphic = (Graphics2D) canvasImage.getGraphics();
            graphic.setColor(backgroundColour);
            graphic.fillRect(0, 0, size.width, size.height);
            graphic.setColor(Color.black);
        }
        frame.setVisible(visible);
    }

    /**
     * Dibuja una forma geométrica en el canvas.
     */
    public void draw(Object referenceObject, String color, Shape shape) {
        objects.remove(referenceObject);
        objects.add(referenceObject);
        shapes.put(referenceObject, new ShapeDescription(shape, color));
        redraw();
    }

    /**
     * Elimina una forma del canvas.
     */
    public void erase(Object referenceObject) {
        objects.remove(referenceObject);
        shapes.remove(referenceObject);
        redraw();
    }

    /**
     * Cambia el color del pincel basado en un String.
     */
    public void setForegroundColor(String colorString) {
        switch (colorString.toLowerCase()) {
            case "red":     graphic.setColor(Color.red);     break;
            case "black":   graphic.setColor(Color.black);   break;
            case "blue":    graphic.setColor(Color.blue);    break;
            case "yellow":  graphic.setColor(Color.yellow);  break;
            case "green":   graphic.setColor(Color.green);   break;
            case "magenta": graphic.setColor(Color.magenta); break;
            case "white":   graphic.setColor(Color.white);   break;
            default:        graphic.setColor(Color.black);
        }
    }

    /**
     * Pausa la ejecución (útil para animaciones).
     */
    public void wait(int milliseconds) {
        try { Thread.sleep(milliseconds); } catch (Exception e) {}
    }

    /**
     * Redibuja todo el escenario: fondo, marcas y objetos.
     */
    private void redraw() {
        erase();
        drawHeightMarks();
        for (Object obj : objects) {
            shapes.get(obj).draw(graphic);
        }
        canvas.repaint();
    }

    /**
     * Limpia el fondo con color sólido y dibuja el borde.
     */
    private void erase() {
        Dimension size = canvas.getSize();
        graphic.setColor(backgroundColour);
        graphic.fillRect(0, 0, size.width, size.height);
        graphic.setColor(Color.black);
        graphic.drawRect(0, 0, size.width - 1, size.height - 1);
    }

    /**
     * Dibuja líneas de escala tipo regla en los bordes.
     */
    private void drawHeightMarks() {
        graphic.setColor(Color.LIGHT_GRAY);
        int pixelsPerCm = 20;
        int markLength  = 10;
        Dimension size  = canvas.getSize();

        for (int y = size.height; y >= 0; y -= pixelsPerCm) {
            graphic.drawLine(0, y, markLength, y);
            graphic.drawLine(size.width - markLength, y, size.width, y);
        }
    }

    // --- Clases Internas ---

    private class CanvasPane extends JPanel {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(canvasImage, 0, 0, null);
        }
    }

    private class ShapeDescription {
        private Shape shape;
        private String colorString;

        public ShapeDescription(Shape shape, String color) {
            this.shape       = shape;
            this.colorString = color;
        }

        public void draw(Graphics2D graphic) {
            setForegroundColor(colorString);
            graphic.fill(shape);
            graphic.setColor(Color.black);
            graphic.draw(shape);
        }
    }
}