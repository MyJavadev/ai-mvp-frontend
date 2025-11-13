# Estado de Implementación de Endpoints

## ✅ Endpoints Completamente Implementados

### Usuarios
- ✅ `POST /users` - Crear/recuperar usuario (UserApiService)
- ✅ `GET /users/:userId` - Obtener datos de usuario (UserApiService)

### Rutas de Estudio
- ✅ `POST /study-path` - Crear ruta de estudio (StudyPathApiService)
- ✅ `GET /study-path-requests/:requestId` - Consultar estado de solicitud (StudyPathApiService)
- ✅ `GET /study-paths` - Listar rutas de estudio (StudyPathApiService)
- ✅ `GET /study-path/:id` - Obtener módulos de una ruta (StudyPathApiService)
- ✅ `GET /study-path-modules/:id` - Obtener un módulo específico (StudyPathApiService)

### Progreso
- ✅ `POST /progress/modules/complete` - Marcar módulo como completado (ProgressApiService)
- ✅ `GET /progress/users/:userId/progress` - Obtener progreso y logros (ProgressApiService)
- ✅ `GET /progress/users/:userId/dashboard` - Obtener resumen para dashboard (ProgressApiService)
- ✅ `GET /progress/users/:userId/timeline` - Obtener línea de tiempo de eventos (ProgressApiService)

### Quizzes
- ✅ `POST /modules/:moduleId/quiz` - Generar quiz (QuizApiService)
- ✅ `GET /modules/:moduleId/quiz` - Obtener quiz de un módulo (QuizApiService)
- ✅ `POST /quizzes/:quizId/submit` - Enviar respuestas de quiz (QuizApiService)
- ✅ `GET /users/:userId/performance` - Obtener rendimiento en quizzes (QuizApiService)

### Búsqueda
- ✅ `GET /search` - Búsqueda semántica (SearchApiService)
- ✅ `GET /search/typesense` - Búsqueda por keywords (SearchApiService)

### Text-to-Speech (TTS)
- ✅ `POST /text-to-speech` - Crear trabajo de TTS (TTSApiService)
- ✅ `GET /text-to-speech` - Listar trabajos de TTS (TTSApiService)
- ✅ `GET /text-to-speech/:jobId` - Consultar estado de trabajo TTS (TTSApiService)

### Agente IA
- ✅ `POST /agent` - Interactuar con el agente de IA (AgentApiService)

## ⚠️ Endpoints Parcialmente Implementados

### Rutas de Estudio
- ⚠️ `POST /generate-images-for-path` - Regenerar imágenes para una ruta
  - **Estado**: API implementada en StudyPathApiService
  - **Falta**: Vista/UI para usar esta funcionalidad

## ❌ Funcionalidades NO Implementadas en el Backend

Según la documentación del backend, NO existen los siguientes endpoints:
- ❌ Sistema de autenticación con login/logout
- ❌ Sistema de sesiones con tokens
- ❌ Endpoints de actualización de perfil de usuario

## 📊 Resumen de Pantallas

### Pantallas Implementadas
1. ✅ **SetupScreen** - Onboarding inicial
2. ✅ **HomeScreen** - Dashboard principal con timeline
3. ✅ **StudyPathListScreen** - Lista de rutas de estudio
4. ✅ **StudyPathDetailScreen** - Detalle de ruta con módulos
5. ✅ **CreateStudyPathScreen** - Crear nueva ruta
6. ✅ **ModuleDetailScreen** - Detalle de módulo con audio
7. ✅ **QuizScreen** - Tomar quiz
8. ✅ **QuizResultScreen** - Resultados de quiz
9. ✅ **SearchScreen** - Búsqueda semántica y por keywords
10. ✅ **AgentChatScreen** - Chat con agente IA

### Pantallas Faltantes
11. ❌ **ProfileScreen** - Perfil del usuario con:
    - Información del usuario
    - Estadísticas de progreso
    - Logros obtenidos
    - Rendimiento en quizzes
    - **Opción de cerrar sesión (logout)**

12. ❌ **PerformanceScreen** - Vista detallada de rendimiento en quizzes
    - Usa: `GET /users/:userId/performance`

## 🔧 Solución para Login/Logout

### Situación Actual
- El backend **NO tiene sistema de autenticación tradicional**
- Solo usa `userId` que se almacena localmente
- No hay tokens, passwords, ni sesiones en el backend

### Implementación de Logout
Como el backend no tiene endpoints de logout, la solución es:

1. **Agregar función de "Cambiar Usuario" o "Cerrar Sesión"**
   - Esta función limpiará el `userId` almacenado localmente
   - Navegará de vuelta a la pantalla de Setup
   - El usuario podrá ingresar un nuevo username

2. **Código necesario:**
   ```kotlin
   // Ya existe en UserPreferences.kt:
   suspend fun clearUser() {
       dataStore.edit { preferences ->
           preferences.clear()
       }
   }
   ```

3. **Flujo de Logout:**
   - Usuario presiona "Cerrar Sesión" en el perfil
   - Se llama a `userPreferences.clearUser()`
   - Se navega a `Screen.Setup` limpiando el backstack
   - Usuario puede crear/seleccionar otro usuario

## 📝 Recomendaciones

### Para Mejorar la App
1. **Crear ProfileScreen** con:
   - Información del usuario
   - Botón de "Cerrar Sesión"
   - Vista de logros
   - Estadísticas de rendimiento

2. **Agregar navegación al perfil** desde:
   - HomeScreen (botón en TopAppBar)
   - Bottom Navigation (si se implementa)

3. **Implementar PerformanceScreen** para mostrar:
   - Historial de quizzes
   - Gráficos de rendimiento
   - Logros desbloqueados

### Para el Backend (futuro)
Si quieres un sistema de autenticación completo:
1. Agregar endpoints de login/registro con passwords
2. Implementar tokens JWT
3. Agregar middleware de autenticación
4. Agregar endpoints de actualización de perfil

## ✅ Conclusión

**Todos los endpoints disponibles en el backend están implementados en la app.**

Lo único que falta es:
1. Crear la pantalla de perfil (ProfileScreen)
2. Implementar la funcionalidad de logout (limpiar sesión local)
3. Crear vista de rendimiento detallado (opcional pero recomendado)

El backend NO tiene sistema de autenticación tradicional, así que el "logout" será simplemente limpiar la sesión local y volver al setup.

