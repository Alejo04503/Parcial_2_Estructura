# Bóveda de Fichas Java

**Proyecto individual de Estructuras de Datos**  
**Lenguaje:** Java  
**Ramas de trabajo:** `main` (base estable) y `desarrollo` (implementación)

## 1. Descripción

El programa lee un archivo de fichas de texto UTF-8, valida que los delimitadores `()`, `[]` y `{}` estén correctamente anidados y, si la validación termina sin errores, descifra un mensaje mediante pistas organizadas en tres prioridades.

Se entregan dos implementaciones equivalentes:

- **Manual:** lista enlazada, pila y tres colas FIFO desarrolladas para el proyecto.
- **Java Collections (`java.util`):** lista, pila y colas implementadas con las estructuras de biblioteca.

Las dos versiones reciben las mismas fichas y generan trazas para comparar el mensaje, el primer error y el orden de los eventos observables.

## 2. Requisitos

- JDK 17 o superior.
- Git.
- Visual Studio Code, NetBeans u otro entorno Java.
- Terminal PowerShell o CMD.

No se necesita instalar dependencias externas.

## 3. Estructura del repositorio

| Ruta | Contenido |
|---|---|
| `src/dominio` | Modelo `Ficha` y enumeración `TipoFicha` conservados de la base del profesor. |
| `src/manual` | Nodo, lista enlazada, pila, colas por prioridad y validador manual. |
| `src/biblioteca` | Validador equivalente con `java.util`. |
| `src/app` | Lector del archivo y clase principal. |
| `src/pruebas` | Ejecutor de casos de prueba. |
| `datos` | Archivo de ejemplo y archivos de prueba. |
| `docs` | Plan de trabajo. |
| `informe` | Informe del proyecto. |
| `evidencias` | Capturas reales de compilación y ejecución. |

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
| `valor` | `ABRE`: `(`, `[` o `{`; `CIERRA`: `)`, `]` o `}`; `LETRA`: una letra; `PISTA`: `-`. |
| `prioridad` | Entero 1, 2 o 3. La prioridad se utiliza para las fichas `PISTA`. |
| `instruccion` | Para `PISTA`: `REVELAR:id` o `ENCADENAR:id1:id2`. Para otros tipos: `-`. |

La posición corresponde al orden de lectura de la ficha, empezando en 1. La línea física es el número de línea real dentro del archivo, contando el encabezado.

### Significado de las instrucciones

- `REVELAR:id`: busca la ficha indicada y agrega su letra al mensaje.
- `ENCADENAR:id1:id2`: revela primero la letra de `id1` y crea una nueva pista `REVELAR:id2`, que se agrega al final de la cola de la misma prioridad.
- Cada ficha de tipo `LETRA` solo puede revelarse una vez.
- Una referencia inexistente o que no apunte a una ficha `LETRA` produce un error.

## 5. Flujo del algoritmo

1. **Lectura y formato.** Se comprueban encabezado, cantidad de campos, identificadores, tipo, valor, prioridad e instrucción. Se conserva posición y línea física.
2. **Lista enlazada manual.** Se insertan todas las fichas en orden de archivo. Se muestra el recorrido y las búsquedas de letras se realizan por ID.
3. **Validación con pila.** Cada apertura se apila. Al encontrar un cierre, se consulta el tope y solo se retira si es la pareja correcta. La pila debe terminar vacía.
4. **Distribución de pistas.** Solo si la pila es válida, se encolan las pistas en tres colas FIFO independientes.
5. **Resolución.** Se atiende primero prioridad 1, después 2 y después 3. Dentro de cada prioridad se conserva FIFO. Las pistas generadas se agregan al final de su cola.
6. **Comparación.** Se ejecutan las versiones manual y `java.util` sobre la misma entrada y se comparan sus trazas.

El primer error detiene las fases posteriores y conserva los eventos producidos hasta ese momento.

## 6. Compilación en Windows

Abre PowerShell en la carpeta raíz del repositorio.

### Compilar las dos implementaciones

