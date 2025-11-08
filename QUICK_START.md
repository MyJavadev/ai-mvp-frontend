# 🚀 INICIO RÁPIDO - 5 Minutos

## ⚡ Ejecución Ultra Rápida

### Opción 1: Usando Android Studio (Recomendado)

```
1. Abre Android Studio
2. File → Open → C:\Users\Deus\Desktop\Mvp
3. Espera sincronización de Gradle (automática)
4. Click en "Run" ▶️
```

### Opción 2: Línea de Comandos

```cmd
cd C:\Users\Deus\Desktop\Mvp
gradlew installDebug
```

---

## 🔥 Guía de 60 Segundos

### 1. Verificar Requisitos (10 seg)
```cmd
# ¿Tienes Java?
java -version

# ¿Tienes Android Studio?
# Si no: https://developer.android.com/studio

# ¿Backend corriendo?
curl http://localhost:3000
```

### 2. Configurar Backend URL (15 seg)

Edita: `app/build.gradle.kts` línea 29:

**Para Emulador:**
```kotlin
buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:3000/api/\"")
```

**Para Dispositivo Real:**
```kotlin
buildConfigField("String", "BASE_URL", "\"http://192.168.1.X:3000/api/\"")
```
*(Reemplaza X con tu IP local)*

### 3. Iniciar Backend (15 seg)
```cmd
cd ia
npm install    # Solo la primera vez
npm run dev
```

### 4. Ejecutar App (20 seg)
```cmd
# En Android Studio: Click Run ▶️
# O desde terminal:
cd C:\Users\Deus\Desktop\Mvp
gradlew installDebug
```

---

## 📱 Primera Prueba - 2 Minutos

### Test del Flujo Completo

1. **Abrir App** → Verás HomeScreen
2. **Click en "My Study Paths"** → Lista vacía (primera vez)
3. **Click en botón "+" (flotar)** → CreateStudyPathScreen
4. **Crear Study Path:**
   - Topic: "Python Programming"
   - Level: Beginner
   - Click "Create Study Path"
5. **Ver Detalles** → Pantalla de módulos
6. **Click en un módulo** → Contenido educativo
7. **Click "Take Quiz"** → Responde preguntas
8. **Click "Submit"** → Ver resultados

✅ **Si todo funciona: ¡Éxito!** 🎉

---

## 🐛 Solución Rápida de Problemas

### Error: "Backend not responding"
```cmd
# Terminal 1: Iniciar backend
cd ia
npm run dev

# Verificar que responda
curl http://localhost:3000
```

### Error: "Cannot resolve symbol"
```cmd
# Android Studio:
File → Invalidate Caches → Restart

# O desde terminal:
gradlew clean build
```

### Error: "Device not found"
```cmd
# Verificar dispositivos
adb devices

# Si está vacío, conecta un dispositivo o inicia emulador
```

### Error: "SDK location not found"
```cmd
# Crear local.properties
echo sdk.dir=C:\\Users\\%USERNAME%\\AppData\\Local\\Android\\Sdk > local.properties
```

---

## 🎯 Checklist Rápido

Antes de ejecutar, verifica:

- [ ] Android Studio instalado
- [ ] Proyecto abierto en Android Studio
- [ ] Gradle sincronizado (sin errores rojos)
- [ ] Backend corriendo (`npm run dev` en carpeta `ia`)
- [ ] Dispositivo/emulador conectado
- [ ] URL del backend configurada correctamente

---

## 📊 Estructura Visual

```
Tu Computadora
│
├── Backend (Terminal 1)
│   └── cd ia && npm run dev
│   └── http://localhost:3000 ✅
│
└── Android App (Android Studio)
    ├── Gradle Sync ✅
    ├── Build ✅
    └── Run ▶️
        │
        ├── Emulador → 10.0.2.2:3000
        └── Dispositivo → 192.168.1.X:3000
```

---

## 🎓 Flujo de Aprendizaje Recomendado

### Día 1: Explorar lo que está hecho
- Revisar `ANDROID_README.md`
- Ejecutar la app
- Probar todas las pantallas
- Ver el código de una pantalla simple (HomeScreen.kt)

### Día 2: Entender la arquitectura
- Leer `IMPLEMENTATION_GUIDE.md`
- Revisar ViewModels
- Entender el flujo de datos (Repository → ViewModel → UI)

### Día 3: Hacer cambios simples
- Cambiar colores en `ui/theme/Color.kt`
- Modificar textos en `HomeScreen.kt`
- Agregar un nuevo campo al formulario

### Día 4+: Nuevas features
- Implementar ProfileScreen
- Agregar SearchScreen
- Mejorar el audio player

---

## 📚 Comandos Más Usados

```cmd
# Compilar
gradlew build

# Limpiar + Compilar
gradlew clean build

# Instalar en dispositivo
gradlew installDebug

# Ver logs
adb logcat | findstr "com.example.cliente"

# Reiniciar ADB si hay problemas
adb kill-server && adb start-server
```

---

## 🆘 Obtener Ayuda

### Archivos de Documentación
1. `FINAL_REPORT.md` - Estado completo del proyecto
2. `ANDROID_README.md` - Documentación técnica
3. `IMPLEMENTATION_GUIDE.md` - Guía paso a paso
4. `COMMANDS.md` - Todos los comandos útiles

### Online
- [Android Developer Docs](https://developer.android.com)
- [Jetpack Compose Tutorial](https://developer.android.com/jetpack/compose/tutorial)
- [Stack Overflow](https://stackoverflow.com/questions/tagged/android)

---

## ✨ Consejos Pro

### 🔥 Hot Reload
Compose tiene "Live Edit" - los cambios en UI se aplican sin recompilar:
```
Android Studio → Tools → Live Edit
```

### 🎨 Preview Composables
Agrega `@Preview` a cualquier Composable:
```kotlin
@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        onNavigateToStudyPaths = {},
        onNavigateToCreatePath = {}
    )
}
```

### 🐛 Debug Network
Ver todas las llamadas HTTP en Logcat:
```cmd
adb logcat | findstr "OkHttp"
```

### 📱 Cambio Rápido de URL
Para cambiar entre emulador y dispositivo sin editar código:
```kotlin
// En NetworkModule.kt, detectar automáticamente:
val baseUrl = if (isEmulator) {
    "http://10.0.2.2:3000/api/"
} else {
    "http://192.168.1.100:3000/api/"
}
```

---

## 🎯 Tu Próximo Paso

**Ahora mismo:**
1. Abre Android Studio
2. Abre el proyecto
3. Click Run ▶️
4. **¡Empieza a desarrollar!** 🚀

---

**Todo está listo. Solo haz clic en Run. ¡Suerte!** 💪✨

