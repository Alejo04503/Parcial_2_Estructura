# Bóveda de Fichas Java

**Proyecto individual de Estructuras de Datos**  
**Lenguaje:** Java  
**Rama de entrega:** `main`  
**Rama de trabajo:** `desarrollo`  
**Estado:** implementación, pruebas y evidencias incorporadas al repositorio.

## 1. Descripción

El programa lee un archivo de fichas de texto UTF-8, valida que los delimitadores `()`, `[]` y `{}` estén correctamente anidados y, si la validación termina sin errores, descifra un mensaje mediante pistas organizadas en tres prioridades.

Se entregan dos implementaciones equivalentes:

- **Manual:** lista enlazada, pila y tres colas FIFO desarrolladas para el proyecto mediante nodos propios.
- **Java Collections (`java.util`):** lista, pila y colas implementadas con estructuras de biblioteca.

Las dos versiones reciben las mismas fichas y producen trazas comparables. La comparación verifica el recorrido, el primer error y el resultado.

## 2. Requisitos

- JDK 17 o superior.
- Git.
- Visual Studio Code, NetBeans u otro entorno Java.
- Terminal PowerShell o CMD.

No se necesitan dependencias externas.

## 3. Estructura del repositorio

| Ruta | Contenido |
|---|---|
| `src/dominio` | Modelo `Ficha` y enumeración `TipoFicha`. |
| `src/manual` | Nodo, lista enlazada, pila, colas por prioridad y validador manual. |
| `src/biblioteca` | Validador equivalente con colecciones `java.util`. |
| `src/app` | Lector del archivo y clase principal. |
| `src/pruebas` | Ejecutor de los 14 casos de prueba. |
| `datos` | Archivo de ejemplo y archivos de prueba persistentes. |
| `informe` | Informe técnico del proyecto. |
| `evidencias` | Capturas de compilación, ejecución y validación. |

## 4. Formato del archivo de entrada

La primera línea debe ser exactamente:

```text
id|tipo|valor|prioridad|instruccion
```

Cada línea posterior contiene cinco campos separados por `|`:

| Campo | Regla |
|---|---|
| `id` | Identificador no vacío y único. |
| `tipo` | `ABRE`, `CIERRA`, `LETRA` o `PISTA`. |
| `valor` | Para `ABRE`: `(`, `[` o `{`; para `CIERRA`: `)`, `]` o `}`; para `LETRA`: una letra; para `PISTA`: `-`. |
| `prioridad` | Entero entre 1 y 3. |
| `instruccion` | Para `PISTA`: `REVELAR:id` o `ENCADENAR:id1:id2`. Para otros tipos: `-`. |

La posición se cuenta desde 1 en el orden de lectura. La línea física corresponde al número real de línea del archivo, incluido el encabezado.

### Instrucciones de las pistas

- `REVELAR:id`: busca la ficha indicada y agrega su letra al mensaje.
- `ENCADENAR:id1:id2`: revela primero la letra de `id1` y genera una nueva pista `REVELAR:id2`, que se agrega al final de la cola de la misma prioridad.
- Una ficha de tipo `LETRA` solo puede revelarse una vez.
- Una referencia inexistente o que no apunte a una ficha `LETRA` produce un error.

## 5. Flujo del algoritmo

1. **Lectura y formato:** se comprueban encabezado, campos, identificadores, tipos, valores, prioridades e instrucciones.
2. **Lista enlazada:** se insertan las fichas en el orden del archivo y se muestra el recorrido.
3. **Pila:** se validan los delimitadores con comportamiento LIFO. Solo se retira una apertura cuando coincide con el cierre esperado.
4. **Colas:** si los delimitadores son correctos, las pistas se distribuyen en tres colas FIFO.
5. **Resolución:** se atiende primero prioridad 1, luego 2 y finalmente 3. Las pistas generadas se agregan al final de su cola.
6. **Comparación:** se ejecutan ambas implementaciones con la misma entrada y se comparan sus trazas.

El procesamiento se detiene ante el primer error. Si la lectura o la validación de delimitadores falla, no se resuelven las pistas.

## 6. Compilación en Windows

Abre PowerShell en la carpeta raíz del repositorio.

### Compilación manual