```powershell
New-Item -ItemType Directory -Force out | Out-Null
$archivos = Get-ChildItem -Recurse src -Filter *.java | ForEach-Object { $_.FullName }
javac -encoding UTF-8 -d out $archivos
```

Si el comando termina sin mostrar errores, la compilación fue correcta. Si aparecen mensajes, conserva la salida completa para identificar el archivo y la línea que fallaron.

### Ejecutar el ejemplo principal

```powershell
java -cp out app.Main datos/ejemplo_valido.txt
```

El ejemplo debe producir el mensaje `SOL`. El programa muestra primero la traza manual, después la traza `java.util` y finalmente el resultado de compararlas.

### Ejecutar la suite de pruebas

```powershell
java -cp out pruebas.CasosPrueba
```

El ejecutor contiene 14 casos que cubren entradas válidas, errores de delimitadores y formato, referencias de pistas, prevención de letras repetidas, orden FIFO y encadenamiento. En la verificación local del 9 de noviembre de 2026, los 14 casos finalizaron correctamente (`14/14`).

## 7. Archivos de prueba disponibles

| Archivo | Propósito | Resultado esperado según el diseño |
|---|---|---|
| `datos/ejemplo_valido.txt` | Ejemplo completo con pista encadenada. | Mensaje `SOL`. |
| `datos/pruebas/tres_prioridades.txt` | Comprueba atención P1 → P2 → P3 aunque el orden físico sea distinto. | Mensaje `SOL`. |
| `datos/pruebas/cierre_incorrecto.txt` | Cambia el cierre de F05 para provocar un cruce de delimitadores. | Error en línea física 6, ficha F05; no se ejecutan pistas. |

Los resultados de esta tabla son expectativas del caso de prueba. La aprobación efectiva debe registrarse después de ejecutar el programa.

## 8. Cobertura de pruebas

La suite `pruebas.CasosPrueba` ejecuta y comprueba los siguientes escenarios:

- Cierre sin apertura y apertura sobrante.
- ID duplicado, línea incompleta, tipo inválido y prioridad fuera del rango 1–3.
- Referencia inexistente y referencia a una ficha que no sea `LETRA`.
- Intento de revelar dos veces la misma letra.
- Dos pistas de una misma prioridad para comprobar FIFO.
- Una pista `ENCADENAR` que agregue una nueva pista a una cola que todavía contiene elementos.
- Comparación del mensaje, primer error y orden de eventos entre las dos versiones.

**Resultado local registrado el 9 de noviembre de 2026:** 14/14 pruebas correctas. También se compiló el proyecto sin errores y se ejecutó `app.Main` con `datos/ejemplo_valido.txt`; ambas versiones produjeron `SOL` y la comparación indicó `TRAZAS EQUIVALENTES`.

## 9. Evidencias de entrega

Guarda las capturas originales en `evidencias/`. Cada evidencia debe incluir objetivo, comando, resultado visible y una explicación breve. Se requieren capturas legibles de:

1. Compilación de la implementación manual.
2. Compilación de la implementación con biblioteca.
3. Recorrido de la lista.
4. Operaciones de la pila.
5. Orden de atención de las tres colas.
6. Mensaje final `SOL` y pista generada.
7. Error de F05 y confirmación de que las pistas no se procesaron.
8. Comparación de ambas implementaciones.

No se deben crear capturas simuladas ni registrar como exitosa una prueba que no se haya ejecutado.

## 10. Estado del proyecto

La rama `desarrollo` contiene ambas implementaciones y la suite ampliada. En la verificación local del 9 de noviembre de 2026, la compilación terminó sin errores, las 14 pruebas finalizaron correctamente y el ejemplo principal produjo `SOL` en ambas versiones con trazas equivalentes.

**Pendiente:** guardar las capturas reales en `evidencias/`, revisar la paginación final del informe y realizar la sustentación. No se deben presentar capturas simuladas ni afirmar que las evidencias gráficas están adjuntas si todavía no se han guardado.

Consulta `informe/Informe_Final.md` para el detalle técnico y el registro de validación.
