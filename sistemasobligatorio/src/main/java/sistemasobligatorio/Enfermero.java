package sistemasobligatorio;

/**
 * Enfermero, clase dedicada a asistir a pacientes y a realizar analisis de
 * Sangre y/o Orina.
 */
public class Enfermero implements Runnable {
    private String Nombre;
    private Boolean Disponibilidad;

    public Enfermero(String nombre) {
        Nombre = nombre;
        Disponibilidad = true;
    }

    @Override
    public void run() {

    }

    public void curar(Paciente paciente) {
        Disponibilidad = false;
        System.out.println("El enfermero " + Nombre + " está curando a: " + paciente.getNombre());

        try {
            Thread.sleep(paciente.getTiempoMaxDeConsulta());
        } catch (InterruptedException ex) {

        }

        Disponibilidad = true;
    }

    public void analisisDeSangre(Paciente paciente) {
        Disponibilidad = false;
        System.out.println(
                "El enfermero " + Nombre + " está haciendole un analisis de sangre a: " + paciente.getNombre());

        try {
            Thread.sleep(paciente.getTiempoMaxDeConsulta());
        } catch (InterruptedException ex) {
        }

        Disponibilidad = true;
    }

    public void analisisDeOrina(Paciente paciente) {
        Disponibilidad = false;
        System.out
                .println("El enfermero " + Nombre + " está haciendole un analisis de orina a: " + paciente.getNombre());

        try {
            Thread.sleep(paciente.getTiempoMaxDeConsulta());
        } catch (InterruptedException ex) {

        }

        Disponibilidad = true;
    }
}
