package sistemasobligatorio;

/**
 * Clase encargada de administrar el ingreso de los pacientes a cada cola. Va
 * ser un de un atributo de la clase
 * clinica. Se encarga de únicamente ingresar los pacientes a cada cola. No
 * administra su eliminacion.
 */
public class Recepcionista implements Runnable {

    private Clinica clinica;

    public Recepcionista(Clinica clinica) {
        this.clinica = clinica;
    }

    public void run() {
    }

    public boolean asignarPaciente(Paciente paciente) {
        try {
            clinica.mutexQueues.acquire();
            if (paciente.getMotivoDeConsulta() == "Emergencia") {
                return clinica.emergencias.add(paciente);
            } else if (paciente.getMotivoDeConsulta() == "Urgencia") {
                return clinica.urgencias.add(paciente);
            } else if (paciente.getMotivoDeConsulta() == "Carne de salud") {
                return clinica.estandar.add(paciente);
            }
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } finally {
            clinica.mutexQueues.release();
        }
    }

    public void reasignarPrioridades(Clinica clinica) {
        try {
            clinica.mutexQueues.acquire();
            Paciente temp = clinica.urgencias.peek();
            if (temp != null && temp.getTiempoDeEsperaAgotado()) {
                clinica.emergencias.add(temp);
                clinica.urgencias.remove();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            clinica.mutexQueues.release();
        }
    }
}

// 1p 2p 3p
// 1p tiempo de espera
// 1p lo proceso el doctor.
// 2p va ir a emergencias