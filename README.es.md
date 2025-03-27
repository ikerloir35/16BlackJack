# 16 BlackJack Juego Android

## Resumen del Proyecto

### Propósito

Desarrollo de un Juego para Android.

Hemos diseñado una version del clasico juego de cartas BlackJack. Cuenta con una interfaz limpia e intuitiva, manteniendo reglas básicas tradicionales del BlackJack. Se ha utilizado las utilidades de Android para la interacción con el calendario, la cámara, las notificaciones, la música y otras funciones.

La persistencia se logra mediante el uso de una base de datos local con SQLite y una base de datos en la nube con Firebase. Para la autenticación de usuarios, se ha utilizado Google OAuth.

### Stack Tecnológico

- **Lenguaje**: Kotlin
- **Plataforma**: Android
- **Arquitectura**: MVVM (Modelo-Vista-VistaModelo)
- **Tecnologías Principales**:
  - Autenticación Firebase
  - Integración de Inicio de Sesión de Google
  - Componentes de Material Design
  - Corrutinas de Kotlin para operaciones asíncronas
  - Gradle con Kotlin DSL
  - Gestión de Configuración basada en Variables de Entorno

### Características

- **Autenticación**:
  - Integración de Inicio de Sesión de Google
  - Gestión segura de sesiones de usuario
  - Estado de inicio de sesión persistente
- **Características del Juego**:
  - Implementación de reglas clásicas de BlackJack
  - Animación interactiva de reparto de cartas
  - Gestión en tiempo real del estado del juego
  - Seguimiento de puntuación
  - Sistema de apuestas
- **Interfaz de Usuario**:
  - Componentes de Material Design 3
  - Diseño responsivo
  - Soporte para temas claro/oscuro
  - Controles de juego intuitivos
- **Características Técnicas**:
  - Configuración segura del entorno
  - Capacidad de funcionamiento offline
  - Optimizado para rendimiento
  - Arquitectura modular

### Seguridad

- **Seguridad de Autenticación**:
  - Implementación de OAuth 2.0 para Inicio de Sesión de Google
  - Gestión segura de tokens
  - Manejo de sesiones
- **Seguridad de Datos**:
  - Configuración basada en variables de entorno
  - Sin datos sensibles en el código
  - Gestión segura de claves API
- **Mejores Prácticas**:
  - Actualizaciones regulares de seguridad
  - Estándares de codificación segura
  - Validación de entrada
  - Manejo de errores

### Co-Desarrolladores

- **Desarrollador Principal**: [Tu Nombre]
  - Iker López Iribas
  - Damià Belles Sampera
- **Contribuidores**:
  - Sebastián Dos Santos Librandi

## Configuración del Entorno

### Prerrequisitos

- Android Studio Hedgehog (2023.1.1) o más reciente
- JDK 21 o superior
- Git

### Claves API Requeridas

1. **Clave API de Google**

   - Propósito: Autenticación Firebase e Inicio de Sesión de Google
   - Ubicación: Consola de Google Cloud
   - Ámbitos Requeridos: API de Inicio de Sesión de Google

2. **ID de Cliente Web de Firebase**
   - Propósito: Autenticación Firebase
   - Ubicación: Consola de Firebase
   - Requerido para: Integración de Inicio de Sesión de Google

### Instrucciones de Configuración

1. **Clonar el Repositorio**

   ```bash
   git clone https://github.com/yourusername/16BlackJack_repo.git
   cd 16BlackJack_repo
   ```

2. **Configuración del Entorno**

   - Copiar `.env.example` a `.env`
   - Completar las claves API requeridas:
     ```
     GOOGLE_API_KEY=tu_clave_api_google
     GOOGLE_SITES_API_KEY=tu_clave_api_google_sites
     FIREBASE_WEB_CLIENT_ID=tu_id_cliente_web_firebase
     ```

3. **Configuración de Compilación**

   - El proyecto usa Gradle con Kotlin DSL
   - Las variables de entorno se copian automáticamente a assets durante la compilación
   - No se requiere configuración manual después de configurar `.env`

4. **Ejecutar la Aplicación**
   - Abrir el proyecto en Android Studio
   - Sincronizar proyecto con archivos Gradle
   - Ejecutar en un emulador o dispositivo físico

### Mejores Prácticas de Seguridad

1. **Gestión de Claves API**

   - Nunca commitear el archivo `.env` al control de versiones
   - Usar diferentes claves API para desarrollo y producción
   - Rotar regularmente las claves API
   - Restringir el uso de claves API en la Consola de Google Cloud

2. **Seguridad del Código**

   - Todos los datos sensibles se cargan desde variables de entorno
   - Sin credenciales hardcodeadas en el código fuente
   - Manejo seguro de errores y logging
   - Auditorías regulares de seguridad

3. **Flujo de Desarrollo**
   - Usar ramas de características para desarrollo
   - Revisión de código requerida para cambios sensibles
   - Actualizaciones regulares de seguridad
   - Escaneo automatizado de seguridad

### Solución de Problemas

1. **Problemas de Compilación**

   - Asegurar que JDK 21 está instalado correctamente
   - Verificar que las variables de entorno están configuradas correctamente
   - Comprobar estado de sincronización de Gradle
   - Limpiar y recompilar el proyecto

2. **Problemas en Tiempo de Ejecución**

   - Verificar que las claves API son válidas
   - Comprobar conectividad a internet
   - Revisar logcat para mensajes de error detallados
   - Asegurar que la configuración de Firebase es correcta

3. **Problemas de Autenticación**
   - Verificar configuración de Inicio de Sesión de Google
   - Comprobar huella digital SHA-1 en la Consola de Firebase
   - Asegurar que la pantalla de consentimiento OAuth está configurada
   - Verificar que el nombre del paquete coincide con la configuración de Firebase

### Despliegue en Producción

1. **Lista de Verificación Pre-despliegue**

   - Actualizar claves API para producción
   - Verificar todas las medidas de seguridad
   - Probar en múltiples dispositivos
   - Revisar manejo de errores
   - Comprobar métricas de rendimiento

2. **Proceso de Lanzamiento**

   - Actualizar números de versión
   - Generar APK/Bundle firmado
   - Probar build de lanzamiento
   - Desplegar en Google Play Store

3. **Post-despliegue**
   - Monitorear reportes de errores
   - Seguir retroalimentación de usuarios
   - Planificar actualizaciones regulares
   - Mantener parches de seguridad

## Licencia

[Tu Licencia Aquí]

## Contribuir

[Tus Guías de Contribución Aquí]
