package sistemasobligatorio;

import java.util.concurrent.Semaphore;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.ArrayList;
import java.util.Comparator;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Clinica {

    public volatile int PacientesAtendidos;
    public volatile int PacientesRechazados;
    public volatile int PacientesMuertos;
    public volatile int[] tiempoSimulado = new int[2];

    // Recursos
    public final Semaphore consultorioReservadoParaEmergencia = new Semaphore(1);
    public final Semaphore consultorioLibre = new Semaphore(1);
    public final Semaphore salaDeEnfermeria = new Semaphore(1);
    public final Semaphore enfermeroDisponible = new Semaphore(1);

    // Cola thread-safe con prioridad
    protected final PriorityBlockingQueue<Paciente> colaPacientes;

    // Hilos trabajadores
    private final ExecutorService ejecutorHilos;
    private final Recepcionista recepcionista;
    private final ArrayList<Doctor> doctores;
    private final Enfermero enfermero;

    public final String archivoDePacientes;

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

    public Clinica(String archivoPacientes, String[] nombresDoctores, String nombreEnfermero,
            String nombreRecepcionista) {
        PacientesAtendidos = 0;
        PacientesRechazados = 0;
        PacientesMuertos = 0;
        tiempoSimulado[0] = 8;
        tiempoSimulado[1] = 0;

        colaPacientes = new PriorityBlockingQueue<>(100, new ComparadorPrioridad());

        recepcionista = new Recepcionista(nombreRecepcionista, this);
        doctores = new ArrayList<Doctor>();
        for (String nombre : nombresDoctores) {
            doctores.add(new Doctor(nombre, this));
        }
        enfermero = new Enfermero(nombreEnfermero, this);
        archivoDePacientes = archivoPacientes;

        ejecutorHilos = Executors.newFixedThreadPool(3 + doctores.size());

        ejecutorHilos.submit(recepcionista);
        ejecutorHilos.submit(new Reloj());
        for (Doctor doctor : doctores) {
            ejecutorHilos.submit(doctor);
        }

    }

    public synchronized int[] getTiempoSimulado() {
        return new int[] { tiempoSimulado[0], tiempoSimulado[1] };
    }

    private synchronized void incrementarTiempo() {
        if (tiempoSimulado[1] == 59) {
            tiempoSimulado[0]++;
            tiempoSimulado[1] = 0;
            System.out.println("\nHora actual: " + tiempoSimulado[0] + ":" + tiempoSimulado[1] + "0");
        } else {
            tiempoSimulado[1]++;
        }

        if (tiempoSimulado[0] == 20 && tiempoSimulado[1] == 0) {
            cerrarClinica();
        }
    }

    public void agregarPaciente(Paciente paciente) {
        colaPacientes.offer(paciente);
        System.out.println("Paciente " + paciente.getNombre() + " agregado a la cola");
    }

    public Paciente tomarPaciente() throws InterruptedException {
        return colaPacientes.take();
    }

    public void cerrarClinica() {
        clinicaAbierta = false;
        System.out.println("La clínica cerró por hoy. Nos vemos mañana!");

        ejecutorHilos.shutdownNow();

        recepcionista.reasignarPrioridades();

        System.out.println("\n=== ESTADISTICAS FINALES ===");
        System.out.println("Pacientes atendidos: " + PacientesAtendidos);
        System.out.println("Pacientes rechazados: " + PacientesRechazados);
        System.out.println("Pacientes muertos: " + PacientesMuertos);
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

    public synchronized void incrementarMuertos() {
        PacientesMuertos++;
    }

    public synchronized int getTiempoDeCola() {
        int tiempoDeCola = 0;
        for (Paciente paciente : colaPacientes) {
            tiempoDeCola += paciente.getTiempoMaxDeConsulta();
        }
        return tiempoDeCola;
    }

    public synchronized int getTiempoRestante() {
        return 720 - ((tiempoSimulado[0] - 8) * 60 + tiempoSimulado[1]);
    }

    private static class ComparadorPrioridad implements Comparator<Paciente> {
        @Override
        public int compare(Paciente p1, Paciente p2) {
            int prioridad1 = obtenerPrioridad(p1.getMotivoDeConsulta());
            int prioridad2 = obtenerPrioridad(p2.getMotivoDeConsulta());

            if (prioridad1 != prioridad2) {
                return Integer.compare(prioridad1, prioridad2);
            }

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

    public void esperarCierre() {
        try {
            ejecutorHilos.awaitTermination(Long.MAX_VALUE, java.util.concurrent.TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public Enfermero getEnfermero() {
        return enfermero;
    }
}
