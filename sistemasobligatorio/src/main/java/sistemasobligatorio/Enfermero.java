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

class Enfermero {
    private final String nombre;
    private final Clinica clinica;
    private volatile boolean disponible = true;

    public Enfermero(String nombre, Clinica clinica) {
        this.nombre = nombre;
        this.clinica = clinica;
    }

    public void atenderPaciente(Paciente paciente) throws InterruptedException {
        String motivo = paciente.getMotivoDeConsulta();

        if ("Emergencia".equals(motivo) || "Urgencia".equals(motivo)) {
            return;
        } else if ("Carne de salud".equals(motivo)) {
            realizarAnalisis(paciente);
        }
    }

    private void realizarAnalisis(Paciente paciente) throws InterruptedException {
        try {
            clinica.salaDeEnfermeria.acquire();
            disponible = false;

            System.out.println(nombre + " realizando análisis para " + paciente.getNombre());
            Thread.sleep(500);
            System.out.println("Análisis de sangre y orina para " + paciente.getNombre() + " completados");

            clinica.incrementarAtendidos();
        } catch (InterruptedException e) {
            System.out.println(nombre + " fue interrumpido durante el análisis de " + paciente.getNombre());
            Thread.currentThread().interrupt();
        } finally {
            disponible = true;
            clinica.salaDeEnfermeria.release();
        }
    }

    public boolean estaDisponible() {
        return disponible;
    }

}
