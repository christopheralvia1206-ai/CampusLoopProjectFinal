package negocio;

import java.util.ArrayList;
import java.util.Scanner;
import modelo.Articulo;
import modelo.Usuario;

public class GestorInventario {
    // las listas guardan objetos creados afuera
    private ArrayList<Usuario> listaUsuarios;
    private ArrayList<Articulo> listaArticulos;

    public GestorInventario() {
        this.listaUsuarios = new ArrayList<>();
        this.listaArticulos = new ArrayList<>();
    }

    public void registrarUsuario(Usuario usuario) {
        listaUsuarios.add(usuario);
    }

    // Registrar artículo con elegibilidad del dueño
    public boolean registrarArticulo(Articulo articulo) {
        Usuario dueno = articulo.getPropietario();
        if (!dueno.verificarElegibilidad()) {
            return false;
        }
        listaArticulos.add(articulo);
        return true;
    }

    // Búsqueda filtrada por campus
    public ArrayList<Articulo> buscarDisponiblesPorCampus(String campus) {
        ArrayList<Articulo> resultados = new ArrayList<>();
        for (Articulo art : listaArticulos) {
            if (art.getCampusBase().equalsIgnoreCase(campus)
                    && art.getEstadoDisponibilidad().equals("DISPONIBLE")) {
                resultados.add(art);
            }
        }
        return resultados;
    }

    public Usuario buscarUsuarioPorId(String idEstudiante) {
        for (Usuario u : listaUsuarios) {
            if (u.getIdEstudiante().equalsIgnoreCase(idEstudiante)) {
                return u;
            }
        }
        return null;
    }

    public Articulo buscarArticuloPorId(String idArticulo) {
        for (Articulo a : listaArticulos) {
            if (a.getIdArticulo().equalsIgnoreCase(idArticulo)) {
                return a;
            }
        }
        return null;
    }

    public ArrayList<Usuario> getListaUsuarios() {
        return listaUsuarios;
    }

    public ArrayList<Articulo> getListaArticulos() {
        return listaArticulos;
    }

    // Chris
    public void mostrarMenuModulo1(Scanner sc) {
        int opcion;
        do {
            System.out.println("\n--- USUARIOS E INVENTARIO POR CAMPUS ---");
            System.out.println("1. Registrar nuevo estudiante");
            System.out.println("2. Publicar artículo en campus");
            System.out.println("3. Consultar artículos disponibles por campus");
            System.out.println("4. Verificar elegibilidad de un estudiante");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione una opción: ");
            opcion = Integer.parseInt(sc.nextLine());

            switch (opcion) {
                case 1:
                    System.out.print("ID Banner/Estudiante: ");
                    String id = sc.nextLine();
                    System.out.print("Nombre completo: ");
                    String nombre = sc.nextLine();
                    System.out.print("Correo institucional (@udla.edu.ec): ");
                    String correo = sc.nextLine();
                    System.out.print("Campus (UDLAPark / Granados / Colon): ");
                    String campus = sc.nextLine();

                    Usuario nuevoUsuario = new Usuario(id, nombre, correo, campus);
                    registrarUsuario(nuevoUsuario);
                    System.out.println("Estudiante registrado correctamente.");
                    break;

                case 2:
                    System.out.print("ID del estudiante propietario: ");
                    String idProp = sc.nextLine();
                    Usuario propietario = buscarUsuarioPorId(idProp);

                    if (propietario == null) {
                        System.out.println("Error: Estudiante no encontrado.");
                        break;
                    }
                    if (!propietario.verificarElegibilidad()) {
                        System.out.println("Acceso denegado: El estudiante tiene estado " + propietario.getEstadoPerfil());
                        break;
                    }

                    System.out.print("Código del artículo (ej. ART-01): ");
                    String idArt = sc.nextLine();
                    System.out.print("Nombre del artículo: ");
                    String nomArt = sc.nextLine();
                    System.out.print("Categoría (Laboratorio / Dibujo / Electrónica / Emergencia): ");
                    String cat = sc.nextLine();
                    System.out.print("Estado físico (Excelente / Bueno / Regular): ");
                    String estFisico = sc.nextLine();
                    System.out.print("Modalidad (Préstamo temporal / Micropago / Regalo): ");
                    String mod = sc.nextLine();
                    System.out.print("Tarifa base ($0 si es gratuito): ");
                    double tarifa = Double.parseDouble(sc.nextLine());

                    Articulo nuevoArticulo = new Articulo(idArt, nomArt, cat, estFisico, mod, tarifa, propietario);
                    registrarArticulo(nuevoArticulo);
                    System.out.println("Artículo publicado en campus " + propietario.getCampus() + " con estado DISPONIBLE.");
                    break;

                case 3:
                    System.out.print("Ingrese el campus a consultar (UDLAPark / Granados / Colon): ");
                    String campusConsulta = sc.nextLine();
                    ArrayList<Articulo> encontrados = buscarDisponiblesPorCampus(campusConsulta);

                    if (encontrados.isEmpty()) {
                        System.out.println("No hay artículos disponibles en ese campus.");
                    } else {
                        for (Articulo a : encontrados) {
                            System.out.println(a);
                        }
                    }
                    break;

                case 4:
                    System.out.print("Ingrese ID del estudiante a verificar: ");
                    String idConsulta = sc.nextLine();
                    Usuario uConsulta = buscarUsuarioPorId(idConsulta);

                    if (uConsulta == null) {
                        System.out.println("Estudiante no encontrado.");
                    } else {
                        uConsulta.verificarElegibilidad();
                        System.out.println(uConsulta);
                    }
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