# Contexto Automático del Agente - Conversaciones Naturales

**Fecha**: 2025-01-15

## Resumen

Se implementó un sistema de contexto automático que permite al agente de IA mantener conversaciones naturales sin solicitar manualmente el `userId` del usuario. Esto elimina la fricción en las interacciones y permite que el agente registre información de forma transparente.

## Problema Original

Antes de esta mejora:
- El agente solicitaba al usuario su ID cada vez que quería registrar información
- Las herramientas `log_mood_snapshot` y `record_user_fact` requerían `userId` como parámetro obligatorio
- Las conversaciones no eran naturales: _"Para registrar tu estado de ánimo, ¿cuál es tu ID de usuario?"_

## Solución Implementada

### 1. Cliente Android

**Archivo**: `app/src/main/java/com/example/cliente/data/repository/AgentRepository.kt`

- `AgentRepository` ahora inyecta `UserPreferences` como dependencia
- Lee automáticamente el `userId` del DataStore antes de enviar cada mensaje
- Construye `AgentRequest` con `userId` y `context` automáticos:

```kotlin
val userId = userPreferences.userId.firstOrNull()?.toIntOrNull()
val response = apiService.chat(
    AgentRequest(
        prompt = prompt,
        userId = userId,
        context = mapOf("client" to "android")
    )
)
```

**Archivo**: `app/src/main/java/com/example/cliente/data/remote/AgentApiService.kt`

- DTO `AgentRequest` actualizado para aceptar `userId` y `context` opcionales

### 2. Backend Node/TypeScript

**Archivos modificados**:

1. **`ia/api/types/express.d.ts`**
   - Extendida interfaz `Request` para incluir `userContext`:
   ```typescript
   interface Request {
     log: Logger;
     userContext?: {
       userId?: number;
     };
   }
   ```

2. **`ia/api/middlewares/userContext.ts`** (nuevo)
   - Middleware que infiere `userId` desde header `x-user-id` o body
   - Inyecta en `req.userContext.userId` para uso en controladores

3. **`ia/api/server.ts`**
   - Registrado `userContextMiddleware` antes de las rutas

4. **`ia/api/controllers/agentController.ts`**
   - Usa `req.userContext?.userId` en lugar de `req.body.userId`
   - Pasa este valor a las herramientas que lo requieren

5. **`ia/services/geminiService.ts`**
   - Eliminado `userId` de los parámetros requeridos en las declaraciones de herramientas
   - `log_mood_snapshot`: solo requiere `mood`
   - `record_user_fact`: solo requiere `summary`

## Flujo Conversacional Mejorado

### Antes
```
Usuario: "Me siento estresado hoy"
Agente: "Para registrar tu estado de ánimo, necesito tu ID de usuario"
Usuario: "1"
Agente: "Gracias, he registrado que te sientes estresado"
```

### Ahora
```
Usuario: "Me siento estresado hoy"
Agente: "Entiendo que estás estresado. ¿Hay algo específico que te preocupa? 
         He registrado tu estado de ánimo para ajustar tu plan de estudio."
```

## Ejemplos de Uso

### 1. Registro de Estado de Ánimo

**Conversación**:
```
Usuario: "Tuve una sesión de gym muy intensa, estoy cansado"
```

**Acción del agente**:
- Detecta expresión emocional
- Ejecuta `log_mood_snapshot` con:
  - `mood`: "agotado"
  - `energyLevel`: 3-4 (inferido)
  - `stressLevel`: 5 (inferido)
  - `note`: "sesión de gym intensa"
  - `tags`: ["gym", "físico"]
- `userId` tomado automáticamente del contexto

**Respuesta del agente**:
```
"Entiendo que estás cansado después del gym. Te recomiendo tomar un descanso 
antes de estudiar. ¿Quieres que ajuste tu plan de hoy para incluir más pausas?"
```

### 2. Registro de Logros

**Conversación**:
```
Usuario: "Hoy completé 3 módulos seguidos sin distracciones, me sentí muy productivo"
```

**Acción del agente**:
- Detecta logro personal
- Ejecuta `record_user_fact` con:
  - `summary`: "Completó 3 módulos sin distracciones, alta productividad"
  - `tags`: ["logro", "productividad", "concentración"]