```powershell
New-Item -ItemType Directory -Force out-manual | Out-Null
$archivos = @("src/dominio/Ficha.java", "src/dominio/TipoFicha.java") + (Get-ChildItem src/manual -Filter *.java | ForEach-Object { $_.FullName })
javac -encoding UTF-8 -d out-manual $archivos
```

### Compilación con java.util

```powershell
New-Item -ItemType Directory -Force out-javautil | Out-Null
$archivos = @("src/dominio/Ficha.java", "src/dominio/TipoFicha.java") + (Get-ChildItem src/biblioteca -Filter *.java | ForEach-Object { $_.FullName })
javac -encoding UTF-8 -d out-javautil $archivos
```

### Compilación integrada

```powershell
New-Item -ItemType Directory -Force out | Out-Null
$archivos = Get-ChildItem -Recurse src -Filter *.java | ForEach-Object { $_.FullName }
javac -encoding UTF-8 -d out $archivos
```

Los directorios `out`, `out-manual` y `out-javautil` contienen archivos compilados `.class`. Son generables y están excluidos del repositorio mediante `.gitignore`; no forman parte de la entrega del código fuente.

### Ejecutar el ejemplo

```powershell
java -cp out app.Main datos/ejemplo_valido.txt
```

Resultado esperado: `SOL` en ambas versiones y el mensaje `TRAZAS EQUIVALENTES`.

### Ejecutar las pruebas

```powershell
java -cp out pruebas.CasosPrueba
```

La suite contiene 14 escenarios. En la verificación local realizada el 12 de noviembre de 2026, el resultado comunicado fue **14/14 pruebas correctas**.

## 7. Archivos de prueba

| Archivo | Propósito | Resultado esperado |
|---|---|---|
| `datos/ejemplo_valido.txt` | Ejemplo completo con pista encadenada. | Mensaje `SOL`. |
| `datos/pruebas/tres_prioridades.txt` | Comprueba atención P1 → P2 → P3 aunque el orden físico sea distinto. | Mensaje `SOL`. |
| `datos/pruebas/cierre_incorrecto.txt` | Provoca un cruce de delimitadores en F05. | Error en línea física 6, ficha F05; no se atienden pistas. |

La suite automatizada genera archivos temporales para los casos adicionales y los elimina al terminar.

## 8. Cobertura de pruebas

Los 14 casos incluidos comprueban:

1. Ejemplo válido: resultado `SOL` y trazas equivalentes.
2. Tres prioridades: resultado `SOL` y orden P1 → P2 → P3.
3. Cierre incorrecto.
4. Cierre sin apertura.
5. Apertura sobrante.
6. Identificador duplicado.
7. Línea incompleta.
8. Tipo inválido.
9. Prioridad fuera del rango 1–3.
10. Referencia inexistente.
11. Referencia que no corresponde a una ficha `LETRA`.
12. Intento de revelar una letra dos veces.
13. FIFO entre pistas de igual prioridad.
14. Encadenamiento detrás de una pista que ya estaba pendiente.

Los casos de ejecución comparan el comportamiento de ambas versiones. Los casos de formato inválido verifican que el lector rechace la entrada antes de iniciar el procesamiento.

## 9. Evidencias

Las capturas se encuentran en `evidencias/`. El archivo `evidencias/README.md` identifica cada imagen y explica qué requisito demuestra.

El conjunto documenta compilaciones, recorrido de estructuras, suite automatizada, error de delimitadores, prioridades, encadenamiento y comparación de las dos implementaciones. Las capturas muestran ejecuciones realizadas en la terminal; no son imágenes simuladas.

## 10. Estado de entrega

La rama `main` contiene la versión de entrega. La rama `desarrollo` conserva la rama de trabajo. El código fuente, los datos, el informe y las capturas están versionados en el repositorio.

La verificación local comunicada el 12 de noviembre de 2026 incluyó compilación completa sin errores, ejecución del ejemplo principal con resultado `SOL`, comparación de trazas equivalentes y 14/14 pruebas automatizadas correctas.

El informe técnico final se encuentra en `informe/Informe_Final.pdf`. Contiene la descripción del diseño, las estructuras implementadas, las pruebas realizadas y sus resultados. Para la sustentación individual, explica la lista enlazada, el comportamiento LIFO de la pila, el FIFO de cada cola, la selección de prioridades y la generación de pistas.

Consulta el informe técnico para el detalle del diseño, las decisiones de implementación, las pruebas y sus resultados.
