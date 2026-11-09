# Informe técnico final: Bóveda de Fichas

**Asignatura:** Estructuras de Datos  
**Modalidad:** Trabajo individual  
**Lenguaje:** Java  
**Repositorio:** [Alejo04503/Parcial_2_Estructura](https://github.com/Alejo04503/Parcial_2_Estructura)  
**Rama documentada:** `desarrollo`  
**Corte del documento:** 9 de noviembre de 2026  
**Estado:** compilación y validación funcional local completadas; evidencias gráficas y sustentación pendientes.

---

## Resumen ejecutivo

El proyecto Bóveda de Fichas consiste en construir un programa Java que lea un archivo de texto con fichas ordenadas, compruebe el anidamiento correcto de delimitadores y descifre una palabra mediante pistas. El enunciado exige dos implementaciones equivalentes: una con estructuras desarrolladas manualmente y otra con estructuras de `java.util`.

La solución organizada en la rama `desarrollo` conserva el modelo de dominio entregado como base por el profesor y completa las clases encargadas de las estructuras y del procesamiento. La versión manual utiliza una lista enlazada propia, una pila de símbolos y tres colas FIFO separadas por prioridad. La segunda versión utiliza colecciones de Java para reproducir el mismo flujo. Ambas reciben la misma lista de fichas y generan trazas de sus operaciones.

El ejemplo principal incluido en el repositorio está diseñado para producir `SOL`. Incluye dos pistas iniciales: una revela la primera letra y otra utiliza `ENCADENAR` para revelar una segunda letra y generar una nueva pista que revela la última. También se prepararon archivos para comprobar las tres prioridades y un cierre incorrecto.

Este informe describe el problema, las decisiones de implementación, las estructuras utilizadas y los resultados de la validación. El 9 de noviembre de 2026 se recibió la salida de una compilación limpia, la ejecución de los 14 casos automatizados y la ejecución del ejemplo principal. Los resultados se registran en las secciones 7 a 9. Las capturas exigidas por el enunciado todavía deben guardarse como archivos reales en la carpeta `evidencias`.

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

En el repositorio existen tres archivos de entrada persistentes:

- `datos/ejemplo_valido.txt`: ejemplo con anidamiento correcto y una pista encadenada.
- `datos/pruebas/tres_prioridades.txt`: pistas distribuidas en las prioridades 1, 2 y 3, con un orden físico distinto al orden de atención.
- `datos/pruebas/cierre_incorrecto.txt`: modifica el cierre de F05 para producir un cruce de delimitadores.

El ejecutor `pruebas.CasosPrueba` amplía la cobertura mediante casos temporales que crea y elimina durante la ejecución. La suite comprueba:

1. Ejemplo válido, con resultado `SOL` y trazas equivalentes.
2. Atención de las tres prioridades, con resultado `SOL`.
3. Cierre incorrecto.
4. Cierre sin apertura.
5. Apertura sobrante.
6. Identificador duplicado.
7. Línea incompleta.
8. Tipo inválido.
9. Prioridad fuera del rango 1–3.
10. Referencia inexistente.
11. Referencia a una ficha que no es `LETRA`.
12. Intento de revelar una letra dos veces.
13. FIFO entre pistas de igual prioridad.
14. Encadenamiento detrás de una pista que ya estaba pendiente en la cola.

Para los casos de ejecución, el programa compara las trazas de las dos implementaciones, además del primer error o el mensaje final según corresponda. Los casos de formato inválido comprueban que el lector rechace la entrada antes de iniciar el procesamiento.

## 8. Compilación, ejecución y reproducibilidad

La verificación local se realizó desde PowerShell, en la raíz del repositorio. Primero se actualizó la rama de trabajo y se eliminó la carpeta de compilación anterior. Después se compiló todo el código fuente:

```powershell
git pull origin desarrollo
Remove-Item -Recurse -Force out
New-Item -ItemType Directory -Force out | Out-Null
$archivos = Get-ChildItem -Recurse src -Filter *.java | ForEach-Object { $_.FullName }
javac -encoding UTF-8 -d out $archivos
```

La compilación terminó sin mostrar errores. La suite se ejecutó con:

```powershell
java -cp out pruebas.CasosPrueba
```

El resultado final fue:

```text
Pruebas terminadas: 14/14 correctas.
```

A continuación se ejecutó el ejemplo principal:

```powershell
java -cp out app.Main datos/ejemplo_valido.txt
```

Las dos implementaciones leyeron nueve fichas, validaron correctamente los delimitadores, atendieron F08 en prioridad 1 y F09 en prioridad 2, y procesaron la pista generada `GENERADA-F09-F06`. Ambas construyeron el mensaje `SOL`. La comparación final mostró:

```text
RESULTADO: SOL
TRAZAS EQUIVALENTES: mismo recorrido, primer error y resultado.
```

Estos resultados corresponden a las salidas de consola compartidas durante la verificación local del 9 de noviembre de 2026. No sustituyen las capturas que deben adjuntarse a la entrega.

## 9. Estado de validación y evidencias

<badge>Validación funcional local: 14/14</badge>

La evidencia de consola confirma tres resultados:

- **Compilación:** todos los archivos Java se compilaron juntos sin errores.
- **Pruebas automatizadas:** los 14 escenarios terminaron correctamente.
- **Ejecución integral:** el archivo principal produjo `SOL` en ambas versiones y la comparación confirmó trazas equivalentes.

La suite también comprobó los errores de cierre sin apertura y apertura sobrante. En estos casos, ambas versiones identifican el error durante la fase de pila y no continúan con la resolución de pistas. Los errores de estructura del archivo, como ID duplicado o línea incompleta, se rechazan en la lectura.

La carpeta `evidencias` está preparada, pero las capturas todavía deben guardarse desde el computador donde se realizaron las pruebas. Se recomienda conservar imágenes legibles que muestren el comando y su salida completa para:

1. Compilación limpia.
2. Ejecución de las 14 pruebas.
3. Recorrido de la lista enlazada.
4. Operaciones de apilar, consultar y retirar.
5. Atención de las tres prioridades.
6. Mensaje final `SOL` y pista generada.
7. Error de delimitadores y detención del procesamiento.
8. Comparación de las dos implementaciones.

No se incluyen imágenes simuladas. Hasta que se agreguen los archivos, las evidencias gráficas deben considerarse pendientes aunque los resultados de consola ya hayan sido comprobados.

## 10. Conclusiones y trabajo pendiente

El diseño separa lectura, estructuras, validación y ejecución. La versión manual demuestra las operaciones de lista enlazada, pila y tres colas FIFO mediante nodos propios. La versión con `java.util` permite contrastar el comportamiento utilizando las colecciones de Java. La pista encadenada es el caso clave para demostrar que una cola puede recibir trabajo nuevo y seguir respetando el orden FIFO.

La validación funcional está completada para los 14 escenarios implementados. Antes de cerrar la entrega, todavía deben completarse estos puntos:

1. Guardar las capturas reales de compilación, suite y ejecución en `evidencias/`.
2. Revisar la paginación del informe al exportarlo para confirmar que cumple la extensión de 6 a 8 páginas solicitada.
3. Preparar la sustentación individual, especialmente la explicación de LIFO, FIFO, selección de prioridades, búsquedas por ID y encadenamiento.
4. Revisar el proyecto completo frente a la rúbrica y comprobar que los archivos de entrega estén incluidos.

La documentación debe mantenerse sincronizada con el comportamiento final del programa. La evidencia de una ejecución no sustituye la explicación del algoritmo: durante la sustentación se debe poder justificar por qué la pila es LIFO, por qué cada cola es FIFO, cómo se seleccionan las prioridades y cómo se inserta y procesa una pista generada.

---

## Referencia principal

Enunciado del parcial: **“Construya en Java un programa que lea un archivo de fichas, compruebe que los delimitadores estén bien anidados y descifre una palabra a partir de pistas”**, documento de seis páginas entregado por el profesor. Las reglas y la lista de pruebas de este informe se basan en dicho enunciado.
