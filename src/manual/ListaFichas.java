package manual;

import dominio.Ficha;

/**
 * Lista enlazada simple para guardar las fichas en el orden del archivo.
 */
public final class ListaFichas {
    private Nodo<Ficha> cabeza;
    private Nodo<Ficha> cola;
    private int tamanio;

    public void agregar(Ficha ficha) {
        Nodo<Ficha> nuevo = new Nodo<>(ficha);

        if (cabeza == null) {
            cabeza = nuevo;
            cola = nuevo;
        } else {
            cola.siguiente = nuevo;
            cola = nuevo;
        }

        tamanio++;
    }

    public Ficha buscar(String id) {
        Nodo<Ficha> actual = cabeza;

        while (actual != null) {
            if (actual.dato.getId().equals(id)) {
                return actual.dato;
            }
            actual = actual.siguiente;
        }

        return null;
    }

    public int tamanio() {
        return tamanio;
    }

    public Ficha primera() {
        if (cabeza == null) {
            return null;
        }
        return cabeza.dato;
    }

    /**
     * Devuelve la ficha que ocupa la posición indicada, comenzando en cero.
     */
    public Ficha obtener(int indice) {
        if (indice < 0 || indice >= tamanio) {
            throw new IndexOutOfBoundsException("Índice fuera de la lista: " + indice);
        }

        Nodo<Ficha> actual = cabeza;
        int posicion = 0;

        while (posicion < indice) {
            actual = actual.siguiente;
            posicion++;
        }

        return actual.dato;
    }
}
