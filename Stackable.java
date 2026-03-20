/**
 * Interfaz que define el contrato para cualquier objeto que pueda
 * ser posicionado y mostrado/ocultado en el canvas del simulador.
 *
 * PRINCIPIO SRP: Separa la responsabilidad de "ser posicionable y visible"
 * de la lógica específica de cada objeto (Cup, Lid).
 *
 * PRINCIPIO OCP: Nuevos objetos visuales pueden implementar esta interfaz
 * sin modificar las clases existentes.
 *
 * @author Juan Castellanos - Juan Uribe
 * @version 1.0
 */
public interface Stackable {
 
    /**
     * Establece la posición absoluta del objeto en el canvas.
     * @param x Coordenada X (centro horizontal).
     * @param y Coordenada Y (base del objeto).
     */
    void setPosition(int x, int y);
 
    /**
     * Hace visible el objeto en el canvas.
     */
    void makeVisible();
 
    /**
     * Oculta el objeto del canvas.
     */
    void makeInvisible();
 
    /**
     * @return Coordenada X actual del objeto.
     */
    int getX();
 
    /**
     * @return Coordenada Y actual del objeto.
     */
    int getY();
 
    /**
     * @return Ancho del objeto en píxeles.
     */
    int getWidth();
 
    /**
     * @return Alto del objeto en píxeles.
     */
    int getHeight();
}