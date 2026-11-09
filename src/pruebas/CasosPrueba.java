package pruebas;

import app.LectorFichas;
import biblioteca.ValidadorBiblioteca;
import dominio.Ficha;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import manual.ValidadorManual;

/**
 * Pruebas de lectura, estructuras y equivalencia de las dos implementaciones.
 * Los archivos temporales se eliminan al terminar cada caso.
 */
public final class CasosPrueba {
    private static int aprobadas;
    private static int total;

    private CasosPrueba() {}

    public static void main(String[] args) throws Exception {
        Path carpeta = Path.of("datos", "pruebas");
        Files.createDirectories(carpeta);

        probarArchivoValido("ejemplo válido", Path.of("datos", "ejemplo_valido.txt"), "SOL");
        probarArchivoValido("tres prioridades", carpeta.resolve("tres_prioridades.txt"), "SOL");
        probarErrorEjecucion("cierre incorrecto", carpeta.resolve("cierre_incorrecto.txt"),
                "se esperaba ]");
        probarErrorEjecucionContenido(carpeta, "cierre sin apertura",
                "id|tipo|valor|prioridad|instruccion\nF01|CIERRA|)|1|-\n",
                "cierre ) sin apertura correspondiente.");
        probarErrorEjecucionContenido(carpeta, "apertura sobrante",
                "id|tipo|valor|prioridad|instruccion\nF01|ABRE|(|1|-\n",
                "línea física 2, ficha F01: apertura ( sin cierre correspondiente.");
        probarErrorContenido(carpeta, "ID duplicado",
                "id|tipo|valor|prioridad|instruccion\n"
                + "F01|LETRA|A|1|-\nF01|LETRA|B|1|-\n",
                "identificador duplicado F01");
        probarErrorContenido(carpeta, "línea incompleta",
                "id|tipo|valor|prioridad|instruccion\nF01|LETRA|A|1\n",
                "se esperaban cinco campos");
        probarErrorContenido(carpeta, "tipo inválido",
                "id|tipo|valor|prioridad|instruccion\nF01|DESCONOCIDO|A|1|-\n",
                "tipo inválido DESCONOCIDO");
        probarErrorContenido(carpeta, "prioridad fuera de rango",
                "id|tipo|valor|prioridad|instruccion\nF01|LETRA|A|4|-\n",
                "la prioridad debe ser 1, 2 o 3.");

        probarErrorEjecucionContenido(carpeta, "referencia inexistente",
                "id|tipo|valor|prioridad|instruccion\nF01|PISTA|-|1|REVELAR:ZZ\n",
                "referencia inexistente ZZ.");
        probarErrorEjecucionContenido(carpeta, "referencia que no es LETRA",
                "id|tipo|valor|prioridad|instruccion\nF01|PISTA|-|1|REVELAR:F01\n",
                "la referencia F01 no apunta a una ficha LETRA.");
        probarErrorEjecucionContenido(carpeta, "letra revelada dos veces",
                "id|tipo|valor|prioridad|instruccion\n"
                + "F01|LETRA|A|3|-\n"
                + "F02|PISTA|-|1|REVELAR:F01\n"
                + "F03|PISTA|-|1|REVELAR:F01\n",
                "la letra F01 ya fue revelada.");

        probarOrdenContenido(carpeta, "FIFO de igual prioridad",
                "id|tipo|valor|prioridad|instruccion\n"
                + "F01|LETRA|A|3|-\n"
                + "F02|LETRA|B|3|-\n"
                + "F03|PISTA|-|2|REVELAR:F01\n"
                + "F04|PISTA|-|2|REVELAR:F02\n",
                "AB", "ATENDER P2: F03", "ATENDER P2: F04");

        probarOrdenContenido(carpeta, "encadenamiento detrás de pista pendiente",
                "id|tipo|valor|prioridad|instruccion\n"
                + "F01|LETRA|A|3|-\n"
                + "F02|LETRA|C|3|-\n"
                + "F03|LETRA|B|3|-\n"
                + "F04|PISTA|-|2|ENCADENAR:F01:F02\n"
                + "F05|PISTA|-|2|REVELAR:F03\n",
                "ABC", "ATENDER P2: F04", "ATENDER P2: F05",
                "ATENDER P2: GENERADA-F04-F02");

        System.out.println("\nPruebas terminadas: " + aprobadas + "/" + total + " correctas.");
        if (aprobadas != total) {
            throw new AssertionError("Hay pruebas fallidas.");
        }
    }

