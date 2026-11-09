package app;
import biblioteca.ValidadorBiblioteca;
import dominio.Ficha;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import manual.ValidadorManual;
public final class Main {
 private Main(){}
 public static void main(String[] args){
  if(args.length!=1){System.out.println("Uso: java -cp out app.Main <ruta-del-archivo.txt>");return;}
  try{
   List<Ficha> fichas=LectorFichas.leer(Path.of(args[0]));
   System.out.println("ARCHIVO LEÍDO CORRECTAMENTE: "+fichas.size()+" fichas.\n");
   String manual=new ValidadorManual().ejecutar(fichas),biblioteca=new ValidadorBiblioteca().ejecutar(fichas);
   System.out.println(manual);System.out.println(biblioteca);System.out.println("=== COMPARACIÓN ===");
   if(quitarEncabezado(manual).equals(quitarEncabezado(biblioteca)))System.out.println("TRAZAS EQUIVALENTES: mismo recorrido, primer error y resultado.");
   else System.out.println("ADVERTENCIA: las trazas de las dos implementaciones presentan diferencias.");
  }catch(IOException|IllegalArgumentException ex){System.out.println("ERROR DE LECTURA/FORMATO: "+ex.getMessage());}
 }
 private static String quitarEncabezado(String t){int i=t.indexOf('\n');return i<0?t:t.substring(i+1);}
}