package sistemasobligatorio;

class Main {
    public static void main(String[] args) {
        Clinica clinica = new Clinica("sistemasobligatorio\\src\\main\\java\\sistemasobligatorio\\PacienteSinInforme.txt",
                "Dr. House",
                "Enfermero Stark", "Pam Beesly");

        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
