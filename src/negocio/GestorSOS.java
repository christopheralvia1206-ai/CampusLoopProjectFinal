package negocio;

import java.util.ArrayList;
import java.util.Scanner;
import modelo.TicketSOS;
import modelo.Usuario;

public class GestorSOS {
    private ArrayList<TicketSOS> listaTickets;
    private int contadorTickets;

    public GestorSOS() {
        this.listaTickets = new ArrayList<>();
        this.contadorTickets = 0;
    }

    public ArrayList<TicketSOS> getListaTickets() {
        return listaTickets;
    }

    private boolean vacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    // Requerimiento 1: Emisión de Ticket SOS (estado inicial SOS_ACTIVO)
    public TicketSOS emitirTicket(Usuario solicitante, String insumo,
                                  String campus, String edificio, String piso) {
        if (solicitante == null || vacio(insumo) || vacio(campus)
                || vacio(edificio) || vacio(piso)) {
            return null;
        }
        contadorTickets++;
        String id = String.format("SOS-%02d", contadorTickets);
        TicketSOS ticket = new TicketSOS(id, solicitante, insumo.trim(),
                campus.trim(), edificio.trim(), piso.trim());
        listaTickets.add(ticket);
        return ticket;
    }

    // Filtro de alertas activas por campus, edificio y piso (vacío = cualquiera)
    public ArrayList<TicketSOS> buscarActivos(String campus, String edificio, String piso) {
        ArrayList<TicketSOS> resultados = new ArrayList<>();
        for (TicketSOS t : listaTickets) {
            if (!t.getEstado().equals(TicketSOS.SOS_ACTIVO)) {
                continue;
            }
            boolean coincideCampus = vacio(campus) || t.getCampus().equalsIgnoreCase(campus.trim());
            boolean coincideEdificio = vacio(edificio) || t.getEdificio().equalsIgnoreCase(edificio.trim());
            boolean coincidePiso = vacio(piso) || t.getPiso().equalsIgnoreCase(piso.trim());
            if (coincideCampus && coincideEdificio && coincidePiso) {
                resultados.add(t);
            }
        }
        return resultados;
    }

    public TicketSOS buscarTicketPorId(String idTicket) {
        for (TicketSOS t : listaTickets) {
            if (t.getIdTicket().equalsIgnoreCase(idTicket)) {
                return t;
            }
        }
        return null;
    }

    // Requerimiento 2: Control de tiempo límite (10 minutos)
    public boolean verificarTiempoLimite(TicketSOS ticket) {
        if (ticket.getEstado().equals(TicketSOS.SOS_ACTIVO)
                && ticket.getMinutosTranscurridos() > TicketSOS.LIMITE_MINUTOS) {
            ticket.setEstado(TicketSOS.SOS_EXPIRADO_REASIGNANDO);
            return true;
        }
        return false;
    }

    // Simula el paso del tiempo en consola y revisa el límite
    public void simularTiempo(TicketSOS ticket, int minutos) {
        if (ticket.getEstado().equals(TicketSOS.SOS_ACTIVO) && minutos > 0) {
            ticket.avanzarMinutos(minutos);
            verificarTiempoLimite(ticket);
        }
    }

    // Confirmación de entrega: SOS_RESUELTO si llega dentro de los 10 minutos
    public String confirmarEntrega(TicketSOS ticket, Usuario ayudante) {
        if (ticket == null) {
            return "Error: Ticket no encontrado.";
        }
        if (ayudante == null) {
            return "Error: El estudiante que entrega no está registrado.";
        }
        if (ayudante == ticket.getSolicitante()) {
            return "Error: El solicitante no puede confirmar su propia entrega.";
        }
        if (ticket.getEstado().equals(TicketSOS.SOS_RESUELTO)) {
            return "Aviso: Este ticket ya está en estado "
                    + TicketSOS.formatear(TicketSOS.SOS_RESUELTO) + ".";
        }

        verificarTiempoLimite(ticket);
        if (ticket.getEstado().equals(TicketSOS.SOS_EXPIRADO_REASIGNANDO)) {
            return "Tiempo límite superado (" + ticket.getMinutosTranscurridos() + " min). "
                    + "Estado: " + TicketSOS.formatear(TicketSOS.SOS_EXPIRADO_REASIGNANDO)
                    + ". La entrega no se acepta.";
        }

        ticket.setAyudante(ayudante);
        ticket.setEstado(TicketSOS.SOS_RESUELTO);
        return "Entrega confirmada a tiempo (" + ticket.getMinutosTranscurridos() + " min). "
                + "Estado: " + TicketSOS.formatear(TicketSOS.SOS_RESUELTO) + ".";
    }

