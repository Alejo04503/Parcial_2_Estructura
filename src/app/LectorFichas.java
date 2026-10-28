package app;
import dominio.Ficha;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/** Contrato de lectura; el estudiante implementa el algoritmo. */
public final class LectorFichas {
    private LectorFichas() {}
    public static List<Ficha> leer(Path ruta) throws IOException {
        // TODO: leer, interpretar y validar fichas.
        throw new UnsupportedOperationException("Pendiente: lectura de fichas");
    }
}
