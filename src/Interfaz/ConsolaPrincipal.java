package interfaz;

import java.util.Scanner;
import modelo.Articulo;
import modelo.TicketSOS;
import modelo.Usuario;
import negocio.GestorInventario;
import negocio.GestorPrestamos;
import negocio.GestorReputacion;
import negocio.GestorSOS;

public class ConsolaPrincipal {

    private Scanner sc;
    private GestorInventario gestorInventario;
    private GestorPrestamos gestorPrestamos;
    private GestorSOS gestorSOS;
    private GestorReputacion gestorReputacion;

    public ConsolaPrincipal() {
        this.sc = new Scanner(System.in);
        this.gestorInventario = new GestorInventario();
        this.gestorPrestamos = new GestorPrestamos();
        this.gestorSOS = new GestorSOS();
        this.gestorReputacion = new GestorReputacion();
        cargarDatosEjemplo();
    }

    private void cargarDatosEjemplo() {
        //
        Usuario christopher = new Usuario("2300001", "Christopher Alvia", "christopher.alvia@udla.edu.ec", "UDLAPark");
        Usuario keira = new Usuario("2300002", "Keira", "keira@udla.edu.ec", "Granados");
        Usuario mateo = new Usuario("2300003", "Mateo", "mateo@udla.edu.ec", "Colon");
        Usuario martina = new Usuario("2300004", "Martina", "martina@udla.edu.ec", "UDLAPark");
        Usuario diego = new Usuario("2300005", "Diego Moreno", "diego.moreno@udla.edu.ec", "Colon");
        diego.agregarReporteNegativo();
        diego.agregarReporteNegativo();
        diego.agregarReporteNegativo();

        gestorInventario.registrarUsuario(christopher);
        gestorInventario.registrarUsuario(keira);
        gestorInventario.registrarUsuario(mateo);
        gestorInventario.registrarUsuario(martina);
        gestorInventario.registrarUsuario(diego);

        gestorInventario.registrarArticulo(new Articulo("ART-01", "Calculadora científica", "Electrónica",
                "Bueno", "Préstamo temporal", 0.50, christopher));
        gestorInventario.registrarArticulo(new Articulo("ART-02", "Kit de dibujo técnico", "Dibujo",
                "Excelente", "Micropago", 1.00, keira));
        gestorInventario.registrarArticulo(new Articulo("ART-03", "Bata de laboratorio", "Laboratorio",
                "Bueno", "Préstamo temporal", 0.75, mateo));
        gestorInventario.registrarArticulo(new Articulo("ART-04", "Botiquín de primeros auxilios", "Emergencia",
                "Excelente", "Regalo", 0.0, martina));
        gestorInventario.registrarArticulo(new Articulo("ART-05", "Cargador de laptop", "Electrónica",
                "Regular", "Micropago", 0.50, christopher));
        gestorInventario.registrarArticulo(new Articulo("ART-06", "Multímetro", "Electrónica",
                "Bueno", "Préstamo temporal", 1.50, keira));

        gestorPrestamos.crearPrestamo("PRES-01", martina, gestorInventario.buscarArticuloPorId("ART-06"), 2);

        TicketSOS t1 = gestorSOS.emitirTicket(mateo, "Cargador de laptop para examen", "Colon", "Bloque A", "2");
        gestorSOS.simularTiempo(t1, 3);                       // SOS_ACTIVO (3 min)

        TicketSOS t2 = gestorSOS.emitirTicket(keira, "Botiquín de primeros auxilios", "Granados", "Edificio Central", "1");
        gestorSOS.simularTiempo(t2, 4);
        gestorSOS.confirmarEntrega(t2, christopher);          // SOS_RESUELTO

        TicketSOS t3 = gestorSOS.emitirTicket(martina, "Multímetro para práctica de laboratorio", "UDLAPark", "Bloque B", "3");
        gestorSOS.simularTiempo(t3, 12);                      // SOS_EXPIRADO_REASIGNANDO

        gestorSOS.emitirTicket(diego, "Calculadora científica", "Colon", "Bloque A", "3"); // SOS_ACTIVO
    }

    private void mostrarDatosEjemplo() {
        System.out.println("\n--- DATOS PRECARGADOS ---");
        System.out.println("Estudiantes:");
        for (Usuario u : gestorInventario.getListaUsuarios()) {
            System.out.println("  " + u);
        }
        System.out.println("Artículos:");
        for (Articulo a : gestorInventario.getListaArticulos()) {
            System.out.println("  " + a);
        }
        System.out.println("Tickets SOS:");
        for (TicketSOS t : gestorSOS.getListaTickets()) {
            System.out.println("  " + t);
        }
        System.out.println("Préstamo de ejemplo: PRES-01 (EN_CURSO).");
    }

    public void iniciar() {
        int opcion = -1;
        do {
            System.out.println("\n===== CAMPUSLOOP =====");
            System.out.println("1. Módulo 1: Usuarios e inventario por campus");
            System.out.println("2. Módulo 2: Préstamos y devoluciones");
            System.out.println("3. Módulo 3: Canal SOS e insumos críticos");
            System.out.println("4. Módulo 4: Reputación y calificaciones");
            System.out.println("5. Ver datos precargados");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");

            try {
                opcion = Integer.parseInt(sc.nextLine().trim());
                switch (opcion) {
                    case 1:
                        gestorInventario.mostrarMenuModulo1(sc);
                        break;
                    case 2:
                        gestorPrestamos.mostrarMenuModulo2(sc, gestorInventario);
                        break;
                    case 3:
                        gestorSOS.mostrarMenuModulo3(sc, gestorInventario);
                        break;
                    case 4:
                        gestorReputacion.mostrarMenuModulo4(sc, gestorInventario);
                        break;
                    case 5:
                        mostrarDatosEjemplo();
                        break;
                    case 0:
                        System.out.println("Saliendo de CampusLoop. ¡Hasta pronto!");
                        break;
                    default:
                        System.out.println("Opción inválida.");
                }
            } catch (NumberFormatException e) {
                // Un dato numérico mal escrito en cualquier submenú regresa al menú principal
                System.out.println("Entrada inválida: se esperaba un número. Regresando al menú principal.");
                opcion = -1;
            }
        } while (opcion != 0);
    }
}
