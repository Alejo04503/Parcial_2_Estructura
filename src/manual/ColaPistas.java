package manual;

/**
 * Tres colas FIFO independientes. La prioridad 1 se atiende antes que 2 y 3.
 */
public final class ColaPistas<T> {
    private static final class NodoCola<E> {
        E dato;
        NodoCola<E> siguiente;

        NodoCola(E dato) {
            this.dato = dato;
        }
    }

    private final NodoCola<T>[] frentes;
    private final NodoCola<T>[] finales;

    @SuppressWarnings("unchecked")
    public ColaPistas() {
        frentes = (NodoCola<T>[]) new NodoCola[3];
        finales = (NodoCola<T>[]) new NodoCola[3];
    }

    public void encolar(T pista, int prioridad) {
        validarPrioridad(prioridad);
        int indice = prioridad - 1;
        NodoCola<T> nuevo = new NodoCola<>(pista);

        if (frentes[indice] == null) {
            frentes[indice] = nuevo;
            finales[indice] = nuevo;
        } else {
            finales[indice].siguiente = nuevo;
            finales[indice] = nuevo;
        }
    }

    public T desencolar() {
        for (int i = 0; i < 3; i++) {
            if (frentes[i] != null) {
                T dato = frentes[i].dato;
                frentes[i] = frentes[i].siguiente;
                if (frentes[i] == null) {
                    finales[i] = null;
                }
                return dato;
            }
        }
        return null;
    }

    public boolean estaVacia() {
        return frentes[0] == null && frentes[1] == null && frentes[2] == null;
    }

    private void validarPrioridad(int prioridad) {
        if (prioridad < 1 || prioridad > 3) {
            throw new IllegalArgumentException("La prioridad debe ser 1, 2 o 3.");
        }
    }
}
