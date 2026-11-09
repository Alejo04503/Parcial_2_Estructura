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
  StringBuilder s=new StringBuilder(); List<Ficha> lista=new ArrayList<>(entrada);
  s.append("=== VERSION JAVA.UTIL ===\nFASE 1. Cargar lista de fichas\n");
  for(Ficha f:lista)s.append("INSERTAR LISTA: ").append(f.getId()).append(" en posición ").append(f.getPosicion()).append("\n");
  s.append("RECORRIDO LISTA: "); for(int i=0;i<lista.size();i++){if(i>0)s.append(" -> ");s.append(lista.get(i).getId());} s.append("\n");
  s.append("FASE 2. Validar delimitadores con pila\n"); Deque<String> pila=new ArrayDeque<>();
  for(Ficha f:lista){
   if(f.getTipo()==TipoFicha.ABRE){pila.push(f.getValor());s.append("APILAR ").append(f.getValor()).append(" [").append(f.getId()).append("]\n");}
   else if(f.getTipo()==TipoFicha.CIERRA){
    String esperado=apertura(f.getValor()),tope=pila.peek();s.append("CONSULTAR PILA: ").append(tope==null?"vacía":tope).append("\n");
    if(tope==null)return error(s,f,"cierre "+f.getValor()+" sin apertura correspondiente.");
    if(!tope.equals(esperado))return error(s,f,"se esperaba "+cierre(tope)+" porque "+tope+" está en el tope.");
    pila.pop();s.append("RETIRAR ").append(tope).append(" [").append(f.getId()).append("]\n");
   }
  }
  if(!pila.isEmpty())return s.append("ERROR: apertura ").append(pila.peek()).append(" sin cierre correspondiente.\n").toString();
  s.append("PILA VACÍA: delimitadores correctos\nFASE 3. Encolar pistas por prioridad\n");
  List<Queue<Ficha>> colas=new ArrayList<>();colas.add(new LinkedList<>());colas.add(new LinkedList<>());colas.add(new LinkedList<>());
  for(Ficha f:lista)if(f.getTipo()==TipoFicha.PISTA){colas.get(f.getPrioridad()-1).offer(f);s.append("ENCOLAR P").append(f.getPrioridad()).append(": ").append(f.getId()).append("\n");}
  s.append("FASE 4. Resolver pistas\n");Set<String> reveladas=new HashSet<>();StringBuilder mensaje=new StringBuilder();int p=0;
  while(p<3){Queue<Ficha> cola=colas.get(p);if(cola.isEmpty()){p++;continue;}Ficha pista=cola.poll();
   s.append("ATENDER P").append(p+1).append(": ").append(pista.getId()).append(" ").append(pista.getInstruccion()).append("\n");
   String[] partes=pista.getInstruccion().split(":");String e=revelar(partes[1],lista,reveladas,mensaje,s,pista);if(e!=null)return s.append(e).toString();
   if(partes[0].equals("ENCADENAR")){
    Ficha destino=buscar(lista,partes[2]);if(destino==null)return error(s,pista,"referencia inexistente "+partes[2]+".");
    if(destino.getTipo()!=TipoFicha.LETRA)return error(s,pista,"la referencia "+partes[2]+" no apunta a una ficha LETRA.");
    Ficha generada=new Ficha("GENERADA-"+pista.getId()+"-"+partes[2],TipoFicha.PISTA,"-",pista.getPrioridad(),"REVELAR:"+partes[2],pista.getPosicion(),pista.getLinea());
    cola.offer(generada);s.append("GENERAR Y ENCOLAR P").append(p+1).append(": REVELAR:").append(partes[2]).append("\n");
   }
   s.append("MENSAJE ACTUAL: ").append(mensaje).append("\n");
  }
  return s.append("RESULTADO: ").append(mensaje).append("\n").toString();
 }
 private Ficha buscar(List<Ficha> l,String id){for(Ficha f:l)if(f.getId().equals(id))return f;return null;}
 private String revelar(String id,List<Ficha> l,Set<String> r,StringBuilder m,StringBuilder s,Ficha p){
  Ficha f=buscar(l,id);s.append("BUSCAR ").append(id).append(": ").append(f==null?"no encontrada":f.getTipo()).append("\n");
  if(f==null)return "ERROR en "+p.getId()+": referencia inexistente "+id+".\n";
  if(f.getTipo()!=TipoFicha.LETRA)return "ERROR en "+p.getId()+": la referencia "+id+" no apunta a una ficha LETRA.\n";
  if(!r.add(id))return "ERROR en "+p.getId()+": la letra "+id+" ya fue revelada.\n";
  m.append(f.getValor());s.append("REVELAR ").append(id).append(" = ").append(f.getValor()).append("\n");return null;
 }
 private String error(StringBuilder s,Ficha f,String d){s.append("ERROR en línea física ").append(f.getLinea()).append(", ficha ").append(f.getId()).append(": ").append(d).append("\n");return s.toString();}
 private String apertura(String c){if(c.equals(")"))return "(";if(c.equals("]"))return "[";return "{";}
 private String cierre(String a){if(a.equals("("))return ")";if(a.equals("["))return "]";return "}";}
}