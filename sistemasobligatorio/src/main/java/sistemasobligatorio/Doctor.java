package sistemasobligatorio;

/**
 * Clase dedicada a atender a los pacientes(consultas y entrevistas), llama a el
 * enfermero.
 * Atender y Entrevista tienen boolean como salida, pero se tiene que ver si es
 * lo correcto.
 */
public class Doctor implements Runnable {
    String Nombre;
    boolean Disponibilidad;

    Doctor(String nombre, boolean disponibilidad) {
        Nombre = nombre;
        Disponibilidad = disponibilidad;
    }

    @Override
    public void run() {

    }

    // No esta implementado.
    public boolean atender(Paciente paciente) {
        return false;
    }

    // No esta implementado.
    public boolean entrevista(Paciente paciente) {
        return false;
    }

    // No esta implementado.
    public boolean llamarEnfermero(Enfermero enfermero) {
        return false;
    }

}