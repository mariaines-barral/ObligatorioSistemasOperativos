package sistemasobligatorio;

import java.util.concurrent.Semaphore;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Comparator;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

class Main {
    public static void main(String[] args) {
        String rutaSalida = "sistemasobligatorio\\src\\main\\java\\sistemasobligatorio\\casoBaseSalida.txt";
        try {
            PrintStream archivoSalida = new PrintStream(rutaSalida);
            System.setOut(archivoSalida);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Clinica clinica = new Clinica("sistemasobligatorio\\src\\main\\java\\sistemasobligatorio\\casoBaseEntrada.txt",
                "Dr. House",
                "Enfermero Stark", "Pam Beesly");
        clinica.esperarCierre();
    }
}
