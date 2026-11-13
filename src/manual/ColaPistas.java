package manual;

public final class ColaPistas<T> {
    private final Nodo<T>[] frentes;
    private final Nodo<T>[] finales;

    @SuppressWarnings("unchecked")
    public ColaPistas() {
        frentes = (Nodo<T>[]) new Nodo<?>[3];
        finales = (Nodo<T>[]) new Nodo<?>[3];
    }

    public void encolar(T pista, int prioridad) {
        validarPrioridad(prioridad);
        int indice = prioridad - 1;
        Nodo<T> nuevo = new Nodo<>(pista);

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