- `userId` tomado automáticamente del contexto

**Respuesta del agente**:
```
"¡Excelente trabajo! He guardado este logro en tu diario. Esta información me 
ayudará a personalizar mejor tus planes futuros. ¿Quieres continuar con otro módulo?"
```

### 3. Gestión de Tareas

**Conversación**:
```
Usuario: "Tengo que repasar Kafka para el viernes"
```

**Acción del agente**:
- Ejecuta `add_task` con:
  - `task`: "Repasar Kafka"
- `userId` inferido del contexto

**Respuesta del agente**:
```
"He añadido 'Repasar Kafka' a tu lista de tareas con fecha límite el viernes. 
¿Te gustaría que incluya esto en tu plan de mañana?"
```

## Integración con "Mi Día"

Toda la información recopilada por el agente se utiliza automáticamente para generar el plan personalizado:

1. **Estados de ánimo** (`log_mood_snapshot`):
   - Energía alta → sesiones de estudio más intensas
   - Estrés alto → más descansos y actividades de relajación
   - Cansancio → tareas más ligeras y pausas frecuentes

2. **Reflexiones y logros** (`record_user_fact`):
   - Patrones de productividad
   - Preferencias de horarios
   - Contexto personal para recomendaciones

3. **Tareas** (`add_task`, `update_task_status`):
   - Priorizadas en el timeline del día
   - Integradas con módulos de estudio
   - Recordatorios contextuales

## Seguridad

### Validaciones Implementadas

1. **Middleware `userContextMiddleware`**:
   - Valida que `userId` sea un número entero positivo
   - Prioriza header `x-user-id` sobre body para evitar spoofing
   - Rechaza peticiones si no se puede inferir usuario válido

2. **Controller `agentController`**:
   - Verifica `req.userContext?.userId` antes de ejecutar herramientas
   - Retorna `400 Bad Request` con mensaje claro si falta contexto
   - Logs de auditoría para todas las acciones

### Recomendaciones Futuras

- [ ] Implementar autenticación JWT para reemplazar header/body por token
- [ ] Agregar middleware de rate limiting por usuario
- [ ] Implementar RBAC si se necesitan permisos granulares
- [ ] Auditoría completa de acciones del agente por usuario

## Testing

### Tests Manuales Realizados

✅ Backend TypeScript compila sin errores (`npx tsc --noEmit`)
✅ Android Kotlin compila sin errores (`./gradlew compileDebugKotlin`)

### Tests Pendientes

- [ ] Test unitario para `userContextMiddleware`
- [ ] Test de integración para herramientas del agente
- [ ] Test E2E del flujo conversacional completo
- [ ] Test de carga para validar concurrencia

## Documentación Actualizada

✅ `ia/docs/endpoints.md` - Sección del agente actualizada con ejemplos conversacionales
✅ `ia/docs/application-flow.md` - Flujo del agente con contexto automático y ejemplos
✅ Este documento - Guía completa de implementación

## Próximos Pasos

1. **Mejorar el prompt del sistema del agente**:
   - Instrucciones más claras sobre cuándo usar cada herramienta
   - Ejemplos de detección de emociones y logros
   - Guías de tono conversacional

2. **Implementar memoria de conversación**:
   - Cache de últimos N mensajes por usuario
   - Contexto mantenido entre sesiones
   - Referencias a conversaciones anteriores

3. **Expandir herramientas**:
   - `suggest_study_path`: Sugerir nueva ruta basada en intereses
   - `schedule_break`: Programar descanso específico
   - `celebrate_achievement`: Registrar logro con imagen generada

4. **Analytics y mejoras**:
   - Dashboard de uso del agente
   - Métricas de efectividad de herramientas
   - A/B testing de prompts del sistema

## Conclusión

El sistema de contexto automático transforma al agente de un formulario conversacional en un verdadero asistente personal que:

- **Conoce al usuario** sin preguntarle repetidamente su identidad
- **Aprende y recuerda** información valiosa de forma transparente
- **Personaliza** recomendaciones basándose en el contexto completo
- **Integra** toda la información en un plan de día coherente

Esto sienta las bases para futuras mejoras en personalización y experiencia de usuario.

