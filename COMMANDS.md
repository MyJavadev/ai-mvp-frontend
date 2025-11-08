# Comandos Útiles - Desarrollo Android

## 🔨 Comandos Gradle

### Compilar el proyecto
```cmd
gradlew build
```

### Limpiar y recompilar
```cmd
gradlew clean build
```

### Instalar en dispositivo/emulador
```cmd
gradlew installDebug
```

### Desinstalar
```cmd
gradlew uninstallDebug
```

### Ver dependencias
```cmd
gradlew :app:dependencies
```

### Actualizar dependencias
```cmd
gradlew build --refresh-dependencies
```

## 🧪 Testing

### Ejecutar tests unitarios
```cmd
gradlew test
```

### Tests con reporte HTML
```cmd
gradlew test
# Abre: app\build\reports\tests\testDebugUnitTest\index.html
```

### Tests instrumentados (requiere dispositivo)
```cmd
gradlew connectedAndroidTest
```

## 🐛 Debug

### Compilar versión debug
```cmd
gradlew assembleDebug
```

### Compilar versión release
```cmd
gradlew assembleRelease
```

### Generar APK
```cmd
gradlew assembleDebug
# APK en: app\build\outputs\apk\debug\app-debug.apk
```

### Generar APK release firmado
```cmd
gradlew assembleRelease
# Requiere configurar signing en build.gradle
```

## 📊 Análisis

### Ver tareas disponibles
```cmd
gradlew tasks
```

### Lint check
```cmd
gradlew lint
# Reporte en: app\build\reports\lint-results-debug.html
```

### Verificar código
```cmd
gradlew check
```

## 🧹 Limpieza

### Limpiar build
```cmd
gradlew clean
```

### Limpiar caché de Gradle
```cmd
gradlew clean cleanBuildCache
```

### Limpiar TODO (Windows)
```cmd
rmdir /s /q .gradle
rmdir /s /q build
rmdir /s /q app\build
gradlew clean
```

## 📱 ADB (Android Debug Bridge)

### Listar dispositivos conectados
```cmd
adb devices
```

### Instalar APK
```cmd
adb install app\build\outputs\apk\debug\app-debug.apk
```

### Desinstalar app
```cmd
adb uninstall com.example.cliente
```

### Ver logs en tiempo real
```cmd
adb logcat | findstr "com.example.cliente"
```

### Limpiar logs
```cmd
adb logcat -c
```

### Ver logs filtrados por tag
```cmd
adb logcat -s "OkHttp"
```

### Screenshot
```cmd
adb shell screencap -p /sdcard/screen.png
adb pull /sdcard/screen.png
```

### Reiniciar ADB
```cmd
adb kill-server
adb start-server
```

## 🌐 Backend

### Iniciar backend
```cmd
cd ia
npm run dev
```

### Ver logs del backend
```cmd
cd ia
npm run dev | findstr "POST GET"
```

### Verificar que el backend responde
```cmd
curl http://localhost:3000/api/health
```

## 🔍 Android Studio

### Invalidar caché
```
File → Invalidate Caches → Invalidate and Restart
```

### Sincronizar Gradle
```
File → Sync Project with Gradle Files
```

### Recompilar proyecto
```
Build → Rebuild Project
```

### Limpiar proyecto
```
Build → Clean Project
```

## 🚀 Emulador

### Listar emuladores disponibles
```cmd
emulator -list-avds
```

### Iniciar emulador específico
```cmd
emulator -avd Pixel_5_API_33
```

### Iniciar con wipe data
```cmd
emulator -avd Pixel_5_API_33 -wipe-data
```

## 📦 Dependencias

### Actualizar wrapper de Gradle
```cmd
gradlew wrapper --gradle-version=8.7
```

### Ver versión de Gradle
```cmd
gradlew --version
```

## 🔐 Signing (Release)

### Generar keystore
```cmd
keytool -genkey -v -keystore my-release-key.keystore -alias my-key-alias -keyalg RSA -keysize 2048 -validity 10000
```

### Verificar keystore
```cmd
keytool -list -v -keystore my-release-key.keystore
```

## 🎨 Recursos

### Generar iconos de launcher
Usa: https://romannurik.github.io/AndroidAssetStudio/

### Generar splash screen
Usa: https://developer.android.com/develop/ui/views/launch/splash-screen

## 📊 Performance

### Profiling con Gradle
```cmd
gradlew --profile assembleDebug
# Reporte en: build\reports\profile\
```

### Build scan
```cmd
gradlew build --scan
```

## 🔄 Git (Opcional)

### Inicializar repositorio
```cmd
git init
git add .
git commit -m "Initial Android implementation"
```

### Crear .gitignore
```gitignore
# Android
*.iml
.gradle
/local.properties
/.idea/
.DS_Store
/build
/captures
.externalNativeBuild
.cxx

# Keystore
*.jks
*.keystore

# App specific
/app/release
/app/debug
```

## 📱 Verificaciones Rápidas

### ¿El proyecto compila?
```cmd
gradlew build
```

### ¿Los tests pasan?
```cmd
gradlew test
```

### ¿Hay errores de lint?
```cmd
gradlew lint
```

### ¿El backend está corriendo?
```cmd
curl http://localhost:3000
```

### ¿El dispositivo está conectado?
```cmd
adb devices
```

## 🆘 Solución de Problemas

### Error: "SDK location not found"
```cmd
# Crear local.properties
echo sdk.dir=C:\\Users\\%USERNAME%\\AppData\\Local\\Android\\Sdk > local.properties
```

### Error: "Daemon will be stopped"
```cmd
gradlew --stop
gradlew clean build
```

### Error: "Unable to start the daemon process"
```cmd
# Aumentar memoria en gradle.properties
echo org.gradle.jvmargs=-Xmx2048m -XX:MaxPermSize=512m >> gradle.properties
```

### Error de red en emulador
```cmd
# Reiniciar emulador con DNS de Google
emulator -avd Pixel_5_API_33 -dns-server 8.8.8.8
```

## 📚 Recursos Útiles

- Android Studio: https://developer.android.com/studio
- Gradle Docs: https://docs.gradle.org/
- ADB Docs: https://developer.android.com/tools/adb
- Jetpack Compose: https://developer.android.com/jetpack/compose

---

## 🎯 Flujo de Desarrollo Típico

```cmd
# 1. Abrir proyecto
cd C:\Users\Deus\Desktop\Mvp

# 2. Iniciar backend
cd ia
npm run dev
# (mantener esta terminal abierta)

# 3. Nueva terminal - Compilar app
cd C:\Users\Deus\Desktop\Mvp
gradlew clean build

# 4. Conectar dispositivo/emulador
adb devices

# 5. Instalar y ejecutar
gradlew installDebug

# 6. Ver logs
adb logcat | findstr "com.example.cliente"
```

---

¡Guarda este archivo como referencia rápida! 📌