    private void imprimirLista(ArrayList<TicketSOS> tickets, String vacioMsg) {
        if (tickets.isEmpty()) {
            System.out.println(vacioMsg);
        } else {
            for (TicketSOS t : tickets) {
                System.out.println(t);
            }
        }
    }

    // Submenú de consola del Módulo 3
    public void mostrarMenuModulo3(Scanner sc, GestorInventario gestorInv) {
        int opcion = -1;
        do {
            System.out.println("\n--- MÓDULO 3: CANAL SOS E INSUMOS CRÍTICOS ---");
            System.out.println("1. Emitir ticket SOS");
            System.out.println("2. Consultar tickets SOS ACTIVO por campus, edificio y piso");
            System.out.println("3. Simular paso del tiempo (control de 10 minutos)");
            System.out.println("4. Confirmar entrega de un ticket");
            System.out.println("5. Listar todos los tickets");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione una opción: ");

            try {
                opcion = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Ingrese un número.");
                continue;
            }

            switch (opcion) {
                case 1:
                    System.out.print("ID del estudiante solicitante: ");
                    Usuario solicitante = gestorInv.buscarUsuarioPorId(sc.nextLine().trim());
                    if (solicitante == null) {
                        System.out.println("Error: Estudiante no encontrado.");
                        break;
                    }
                    System.out.print("Insumo crítico requerido: ");
                    String insumo = sc.nextLine();
                    System.out.print("Campus (UDLAPark / Granados / Colon): ");
                    String campus = sc.nextLine();
                    System.out.print("Edificio: ");
                    String edificio = sc.nextLine();
                    System.out.print("Piso: ");
                    String piso = sc.nextLine();

                    TicketSOS nuevo = emitirTicket(solicitante, insumo, campus, edificio, piso);
                    if (nuevo == null) {
                        System.out.println("Error: Todos los campos son obligatorios.");
                    } else {
                        System.out.println("Ticket emitido con estado " + nuevo.getEstadoVisible() + ":");
                        System.out.println(nuevo);
                    }
                    break;

                case 2:
                    System.out.println("(Deje un campo vacío para no filtrar por él)");
                    System.out.print("Campus: ");
                    String fCampus = sc.nextLine();
                    System.out.print("Edificio: ");
                    String fEdificio = sc.nextLine();
                    System.out.print("Piso: ");
                    String fPiso = sc.nextLine();
                    imprimirLista(buscarActivos(fCampus, fEdificio, fPiso),
                            "No hay tickets SOS ACTIVO con ese filtro.");
                    break;

                case 3:
                    System.out.print("ID del ticket (ej. SOS-01): ");
                    TicketSOS tSim = buscarTicketPorId(sc.nextLine().trim());
                    if (tSim == null) {
                        System.out.println("Error: Ticket no encontrado.");
                        break;
                    }
                    if (!tSim.getEstado().equals(TicketSOS.SOS_ACTIVO)) {
                        System.out.println("El ticket ya no está activo. Estado: " + tSim.getEstadoVisible());
                        break;
                    }
                    System.out.print("Minutos que transcurren: ");
                    int minutos;
                    try {
                        minutos = Integer.parseInt(sc.nextLine().trim());
                    } catch (NumberFormatException e) {
                        System.out.println("Error: Ingrese un número entero de minutos.");
                        break;
                    }
                    if (minutos <= 0) {
                        System.out.println("Error: Los minutos deben ser mayores a 0.");
                        break;
                    }
                    simularTiempo(tSim, minutos);
                    System.out.println(tSim);
                    if (tSim.getEstado().equals(TicketSOS.SOS_EXPIRADO_REASIGNANDO)) {
                        System.out.println("Se superaron los " + TicketSOS.LIMITE_MINUTOS
                                + " minutos sin confirmación: ticket en reasignación.");
                    }
                    break;

                case 4:
                    System.out.print("ID del ticket (ej. SOS-01): ");
                    TicketSOS tConf = buscarTicketPorId(sc.nextLine().trim());
                    if (tConf == null) {
                        System.out.println("Error: Ticket no encontrado.");
                        break;
                    }
                    System.out.print("ID del estudiante que entrega el insumo: ");
                    Usuario entrega = gestorInv.buscarUsuarioPorId(sc.nextLine().trim());
                    System.out.println(confirmarEntrega(tConf, entrega));
                    break;

                case 5:
                    imprimirLista(listaTickets, "No hay tickets registrados.");
                    break;

                case 0:
                    System.out.println("Regresando al menú principal...");
                    break;

                default:
                    System.out.println("Opción inválida.");
            }
        } while (opcion != 0);
    }
}