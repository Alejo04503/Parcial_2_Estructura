package manual;

import dominio.Ficha;

public final class PilaSimbolos {
    private Nodo<Ficha> tope;

    public void apilar(Ficha ficha) {
        Nodo<Ficha> nuevo = new Nodo<>(ficha);
        nuevo.siguiente = tope;
        tope = nuevo;
    }

    public Ficha consultar() {
        if (estaVacia()) {
            return null;
        }
        return tope.dato;
    }

    public Ficha retirar() {
        if (estaVacia()) {
            return null;
        }
        Ficha ficha = tope.dato;
        tope = tope.siguiente;
        return ficha;
    }

    public boolean estaVacia() {
        return tope == null;
    }
}
