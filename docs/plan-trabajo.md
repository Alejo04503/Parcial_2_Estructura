# Plan de trabajo

## Fase 1. Preparación y diseño
- [ ] Crear repositorio individual en GitHub.
- [ ] Subir la plantilla original sin borrar los `TODO`.
- [ ] Leer el enunciado y explicar las reglas con palabras propias.
- [ ] Dibujar el flujo: lectura → lista → pila → colas → mensaje.
- [ ] Escribir pseudocódigo y definir cómo se reporta el primer error.

## Fase 2. Estructuras manuales
- [ ] Implementar y probar `ListaFichas`.
- [ ] Implementar y probar `PilaSimbolos`.
- [ ] Implementar y probar `ColaPistas` con prioridades 1, 2 y 3 y FIFO.

## Fase 3. Lector y validador manual
- [ ] Validar encabezado, campos, tipos, valores, prioridad e instrucciones.
- [ ] Detectar identificadores repetidos y referencias inválidas.
- [ ] Validar anidamiento y conservar la primera falla.
- [ ] Procesar `REVELAR` y `ENCADENAR`, incluyendo pistas generadas.
- [ ] Registrar eventos en la traza.

## Fase 4. Versión java.util
- [ ] Implementar el mismo comportamiento con estructuras de biblioteca.
- [ ] Ejecutar las mismas entradas en ambas versiones.
- [ ] Comparar mensaje, primer error y orden de eventos.

## Fase 5. Pruebas y entrega
- [ ] Probar todas las entradas mínimas del enunciado.
- [ ] Añadir casos de frontera y regresión.
- [ ] Guardar capturas reales con objetivo, comando, resultado y explicación.
- [ ] Redactar informe de 6 a 8 páginas.
- [ ] Practicar la sustentación individual y una variación sorpresa.
