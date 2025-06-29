package sistemasobligatorio;

/**
 * Clase dedicada de los pacientes, son nuestros procesos, esos tienen los
 * valores para ser posteriormente leidos
 * por las otras clases, especialmente el recepcionista(ingreso) y
 * clinica(gestion de colas).
 * - Falta resolver el tiempo. Posiblemente implmentar Timer de JDK(Java.utils).
 */
public class Paciente {
    private String Nombre;
    private String MotivoDeConsulta;
    private int TiempoMaxDeConsulta;
    private Boolean TieneInformeOdontologico;
    private int TiempoEsperando;
    private Boolean TiempoDeEsperaAgotado;

    public Paciente(String nombre, String motivoDeConsulta, int tiempoMaxDeConsulta,
            Boolean tieneInformeOdontologico, int tiempoEsperando, Boolean tiempoDeEsperaAgotado) {
        Nombre = nombre;
        MotivoDeConsulta = motivoDeConsulta;
        TiempoMaxDeConsulta = tiempoMaxDeConsulta;
        TieneInformeOdontologico = tieneInformeOdontologico;
        TiempoEsperando = tiempoEsperando;
        TiempoDeEsperaAgotado = tiempoDeEsperaAgotado;
    }

    public String getNombre() {
        return this.Nombre;
    }

    public String getMotivoDeConsulta() {
        return this.MotivoDeConsulta;
    }

    public int getTiempoMaxDeConsulta() {
        return this.TiempoMaxDeConsulta;
    }

    public Boolean getTieneInformeOdontologico() {
        return this.TieneInformeOdontologico;
    }

    public int getTiempoEsperando() {
        return this.TiempoEsperando;
    }

    public void incrementarTiempoEsperando() {
        this.TiempoEsperando++;
        if (this.MotivoDeConsulta.equals("Urgencia") && this.TiempoEsperando >= 120) {
            this.MotivoDeConsulta = "Emergencia";
            this.TiempoEsperando = 0;
        } else if (this.MotivoDeConsulta.equals("Emergencia") && this.TiempoEsperando >= 10) {
            setTiempoDeEsperaAgotado();
        }
    }

    public Boolean getTiempoDeEsperaAgotado() {
        return this.TiempoDeEsperaAgotado;
    }

    public void setTiempoDeEsperaAgotado() {
        this.TiempoDeEsperaAgotado = true;
    }
}
