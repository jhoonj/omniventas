-- Tabla de Roles
CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(50) UNIQUE NOT NULL
);

-- Tabla de Usuarios
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100),
    email VARCHAR(100) UNIQUE,
    contrasena_hash TEXT,
    rol_id BIGINT REFERENCES roles(id) ON DELETE SET NULL
);

-- Tabla de Clientes
CREATE TABLE IF NOT EXISTS clientes (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100),
    email VARCHAR(100),
    telefono VARCHAR(20),
    direccion TEXT
);

-- Tabla de Fórmulas Médicas
CREATE TABLE IF NOT EXISTS formulas_medicas (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT REFERENCES clientes(id) ON DELETE CASCADE,
    fecha_emision DATE,
    od_esfera NUMERIC(5,2),
    od_cilindro NUMERIC(5,2),
    od_eje INT,
    oi_esfera NUMERIC(5,2),
    oi_cilindro NUMERIC(5,2),
    oi_eje INT,
    distancia_interpupilar NUMERIC(5,2),
    observaciones TEXT
);

-- Tabla de Proveedores
CREATE TABLE IF NOT EXISTS proveedores (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100),
    email VARCHAR(100),
    telefono VARCHAR(20),
    direccion TEXT
);

-- Tabla de Productos
CREATE TABLE IF NOT EXISTS productos (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100),
    descripcion TEXT,
    tipo VARCHAR(10),
    precio NUMERIC(10,2),
    stock INT,
    proveedor_id BIGINT REFERENCES proveedores(id) ON DELETE SET NULL
);

-- Tabla de Estados de Pedido
CREATE TABLE IF NOT EXISTS estados_pedido (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(20) UNIQUE NOT NULL
);

-- Tabla de Pedidos
CREATE TABLE IF NOT EXISTS pedidos (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT REFERENCES clientes(id) ON DELETE CASCADE,
    fecha_pedido TIMESTAMP,
    estado_id BIGINT REFERENCES estados_pedido(id) ON DELETE SET NULL,
    total NUMERIC(10,2),
    formula_id BIGINT REFERENCES formulas_medicas(id) ON DELETE SET NULL
);

-- Tabla de Detalle de Pedidos
CREATE TABLE IF NOT EXISTS detalle_pedidos (
    id BIGSERIAL PRIMARY KEY,
    pedido_id BIGINT REFERENCES pedidos(id) ON DELETE CASCADE,
    producto_id BIGINT REFERENCES productos(id) ON DELETE SET NULL,
    cantidad INT,
    precio_unitario NUMERIC(10,2)
);

-- Tabla de Métodos de Pago
CREATE TABLE IF NOT EXISTS metodos_pago (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(50) UNIQUE NOT NULL
);

-- Tabla de Pagos
CREATE TABLE IF NOT EXISTS pagos (
    id BIGSERIAL PRIMARY KEY,
    pedido_id BIGINT REFERENCES pedidos(id) ON DELETE CASCADE,
    fecha_pago TIMESTAMP,
    metodo_pago_id BIGINT REFERENCES metodos_pago(id) ON DELETE SET NULL,
    monto NUMERIC(10,2),
    estado VARCHAR(20)
);

-- Tabla de Estados de Envío
CREATE TABLE IF NOT EXISTS estados_envio (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(50) UNIQUE NOT NULL
);

-- Tabla de Envíos
CREATE TABLE IF NOT EXISTS envios (
    id BIGSERIAL PRIMARY KEY,
    pedido_id BIGINT REFERENCES pedidos(id) ON DELETE CASCADE,
    direccion_envio TEXT,
    empresa_envio VARCHAR(100),
    numero_guia VARCHAR(100),
    estado_envio_id BIGINT REFERENCES estados_envio(id) ON DELETE SET NULL,
    fecha_envio DATE,
    fecha_entrega_estimada DATE
);
