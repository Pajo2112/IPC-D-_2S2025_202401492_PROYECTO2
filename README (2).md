## 🔧 Instalación y Ejecución

### Prerrequisitos
- Java JDK 8 o superior
- Biblioteca iText para generación de PDFs

### Compilación y Ejecución
```bash
# Compilar
javac -cp ".;itextpdf-5.5.13.3.jar" com/proyecto2/*.java com/proyecto2/**/*.java

# Ejecutar
java -cp ".;itextpdf-5.5.13.3.jar" com.proyecto2.Main
```

### Credenciales por Defecto
**Administrador:**
- Código: `admin`
- Contraseña: `IPC1D`

---

## 📋 Funcionalidades por Módulo

### 🔐 Módulo de Administración
- Gestión de usuarios (crear, actualizar, eliminar)
- Gestión de vendedores con CSV
- Gestión de productos con categorías específicas
- Generación de reportes PDF
- Importación/Exportación de datos

### 💼 Módulo de Vendedor
- Confirmación de pedidos pendientes
- Gestión de clientes
- Visualización de productos y stock
- Carga masiva de stock via CSV

### 🛍️ Módulo de Cliente
- Catálogo de productos
- Carrito de compras
- Realización de pedidos
- Historial de compras
- Seguimiento de pedidos

---

## 📊 Formatos CSV Soportados

### Productos
```text
codigo,nombre,categoria,precio,stock,atributo_especifico
PR-001,Computadora,TECNOLOGIA,5000.00,10,12
PR-002,Pan,ALIMENTO,5.00,50,15/12/2024
```

### Vendedores
```text
codigo,nombre,genero,contrasena
VE-001,Juan Perez,M,123ABC
VE-002,María Lopez,F,456DEF
```

---

## 🎯 Flujo de Trabajo
1. **Inicio de Sesión:** Autenticación por código y contraseña  
2. **Navegación:** Interfaz adaptada al rol del usuario  
3. **Gestión:** Operaciones CRUD según permisos  
4. **Reportes:** Generación de PDFs con datos actualizados  
5. **Persistencia:** Guardado automático cada 60 segundos  

---

## ⚙️ Configuración

### Archivos de Configuración
- `datastore.ser`: Base de datos serializada  
- `bitacora.txt`: Registro de actividades del sistema  
- Archivos CSV: Importación/Exportación de datos  

### Monitores del Sistema
- Sesiones Activas: Monitoreo cada 10 segundos  
- Pedidos Pendientes: Verificación cada 8 segundos  
- Estadísticas: Actualización cada 15 segundos  
- AutoSave: Guardado automático cada 60 segundos  

---

## 🐛 Solución de Problemas

**Error:** `"iText no en classpath"`  
```bash
# Descargar iText y agregar al classpath
java -cp ".;itextpdf-5.5.13.3.jar" com.proyecto2.Main
```

**Error:** `"Archivo datastore.ser corrupto"`  
- Eliminar `datastore.ser` para regenerar con datos por defecto  
- Se creará automáticamente con usuario admin  

**Error:** `"Permisos de archivo"`  
- Verificar permisos de escritura en el directorio actual  
- Asegurar que la aplicación pueda crear/modificar archivos  

---

## 📞 Soporte
Para reportar *issues* o solicitar características:
1. Verificar que el problema no esté documentado  
2. Revisar el archivo `bitacora.txt` para logs detallados  
3. Proporcionar información del sistema y pasos para reproducir  
