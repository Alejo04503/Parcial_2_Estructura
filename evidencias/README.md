# Evidencias de ejecución

Las capturas de esta carpeta corresponden a ejecuciones realizadas desde PowerShell en la carpeta raíz del proyecto. Se conservan las imágenes originales. Algunas ejecuciones están divididas en dos capturas para que la salida siga siendo legible.

## Evidencia 01. Compilación manual

**Archivo:** `01_compilacion_manual.png`

- **Objetivo:** demostrar que las clases de dominio y las estructuras desarrolladas manualmente compilan de forma independiente.
- **Comando:**

```powershell
New-Item -ItemType Directory -Force out-manual | Out-Null
$archivos = @("src/dominio/Ficha.java", "src/dominio/TipoFicha.java") + (Get-ChildItem src/manual -Filter *.java | ForEach-Object { $_.FullName })
javac -encoding UTF-8 -d out-manual $archivos
```

- **Resultado:** compilación sin errores.
- **Explicación:** se compila el modelo de dominio junto con las clases del paquete `manual`, sin depender de la implementación con biblioteca.

## Evidencia 02. Compilación con java.util

**Archivo:** `02_compilacion_javautil.png`

- **Objetivo:** demostrar que la implementación basada en las colecciones de Java compila de forma independiente.
- **Comando:**

```powershell
New-Item -ItemType Directory -Force out-javautil | Out-Null
$archivos = @("src/dominio/Ficha.java", "src/dominio/TipoFicha.java") + (Get-ChildItem src/biblioteca -Filter *.java | ForEach-Object { $_.FullName })
javac -encoding UTF-8 -d out-javautil $archivos
```

- **Resultado:** compilación sin errores.
- **Explicación:** se compilan el dominio y el validador del paquete `biblioteca`, que utiliza estructuras de `java.util`.

## Evidencia 03. Recorrido de lista, operaciones de pila y estructuras

**Archivos:** `03_lista_pila_colas_resultad(1).png` y `03_lista_pila_colas_resltado(2).png`

- **Objetivo:** mostrar el recorrido de las fichas y las operaciones principales de las estructuras durante la ejecución.
- **Comando:**

```powershell
java -cp out app.Main datos/ejemplo_valido.txt
```

- **Resultado:** se muestra la lista F01 a F09; la pila apila, consulta y retira delimitadores hasta quedar vacía; las pistas se encolan y atienden por prioridad.
- **Explicación:** las dos capturas son partes de la misma ejecución y permiten observar la lista enlazada, el comportamiento LIFO de la pila y el flujo de atención de pistas.

## Evidencia 04. Pruebas automatizadas

**Archivo:** `04_pruebas_automatizadas.png`

- **Objetivo:** comprobar los escenarios válidos, inválidos y los comportamientos de prioridad y encadenamiento.
- **Comando:**

```powershell
java -cp out pruebas.CasosPrueba
```

- **Resultado:** 14/14 pruebas correctas.
- **Explicación:** el ejecutor verifica los resultados esperados y, en los casos de ejecución, compara las trazas de ambas implementaciones.

## Evidencia 05. Detección del primer error

**Archivo:** `05_deteccion_primer_error.png`

- **Objetivo:** demostrar que un delimitador incorrecto se detecta antes de procesar las pistas.
- **Comando:**

```powershell
java -cp out app.Main datos/pruebas/cierre_incorrecto.txt
```

- **Resultado:** error en la línea física 6, ficha F05, porque se esperaba `]` mientras `[` estaba en el tope.
- **Explicación:** ambas versiones detectan el mismo primer error y detienen el procesamiento antes de atender pistas.

## Evidencia 06. Prioridades y orden FIFO

**Archivos:** `06_prioridades_fifo(1).png` y `06_prioridades_fifo(2).png`

- **Objetivo:** comprobar que las pistas se atienden por prioridad P1, P2 y P3 aunque aparezcan en otro orden en el archivo.
- **Comando:**

```powershell
java -cp out app.Main datos/pruebas/tres_prioridades.txt
```

- **Resultado:** se atienden primero F10 (P1), después F09 (P2) y finalmente F08 (P3); el mensaje obtenido es `SOL`.
- **Explicación:** las capturas muestran la distribución inicial y el orden real de atención. La suite incluye además un caso que comprueba FIFO entre pistas de una misma prioridad.

## Evidencia 07. Encadenamiento y comparación de versiones

**Archivos:** `07_encadenamiento_y_comparacion(1).png` y `07_encadenamiento_y_comparacion(2).png`

- **Objetivo:** demostrar que una pista puede generar otra pista en la misma cola y que ambas implementaciones producen trazas equivalentes.
- **Comando:**

```powershell
java -cp out app.Main datos/ejemplo_valido.txt
```

- **Resultado:** F09 ejecuta `ENCADENAR:F04:F06`, genera y encola `REVELAR:F06`, se atiende la pista generada, ambas versiones producen `SOL` y la comparación informa `TRAZAS EQUIVALENTES`.
- **Explicación:** esta evidencia cubre el encadenamiento y la comparación final. No se necesita una captura adicional para la equivalencia.

## Relación con los requisitos de entrega

El conjunto documenta las ocho categorías solicitadas: compilación manual, compilación con biblioteca, recorrido de lista, operaciones de pila, orden de colas, mensaje `SOL`, error de F05 y comparación de versiones. Las evidencias 03 y 07 agrupan varias observaciones de una misma ejecución; por eso algunas categorías aparecen en una misma captura o en dos partes.

Los sufijos `(1)` y `(2)` identifican partes de una evidencia dividida. Los nombres de archivo se conservan exactamente como están versionados.
