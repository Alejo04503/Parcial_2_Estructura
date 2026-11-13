# Evidencias de ejecución

Las capturas de esta carpeta corresponden a ejecuciones realizadas desde PowerShell en el proyecto. Se conservan las imágenes originales para que el comando y la salida puedan leerse.

| Evidencia | Archivo(s) | Qué demuestra |
|---|---|---|
| 01. Compilación manual | `01_compilacion_manual.png` | Compilación de las clases de dominio y estructuras manuales. |
| 02. Compilación con biblioteca | `02_compilacion_javautil.png` | Compilación de la implementación basada en `java.util`. |
| 03. Lista, pila y colas | `03_lista_pila_colas_resultad(1).png`, `03_lista_pila_colas_resltado(2).png` | Recorrido de la lista, operaciones de pila y ejecución de las estructuras. Son dos partes de la evidencia. |
| 04. Pruebas automatizadas | `04_pruebas_automatizadas.png` | Ejecución de la suite completa: 14/14 casos correctos. |
| 05. Primer error | `05_deteccion_primer_error.png` | Detección del cierre incorrecto en F05 y detención de las fases posteriores. |
| 06. Prioridades FIFO | `06_prioridades_fifo(1).png`, `06_prioridades_fifo(2).png` | Atención de pistas en orden P1, P2 y P3, conservando FIFO. |
| 07. Encadenamiento y comparación | `07_encadenamiento_y_comparacion(1).png`, `07_encadenamiento_y_comparacion(2).png` | Procesamiento de `ENCADENAR`, generación y atención de la pista nueva, resultado `SOL` y comparación de trazas equivalentes. |

## Comandos principales

Compilación integrada:

```powershell
$archivos = Get-ChildItem -Recurse src -Filter *.java | ForEach-Object { $_.FullName }
javac -encoding UTF-8 -d out $archivos
```

Suite de pruebas:

```powershell
java -cp out pruebas.CasosPrueba
```

Ejemplo válido, que también muestra el encadenamiento y la comparación:

```powershell
java -cp out app.Main datos/ejemplo_valido.txt
```

Error de delimitadores:

```powershell
java -cp out app.Main datos/pruebas/cierre_incorrecto.txt
```

Prueba de prioridades:

```powershell
java -cp out app.Main datos/pruebas/tres_prioridades.txt
```

## Nota de lectura

Algunas evidencias están divididas en dos imágenes para conservar el texto legible. Los sufijos `(1)` y `(2)` identifican las partes de una misma ejecución. La evidencia 07 cubre tanto el encadenamiento como la comparación final entre implementaciones; no hace falta duplicarla.
