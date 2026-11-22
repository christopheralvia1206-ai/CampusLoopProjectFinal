package modelo;

public class TicketSOS {

    // Estados internos (se comparan con estos valores exactos)
    public static final String SOS_ACTIVO = "SOS_ACTIVO";
    public static final String SOS_RESUELTO = "SOS_RESUELTO";
    public static final String SOS_EXPIRADO_REASIGNANDO = "SOS_EXPIRADO_REASIGNANDO";

    // Tiempo límite para confirmar la entrega (minutos)
    public static final int LIMITE_MINUTOS = 10;

    private String idTicket;
    private Usuario solicitante;
    private String insumo;
    private String campus;
    private String edificio;
    private String piso;
    private String estado;
    private int minutosTranscurridos; // Tiempo simulado en consola
    private Usuario ayudante;         // Quien confirma la entrega

    public TicketSOS(String idTicket, Usuario solicitante, String insumo,
                     String campus, String edificio, String piso) {
        this.idTicket = idTicket;
        this.solicitante = solicitante;
        this.insumo = insumo;
        this.campus = campus;
        this.edificio = edificio;
        this.piso = piso;
        this.estado = SOS_ACTIVO;
        this.minutosTranscurridos = 0;
        this.ayudante = null;
    }

    public String getIdTicket() {
        return idTicket;
    }

    public Usuario getSolicitante() {
        return solicitante;
    }

    public String getInsumo() {
        return insumo;
    }

    public String getCampus() {
        return campus;
    }

    public String getEdificio() {
        return edificio;
    }

    public String getPiso() {
        return piso;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    // Solo para mostrar en pantalla: SOS_ACTIVO -> SOS ACTIVO
    public String getEstadoVisible() {
        return formatear(estado);
    }

    public static String formatear(String estado) {
        return estado.replace("_", " ");
    }

    public int getMinutosTranscurridos() {
        return minutosTranscurridos;
    }

    public void avanzarMinutos(int minutos) {
        if (minutos > 0) {
            this.minutosTranscurridos += minutos;
        }
    }

    public Usuario getAyudante() {
        return ayudante;
    }

    public void setAyudante(Usuario ayudante) {
        this.ayudante = ayudante;
    }

    @Override
    public String toString() {
        String texto = "[" + idTicket + "] " + insumo
                + " | Solicitante: " + solicitante.getNombre()
                + " | Ubicación: " + campus + " - " + edificio + " - Piso " + piso
                + " | Tiempo: " + minutosTranscurridos + "/" + LIMITE_MINUTOS + " min"
                + " | Estado: " + getEstadoVisible();
        if (ayudante != null) {
            texto += " | Entregado por: " + ayudante.getNombre();
        }
        return texto;
    }
}