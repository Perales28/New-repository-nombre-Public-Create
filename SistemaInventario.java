import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Interfaz que define el comportamiento común de todo producto inventariable.
 * Aplica abstracción: cualquier clase que la implemente debe saber
 * calcular su valor en inventario y decir si está disponible.
 */
interface Inventariable {
    double calcularValorInventario();
    boolean verificarDisponibilidad();
}

/**
 * Clase abstracta base del sistema de inventarios.
 * No se puede instanciar directamente: existe para heredar
 * atributos y comportamiento común a todos los productos.
 */
abstract class Producto implements Inventariable {

    // Atributos comunes a todos los productos (encapsulados)
    protected String codigo;
    protected String nombre;
    protected double precio;
    protected int cantidad;

    public Producto(String codigo, String nombre, double precio, int cantidad) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
    }

    // Getters y setters
    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public double getPrecio() { return precio; }
    public int getCantidad() { return cantidad; }

    public void setPrecio(double precio) { this.precio = precio; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    /**
     * Método abstracto: cada subclase define cómo se calcula su valor.
     * Aquí vive el polimorfismo.
     */
    @Override
    public abstract double calcularValorInventario();

    /** Método abstracto: cada subclase indica su tipo. */
    public abstract String getTipoProducto();

    /** Método concreto heredado por todas las subclases. */
    @Override
    public boolean verificarDisponibilidad() {
        return cantidad > 0;
    }

    /** Información general del producto. */
    public String mostrarInfo() {
        return String.format(
            "%-12s | %-18s | $%8.2f | Stock: %3d | Valor: $%10.2f | %s",
            getTipoProducto(), nombre, precio, cantidad,
            calcularValorInventario(),
            verificarDisponibilidad() ? "Disponible" : "Agotado"
        );
    }
}

/**
 * Subclase que representa productos electrónicos.
 * Hereda de Producto y añade atributos propios.
 */
class Electronico extends Producto {
    private int garantiaMeses;
    private double voltaje;

    public Electronico(String codigo, String nombre, double precio,
                       int cantidad, int garantiaMeses, double voltaje) {
        super(codigo, nombre, precio, cantidad);
        this.garantiaMeses = garantiaMeses;
        this.voltaje = voltaje;
    }

    /**
     * Los electrónicos con garantía mayor a 12 meses tienen
     * un recargo del 5% en su valor de inventario.
     */
    @Override
    public double calcularValorInventario() {
        double valorBase = precio * cantidad;
        if (garantiaMeses > 12) {
            valorBase *= 1.05;
        }
        return valorBase;
    }

    @Override
    public String getTipoProducto() { return "ELECTRÓNICO"; }
}

/**
 * Subclase que representa productos alimenticios.
 * Considera fecha de caducidad y si es perecedero.
 */
class Alimento extends Producto {
    private LocalDate fechaCaducidad;
    private boolean perecedero;

    public Alimento(String codigo, String nombre, double precio,
                    int cantidad, LocalDate fechaCaducidad, boolean perecedero) {
        super(codigo, nombre, precio, cantidad);
        this.fechaCaducidad = fechaCaducidad;
        this.perecedero = perecedero;
    }

    /**
     * Si el producto perecedero caduca en menos de 7 días,
     * su valor de inventario se reduce al 50%.
     */
    @Override
    public double calcularValorInventario() {
        double valorBase = precio * cantidad;
        if (perecedero && fechaCaducidad.isBefore(LocalDate.now().plusDays(7))) {
            valorBase *= 0.5;
        }
        return valorBase;
    }

    @Override
    public String getTipoProducto() { return "ALIMENTO"; }
}

/**
 * Subclase que representa prendas de ropa.
 * Añade talla y color.
 */
class Ropa extends Producto {
    private String talla;
    private String color;

    public Ropa(String codigo, String nombre, double precio,
                int cantidad, String talla, String color) {
        super(codigo, nombre, precio, cantidad);
        this.talla = talla;
        this.color = color;
    }

    /**
     * La ropa de talla única tiene un recargo del 10%
     * por su mayor demanda.
     */
    @Override
    public double calcularValorInventario() {
        double valorBase = precio * cantidad;
        if ("Única".equalsIgnoreCase(talla)) {
            valorBase *= 1.10;
        }
        return valorBase;
    }

    @Override
    public String getTipoProducto() { return "ROPA"; }
}

/**
 * Clase principal: demuestra el polimorfismo.
 * Se declara una lista de tipo Producto y se guardan objetos
 * de distintas subclases. Al recorrerla, cada objeto ejecuta
 * SU propia versión de los métodos.
 */
public class SistemaInventario {
    public static void main(String[] args) {

        // Lista polimórfica: tipo padre, objetos hijos
        List<Producto> inventario = new ArrayList<>();

        inventario.add(new Electronico("E001", "Laptop Lenovo", 15000.00, 5, 24, 19.5));
        inventario.add(new Electronico("E002", "Mouse Logitech", 350.00, 20, 6, 5.0));
        inventario.add(new Alimento("A001", "Leche Entera 1L", 25.50, 40,
                LocalDate.now().plusDays(3), true));
        inventario.add(new Alimento("A002", "Arroz 1kg", 32.00, 60,
                LocalDate.now().plusMonths(8), false));
        inventario.add(new Ropa("R001", "Playera Básica", 180.00, 15, "M", "Negro"));
        inventario.add(new Ropa("R002", "Gorra Deportiva", 220.00, 0, "Única", "Azul"));

        System.out.println("=".repeat(95));
        System.out.println("        SISTEMA DE GESTIÓN DE INVENTARIOS - DEMOSTRACIÓN DE POLIMORFISMO");
        System.out.println("=".repeat(95));
        System.out.printf("%-12s | %-18s | %9s | %5s | %12s | %s%n",
                "TIPO", "NOMBRE", "PRECIO", "STOCK", "VALOR INV.", "ESTADO");
        System.out.println("-".repeat(95));

        double totalInventario = 0;

        // POLIMORFISMO: el mismo método, distintos comportamientos
        for (Producto p : inventario) {
            System.out.println(p.mostrarInfo());
            totalInventario += p.calcularValorInventario();
        }

        System.out.println("-".repeat(95));
        System.out.printf("VALOR TOTAL DEL INVENTARIO: $%.2f%n", totalInventario);
        System.out.println("=".repeat(95));

        // Prueba adicional: contar productos por tipo usando instanceof
        int electronicos = 0, alimentos = 0, ropa = 0;
        for (Producto p : inventario) {
            if (p instanceof Electronico) electronicos++;
            else if (p instanceof Alimento) alimentos++;
            else if (p instanceof Ropa) ropa++;
        }
        System.out.println("\nResumen por categoría:");
        System.out.println("  Electrónicos: " + electronicos);
        System.out.println("  Alimentos:    " + alimentos);
        System.out.println("  Ropa:         " + ropa);
        System.out.println("\nDemostración completada: cada objeto respondió con su propio cálculo.");
    }
}
