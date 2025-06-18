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
        Clinica clinica = new Clinica();

        // Cargar pacientes del archivo
        clinica.cargarPacientesDelArchivo(
                "sistemasobligatorio\\src\\main\\java\\sistemasobligatorio\\pacientesDelDia.txt");

        // Agregar algunos pacientes manualmente para probar
        clinica.agregarPaciente(new Paciente("María", "Emergencia", 25, false, 0, false));
        clinica.agregarPaciente(new Paciente("Carlos", "Carne de salud", 15, true, 5, false));
        clinica.agregarPaciente(new Paciente("Laura", "Urgencia", 20, false, 2, false));

        // Simular funcionamiento por 30 segundos
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Cerrar clínica
        clinica.cerrarClinica();

        // Mostrar estadísticas
        System.out.println("\n=== ESTADÍSTICAS FINALES ===");
        System.out.println("Pacientes atendidos: " + clinica.PacientesAtendidos);
        System.out.println("Pacientes rechazados: " + clinica.PacientesRechazados);
    }
}
