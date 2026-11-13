# Informe técnico final: Bóveda de Fichas

**Asignatura:** Estructuras de Datos  
**Modalidad:** Trabajo individual  
**Lenguaje:** Java  
**Repositorio:** [Alejo04503/Parcial_2_Estructura](https://github.com/Alejo04503/Parcial_2_Estructura)  
**Rama de entrega:** `main`  
**Fecha de actualización:** 12 de noviembre de 2026  
**Estado:** implementación, pruebas y evidencias incorporadas al repositorio.

---

## Resumen ejecutivo

El proyecto Bóveda de Fichas consiste en desarrollar un programa Java que lea un archivo de texto con fichas ordenadas, compruebe el anidamiento correcto de delimitadores y descifre un mensaje mediante pistas. El trabajo contempla dos implementaciones equivalentes: una que desarrolla manualmente las estructuras de datos y otra que utiliza colecciones de `java.util`.

La implementación manual utiliza una lista enlazada propia, una pila de símbolos y tres colas FIFO independientes, una por cada prioridad. La implementación con biblioteca reproduce el mismo procesamiento mediante una lista de Java, una pila basada en `Deque` y tres colas `Queue`. Ambas versiones reciben las mismas fichas y generan trazas que permiten comparar el recorrido, el primer error y el resultado.

El ejemplo principal contiene nueve fichas y debe producir el mensaje `SOL`. La pista F09 ejecuta la instrucción `ENCADENAR:F04:F06`: revela la letra O y genera una nueva pista que revela L. Además del ejemplo válido, el repositorio contiene una entrada con las tres prioridades y otra que provoca un error de delimitadores en F05.

La verificación local comunicada el 12 de noviembre de 2026 incluyó compilación completa sin errores, ejecución del ejemplo principal, comparación de trazas equivalentes y 14 de 14 pruebas automatizadas correctas. Las capturas de las ejecuciones se incorporaron a la carpeta `evidencias` y se describen en su archivo índice.

## 1. Planteamiento y reglas de entrada

La aplicación recibe un archivo de texto UTF-8. Su primera línea debe contener el encabezado:

```text
id|tipo|valor|prioridad|instruccion
```

Cada registro posterior contiene cinco campos separados por el carácter `|`. El identificador debe ser único y no puede estar vacío. El tipo debe ser uno de los siguientes: `ABRE`, `CIERRA`, `LETRA` o `PISTA`. El valor depende del tipo: una apertura, un cierre, una letra individual o el guion `-`. La prioridad debe estar entre 1 y 3. Las instrucciones válidas para las pistas son `REVELAR:id` y `ENCADENAR:id1:id2`; los demás tipos deben utilizar `-`.

El lector conserva dos referencias de ubicación. La posición identifica el orden de la ficha dentro de la colección, empezando en 1. La línea física identifica el número real de línea del archivo e incluye el encabezado. Esto permite emitir mensajes de error precisos y distinguir la posición lógica de la ficha de su ubicación en el documento.

La instrucción `REVELAR:id` busca una ficha por su identificador y agrega su valor al mensaje si corresponde a una ficha de tipo `LETRA` que todavía no se haya revelado. La instrucción `ENCADENAR:id1:id2` revela primero la ficha `id1` y crea una nueva pista `REVELAR:id2`. La pista generada se agrega al final de la cola de la misma prioridad que la pista que la originó. Una referencia inexistente, una referencia a un tipo diferente de `LETRA` o el intento de revelar una letra por segunda vez provoca un error.

El orden de procesamiento es significativo: primero se valida el archivo, luego se construye y recorre la lista, después se comprueban los delimitadores con la pila y solo entonces se procesan las pistas. Si una fase detecta un error, la ejecución se detiene y no continúa con las fases posteriores.

## 2. Organización del programa

El proyecto se distribuye en paquetes para separar responsabilidades:

- **`dominio`:** contiene la clase `Ficha` y la enumeración `TipoFicha`, que representan los datos y tipos permitidos.
- **`app`:** contiene `LectorFichas`, responsable de leer y validar el archivo, y `Main`, que coordina las dos implementaciones y compara sus resultados.
- **`manual`:** contiene `Nodo`, `ListaFichas`, `PilaSimbolos`, `ColaPistas` y `ValidadorManual`.
- **`biblioteca`:** contiene `ValidadorBiblioteca`, que realiza el mismo proceso mediante colecciones de Java.
- **`pruebas`:** contiene `CasosPrueba`, encargado de ejecutar los escenarios automatizados.

La separación permite que la lectura del archivo no dependa de la forma en que se implementan las estructuras. El lector prepara una lista de objetos `Ficha` y rechaza los registros mal formados. Los validadores reciben esa entrada y ejecutan las fases del algoritmo. La clase principal muestra ambas trazas y comprueba si coinciden.

## 3. Implementación manual de las estructuras

### 3.1 Lista enlazada

La clase `ListaFichas` administra nodos enlazados. Mantiene referencias a la cabeza y a la cola, además de un contador de elementos. Cuando se agrega la primera ficha, el nodo nuevo se convierte en cabeza y cola. En las inserciones posteriores, el nodo que estaba al final apunta al nuevo nodo y la referencia de cola se actualiza.

La lista conserva el orden de lectura del archivo. El recorrido permite mostrar las fichas desde la primera hasta la última. Para resolver una pista, el método de búsqueda comienza en la cabeza y compara el identificador de cada ficha hasta encontrarlo o llegar al final. Esta búsqueda secuencial es necesaria porque las instrucciones hacen referencia a fichas mediante su ID.

La implementación manual también utiliza una segunda lista para registrar las fichas que ya fueron reveladas. Antes de agregar una letra al mensaje, se busca su identificador en esa lista. De esta manera se impide revelar dos veces la misma ficha sin utilizar un conjunto de la biblioteca de Java en el procesamiento manual.

### 3.2 Pila de símbolos

La clase `PilaSimbolos` se construye con nodos enlazados y una referencia al tope. Cada nodo conserva la ficha de apertura completa, lo que permite recuperar su identificador y línea física si queda una apertura sin cerrar.

La operación `apilar` coloca el nuevo nodo en el tope. La operación `consultar` devuelve la ficha superior sin retirarla. La operación `retirar` elimina el nodo superior y devuelve la ficha que contenía. Estas operaciones representan el comportamiento LIFO: el último elemento que entra es el primero que sale.

Al encontrar un símbolo de apertura, el validador lo apila. Al encontrar un cierre, consulta el tope y determina cuál es la apertura esperada. Solo retira el elemento si ambos símbolos forman una pareja correcta. Por ejemplo, si se apila primero `(` y después `[`, el siguiente cierre válido debe ser `]`; no puede cerrarse el paréntesis mientras el corchete siga en el tope. Si la pila está vacía ante un cierre, o si el símbolo no coincide, se informa el error y se detiene el proceso. Al finalizar el recorrido, la pila también debe estar vacía.

### 3.3 Tres colas FIFO por prioridad

La clase genérica `ColaPistas` administra tres colas enlazadas mediante arreglos de referencias a nodos: uno para los frentes y otro para los finales. Cada prioridad tiene su propio frente y final. Al encolar, el nodo nuevo se agrega al final de la cola correspondiente. Al desencolar, se busca desde prioridad 1 hasta prioridad 3 y se retira el primer nodo disponible.

La política combina dos reglas. Entre prioridades distintas, siempre se atiende primero la prioridad numéricamente menor: P1 antes que P2 y P2 antes que P3. Dentro de una misma prioridad se conserva FIFO: la primera pista en entrar es la primera en salir. Cuando una instrucción genera otra pista, esta se agrega al final de la misma cola. Por tanto, no adelanta a las pistas que ya estaban esperando.

La clase valida que la prioridad esté entre 1 y 3. Si una cola queda vacía después de retirar su último elemento, también se actualiza su referencia final para mantener correctamente el estado de la estructura.

## 4. Implementación equivalente con java.util

La segunda implementación reproduce las mismas fases con estructuras de la biblioteca estándar. La lista de fichas se almacena en una lista de Java. La pila utiliza `Deque` con las operaciones `push`, `peek` y `pop`. Para las prioridades se crea una colección con tres colas FIFO de tipo `Queue`, implementadas mediante `LinkedList`, que utilizan `offer`, `poll` e `isEmpty`.

Se mantienen tres colas independientes en lugar de una única cola de prioridad automática. Esta decisión hace explícito el requisito de atender primero todas las pistas de P1, después las de P2 y finalmente las de P3, conservando el orden de llegada dentro de cada grupo. Una pista generada se agrega a la cola que corresponde a la prioridad de su pista de origen.

La equivalencia entre las implementaciones no significa que compartan la misma estructura interna. Significa que, con la misma entrada, producen el mismo recorrido observable, el mismo primer error, el mismo orden de eventos y el mismo mensaje. La clase `Main` elimina el encabezado específico de cada versión y compara el resto de las trazas.

## 5. Lectura y validación de archivos

La clase `LectorFichas` lee el archivo con codificación UTF-8. Primero comprueba que exista el encabezado esperado. Después divide cada registro en cinco campos, conserva los campos vacíos para detectar líneas incompletas y valida el identificador, el tipo, la prioridad, el valor y la instrucción.

Los identificadores se registran durante la lectura para detectar duplicados. La prioridad se convierte a entero y se comprueba que esté en el intervalo permitido. El valor se valida según el tipo de ficha: las aperturas y cierres solo admiten sus tres símbolos correspondientes; una ficha `LETRA` debe contener un carácter alfabético; una ficha `PISTA` debe utilizar `-`. También se comprueba la forma de las instrucciones y que los tipos que no son pista utilicen `-`.

La lectura rechaza los archivos vacíos, los encabezados incorrectos, las líneas que no contienen cinco campos y los archivos que no contienen fichas. Los errores incluyen la línea física y, cuando corresponde, el ID de la ficha. Esta validación evita que las estructuras reciban registros que no cumplen el formato definido.

Durante la validación de delimitadores, cada apertura se apila y cada cierre se compara con el tope. Si hay un cierre sin apertura, un cruce de delimitadores o una apertura sobrante, se informa el primer error. La resolución de pistas solo comienza si la pila queda vacía. En esta fase se comprueba que cada referencia exista, que corresponda a una ficha `LETRA` y que no haya sido revelada anteriormente.

## 6. Ejecución del ejemplo principal

El archivo `datos/ejemplo_valido.txt` contiene las fichas F01 a F09. Las fichas F01 y F03 abren paréntesis y corchetes, respectivamente. Más adelante F05 cierra el corchete y F07 cierra el paréntesis. La pila permite verificar el orden correcto: primero se retira `[` y después `(`. Una vez vacía la pila, el programa distribuye las pistas.

La ficha F08 pertenece a prioridad 1 y contiene `REVELAR:F02`. La ficha F09 pertenece a prioridad 2 y contiene `ENCADENAR:F04:F06`. La ejecución produce la siguiente secuencia:

| Paso | Operación | Mensaje acumulado |
|---|---|---|
| 1 | Se atiende F08 y se revela F02, cuyo valor es S. | S |
| 2 | Se atiende F09 y se revela F04, cuyo valor es O. | SO |
| 3 | F09 genera y encola `REVELAR:F06` en P2. | SO |
| 4 | Se atiende la pista generada y se revela F06, cuyo valor es L. | SOL |

El resultado final es `SOL`. La salida compartida de la ejecución muestra que ambas implementaciones procesan las mismas fichas, generan la misma pista y terminan con el mismo mensaje. La comparación final informa `TRAZAS EQUIVALENTES: mismo recorrido, primer error y resultado.`

## 7. Pruebas y cobertura

El ejecutor `pruebas.CasosPrueba` contiene 14 escenarios. Dos utilizan los archivos persistentes de ejemplo y de tres prioridades. Los demás construyen archivos temporales para provocar entradas inválidas o comprobar comportamientos concretos. Los temporales se eliminan al finalizar cada prueba.

| N.º | Caso | Comprobación |
|---:|---|---|
| 1 | Ejemplo válido | Resultado `SOL` y trazas equivalentes. |
| 2 | Tres prioridades | Resultado `SOL` y atención P1 → P2 → P3. |
| 3 | Cierre incorrecto | Se detecta el cruce de delimitadores. |
| 4 | Cierre sin apertura | Se rechaza el cierre que no tiene apertura. |
| 5 | Apertura sobrante | Se detecta la apertura que queda en la pila. |
| 6 | ID duplicado | El lector rechaza el identificador repetido. |
| 7 | Línea incompleta | El lector exige cinco campos. |
| 8 | Tipo inválido | Se rechaza un tipo no permitido. |
| 9 | Prioridad fuera de rango | Se rechaza una prioridad distinta de 1, 2 o 3. |
| 10 | Referencia inexistente | La pista informa que el ID no existe. |
| 11 | Referencia que no es LETRA | La pista rechaza una referencia de otro tipo. |
| 12 | Letra revelada dos veces | Se evita agregar dos veces la misma ficha al mensaje. |
| 13 | FIFO de igual prioridad | Se conserva el orden de llegada. |
| 14 | Encadenamiento con pista pendiente | La pista generada se atiende detrás de la que ya esperaba. |

En los casos de ejecución se comprueba el resultado o error esperado y se comparan las trazas de ambas implementaciones. En los casos de formato inválido se comprueba que el lector rechace el archivo antes de ejecutar las estructuras.

## 8. Compilación y reproducibilidad

La verificación se realizó desde PowerShell en la carpeta raíz del proyecto. Para compilar el programa integrado se utiliza:

```powershell
New-Item -ItemType Directory -Force out | Out-Null
$archivos = Get-ChildItem -Recurse src -Filter *.java | ForEach-Object { $_.FullName }
javac -encoding UTF-8 -d out $archivos
```

La ejecución de la suite se realiza con:

```powershell
java -cp out pruebas.CasosPrueba
```

El resultado final comunicado fue:

```text
Pruebas terminadas: 14/14 correctas.
```

Para ejecutar el ejemplo principal:

```powershell
java -cp out app.Main datos/ejemplo_valido.txt
```

La salida compartida contiene nueve fichas leídas correctamente, la traza manual, la traza de `java.util`, el resultado `SOL` en ambas y la comparación de trazas equivalentes. También se ejecutaron los archivos de prueba de prioridades y cierre incorrecto. La compilación manual y la compilación de la versión con biblioteca se realizaron de forma independiente para obtener sus respectivas evidencias.

Los directorios `out`, `out-manual` y `out-javautil` almacenan archivos compilados `.class`. Pueden eliminarse y regenerarse con los comandos de compilación; por eso se excluyen del control de versiones.

## 9. Evidencias de ejecución

Las capturas se encuentran incorporadas en `evidencias/`. El archivo `evidencias/README.md` contiene el índice de imágenes, los comandos principales y la explicación de lo que demuestra cada grupo. Algunas ejecuciones se dividieron en dos imágenes para conservar la legibilidad de la salida completa.

El conjunto de evidencias cubre los requisitos solicitados:

1. Compilación de la implementación manual.
2. Compilación de la implementación con biblioteca.
3. Recorrido de la lista.
4. Operaciones de la pila.
5. Orden de atención de las colas por prioridad.
6. Mensaje final `SOL` y procesamiento de la pista generada.
7. Error de delimitadores en F05 y detención del procesamiento.
8. Comparación de las dos implementaciones.

La evidencia 07 muestra tanto el encadenamiento como la comparación final. En ella se observa la atención de F09, la generación y encolado de `REVELAR:F06`, la atención de la pista generada, el resultado `SOL` y el mensaje de trazas equivalentes. Por tanto, no se requiere una captura adicional para la comparación.

Las imágenes son registros de las ejecuciones realizadas en la terminal. No se han generado capturas simuladas. El índice permite localizar cada archivo y relacionarlo con el requisito correspondiente.

## 10. Conclusiones

La solución permite observar cómo distintas estructuras resuelven responsabilidades diferentes dentro de un mismo algoritmo. La lista enlazada conserva el orden de las fichas y permite buscarlas por ID. La pila valida delimitadores mediante el comportamiento LIFO. Las tres colas aplican prioridad entre grupos y FIFO dentro de cada grupo. El encadenamiento demuestra que una operación puede generar trabajo nuevo sin alterar el orden de llegada de las pistas que ya estaban pendientes.

La implementación con `java.util` sirve como contraste funcional de la versión manual. La comparación de trazas permite comprobar que ambas procesan la misma entrada y producen los mismos eventos observables, errores y resultados. La suite automatizada cubre entradas válidas, errores de formato, delimitadores, referencias, repetición de letras, prioridades y encadenamiento.

La verificación local comunicada el 12 de noviembre de 2026 registró compilación completa sin errores, 14 de 14 pruebas correctas y el mensaje `SOL` en ambas implementaciones con trazas equivalentes. El repositorio `main` contiene el código fuente, los archivos de datos, el informe y las capturas.

Para la sustentación individual es importante poder explicar con claridad la diferencia entre LIFO y FIFO, el motivo de utilizar tres colas, la búsqueda por identificador, la validación previa de delimitadores y el proceso mediante el cual una pista `ENCADENAR` crea y agrega otra pista a la misma prioridad.

---

## Referencia

Enunciado del parcial individual **“La bóveda de los patrones”**, documento de seis páginas suministrado por el profesor. La solución, las reglas de entrada y los casos de prueba se desarrollaron tomando como referencia sus requisitos de implementación, validación y entrega.
