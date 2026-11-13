package biblioteca;

import dominio.Ficha;
import dominio.TipoFicha;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public final class ValidadorBiblioteca {

    public String ejecutar(List<Ficha> entrada) {
        StringBuilder salida = new StringBuilder();
        List<Ficha> lista = new ArrayList<>(entrada);

        salida.append("=== VERSION JAVA.UTIL ===\n");
        salida.append("FASE 1. Cargar lista de fichas\n");

        for (Ficha ficha : lista) {
            salida.append("INSERTAR LISTA: ").append(ficha.getId())
                    .append(" en posición ").append(ficha.getPosicion()).append("\n");
        }

        salida.append("RECORRIDO LISTA: ");
        for (int i = 0; i < lista.size(); i++) {
            if (i > 0) {
                salida.append(" -> ");
            }
            salida.append(lista.get(i).getId());
        }
        salida.append("\n");

        salida.append("FASE 2. Validar delimitadores con pila\n");
        Deque<Ficha> pila = new ArrayDeque<>();

        for (Ficha ficha : lista) {
            if (ficha.getTipo() == TipoFicha.ABRE) {
                pila.push(ficha);
                salida.append("APILAR ").append(ficha.getValor()).append(" [")
                        .append(ficha.getId()).append("]\n");

            } else if (ficha.getTipo() == TipoFicha.CIERRA) {
                String esperado = parejaApertura(ficha.getValor());
                Ficha apertura = pila.peek();
                String tope = apertura == null ? null : apertura.getValor();

                salida.append("CONSULTAR PILA: ")
                        .append(tope == null ? "vacía" : tope).append("\n");

                if (apertura == null) {
                    return error(salida, ficha, "cierre " + ficha.getValor()
                            + " sin apertura correspondiente.");
                }

                if (!tope.equals(esperado)) {
                    return error(salida, ficha, "se esperaba " + parejaCierre(tope)
                            + " porque " + tope + " está en el tope.");
                }

                pila.pop();
                salida.append("RETIRAR ").append(tope).append(" [")
                        .append(ficha.getId()).append("]\n");
            }
        }

        if (!pila.isEmpty()) {
            Ficha apertura = pila.peek();
            return error(salida, apertura, "apertura " + apertura.getValor()
                    + " sin cierre correspondiente.");
        }

        salida.append("PILA VACÍA: delimitadores correctos\n");
        salida.append("FASE 3. Encolar pistas por prioridad\n");

        List<Queue<Ficha>> colas = new ArrayList<>();
        colas.add(new LinkedList<>());
        colas.add(new LinkedList<>());
        colas.add(new LinkedList<>());

        for (Ficha ficha : lista) {
            if (ficha.getTipo() == TipoFicha.PISTA) {
                colas.get(ficha.getPrioridad() - 1).offer(ficha);
                salida.append("ENCOLAR P").append(ficha.getPrioridad()).append(": ")
                        .append(ficha.getId()).append("\n");
            }
        }

        salida.append("FASE 4. Resolver pistas\n");
        Set<String> reveladas = new HashSet<>();
        StringBuilder mensaje = new StringBuilder();
        int prioridadActual = 0;

        while (prioridadActual < 3) {
            Queue<Ficha> cola = colas.get(prioridadActual);

            if (cola.isEmpty()) {
                prioridadActual++;
                continue;
            }

            Ficha pista = cola.poll();
            salida.append("ATENDER P").append(prioridadActual + 1).append(": ")
                    .append(pista.getId()).append(" ")
                    .append(pista.getInstruccion()).append("\n");

            String[] partes = pista.getInstruccion().split(":");
            String errorRevelar = revelar(partes[1], lista, reveladas, mensaje, salida, pista);

            if (errorRevelar != null) {
                return salida.append(errorRevelar).toString();
            }

            if (partes[0].equals("ENCADENAR")) {
                Ficha destino = buscar(lista, partes[2]);

                if (destino == null) {
                    return error(salida, pista, "referencia inexistente " + partes[2] + ".");
                }

                if (destino.getTipo() != TipoFicha.LETRA) {
                    return error(salida, pista, "la referencia " + partes[2]
                            + " no apunta a una ficha LETRA.");
                }

                Ficha generada = new Ficha(
                        "GENERADA-" + pista.getId() + "-" + partes[2],
                        TipoFicha.PISTA,
                        "-",
                        pista.getPrioridad(),
                        "REVELAR:" + partes[2],
                        pista.getPosicion(),
                        pista.getLinea()
                );

                cola.offer(generada);
                salida.append("GENERAR Y ENCOLAR P").append(prioridadActual + 1)
                        .append(": REVELAR:").append(partes[2]).append("\n");
            }

            salida.append("MENSAJE ACTUAL: ").append(mensaje).append("\n");
        }

        salida.append("RESULTADO: ").append(mensaje).append("\n");
        return salida.toString();
    }

    private Ficha buscar(List<Ficha> lista, String id) {
        for (Ficha ficha : lista) {
            if (ficha.getId().equals(id)) {
                return ficha;
            }
        }
        return null;
    }

    private String revelar(String id, List<Ficha> lista, Set<String> reveladas,
                           StringBuilder mensaje, StringBuilder salida, Ficha pista) {
        Ficha ficha = buscar(lista, id);

        salida.append("BUSCAR ").append(id).append(": ")
                .append(ficha == null ? "no encontrada" : ficha.getTipo()).append("\n");

        if (ficha == null) {
            return "ERROR en " + pista.getId() + ": referencia inexistente " + id + ".\n";
        }

        if (ficha.getTipo() != TipoFicha.LETRA) {
            return "ERROR en " + pista.getId()
                    + ": la referencia " + id + " no apunta a una ficha LETRA.\n";
        }

        if (!reveladas.add(id)) {
            return "ERROR en " + pista.getId()
                    + ": la letra " + id + " ya fue revelada.\n";
        }

        mensaje.append(ficha.getValor());
        salida.append("REVELAR ").append(id).append(" = ")
                .append(ficha.getValor()).append("\n");
        return null;
    }

    private String error(StringBuilder salida, Ficha ficha, String detalle) {
        salida.append("ERROR en línea física ").append(ficha.getLinea())
                .append(", ficha ").append(ficha.getId()).append(": ")
                .append(detalle).append("\n");
        return salida.toString();
    }

    private String parejaApertura(String cierre) {
        if (cierre.equals(")")) return "(";
        if (cierre.equals("]")) return "[";
        return "{";
    }

    private String parejaCierre(String apertura) {
        if (apertura.equals("(")) return ")";
        if (apertura.equals("[")) return "]";
        return "}";
    }
}
