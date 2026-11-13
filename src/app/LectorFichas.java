package app;

import dominio.Ficha;
import dominio.TipoFicha;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class LectorFichas {
    private static final String ENCABEZADO = "id|tipo|valor|prioridad|instruccion";

    private LectorFichas() {}

    public static List<Ficha> leer(Path ruta) throws IOException {
        List<String> lineas = Files.readAllLines(ruta, StandardCharsets.UTF_8);
        if (lineas.isEmpty()) {
            throw new IllegalArgumentException("El archivo está vacío; falta el encabezado.");
        }
        if (!ENCABEZADO.equals(lineas.get(0).trim())) {
            throw new IllegalArgumentException("Línea física 1: encabezado incorrecto. Se esperaba: " + ENCABEZADO);
        }

        List<Ficha> fichas = new ArrayList<>();
        Set<String> identificadores = new HashSet<>();
        int posicion = 0;

        for (int i = 1; i < lineas.size(); i++) {
            int lineaFisica = i + 1;
            String linea = lineas.get(i);
            String[] campos = linea.split("\\|", -1);

            if (campos.length != 5) {
                throw new IllegalArgumentException("Línea física " + lineaFisica
                        + ": se esperaban cinco campos separados por |.");
            }

            String id = campos[0].trim();
            String tipoTexto = campos[1].trim();
            String valor = campos[2].trim();
            String prioridadTexto = campos[3].trim();
            String instruccion = campos[4].trim();

            if (id.isEmpty()) {
                throw new IllegalArgumentException("Línea física " + lineaFisica + ": el ID está vacío.");
            }
            if (!identificadores.add(id)) {
                throw new IllegalArgumentException("Línea física " + lineaFisica
                        + ": identificador duplicado " + id + ".");
            }

            TipoFicha tipo;
            try {
                tipo = TipoFicha.valueOf(tipoTexto);
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Línea física " + lineaFisica
                        + ", ficha " + id + ": tipo inválido " + tipoTexto + ".");
            }

            int prioridad;
            try {
                prioridad = Integer.parseInt(prioridadTexto);
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Línea física " + lineaFisica
                        + ", ficha " + id + ": prioridad no numérica.");
            }
            if (prioridad < 1 || prioridad > 3) {
                throw new IllegalArgumentException("Línea física " + lineaFisica
                        + ", ficha " + id + ": la prioridad debe ser 1, 2 o 3.");
            }

            validarValor(tipo, valor, lineaFisica, id);
            validarInstruccion(tipo, instruccion, lineaFisica, id);

            posicion++;
            fichas.add(new Ficha(id, tipo, valor, prioridad, instruccion, posicion, lineaFisica));
        }

        if (fichas.isEmpty()) {
            throw new IllegalArgumentException("El archivo no contiene fichas.");
        }
        return fichas;
    }

    private static void validarValor(TipoFicha tipo, String valor, int linea, String id) {
        boolean valido;
        switch (tipo) {
            case ABRE:
                valido = valor.equals("(") || valor.equals("[") || valor.equals("{");
                break;
            case CIERRA:
                valido = valor.equals(")") || valor.equals("]") || valor.equals("}");
                break;
            case LETRA:
                valido = valor.length() == 1 && Character.isLetter(valor.charAt(0));
                break;
            case PISTA:
                valido = valor.equals("-");
                break;
            default:
                valido = false;
        }
        if (!valido) {
            throw new IllegalArgumentException("Línea física " + linea + ", ficha " + id
                    + ": valor inválido para el tipo " + tipo + ".");
        }
    }

    private static void validarInstruccion(TipoFicha tipo, String instruccion, int linea, String id) {
        if (tipo != TipoFicha.PISTA) {
            if (!instruccion.equals("-")) {
                throw new IllegalArgumentException("Línea física " + linea + ", ficha " + id
                        + ": las fichas que no son PISTA deben tener instrucción -.");
            }
            return;
        }

        String[] partes = instruccion.split(":", -1);
        boolean revelar = partes.length == 2 && partes[0].equals("REVELAR") && !partes[1].isEmpty();
        boolean encadenar = partes.length == 3 && partes[0].equals("ENCADENAR")
                && !partes[1].isEmpty() && !partes[2].isEmpty();

        if (!revelar && !encadenar) {
            throw new IllegalArgumentException("Línea física " + linea + ", ficha " + id
                    + ": instrucción inválida. Use REVELAR:id o ENCADENAR:id1:id2.");
        }
    }
}
