# Bóveda de Fichas Java

Programa académico en Java que lee fichas desde un archivo UTF-8, valida delimitadores correctamente anidados y descifra un mensaje mediante pistas con tres prioridades.

## Estado del proyecto

La rama desarrollo contiene la implementación de las estructuras manuales y la versión equivalente con java.util. Se conserva el modelo base del profesor: Ficha, TipoFicha y Nodo. Se completan las clases y métodos que estaban pendientes en la plantilla.

## Requisitos

- JDK 17 o superior.
- Visual Studio Code o cualquier editor Java.
- Git.

## Estructura

- src/app: lectura del archivo y punto de entrada.
- src/dominio: modelo de ficha y tipos permitidos.
- src/manual: lista enlazada, pila, tres colas FIFO y validador propio.
- src/biblioteca: implementación equivalente con java.util.
- src/pruebas: pruebas reproducibles.
- datos: archivos de entrada válidos e inválidos.
- docs: plan de trabajo.
- informe: informe final.
- evidencias: capturas reales de compilación y ejecución.

## Compilar en Windows PowerShell

Ejecutar desde la raíz del repositorio:

    New-Item -ItemType Directory -Force out | Out-Null
    $files = Get-ChildItem -Recurse src -Filter *.java | ForEach-Object { $_.FullName }
    javac -encoding UTF-8 -d out $files

## Ejecutar ambas versiones y comparar trazas

    java -cp out app.Main datos/ejemplo_valido.txt

El ejemplo debe producir SOL. El programa muestra el recorrido de la lista, operaciones de pila, orden de atención de las colas, pistas generadas y comparación de trazas.

## Ejecutar pruebas básicas

    java -cp out pruebas.CasosPrueba

## Formato del archivo

La primera línea debe ser:

    id|tipo|valor|prioridad|instruccion

Cada fila tiene cinco campos separados por |:

- id: identificador único.
- tipo: ABRE, CIERRA, LETRA o PISTA.
- valor: para ABRE/CIERRA uno de los delimitadores; para LETRA una letra; para PISTA el guion -.
- prioridad: 1, 2 o 3.
- instruccion: REVELAR:id o ENCADENAR:id1:id2 para PISTA; - para los demás tipos.

## Reglas del algoritmo

1. Leer y validar encabezado, campos, tipos, valores, prioridades, instrucciones e IDs duplicados.
2. Insertar las fichas en una lista enlazada propia y conservar el orden de lectura.
3. Recorrer la lista: apilar aperturas y retirar únicamente cuando el cierre corresponda con el tope.
4. Si los delimitadores son válidos, distribuir pistas en tres colas FIFO: prioridad 1, luego 2 y luego 3.
5. REVELAR:id agrega una letra al mensaje una sola vez.
6. ENCADENAR:id1:id2 revela primero id1 y agrega REVELAR:id2 al final de la misma cola.
7. Ante el primer error se detiene el proceso y se conserva la traza previa.

## Flujo Git

- main: versión estable.
- desarrollo: integración del trabajo.
- Los cambios se prueban en desarrollo antes de integrarse a main.

## Evidencias pendientes

Las capturas deben corresponder a ejecuciones reales del computador. No se deben presentar capturas simuladas ni afirmar que una prueba pasó sin ejecutarla.
