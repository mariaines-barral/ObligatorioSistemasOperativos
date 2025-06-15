package sistemasobligatorio;

import java.util.concurrent.Semaphore;
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
    private Recepcionista Recepcionista; // deberia ser estatico?
    public Doctor Doctor; // Lista de doctores?
    public Enfermero Enfermero; // Lista de enfermeros?
    public int ConsultoriosLibres;
    public int SalaDeEnfermeria;
    // Ejemplo de como crear una cola.
    PriorityQueue<Paciente> emergencias = new PriorityQueue();
    PriorityQueue<Paciente> urgencias = new PriorityQueue();
    PriorityQueue<Paciente> estandar = new PriorityQueue();
    public final Semaphore mutexQueues = new Semaphore(1);

    public Clinica(Doctor doctor, Enfermero enfermero) {
        PacientesAtendidos = 0;
        PacientesRechazados = 0;
        PacientesMuertos = 0;
        Recepcionista = new Recepcionista(this);
        Doctor = doctor;
        Enfermero = enfermero;
        ConsultoriosLibres = 2;
        SalaDeEnfermeria = 1;
    }
}