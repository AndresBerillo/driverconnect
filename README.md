# DriverConnect

DriverConnect es una aplicación móvil diseñada para conectar usuarios con conductores disponibles en caso de que necesiten ayuda para transportarse. Esta aplicación permite gestionar usuarios, choferes y solicitudes de servicios de una manera eficiente y estructurada.

---

## ✨ Características principales

1. **Gestión de usuarios:**
   - Registro de nuevos usuarios.
   - Inicio de sesión.
   - Edición de información del usuario.

2. **Gestión de choferes:**
   - Registro de nuevos choferes.
   - Visualización y edición de información de los choferes.
   - Actualización del estado del chofer (Disponible/En servicio).

3. **Solicitudes de servicio:**
   - Creación de solicitudes con origen, destino y distancia.
   - Asignación de un chofer disponible.
   - Cancelación o finalización de solicitudes.

4. **Panel de administración:**
   - Administración de usuarios y choferes.
   - Visualización de solicitudes activas.

---

## 🛠️ Tecnologías utilizadas

- **Lenguaje:** Java.
- **Entorno de desarrollo:** Android Studio.
- **Base de datos:** SQLite.
- **Diseño de interfaz:** ConstraintLayout, CardView, y elementos personalizables (Drawable, XML).
- **Gestión de hilos:** ExecutorService y callbacks personalizados.
- **Repositorio de código:** [GitHub](https://github.com/AndresBerillo/driverconnect.git).

---

## ⭐ Instalación

Sigue estos pasos para instalar y ejecutar la aplicación en tu entorno local:

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/AndresBerillo/driverconnect.git
   ```

2. **Abrir el proyecto en Android Studio:**
   - Abre Android Studio y selecciona `File > Open...`.
   - Navega hasta la carpeta clonada y selecciona el archivo del proyecto.

3. **Configurar el entorno:**
   - Asegúrate de que tienes instalado un emulador o un dispositivo Android conectado para pruebas.
   - Verifica que `Gradle` esté sincronizado correctamente.

4. **Ejecutar la aplicación:**
   - Haz clic en el botón `Run` (🔄) o presiona `Shift + F10` para compilar y ejecutar la aplicación.

---

## 🎨 Diseño de la interfaz

La aplicación utiliza un diseño moderno y responsivo con los siguientes componentes:

- **Drawable personalizados:**
  - Botones con efectos de sombra y ripple.
  - Fondos gradientes radiales.

- **Layouts:**
  - ConstraintLayout para estructuras flexibles y ajustables.
  - LinearLayout dentro de CardViews para campos de entrada.

---

## ⚡ Gestión de hilos y callbacks

Para garantizar una experiencia fluida en la aplicación, DriverConnect utiliza `ExecutorService` para ejecutar operaciones de base de datos en segundo plano. Los resultados se manejan mediante callbacks personalizados para actualizar la interfaz de usuario sin bloquear el hilo principal.

Ejemplo de un callback:
```java
public interface Callback<T> {
    void onComplete(T result);
    void onError(Exception e);
}
```

