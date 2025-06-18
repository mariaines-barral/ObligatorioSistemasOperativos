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

    // Recursos limitados
    public final Semaphore consultoriosLibres = new Semaphore(2);
    public final Semaphore salaDeEnfermeria = new Semaphore(1);

    // Colas thread-safe con prioridad
    private final PriorityBlockingQueue<Paciente> colaPacientes;

    // Hilos trabajadores
    private final ExecutorService ejecutorHilos;
    private final Recepcionista recepcionista;
    private final Doctor doctor;
    private final Enfermero enfermero;

    // Control de simulación
    private volatile boolean clinicaAbierta = true;

    public Clinica() {
        PacientesAtendidos = 0;
        PacientesRechazados = 0;
        PacientesMuertos = 0;

        // Cola única con comparador de prioridad
        colaPacientes = new PriorityBlockingQueue<>(100, new ComparadorPrioridad());

        // Pool de hilos
        ejecutorHilos = Executors.newFixedThreadPool(4);

        // Crear trabajadores
        recepcionista = new Recepcionista(this);
        doctor = new Doctor("Dr. García", this);
        enfermero = new Enfermero("Enfermero López", this);

        // Iniciar hilos
        ejecutorHilos.submit(recepcionista);
        ejecutorHilos.submit(doctor);
        ejecutorHilos.submit(enfermero);
    }

    public void agregarPaciente(Paciente paciente) {
        colaPacientes.offer(paciente);
        System.out.println("Paciente " + paciente.getNombre() + " agregado a la cola");
    }

    public Paciente tomarPaciente() throws InterruptedException {
        return colaPacientes.take(); // Bloquea hasta que haya un paciente
    }

    public void cargarPacientesDelArchivo(String archivo) {
        ejecutorHilos.submit(() -> {
            try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    String[] datos = linea.split(",");
                    if (datos.length >= 7) {
                        String tiempo = datos[0];
                        String nombre = datos[1].replace("\"", "");
                        String motivo = datos[2].replace("\"", "");
                        int tiempoConsulta = Integer.parseInt(datos[3]);
                        boolean tieneInforme = Boolean.parseBoolean(datos[4]);
                        int tiempoEsperando = Integer.parseInt(datos[5]);
                        boolean tiempoAgotado = Boolean.parseBoolean(datos[6]);

                        Paciente paciente = new Paciente(nombre, motivo, tiempoConsulta,
                                tieneInforme, tiempoEsperando, tiempoAgotado);

                        // Simular llegada en el tiempo especificado
                        Thread.sleep(1000); // Simular tiempo entre llegadas
                        agregarPaciente(paciente);
                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public void cerrarClinica() {
        clinicaAbierta = false;
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
            switch (motivo) {
                case "Emergencia":
                    return 1;
                case "Urgencia":
                    return 2;
                case "Carne de salud":
                    return 3;
                default:
                    return 4;
            }
        }
    }
}
