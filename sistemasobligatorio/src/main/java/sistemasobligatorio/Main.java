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

class Main {
    public static void main(String[] args) {
        Clinica clinica = new Clinica("src\\main\\java\\sistemasobligatorio\\pacientesDelDia.txt");

        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n=== ESTADÍSTICAS FINALES ===");
        System.out.println("Pacientes atendidos: " + clinica.PacientesAtendidos);
        System.out.println("Pacientes rechazados: " + clinica.PacientesRechazados);
        System.out.println("Pacientes muertos: " + clinica.PacientesMuertos);

    }
}
