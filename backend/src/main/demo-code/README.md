# 🧪 Clases Java de Ejemplo para Testing

Este directorio contiene **5 clases Java** con múltiples problemas de código intencionados para probar el **Agente de Revisión de Pull Requests**.

## 📋 **Clases Disponibles**

### 1. 👤 **UserService.java**
**Propósito**: Servicio de gestión de usuarios  
**Problemas incluidos**:
- ❌ Password hardcodeado
- ❌ Falta de JavaDoc
- ❌ Métodos sin tests
- ❌ Nomenclatura incorrecta (snake_case vs camelCase)
- ❌ Métodos complejos sin documentación

### 2. 📦 **OrderService.java** 
**Propósito**: Servicio de gestión de pedidos  
**Problemas incluidos**:
- ❌ Campos públicos en lugar de private
- ❌ Constructor sin validación
- ❌ Múltiples responsabilidades en un método
- ❌ Números mágicos
- ❌ Métodos sin sincronización
- ❌ Lógica de negocio hardcodeada
- ❌ Complejidad ciclomática alta

### 3. 🏪 **ProductManager.java**
**Propósito**: Gestor de productos e inventario  
**Problemas incluidos**:
- ❌ Variables estáticas mutables
- ❌ API key hardcodeada
- ❌ Atributos sin encapsulación
- ❌ Raw types (tipos sin parametrizar)
- ❌ Múltiples return statements
- ❌ Operaciones costosas sin optimización
- ❌ Side effects no documentados
- ❌ Nested loops con alta complejidad

### 4. 🗄️ **DatabaseHelper.java**
**Propósito**: Helper para operaciones de base de datos  
**Problemas incluidos**:
- ❌ Sin package declaration
- ❌ Credenciales hardcodeadas
- ❌ Singleton mal implementado
- ❌ SQL injection vulnerable
- ❌ Recursos sin cerrar
- ❌ printStackTrace() en lugar de logging
- ❌ Memory leaks potenciales
- ❌ Demasiados parámetros en métodos

### 5. 🔐 **SecurityUtils.java**
**Propósito**: Utilidades de seguridad  
**Problemas incluidos**:
- ❌ Weak encryption con claves hardcodeadas
- ❌ Salt fijo (no aleatorio)
- ❌ Variables compartidas sin sincronización
- ❌ MD5 para hashing (débil)
- ❌ Validación de password insuficiente
- ❌ Logging de credenciales
- ❌ Tokens predecibles
- ❌ Race conditions
- ❌ Hardcoded backdoor
- ❌ Información sensible expuesta

---

## 🎯 **Tipos de Problemas Cubiertos**

### 🔒 **Seguridad**
- Credenciales hardcodeadas
- SQL injection
- Weak encryption
- Información sensible en logs
- Backdoors
- Validación insuficiente

### 🏗️ **Arquitectura y Diseño**
- Violación de Single Responsibility Principle
- Múltiples responsabilidades
- Acoplamiento fuerte
- Singleton mal implementado
- Exposición de detalles internos

### 🧹 **Calidad de Código**
- Falta de JavaDoc
- Nomenclatura incorrecta
- Raw types
- Números mágicos
- Métodos complejos
- Multiple return statements

### ⚡ **Rendimiento**
- Memory leaks
- Recursos sin cerrar
- Operaciones ineficientes
- Nested loops
- Operaciones costosas sin optimización

### 🧵 **Concurrencia**
- Race conditions
- Variables compartidas sin sincronización
- Operaciones no atómicas

### 🧪 **Testing**
- Métodos sin tests unitarios
- Constructores que pueden fallar
- Métodos complejos sin coverage

---

## 🚀 **Cómo Usar Estas Clases**

### 1. **Para Probar el Agente de Revisión**
```bash
# Crear un PR simulado con estos archivos
cp scripts/demo-code/*.java src/demo/

# Enviar webhook simulado
curl -X POST http://localhost:3000/webhook/github \
  -H "Content-Type: application/json" \
  -d @scripts/demo-webhook-payload.json
```

