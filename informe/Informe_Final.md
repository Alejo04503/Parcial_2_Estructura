# Informe técnico final: Bóveda de Fichas

**Asignatura:** Estructuras de Datos  
**Modalidad:** Trabajo individual  
**Lenguaje:** Java  
**Repositorio:** [Alejo04503/Parcial_2_Estructura](https://github.com/Alejo04503/Parcial_2_Estructura)  
**Rama documentada:** `desarrollo`  
**Corte del documento:** 9 de noviembre de 2026  
**Estado:** Implementación preparada para verificación local; resultados de ejecución pendientes.

---

## Resumen ejecutivo

El proyecto Bóveda de Fichas consiste en construir un programa Java que lea un archivo de texto con fichas ordenadas, compruebe el anidamiento correcto de delimitadores y descifre una palabra mediante pistas. El enunciado exige dos implementaciones equivalentes: una con estructuras desarrolladas manualmente y otra con estructuras de `java.util`.

La solución organizada en la rama `desarrollo` conserva el modelo de dominio entregado como base por el profesor y completa las clases encargadas de las estructuras y del procesamiento. La versión manual utiliza una lista enlazada propia, una pila de símbolos y tres colas FIFO separadas por prioridad. La segunda versión utiliza colecciones de Java para reproducir el mismo flujo. Ambas reciben la misma lista de fichas y generan trazas de sus operaciones.

El ejemplo principal incluido en el repositorio está diseñado para producir `SOL`. Incluye dos pistas iniciales: una revela la primera letra y otra utiliza `ENCADENAR` para revelar una segunda letra y generar una nueva pista que revela la última. También se prepararon archivos para comprobar las tres prioridades y un cierre incorrecto.

Este informe describe el problema, las decisiones de implementación, las estructuras utilizadas, el funcionamiento esperado y el plan de validación. Es importante separar el estado del código en GitHub de los resultados obtenidos al ejecutar el programa: a la fecha de este corte no se ha recibido una salida de compilación ni una ejecución local verificable. Por esa razón, las pruebas se presentan como pendientes y no como aprobadas. Las capturas exigidas por el enunciado deberán obtenerse del computador donde se compile y ejecute el proyecto.

## 1. Problema y reglas de entrada

La bóveda recibe un archivo UTF-8. La primera línea contiene el encabezado:

```text
id|tipo|valor|prioridad|instruccion
```

Cada registro posterior tiene cinco campos separados por el carácter `|`. El identificador debe ser único. El tipo puede ser `ABRE`, `CIERRA`, `LETRA` o `PISTA`. El valor depende del tipo: una apertura, un cierre, una letra individual o el guion `-`. La prioridad debe estar entre 1 y 3. Las instrucciones de una pista son `REVELAR:id` o `ENCADENAR:id1:id2`; los otros tipos utilizan `-`.

La posición de una ficha se cuenta desde 1 en el orden de lectura. También se conserva la línea física, que incluye el encabezado al contar las líneas del archivo. Estos dos datos permiten identificar dónde se originó un registro y comunicar errores con mayor precisión.

La instrucción `REVELAR:id` busca la ficha referenciada y agrega su letra al mensaje. La instrucción `ENCADENAR:id1:id2` revela primero la letra asociada a `id1` y crea una nueva pista `REVELAR:id2`. Esta pista generada se coloca al final de la cola correspondiente a la prioridad de la pista que la produjo. Una letra solo puede revelarse una vez. Si la referencia no existe o no apunta a una ficha de tipo `LETRA`, el proceso informa un error.

El orden de fases es obligatorio. Primero se valida el archivo. Después se construye y recorre la lista enlazada. A continuación se valida el anidamiento con una pila. Solo cuando la pila queda vacía se distribuyen y atienden las pistas. Un error detiene las fases posteriores y conserva la traza que ya se había producido.

## 2. Diseño general de la solución

La solución se divide en cuatro responsabilidades:

1. **Dominio:** representa los datos de cada ficha y los tipos válidos.
2. **Lectura:** transforma las líneas del archivo en objetos ficha y comprueba el formato.
3. **Procesamiento manual y con biblioteca:** ejecuta las mismas fases utilizando estructuras diferentes.
4. **Ejecución y pruebas:** permite iniciar el programa desde consola, mostrar las trazas y ejecutar casos reproducibles.

La clase `Ficha` contiene identificador, tipo, valor, prioridad, instrucción, posición y línea física. `TipoFicha` restringe los tipos admitidos. Estas clases forman parte de la base del profesor y se mantienen como modelo del proyecto.

La organización por paquetes evita mezclar la lectura del archivo con las operaciones internas de las estructuras. El lector no resuelve el mensaje; prepara los datos y rechaza registros con formato inválido. Los validadores reciben la lista ya leída y ejecutan las fases del algoritmo.

## 3. Implementación manual de las estructuras

### 3.1 Lista enlazada de fichas

La clase `ListaFichas` utiliza nodos enlazados. Mantiene una referencia a la cabeza, otra a la cola y un contador de elementos. Cuando se agrega la primera ficha, el nuevo nodo pasa a ser cabeza y cola. En las inserciones posteriores, el nodo que era cola apunta al nuevo y la referencia de cola se actualiza.

La lista conserva el orden del archivo. El método de búsqueda recorre los nodos desde la cabeza y compara el identificador de cada ficha. El método de acceso por índice permite recorrer la lista y mostrar el orden de inserción. La búsqueda por ID es necesaria para resolver las instrucciones de las pistas.

### 3.2 Pila de símbolos

La clase `PilaSimbolos` se implementa mediante nodos enlazados y una referencia al tope. La operación de apilar coloca un nuevo nodo delante del anterior tope. Consultar devuelve el símbolo superior sin retirarlo. Retirar mueve la referencia al nodo siguiente y devuelve el símbolo que estaba arriba.

La pila es LIFO: el último delimitador de apertura que entra debe ser el primero que salga. Esta propiedad permite validar anidamientos. Por ejemplo, después de apilar `(` y luego `[`, el siguiente cierre correcto debe ser `]`. Un cierre `)` en ese momento no puede retirar la apertura exterior porque la apertura cuadrada sigue en el tope.

### 3.3 Tres colas FIFO de prioridad

La clase `ColaPistas` administra tres colas enlazadas independientes. Cada cola mantiene referencias a su frente y final. Al encolar, el nuevo nodo se agrega al final. Al desencolar, se retira el nodo del frente. La prioridad 1 se revisa antes que la 2, y la 2 antes que la 3.

El orden FIFO se conserva dentro de cada prioridad. Cuando una instrucción genera otra pista, esta se agrega al final de la misma cola. Así, la pista generada no adelanta a las pistas que ya estaban esperando en esa prioridad. La implementación manual no utiliza una colección de colas de Java para resolver esta fase: los nodos y enlaces de las tres colas se administran en la clase propia.

## 4. Implementación equivalente con java.util

La segunda versión reproduce las mismas fases con estructuras de biblioteca. La lista de fichas se representa mediante una lista de Java. Para la pila se utiliza `Deque` con operaciones `push`, `peek` y `pop`. Para las prioridades se mantienen tres colas FIFO de tipo `Queue`, implementadas con `LinkedList`, y se utilizan `offer`, `poll` e `isEmpty`.

La selección de tres colas, en lugar de una sola cola de prioridad ordenada automáticamente, hace explícito el requisito del enunciado: atender prioridad 1 completa antes de pasar a prioridad 2 y después a prioridad 3, conservando FIFO en cada grupo. La pista generada se agrega a la misma cola que la originó.

La equivalencia no significa que las dos implementaciones utilicen el mismo código interno. Significa que, para la misma entrada, deben observar el mismo recorrido, las mismas operaciones relevantes, el mismo primer error y el mismo mensaje final. La comparación se realiza sobre las trazas producidas por los validadores.

## 5. Lectura, validación y manejo de errores

La clase `LectorFichas` lee todas las líneas con codificación UTF-8. Comprueba que exista el encabezado esperado y que cada registro tenga cinco campos. Después valida que el ID no esté vacío ni repetido, que el tipo pertenezca a los valores admitidos, que la prioridad sea válida y que el valor e instrucción correspondan al tipo.

La lectura conserva la posición de la ficha y el número de línea física. Los errores de formato se informan antes de iniciar las fases de lista, pila y colas, porque no es posible procesar de forma confiable un registro que no cumple la gramática de entrada.

Durante la validación de delimitadores, cada apertura se apila. Ante un cierre se consulta el tope. Si la pila está vacía, el cierre no tiene apertura correspondiente. Si el símbolo del tope no es la apertura esperada, se informa el cruce de delimitadores y se detiene la ejecución. Si al terminar quedan aperturas en la pila, el archivo también es inválido.

Las pistas solo se procesan después de una validación correcta de delimitadores. En la resolución se verifica que cada referencia exista, que su tipo sea `LETRA` y que no se haya revelado anteriormente. Al encontrar el primer error de una pista, no se atienden las siguientes y se conserva la traza de lo ejecutado hasta ese punto.

## 6. Recorrido del ejemplo principal

El archivo `datos/ejemplo_valido.txt` contiene las fichas F01 a F09. La secuencia de delimitadores abre primero paréntesis y luego corchetes. Más adelante cierra corchetes y finalmente paréntesis. Por lo tanto, el orden LIFO permite retirar primero `[` y después `(`.

Una vez que la pila queda vacía, se distribuyen las pistas. F08 pertenece a prioridad 1 y contiene `REVELAR:F02`; F09 pertenece a prioridad 2 y contiene `ENCADENAR:F04:F06`.

La resolución esperada es:

| Momento | Operación | Mensaje acumulado |
|---|---|---|
| Atención de F08 | Revelar F02, cuyo valor es S. | S |
| Atención de F09 | Revelar F04, cuyo valor es O. | SO |
| Generación desde F09 | Encolar `REVELAR:F06` al final de P2. | SO |
| Atención de la pista generada | Revelar F06, cuyo valor es L. | SOL |

El resultado definido por el enunciado para el ejemplo es `SOL`. La ejecución local debe confirmar que ambas versiones lo producen y que el orden de eventos coincide.

## 7. Archivos de prueba y cobertura requerida

En el repositorio existen tres archivos de entrada iniciales:

- `datos/ejemplo_valido.txt`: ejemplo con anidamiento correcto y una pista encadenada.
- `datos/pruebas/tres_prioridades.txt`: pistas distribuidas en las prioridades 1, 2 y 3, con un orden físico distinto al orden de atención.
- `datos/pruebas/cierre_incorrecto.txt`: modifica el cierre de F05 para producir el primer error de anidamiento descrito en el enunciado.

El enunciado también requiere casos para cierre sin apertura, apertura sobrante, identificador repetido, línea incompleta, tipo inválido, prioridad fuera del rango, referencia inexistente, referencia a una ficha de otro tipo, letra repetida, dos pistas de la misma prioridad y encadenamiento mientras aún quedan pistas en cola.

El ejecutor `pruebas.CasosPrueba` contiene actualmente comprobaciones iniciales del ejemplo, el archivo de tres prioridades y el cierre incorrecto. Esto no cubre todavía toda la matriz mínima. Los casos restantes deben agregarse y ejecutarse antes de declarar completa la validación.

## 8. Compilación, ejecución y reproducibilidad

Desde la raíz del repositorio, en PowerShell, se crea la carpeta de salida y se compilan todos los archivos Java:

```powershell
New-Item -ItemType Directory -Force out | Out-Null
$archivos = Get-ChildItem -Recurse src -Filter *.java | ForEach-Object { $_.FullName }
javac -encoding UTF-8 -d out $archivos
```

El programa principal se inicia con:

```powershell
java -cp out app.Main datos/ejemplo_valido.txt
```

El ejecutor de pruebas iniciales se inicia con:

```powershell
java -cp out pruebas.CasosPrueba
```

La compilación conjunta incluye las dos implementaciones y permite que el punto de entrada ejecute ambas sobre el mismo archivo. Para la entrega deben conservarse las salidas de consola que permitan identificar el comando, el resultado y cualquier error. Si la compilación presenta errores, deben corregirse antes de continuar con las pruebas funcionales.

## 9. Estado de validación y evidencias

A la fecha de corte, la rama `desarrollo` contiene el código y los archivos iniciales descritos en este informe. Sin embargo, no se dispone todavía de una salida de compilación o ejecución local confirmada por el estudiante. Por tanto, no se afirma que el proyecto compile correctamente ni que los casos hayan pasado. Las salidas esperadas descritas anteriormente proceden de las reglas del enunciado y del diseño de los archivos de entrada, no de una ejecución que se esté presentando como evidencia.

La entrega requiere capturas legibles de compilación manual, compilación con biblioteca, recorrido de lista, operaciones de pila, orden de colas, mensaje `SOL`, error de F05 y comparación de versiones. Cada captura debe incluir el objetivo, el comando utilizado, el resultado visible y una explicación breve. Las imágenes deben ser capturadas durante ejecuciones reales.

La carpeta `evidencias` está preparada para recibir esos archivos. No se incluyen imágenes de muestra ni capturas simuladas. Una vez ejecutadas las pruebas, se debe actualizar este apartado con fecha, nombre de archivo, comando y resultado real de cada evidencia.

## 10. Conclusiones y trabajo pendiente

El diseño separa lectura, estructuras, validación y ejecución. La versión manual demuestra las operaciones de lista enlazada, pila y tres colas FIFO mediante nodos propios. La versión con `java.util` permite contrastar el comportamiento utilizando las colecciones de Java. La pista encadenada es el caso clave para demostrar que una cola puede recibir trabajo nuevo y seguir respetando el orden FIFO.

El proyecto está preparado para iniciar la etapa de verificación local, pero no debe considerarse terminado hasta completar estos puntos:

1. Compilar todos los archivos Java y corregir cualquier error encontrado.
2. Ejecutar el ejemplo y confirmar `SOL` en ambas versiones.
3. Ejecutar el caso de tres prioridades y verificar el orden P1 → P2 → P3.
4. Ejecutar el cierre incorrecto y confirmar que se detiene antes de procesar pistas.
5. Agregar y ejecutar los demás casos mínimos y casos de frontera.
6. Comparar las trazas y corregir cualquier diferencia observable.
7. Guardar las ocho evidencias solicitadas.
8. Actualizar este informe con los resultados reales y preparar la sustentación individual.

La documentación debe mantenerse sincronizada con el comportamiento final del programa. La evidencia de una ejecución no sustituye la explicación del algoritmo: durante la sustentación se debe poder justificar por qué la pila es LIFO, por qué cada cola es FIFO, cómo se seleccionan las prioridades y cómo se inserta y procesa una pista generada.

---

## Referencia principal

Enunciado del parcial: **“Construya en Java un programa que lea un archivo de fichas, compruebe que los delimitadores estén bien anidados y descifre una palabra a partir de pistas”**, documento de seis páginas entregado por el profesor. Las reglas y la lista de pruebas de este informe se basan en dicho enunciado.
