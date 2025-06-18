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

class Recepcionista implements Runnable {
    private final Clinica clinica;

    public Recepcionista(Clinica clinica) {
        this.clinica = clinica;
    }

    @Override
    public void run() {
        System.out.println("Recepcionista iniciado - Gestionando llegadas de pacientes");

        while (clinica.estaAbierta()) {
            try {
                // El recepcionista puede hacer otras tareas como reasignar prioridades
                Thread.sleep(5000); // Revisa cada 5 segundos
                reasignarPrioridades();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void reasignarPrioridades() {
        // Lógica para reasignar prioridades basada en tiempo de espera
        System.out.println("Recepcionista revisando prioridades...");
    }
}
