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

class Doctor implements Runnable {
    private final String nombre;
    private final Clinica clinica;
    private volatile boolean disponible = true;

    public Doctor(String nombre, Clinica clinica) {
        this.nombre = nombre;
        this.clinica = clinica;
    }

    @Override
    public void run() {
        System.out.println(nombre + " iniciado - Esperando pacientes");

        while (clinica.estaAbierta()) {
            try {
                Paciente paciente = clinica.tomarPaciente();
                atenderPaciente(paciente);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void atenderPaciente(Paciente paciente) throws InterruptedException {
        String motivo = paciente.getMotivoDeConsulta();

        if ("Emergencia".equals(motivo) || "Urgencia".equals(motivo)) {
            atenderEmergenciaOUrgencia(paciente);
        } else if ("Carne de salud".equals(motivo)) {
            atenderCarnetSalud(paciente);
        }
    }

    private void atenderEmergenciaOUrgencia(Paciente paciente) throws InterruptedException {
        // Necesita consultorio y colaboración con enfermero
        clinica.consultoriosLibres.acquire();

        try {
            disponible = false;
            System.out.println(nombre + " atendiendo " + paciente.getMotivoDeConsulta() +
                    " de " + paciente.getNombre());

            // Simular atención conjunta (doctor + enfermero)
            Thread.sleep(paciente.getTiempoMaxDeConsulta() * 100); // Convertir a ms

            clinica.incrementarAtendidos();
            System.out.println(paciente.getNombre() + " atendido exitosamente");

        } finally {
            disponible = true;
            clinica.consultoriosLibres.release();
        }
    }

    private void atenderCarnetSalud(Paciente paciente) throws InterruptedException {
        // Solo necesita consultorio para la consulta médica
        clinica.consultoriosLibres.acquire();

        try {
            disponible = false;
            System.out.println(nombre + " realizando consulta de carnet de salud a " +
                    paciente.getNombre());

            Thread.sleep(paciente.getTiempoMaxDeConsulta() * 100);

            System.out.println("Consulta médica completada para " + paciente.getNombre());

        } finally {
            disponible = true;
            clinica.consultoriosLibres.release();
        }
    }

    public boolean estaDisponible() {
        return disponible;
    }
}
