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
        System.out.println(nombre + " trabajando - Esperando pacientes para atender B)");

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

        if ("Emergencia".equals(motivo)) {
            atenderEmergencia(paciente);
        } else if ("Urgencia".equals(motivo)) {
            atenderUrgencia(paciente);
        } else if ("Carne de salud".equals(motivo)) {
            atenderCarnetSalud(paciente);
        }
    }

    private void atenderEmergencia(Paciente paciente) throws InterruptedException {
        boolean usoConsultorioNormal = false;
        if (clinica.consultorioLibre.tryAcquire()) {
            usoConsultorioNormal = true;
        } else {
            clinica.consultorioReservadoParaEmergencia.acquire();
        }
        clinica.enfermeroDisponible.acquire();

        try {
            disponible = false;
            System.out.println(nombre + " atendiendo " + paciente.getMotivoDeConsulta() +
                    " de " + paciente.getNombre() + " junto al enfermero.");

            Thread.sleep(paciente.getTiempoMaxDeConsulta() * 100);

            clinica.incrementarAtendidos();
            System.out.println(paciente.getNombre() + " atendido exitosamente :)");

        } finally {
            disponible = true;
            clinica.enfermeroDisponible.release();
            if (usoConsultorioNormal) {
                clinica.consultorioLibre.release();
            } else {
                clinica.consultorioReservadoParaEmergencia.release();
            }
        }
    }

    private void atenderUrgencia(Paciente paciente) throws InterruptedException {
        clinica.consultorioLibre.acquire();
        clinica.enfermeroDisponible.acquire();

        try {
            disponible = false;
            System.out.println(nombre + " atendiendo " + paciente.getMotivoDeConsulta() +
                    " de " + paciente.getNombre() + " junto al enfermero.");

            // Simular atención conjunta (doctor + enfermero)
            Thread.sleep(paciente.getTiempoMaxDeConsulta() * 100);

            clinica.incrementarAtendidos();
            System.out.println(paciente.getNombre() + " atendido exitosamente");

        } finally {
            disponible = true;
            clinica.enfermeroDisponible.release(); // Libera al enfermero
            clinica.consultorioLibre.release();
        }
    }

    private void atenderCarnetSalud(Paciente paciente) throws InterruptedException {

        clinica.consultorioLibre.acquire();
        try {
            disponible = false;
            System.out.println(nombre + " entrevistando a " + paciente.getNombre() + " para el carné de salud.");
            Thread.sleep(paciente.getTiempoMaxDeConsulta() * 100);
            System.out
                    .println("Entrevista completada. Enviando a análisis de sangre y orina a " + paciente.getNombre());

            clinica.consultorioLibre.release();

            clinica.enfermeroDisponible.acquire();
            try {
                Enfermero enfermero = clinica.getEnfermero();
                enfermero.atenderPaciente(paciente);
            } finally {
                clinica.enfermeroDisponible.release();
            }

        } finally {
            disponible = true;
        }
    }

    public boolean estaDisponible() {
        return disponible;
    }
}
