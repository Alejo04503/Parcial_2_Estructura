# Bóveda de Fichas Java

Programa académico en Java que lee un archivo UTF-8 de fichas, valida el anidamiento de delimitadores y descifra un mensaje a partir de pistas.

> Estado actual: plantilla inicial. Los métodos marcados con `TODO` todavía deben implementarse.

## Requisitos

- JDK instalado (incluye `java` y `javac`).
- Visual Studio Code, recomendado para editar el proyecto.
- Git para controlar versiones y sincronizar con GitHub.

## Estructura

- `src/app`: punto de entrada y lectura del archivo.
- `src/dominio`: modelo de ficha y tipos permitidos.
- `src/manual`: implementaciones propias de lista, pila y cola, y su validador.
- `src/biblioteca`: versión equivalente usando `java.util`.
- `src/pruebas`: pruebas reproducibles.
- `datos`: archivos de entrada válidos e inválidos.
- `evidencias`: capturas reales de compilación y ejecución.
- `informe`: informe final de 6 a 8 páginas.
- `docs`: diseño y plan de trabajo.

## Compilar (PowerShell, Windows)

Desde la raíz del repositorio:

```powershell
New-Item -ItemType Directory -Force out | Out-Null
$files = Get-ChildItem -Recurse src -Filter *.java | ForEach-Object { $_.FullName }
javac -encoding UTF-8 -d out $files
```

## Ejecutar

Cuando se complete el punto de entrada y el algoritmo, el ejemplo válido se ejecutará así:

```powershell
java -cp out app.Main datos/ejemplo_valido.txt
```

El ejemplo válido debe producir el mensaje `SOL`. La salida exacta y las trazas se documentarán al implementar el algoritmo.

## Reglas centrales

1. Leer el encabezado y validar cinco campos por ficha.
2. Mantener identificadores únicos y validar tipo, valor, prioridad e instrucción.
3. Recorrer las fichas en orden usando una pila para comprobar los delimitadores.
4. Si la validación de delimitadores falla, no procesar pistas.
5. Procesar pistas en prioridad 1, luego 2 y luego 3; conservar FIFO dentro de cada prioridad.
6. `REVELAR:id` añade una letra una sola vez. `ENCADENAR:id1:id2` añade la letra de `id1` y crea una pista `REVELAR:id2` al final de la cola de la misma prioridad.
7. Informar el primer error según las fases y el orden de procesamiento, conservando la traza anterior al error.
8. La implementación manual y la versión con `java.util` deben ser equivalentes.

## Flujo Git

- `main`: versión estable y comprobada.
- Trabajar cada tarea en una rama `feature/...`.
- Compilar y probar antes de confirmar cambios.
- Subir la rama, revisar los cambios y crear un Pull Request hacia `main`.

## Entrega

El repositorio debe contener código fuente, datos de prueba, README, informe y evidencias reales. No se deben inventar capturas ni afirmar que una prueba pasó si no se ejecutó.
