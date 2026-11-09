package manual;

public final class PilaSimbolos {
    private Nodo<String> tope;

    public void apilar(String simbolo) {
        Nodo<String> nuevo = new Nodo<>(simbolo);
        nuevo.siguiente = tope;
        tope = nuevo;
    }

    public String consultar() {
        if (estaVacia()) {
            return null;
        }
        return tope.dato;
    }

    public String retirar() {
        if (estaVacia()) {
            return null;
        }
        String simbolo = tope.dato;
        tope = tope.siguiente;
        return simbolo;
    }

    public boolean estaVacia() {
        return tope == null;
    }
}
