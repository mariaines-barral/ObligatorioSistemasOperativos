import java.util.PriorityQueue;

/**
 * Clase dedicada a la clinica, donde se gestionan los pacientes(Colas de estos)
 * y se ingresan en las Salas.
 * 
 */
public class Clinica {
    public int PacientesAtendidos;
    public int PacientesRechazados;
    public int PacientesMuertos;
    public Recepcionista Recepcionista;
    public Doctor Doctor;
    public Enfermero Enfermero;
    public int ConsultoriosLibres;
    public int SalaDeEnfermeria;
    // Ejemplo de como crear una cola.
    PriorityQueue<Paciente> example = new PriorityQueue();

    public Clinica(Doctor doctor, Enfermero enfermero) {
        PacientesAtendidos = 0;
        PacientesRechazados = 0;
        PacientesMuertos = 0;
        Recepcionista = new Recepcionista();
        Doctor = doctor;
        Enfermero = enfermero;
        ConsultoriosLibres = 2;
        SalaDeEnfermeria = 1;
    }
}
