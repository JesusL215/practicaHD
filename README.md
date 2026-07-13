# SmartPark - Sistema de Gestión de Estacionamiento Inteligente

SmartPark es una aplicación de escritorio desarrollada en **Java y JavaFX** orientada a la gestión eficiente de un 
estacionamiento vehicular. El proyecto aplica rigurosamente **Patrones de Diseño de Software (GoF)** y principios 
SOLID para garantizar una arquitectura limpia, escalable y mantenible.

## Tecnologías y Herramientas

* **Lenguaje:** Java 21
* **Interfaz Gráfica:** JavaFX 21 (FXML & CSS)
* **Gestor de Dependencias:** Maven
* **Base de Datos:** PostgreSQL
* **Control de Versiones:** Git & GitHub (Git Flow Workflow)
* **Gestión de Entorno:** dotenv-java (Variables de entorno `.env`)

---

## Arquitectura y Patrones de Diseño Implementados

El núcleo de este sistema fue construido aplicando los siguientes patrones de diseño para resolver problemas específicos del dominio:

### Patrones Creacionales
* **Singleton:** Aplicado en la clase `DBConnection` para garantizar una única instancia de la conexión a la base de datos en todo el ciclo de vida de la aplicación.
* **Factory Method:** Aplicado en `VehiculoFactory` para abstraer y delegar la instanciación dinámica de diferentes tipos de vehículos (`Auto`, `Moto`), facilitando la adición de futuros tipos de transporte.

### Patrones Estructurales
* **Decorator:** Utilizado para el cálculo dinámico de tarifas (`IParkingCost`). Permite añadir servicios extra en tiempo de ejecución (como el `CarWashDecorator`) al costo base del estacionamiento sin modificar las clases principales.
* **Proxy:** Implementado a través de `HistoryCacheProxy` (`IHistoryService`). Intercepta las llamadas a la base de datos para recuperar el historial de tickets, guardándolos en memoria caché para consultas más rápidas y aliviando la carga de la BD.

### Patrones de Comportamiento
* **State:** Gestiona los estados de un espacio de estacionamiento (`ParkingSlot`) alternando entre `AvailableState` (Disponible) y `OccupiedState` (Ocupado), encapsulando la lógica de transición.
* **Observer:** Implementado mediante `ParkingNotifier` para actualizar el "Mapa del Estacionamiento" (Dashboard UI) en tiempo real cada vez que un vehículo entra o sale, manteniendo la interfaz sincronizada de forma reactiva.
* **Memento:** Proporciona la funcionalidad de **"Deshacer"** mediante `Caretaker` y `TicketMemento`. Permite revertir la salida de un vehículo y restaurar su estado anterior (Ticket Activo y Espacio Ocupado) en caso de un error de cobro.

---

## Características Principales

1. **Control de Acceso:** Registro de entrada identificando placa, tipo de vehículo y asignación de espacios de estacionamiento (Slots).
2. **Mapa en Tiempo Real:** Interfaz visual (Grid) que muestra los espacios disponibles (verde) y ocupados (rojo).
3. **Facturación Dinámica:** Cálculo de tarifas por horas según el tipo de vehículo, con posibilidad de agregar servicios extra como Lavado de Auto.
4. **Historial y Reportes:** Visualización del historial completo de transacciones almacenadas en caché y reportes financieros diarios.
5. **Acción de Reversión:** Opción de deshacer el último cobro de forma segura usando estados guardados.

---

## Instalación y Ejecución Local

### Prerrequisitos
* Java Development Kit (JDK) 21
* Apache Maven
* PostgreSQL instalado y ejecutándose.

## Pasos de Configuración

### 1. Clonar el repositorio:
```bash
    git clone [https://github.com/tu-usuario/SmarkPark.git]
    cd SmarkPark
```

### 2. Configurar la Base de Datos

* Crea una base de datos en PostgreSQL llamada `smartpark_db` y ejecuta el script de creación de tablas (DAOs).
* Crea un archivo llamado `.env` en la raíz del proyecto basándote en la siguiente estructura:

```env
    DB_URL=jdbc:postgresql://localhost:5432/smartpark_db
    DB_USER=tu_usuario
    DB_PASSWORD=tu_contraseña
```
### 3. Compilar y Ejecutar:
Utilizando Maven, ejecuta los siguientes comandos en tu terminal:
```bash
    mvn clean install
    mvn javafx:run
```
---
## Flujo de Trabajo del Equipo
Este proyecto fue desarrollado bajo una simulación de entorno profesional continuo utilizando:

* **Feature Branches:** Una rama por cada módulo (ej. feature/domain-and-daos, feature/parking-services).
* **Pull Requests & Code Review:** Fusiones a la rama integradora develop previa revisión por pares y aprobaciones.
* **Resolución de Conflictos & Bugfix:** Manejo y documentación de Merge Conflicts y creación de ramas bugfix/ para parches.
* **Releases & Tags:** Versionamiento semántico del proyecto para entregas estables en la rama main.