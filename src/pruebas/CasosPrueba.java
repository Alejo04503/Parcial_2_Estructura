package pruebas;

import app.LectorFichas;
import biblioteca.ValidadorBiblioteca;
import dominio.Ficha;
import java.nio.file.Path;
import java.util.List;
import manual.ValidadorManual;

public final class CasosPrueba {
    private CasosPrueba() {}

    public static void main(String[] args) throws Exception {
        probar("datos/ejemplo_valido.txt", "SOL");
        probar("datos/pruebas/tres_prioridades.txt", "SOL");
        probarError("datos/pruebas/cierre_incorrecto.txt", "se esperaba ]");
        System.out.println("\nPruebas básicas terminadas.");
    }

    private static void probar(String ruta, String esperado) throws Exception {
        List<Ficha> fichas = LectorFichas.leer(Path.of(ruta));
        String manual = new ValidadorManual().ejecutar(fichas);
        String biblioteca = new ValidadorBiblioteca().ejecutar(fichas);
        boolean correcto = manual.contains("RESULTADO: " + esperado)
                && biblioteca.contains("RESULTADO: " + esperado);
        System.out.println((correcto ? "OK " : "FALLO ") + ruta
                + " | esperado=" + esperado);
        if (!correcto) throw new AssertionError("Falló " + ruta);
    }

    private static void probarError(String ruta, String esperado) throws Exception {
        List<Ficha> fichas = LectorFichas.leer(Path.of(ruta));
        String manual = new ValidadorManual().ejecutar(fichas);
        String biblioteca = new ValidadorBiblioteca().ejecutar(fichas);
        boolean correcto = manual.contains(esperado) && biblioteca.contains(esperado)
                && !manual.contains("FASE 3") && !biblioteca.contains("FASE 3");
        System.out.println((correcto ? "OK " : "FALLO ") + ruta
                + " | error=" + esperado);
        if (!correcto) throw new AssertionError("Falló " + ruta);
    }
}
