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

class Enfermero implements Runnable {
    private final String nombre;
    private final Clinica clinica;
    private volatile boolean disponible = true;

    public Enfermero(String nombre, Clinica clinica) {
        this.nombre = nombre;
        this.clinica = clinica;
    }

    @Override
    public void run() {
        System.out.println(nombre + " trabajando - Esperando pacientes para atender :^)");

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
            // Ya está siendo atendido junto con el doctor
            return;
        } else if ("Carne de salud".equals(motivo)) {
            realizarAnalisis(paciente);
        }
    }

    private void realizarAnalisis(Paciente paciente) throws InterruptedException {
        // Necesita sala de enfermería
        clinica.salaDeEnfermeria.acquire();

        try {
            disponible = false;
            System.out.println(nombre + " realizando análisis de sangre y orina a " +
                    paciente.getNombre());

            // Simular análisis de sangre
            Thread.sleep(paciente.getTiempoMaxDeConsulta() * 50);
            System.out.println("Análisis de sangre completado para " + paciente.getNombre());

            // Simular análisis de orina
            Thread.sleep(paciente.getTiempoMaxDeConsulta() * 50);
            System.out.println("Análisis de orina completado para " + paciente.getNombre());

            clinica.incrementarAtendidos();
            System.out.println("Carnet de salud completado para " + paciente.getNombre());

        } finally {
            disponible = true;
            clinica.salaDeEnfermeria.release();
        }
    }

    public boolean estaDisponible() {
        return disponible;
    }
    
    public void hacerAnalisis(Paciente paciente){
           try {
                System.out.println("Enfermero haciendo análisis de sangre y orina a " + paciente.getNombre());
                Thread.sleep(paciente.getTiempoMaxDeConsulta() * 100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                clinica.enfermeroDisponible.release();
            }
    }
}
