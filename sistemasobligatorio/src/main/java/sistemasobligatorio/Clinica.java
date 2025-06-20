package sistemasobligatorio;

import java.util.concurrent.Semaphore;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Comparator;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Clinica {

    public volatile int PacientesAtendidos;
    public volatile int PacientesRechazados;
    public volatile int PacientesMuertos;
    public volatile int[] tiempoSimulado = new int[2];

    // Recursos limitados
    public final Semaphore consultorioReservadoParaEmergencia = new Semaphore(1);
    public final Semaphore consultorioLibre = new Semaphore(1);
    public final Semaphore salaDeEnfermeria = new Semaphore(1);
    public final Semaphore enfermeroDisponible = new Semaphore(1);

    // Cola thread-safe con prioridad
    protected final PriorityBlockingQueue<Paciente> colaPacientes;

    // Hilos trabajadores
    private final ExecutorService ejecutorHilos;
    private final Recepcionista recepcionista;
    private final Doctor doctor;
    private final Enfermero enfermero;

    public final String archivoDePacientes;

    // Control de simulación
    private volatile boolean clinicaAbierta = true;

    public class Reloj implements Runnable {
        @Override
        public void run() {
            while (clinicaAbierta) {
                try {
                    Thread.sleep(50);
                    incrementarTiempo();
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    public Clinica(String archivoPacientes) {
        PacientesAtendidos = 0;
        PacientesRechazados = 0;
        PacientesMuertos = 0;
        tiempoSimulado[0] = 8;
        tiempoSimulado[1] = 0;

        // Cola única con comparador de prioridad
        colaPacientes = new PriorityBlockingQueue<>(100, new ComparadorPrioridad());

        // Pool de hilos
        ejecutorHilos = Executors.newFixedThreadPool(3);

        // Crear trabajadores
        recepcionista = new Recepcionista(this);
        doctor = new Doctor("Dr. García", this);
        enfermero = new Enfermero("Enfermero López", this);
        archivoDePacientes = archivoPacientes;

        // Iniciar hilos
        ejecutorHilos.submit(recepcionista);
        ejecutorHilos.submit(doctor);
        ejecutorHilos.submit(enfermero);

        iniciarRelojSimulado();
    }

    public synchronized int[] getTiempoSimulado() {
        return new int[] { tiempoSimulado[0], tiempoSimulado[1] };
    }

    private synchronized void incrementarTiempo() {
        if (tiempoSimulado[1] == 59) {
            tiempoSimulado[0]++;
            tiempoSimulado[1] = 0;
        } else {
            tiempoSimulado[1]++;
        }

        if (tiempoSimulado[0] == 20 && tiempoSimulado[1] == 0) {
            cerrarClinica();
        }
    }

    private void iniciarRelojSimulado() {
        Thread reloj = new Thread(new Reloj());
        reloj.start();
    }

    public void agregarPaciente(Paciente paciente) {
        colaPacientes.offer(paciente);
        System.out.println("Paciente " + paciente.getNombre() + " agregado a la cola");
    }

    public Paciente tomarPaciente() throws InterruptedException {
        return colaPacientes.take(); // Bloquea hasta que haya un paciente
    }

    public void cerrarClinica() {
        clinicaAbierta = false;
        System.out.println("La clínica cerró por hoy. Nos vemos mañana!");
        ejecutorHilos.shutdown();
    }

    public boolean estaAbierta() {
        return clinicaAbierta;
    }

    public synchronized void incrementarAtendidos() {
        PacientesAtendidos++;
    }

    public synchronized void incrementarRechazados() {
        PacientesRechazados++;
    }

    // Comparador para priorizar pacientes
    private static class ComparadorPrioridad implements Comparator<Paciente> {
        @Override
        public int compare(Paciente p1, Paciente p2) {
            int prioridad1 = obtenerPrioridad(p1.getMotivoDeConsulta());
            int prioridad2 = obtenerPrioridad(p2.getMotivoDeConsulta());

            if (prioridad1 != prioridad2) {
                return Integer.compare(prioridad1, prioridad2);
            }

            // Si tienen la misma prioridad, el que lleva más tiempo esperando va primero
            return Integer.compare(p2.getTiempoEsperando(), p1.getTiempoEsperando());
        }

        private int obtenerPrioridad(String motivo) {
            if (motivo.equals("Emergencia")) {
                return 1;
            } else if (motivo.equals("Urgencia")) {
                return 2;
            } else if (motivo.equals("Carne de salud")) {
                return 3;
            } else {
                throw new IllegalArgumentException("Motivo de consulta desconocido: " + motivo);
            }
        }
    }
}