### 2. **Para Testing Manual**
```bash
# Analizar archivo específico
node src/ai-engine/analyzer.js scripts/demo-code/UserService.java

# Generar correcciones automáticas
node src/auto-fix/fixGenerator.js scripts/demo-code/SecurityUtils.java
```

### 3. **Para Benchmarking**
```bash
# Procesar todas las clases y medir tiempo
time for file in scripts/demo-code/*.java; do
  echo "Analizando: $file"
  node src/ai-engine/analyzer.js "$file"
done
```

---

## 📊 **Problemas Esperados por Clase**

| Clase | Problemas de Seguridad | Problemas de Calidad | Problemas de Rendimiento | Total Estimado |
|-------|----------------------|---------------------|--------------------------|----------------|
| UserService | 1 | 4 | 1 | **~6** |
| OrderService | 0 | 7 | 2 | **~9** |
| ProductManager | 1 | 6 | 3 | **~10** |
| DatabaseHelper | 4 | 5 | 4 | **~13** |
| SecurityUtils | 8 | 4 | 2 | **~14** |
| **TOTAL** | **14** | **26** | **12** | **~52** |

---

## 🎯 **Objetivos de Testing**

### ✅ **Detección de Problemas**
- El agente debe detectar al menos **80%** de los problemas
- Clasificar correctamente por severidad (high/medium/low)
- Identificar tipos específicos (security/performance/maintainability)

### ✅ **Generación de Sugerencias**
- Proporcionar sugerencias concretas y accionables
- Incluir ejemplos de código corregido
- Priorizar problemas por impacto

### ✅ **Auto-corrección**
- Corregir automáticamente problemas simples
- Generar PRs con correcciones
- Mantener funcionalidad original

---

## 🔧 **Configuración para Testing**

### 📝 **Variables de Entorno Recomendadas**
```bash
# Para testing exhaustivo
export OPENAI_MODEL="gpt-4"
export AI_ANALYSIS_DEPTH="detailed"
export AUTO_FIX_ENABLED="true"

# Para testing rápido
export OPENAI_MODEL="gpt-3.5-turbo"
export AI_ANALYSIS_DEPTH="basic"
export AUTO_FIX_ENABLED="false"
```

### ⚙️ **Configuración de Umbrales**
```javascript
// En configuración del sistema
{
  "review_approval_threshold": 30,  // Bajo para testing
  "auto_fix_enabled": true,
  "max_processing_time_ms": 180000, // 3 minutos
  "notification_threshold_low_approval": 20
}
```

---

## 📈 **Métricas de Éxito**

### 🎯 **KPIs Esperados**
- **Tasa de detección**: >80% de problemas identificados
- **Tiempo de análisis**: <60 segundos por clase
- **Precisión**: <10% falsos positivos
- **Auto-corrección**: >50% de problemas simples corregidos

### 📊 **Reportes Esperados**
- Score promedio: **20-40** (debido a múltiples problemas)
- Tasa de aprobación: **0%** (todas deberían ser rechazadas)
- Issues detectados: **8-15 por clase**
- Sugerencias: **5-10 por clase**

---

## 🚀 **Comandos Útiles**

```bash
# Limpiar datos previos
./scripts/quick-cleanup.sh

# Analizar todas las clases
for file in scripts/demo-code/*.java; do
  echo "=== Analizando: $(basename $file) ==="
  # Simular análisis con webhook
done

# Ver resultados en dashboard
open http://localhost:3000/dashboard.html

# Verificar base de datos
sqlite3 database/reviews.db "
  SELECT repository, title, approved, score, 
         COUNT(*) as issues_count 
  FROM reviews 
  WHERE repository LIKE '%demo%' 
  GROUP BY repository, title;
"
```

**¡Las clases están listas para testing del sistema de revisión de PRs!** 🎉
