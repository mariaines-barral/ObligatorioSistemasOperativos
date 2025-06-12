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

    // Esto hay que ver como se va actualizando y cada cuanto tiempo
    public void setTiempoEsperando() {
        this.TiempoEsperando++;
    }

    public Boolean getTiempoDeEsperaAgotado() {
        return this.TiempoDeEsperaAgotado;
    }

    public void setTiempoDeEsperaAgotado() {
        this.TiempoDeEsperaAgotado = true;
    }
}
