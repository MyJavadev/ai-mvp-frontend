# Learning Platform - Android App

Una aplicación Android moderna desarrollada con Kotlin y Jetpack Compose para consumir la API de aprendizaje AI.

## 🏗️ Arquitectura

Esta aplicación sigue los principios de **Clean Architecture** y está estructurada en tres capas principales:

### 📁 Estructura del Proyecto

```
app/
├── data/
│   ├── model/          # DTOs y modelos de datos
│   ├── remote/         # API Services con Retrofit
│   └── repository/     # Repositorios para acceso a datos
├── di/                 # Módulos de Dependency Injection (Hilt)
├── presentation/       # Capa de UI
│   ├── home/
│   ├── studypath/
│   ├── module/
│   ├── quiz/
│   └── navigation/
├── ui/theme/          # Temas y estilos de Material3
└── util/              # Utilidades y helpers
```

## 🚀 Tecnologías Utilizadas

- **Kotlin** - Lenguaje de programación
- **Jetpack Compose** - UI moderna y declarativa
- **Material3** - Sistema de diseño
- **Hilt** - Dependency Injection
- **Retrofit** - Cliente HTTP
- **Kotlin Serialization** - Serialización JSON
- **Navigation Compose** - Navegación entre pantallas
- **Coroutines & Flow** - Programación asíncrona
- **Coil** - Carga de imágenes
- **ExoPlayer (Media3)** - Reproducción de audio
- **StateFlow** - Manejo de estado reactivo

## 📱 Características Principales

### 1. Study Paths (Rutas de Estudio)
- Crear rutas de estudio personalizadas
- Ver progreso de cada ruta
- Niveles: Beginner, Intermediate, Advanced

### 2. Módulos de Aprendizaje
- Contenido educativo con texto, imágenes y audio
- Navegación entre módulos
- Marcar módulos como completados

### 3. Sistema de Quizzes
- Generación dinámica de cuestionarios
- Múltiples opciones de respuesta
- Resultados detallados con explicaciones
- Revisión de respuestas correctas e incorrectas

### 4. Búsqueda
- Búsqueda de contenido
- Filtros avanzados

### 5. Perfil y Progreso
- Estadísticas de aprendizaje
- Sistema de logros
- Historial de actividades

## 🔧 Configuración

### Requisitos Previos
- Android Studio Hedgehog | 2023.1.1 o superior
- JDK 17
- Android SDK API 26+ (Android 8.0+)
- Gradle 8.7+

### Configuración del Backend

Edita `app/build.gradle.kts` y actualiza la URL base de la API:

```kotlin
buildConfigField("String", "BASE_URL", "\"http://TU_IP:3000/api/\"")
```

Para el emulador de Android usa: `http://10.0.2.2:3000/api/`
Para dispositivo físico usa: `http://TU_IP_LOCAL:3000/api/`

### Compilar el Proyecto

```bash
# Limpiar el proyecto
gradlew clean

# Compilar
gradlew build

# Instalar en dispositivo
gradlew installDebug
```

## 📋 Endpoints Implementados

### User API
- `POST /api/users` - Crear usuario
- `GET /api/users/{userId}` - Obtener usuario
- `PUT /api/users/{userId}` - Actualizar usuario

### Study Path API
- `POST /api/study-paths` - Crear ruta de estudio
- `GET /api/study-paths/user/{userId}` - Obtener rutas del usuario
- `GET /api/study-paths/{pathId}` - Obtener detalles de ruta
- `PUT /api/study-paths/{pathId}/modules/{moduleId}/progress` - Actualizar progreso

### Quiz API
- `POST /api/quiz/generate` - Generar quiz
- `GET /api/quiz/{quizId}` - Obtener quiz
- `POST /api/quiz/submit` - Enviar respuestas

### Search API
- `GET /api/search` - Búsqueda simple
- `POST /api/search/advanced` - Búsqueda avanzada

### Progress API
- `GET /api/progress/{userId}` - Obtener progreso del usuario
- `GET /api/progress/{userId}/achievements` - Obtener logros

### Agent API
- `POST /api/agent/chat` - Chat con el asistente AI

## 🎨 Flujo de Navegación

```
HomeScreen
    ├── StudyPathListScreen
    │   └── StudyPathDetailScreen
    │       └── ModuleDetailScreen
    │           └── QuizScreen
    │               └── QuizResultScreen
    ├── CreateStudyPathScreen
    ├── SearchScreen
    └── ProfileScreen
```

## 🔐 Permisos

La aplicación requiere los siguientes permisos:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## 🧪 Testing

```bash
# Tests unitarios
gradlew test

# Tests instrumentados
gradlew connectedAndroidTest
```

## 📦 Componentes Clave

### ViewModels
- `StudyPathViewModel` - Gestión de rutas de estudio
- `QuizViewModel` - Gestión de quizzes
- `SearchViewModel` - Gestión de búsquedas

### Composables Principales
- `HomeScreen` - Pantalla principal
- `StudyPathListScreen` - Lista de rutas
- `CreateStudyPathScreen` - Crear nueva ruta
- `StudyPathDetailScreen` - Detalles y módulos
- `ModuleDetailScreen` - Contenido del módulo
- `QuizScreen` - Interfaz del quiz
- `QuizResultScreen` - Resultados del quiz

## 🛠️ Próximas Características

- [ ] Caché offline con Room Database
- [ ] Sincronización en segundo plano
- [ ] Notificaciones push
- [ ] Modo oscuro personalizado
- [ ] Soporte multiidioma
- [ ] Reproductor de audio mejorado
- [ ] Visualización de PDF
- [ ] Compartir progreso en redes sociales
- [ ] Widget de progreso

## 📝 Notas de Desarrollo

### Convenciones de Código
- Seguir las guías de estilo de Kotlin
- Usar nombres descriptivos en inglés
- Comentar código complejo
- Mantener funciones pequeñas y enfocadas

### Manejo de Estados
Se utiliza `StateFlow` para manejar el estado de la UI:

```kotlin
sealed class Resource<T> {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String) : Resource<T>()
    class Loading<T> : Resource<T>()
}
```

### Inyección de Dependencias
Todos los módulos usan Hilt para DI:

```kotlin
@HiltViewModel
class MyViewModel @Inject constructor(
    private val repository: MyRepository
) : ViewModel()
```

## 🐛 Debugging

Para habilitar logs detallados, la app usa `OkHttp Logging Interceptor` en modo DEBUG.

## 📄 Licencia

Este proyecto es parte del MVP de la plataforma de aprendizaje AI.

## 👥 Contribución

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📞 Soporte

Para soporte técnico o preguntas, contacta al equipo de desarrollo.

