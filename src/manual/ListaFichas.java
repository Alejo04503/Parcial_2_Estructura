package manual;
import dominio.Ficha;
public final class ListaFichas {
    private Nodo<Ficha> cabeza;
    private Nodo<Ficha> cola;
    private int tamanio;
    public void agregar(Ficha ficha){ throw new UnsupportedOperationException("Pendiente: agregar"); }
    public Ficha buscar(String id){ throw new UnsupportedOperationException("Pendiente: buscar"); }
    public int tamanio(){return tamanio;}
    public Ficha primera(){ throw new UnsupportedOperationException("Pendiente: primera"); }
    // TODO: definir recorrido sin exponer Nodo.
}