    private static void probarArchivoValido(String nombre, Path ruta, String esperado) throws Exception {
        total++;
        List<Ficha> fichas = LectorFichas.leer(ruta);
        String manual = new ValidadorManual().ejecutar(fichas);
        String biblioteca = new ValidadorBiblioteca().ejecutar(fichas);
        comprobar(nombre, manual.contains("RESULTADO: " + esperado)
                && biblioteca.contains("RESULTADO: " + esperado)
                && trazasEquivalentes(manual, biblioteca), "resultado " + esperado + " y trazas equivalentes");
    }

    private static void probarErrorEjecucion(String nombre, Path ruta, String esperado) throws Exception {
        total++;
        List<Ficha> fichas = LectorFichas.leer(ruta);
        String manual = new ValidadorManual().ejecutar(fichas);
        String biblioteca = new ValidadorBiblioteca().ejecutar(fichas);
        comprobar(nombre, manual.contains(esperado) && biblioteca.contains(esperado)
                && trazasEquivalentes(manual, biblioteca), "error esperado y trazas equivalentes");
    }

    private static void probarErrorContenido(Path carpeta, String nombre, String contenido,
                                             String esperado) throws Exception {
        total++;
        Path temporal = crearTemporal(carpeta, contenido);
        try {
            boolean correcto = false;
            try {
                LectorFichas.leer(temporal);
            } catch (IllegalArgumentException ex) {
                correcto = ex.getMessage().contains(esperado);
            }
            comprobar(nombre, correcto, "rechazo de entrada: " + esperado);
        } finally {
            Files.deleteIfExists(temporal);
        }
    }

    private static void probarErrorEjecucionContenido(Path carpeta, String nombre, String contenido,
                                                       String esperado) throws Exception {
        total++;
        Path temporal = crearTemporal(carpeta, contenido);
        try {
            List<Ficha> fichas = LectorFichas.leer(temporal);
            String manual = new ValidadorManual().ejecutar(fichas);
            String biblioteca = new ValidadorBiblioteca().ejecutar(fichas);
            comprobar(nombre, manual.contains(esperado) && biblioteca.contains(esperado)
                    && trazasEquivalentes(manual, biblioteca), "error esperado y trazas equivalentes");
        } finally {
            Files.deleteIfExists(temporal);
        }
    }

    private static void probarOrdenContenido(Path carpeta, String nombre, String contenido,
                                             String resultado, String... orden) throws Exception {
        total++;
        Path temporal = crearTemporal(carpeta, contenido);
        try {
            List<Ficha> fichas = LectorFichas.leer(temporal);
            String manual = new ValidadorManual().ejecutar(fichas);
            String biblioteca = new ValidadorBiblioteca().ejecutar(fichas);
            boolean correcto = manual.contains("RESULTADO: " + resultado)
                    && biblioteca.contains("RESULTADO: " + resultado)
                    && trazasEquivalentes(manual, biblioteca)
                    && contieneEnOrden(manual, orden)
                    && contieneEnOrden(biblioteca, orden);
            comprobar(nombre, correcto, "resultado " + resultado + " y orden FIFO esperado");
        } finally {
            Files.deleteIfExists(temporal);
        }
    }

    private static Path crearTemporal(Path carpeta, String contenido) throws Exception {
        Path temporal = Files.createTempFile(carpeta, "caso_temporal_", ".txt");
        Files.writeString(temporal, contenido, StandardCharsets.UTF_8);
        return temporal;
    }

    private static boolean trazasEquivalentes(String manual, String biblioteca) {
        return quitarPrimeraLinea(manual).equals(quitarPrimeraLinea(biblioteca));
    }

    private static String quitarPrimeraLinea(String texto) {
        int salto = texto.indexOf('\n');
        return salto < 0 ? texto : texto.substring(salto + 1);
    }

    private static boolean contieneEnOrden(String texto, String... fragmentos) {
        int posicionAnterior = -1;
        for (String fragmento : fragmentos) {
            int posicion = texto.indexOf(fragmento);
            if (posicion < 0 || posicion <= posicionAnterior) {
                return false;
            }
            posicionAnterior = posicion;
        }
        return true;
    }

    private static void comprobar(String nombre, boolean correcto, String detalle) {
        if (correcto) {
            aprobadas++;
            System.out.println("OK " + nombre + " | " + detalle);
        } else {
            System.out.println("FALLO " + nombre + " | " + detalle);
        }
    }
}
