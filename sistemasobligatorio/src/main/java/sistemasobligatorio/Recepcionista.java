package sistemasobligatorio;

import java.util.concurrent.Semaphore;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

class Recepcionista implements Runnable {
    private final Clinica clinica;
    private final String nombre;

    public Recepcionista(String nombre, Clinica clinica) {
        this.clinica = clinica;
        this.nombre = nombre;
    }

    @Override
    public void run() {
        System.out.println("Recepcionista" + this.nombre + "trabajando: Gestionando cola de pacientes :)");
        cargarPacientesDelArchivo(clinica.archivoDePacientes);
        while (clinica.estaAbierta()) {
            try {
                Thread.sleep(1000);
                reasignarPrioridades();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }   
        }
    }

    public void cargarPacientesDelArchivo(String archivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length == 7) {
                    String tiempo = datos[0];
                    String[] partes = tiempo.split("\\.");
                    int horaLlegada = Integer.parseInt(partes[0]);
                    int minutoLlegada = 0;
                    if (partes.length > 1) {
                        minutoLlegada = Integer.parseInt(partes[1]);
                    }
                    String nombre = datos[1].replace("\"", "");
                    String motivo = datos[2].replace("\"", "");
                    int tiempoConsulta = Integer.parseInt(datos[3]);
                    boolean tieneInforme = Boolean.parseBoolean(datos[4]);
                    int tiempoEsperando = Integer.parseInt(datos[5]);
                    boolean tiempoAgotado = Boolean.parseBoolean(datos[6]);
                    while (true) {
                        int[] tiempoActual = clinica.getTiempoSimulado();
                        if (tiempoActual[0] > horaLlegada ||
                                (tiempoActual[0] == horaLlegada && tiempoActual[1] >= minutoLlegada)) {
                            break;
                        }
                        Thread.sleep(10);
                    }

                    if (!tieneInforme && motivo.equals("Carne de salud")) {
                        clinica.incrementarRechazados();
                        System.out.println("Paciente " + nombre + " fue rechazado por no traer examen odontológico");
                        continue;
                    }
                    Paciente paciente = new Paciente(nombre, motivo, tiempoConsulta,
                            tieneInforme, tiempoEsperando, tiempoAgotado);

                    clinica.agregarPaciente(paciente);
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void reasignarPrioridades() {
        List<Paciente> pacientes = new ArrayList<>();
        clinica.colaPacientes.drainTo(pacientes);

        // Actualizar tiempo de espera de los pacientes
        for (Paciente p : pacientes) {
            p.incrementarTiempoEsperando();
            if (p.getTiempoDeEsperaAgotado()) {
                clinica.incrementarMuertos();
                System.out.println("Paciente " + p.getNombre() + " ha muerto esperando :'(");
                pacientes.remove(p);
            }
        }

        // Volver a insertar los pacientes en la cola ordenados por prioridad
        clinica.colaPacientes.addAll(pacientes);
    }
}
