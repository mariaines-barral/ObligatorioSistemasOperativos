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
        // Necesita consultorio y colaboración con enfermero
        boolean usoConsultorioNormal = false;
        if (clinica.consultorioLibre.tryAcquire()) {
            usoConsultorioNormal = true;
        } else {
            clinica.consultorioReservadoParaEmergencia.acquire();
        }
        clinica.enfermeroDisponible.acquire(); // Espera a que el enfermero esté libre

        try {
            disponible = false;
            System.out.println(nombre + " y el enfermero atendiendo " + paciente.getMotivoDeConsulta() +
                    " de " + paciente.getNombre());

            // Simular atención conjunta (doctor + enfermero)
            Thread.sleep(paciente.getTiempoMaxDeConsulta() * 100);

            clinica.incrementarAtendidos();
            System.out.println(paciente.getNombre() + " atendido exitosamente :)");

        } finally {
            disponible = true;
            clinica.enfermeroDisponible.release(); // Libera al enfermero
            if (usoConsultorioNormal) {
                clinica.consultorioLibre.release();
            } else {
                clinica.consultorioReservadoParaEmergencia.release();
            }
        }
    }

    private void atenderUrgencia(Paciente paciente) throws InterruptedException {
        // Necesita consultorio y colaboración con enfermero
        clinica.consultorioLibre.acquire();
        clinica.enfermeroDisponible.acquire(); // Espera a que el enfermero esté libre

        try {
            disponible = false;
            System.out.println(nombre + " y el enfermero atendiendo " + paciente.getMotivoDeConsulta() +
                    " de " + paciente.getNombre());

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
        disponible = false; // deberia ser un semaphore si se implementa con varios doctores.
        System.out.println(nombre + " entrevistando a " + paciente.getNombre() + " para el carné de salud.");
        // Simular entrevista
        Thread.sleep(paciente.getTiempoMaxDeConsulta() * 100);
        System.out.println("Entrevista completada. Enviando a análisis de sangre y orina a " + paciente.getNombre());
        disponible = true; // Cuando el doctor se libera termina su rol. y se puede llamar en otro sitio.
        clinica.enfermeroDisponible.acquire();

        clinica.consultorioLibre.release();
        Enfermero enfermero = clinica.getEnfermero();
        enfermero.hacerAnalisis(paciente); // Con el paciente.
        // metodo auxiliar del enfemero, lleme al paciente.

    }

    public boolean estaDisponible() {
        return disponible;
    }
}
