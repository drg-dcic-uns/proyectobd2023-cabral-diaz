#Projecto 2
#Integrantes: Maria Luz Cabral, Diaz Nahuel Victor.
#creo la dabe de datos
CREATE DATABASE parquimetros;

#selecciono la base de datos a utilizar
USE parquimetros;

#Creacion de tablas para las entidades

CREATE TABLE conductores (
	dni INT UNSIGNED NOT NULL,
	nombre VARCHAR(45) NOT NULL,
	apellido VARCHAR(45) NOT NULL,
	direccion VARCHAR(45) NOT NULL,
	telefono VARCHAR(45),
	registro INT UNSIGNED NOT NULL,

	CONSTRAINT pk_conductores
	PRIMARY KEY (dni)
) ENGINE = InnoDB;

CREATE TABLE automoviles (
	patente VARCHAR(6) NOT NULL,
	marca VARCHAR(45) NOT NULL,
	modelo VARCHAR(45) NOT NULL,
	color VARCHAR(45) NOT NULL,
	dni INT UNSIGNED NOT NULL,

	CONSTRAINT pk_automoviles
	PRIMARY KEY (patente),

	CONSTRAINT FK_automoviles_conductores
	FOREIGN KEY (dni) REFERENCES conductores (dni)
		ON DELETE RESTRICT ON UPDATE CASCADE
)ENGINE = InnoDB;

CREATE TABLE tipos_tarjeta(
	tipo VARCHAR(45) NOT NULL,
	descuento DECIMAL(3,2) UNSIGNED NOT NULL CHECK(descuento >= 0 and descuento <= 1),

	CONSTRAINT pk_tipos_tarjeta
	PRIMARY KEY (tipo)
) ENGINE=InnoDB;

CREATE TABLE tarjetas(
	id_tarjeta INT UNSIGNED NOT NULL AUTO_INCREMENT,
	saldo DECIMAL(5,2) NOT NULL,
	tipo VARCHAR(45) NOT NULL,
	patente VARCHAR(6) NOT NULL,

	CONSTRAINT pk_tarjetas
	PRIMARY KEY (id_tarjeta),

	CONSTRAINT FK_tarjetas_tipos_tarjeta
	FOREIGN KEY (tipo) REFERENCES tipos_tarjeta (tipo)
	ON DELETE RESTRICT ON UPDATE CASCADE,

	CONSTRAINT FK_tarjetas_automoviles
	FOREIGN KEY (patente) REFERENCES automoviles (patente)
	ON DELETE RESTRICT ON UPDATE CASCADE
)ENGINE=InnoDB;

CREATE TABLE recargas(
	id_tarjeta INT UNSIGNED NOT NULL AUTO_INCREMENT,
	fecha DATE NOT NULL,
	hora TIME NOT NULL,
	saldo_anterior DECIMAL(5,2) NOT NULL,
	saldo_posterior DECIMAL(5,2) NOT NULL,

	CONSTRAINT pk_recargas
	PRIMARY KEY (id_tarjeta,fecha,hora),

	CONSTRAINT FK_recargas_tarjetas
	FOREIGN KEY (id_tarjeta) REFERENCES tarjetas(id_tarjeta)
	ON DELETE CASCADE ON UPDATE CASCADE
)ENGINE=InnoDB;

CREATE TABLE inspectores(
	legajo INT UNSIGNED NOT NULL,
	dni INT UNSIGNED NOT NULL,
	nombre VARCHAR(45) NOT NULL,
	apellido VARCHAR(45) NOT NULL,
	password VARCHAR(32) NOT NULL,

	CONSTRAINT pk_inspectores
	PRIMARY KEY (legajo)
)ENGINE=InnoDB;

CREATE TABLE ubicaciones(
	calle VARCHAR(32) NOT NULL,
	altura INT UNSIGNED NOT NULL,
	tarifa DECIMAL(5,2) UNSIGNED NOT NULL,

	CONSTRAINT pk_ubicaciones
	PRIMARY KEY (calle,altura)
)ENGINE=InnoDB;

CREATE TABLE parquimetros(
	id_parq INT UNSIGNED NOT NULL,
	numero INT UNSIGNED NOT NULL,
	calle VARCHAR(32) NOT NULL,
	altura INT UNSIGNED NOT NULL,

	CONSTRAINT pk_parquimetros
	PRIMARY KEY (id_parq),

	CONSTRAINT FK_parquimetros_ubicacion
	FOREIGN KEY (calle,altura) REFERENCES ubicaciones(calle,altura)
	ON DELETE RESTRICT ON UPDATE CASCADE
)ENGINE=InnoDB;

CREATE TABLE estacionamientos(
	id_tarjeta INT UNSIGNED NOT NULL,
	id_parq	INT UNSIGNED NOT NULL,
	fecha_ent DATE NOT NULL,
	fecha_sal DATE,
	hora_ent TIME NOT NULL,
	hora_sal TIME,

	CONSTRAINT pk_estacionamientos
	PRIMARY KEY (fecha_ent,hora_ent,id_parq),

	CONSTRAINT FK_estacionamientos_tarjeta
	FOREIGN KEY (id_tarjeta) REFERENCES tarjetas (id_tarjeta)
	ON DELETE RESTRICT ON UPDATE CASCADE,

	CONSTRAINT FK_estacionamientos_parquimetros
	FOREIGN KEY (id_parq) REFERENCES parquimetros (id_parq)
	ON DELETE RESTRICT ON UPDATE CASCADE
)ENGINE=InnoDB;

CREATE TABLE accede(
	legajo INT UNSIGNED NOT NULL,
	id_parq INT UNSIGNED NOT NULL,
	fecha DATE NOT NULL,
	hora TIME NOT NULL,

	CONSTRAINT pk_accede
	PRIMARY KEY (id_parq,fecha,hora),

	CONSTRAINT FK_accede_parquimetros
	FOREIGN KEY (id_parq) REFERENCES parquimetros(id_parq)
	ON DELETE RESTRICT ON UPDATE CASCADE,

	CONSTRAINT FK_accede_inspectores
	FOREIGN KEY (legajo) REFERENCES inspectores(legajo)
	ON DELETE RESTRICT ON UPDATE CASCADE
)ENGINE=InnoDB;

CREATE TABLE asociado_con(
	id_asociado_con INT UNSIGNED NOT NULL AUTO_INCREMENT,
	legajo INT UNSIGNED NOT NULL,
	calle VARCHAR(32) NOT NULL,
	altura INT UNSIGNED NOT NULL,
	dia	enum('do','lu','ma','mi','ju','vi','sa') NOT NULL,
	turno enum('m','t') NOT NULL,
	
	CONSTRAINT pk_asociado_con
	PRIMARY KEY (id_asociado_con),

	CONSTRAINT FK_asociado_con_inspectores
	FOREIGN KEY (legajo) REFERENCES inspectores (legajo)
	ON DELETE RESTRICT ON UPDATE CASCADE,

	CONSTRAINT FK_asociado_con_ubicaciones
	FOREIGN KEY (calle,altura) REFERENCES ubicaciones (calle,altura)
	ON DELETE RESTRICT ON UPDATE CASCADE
)ENGINE=InnoDB;

CREATE TABLE multa(
	numero INT UNSIGNED NOT NULL AUTO_INCREMENT,
	fecha DATE NOT NULL,
	hora TIME NOT NULL,
	patente VARCHAR(6) NOT NULL,
	id_asociado_con INT UNSIGNED NOT NULL,

	CONSTRAINT pk_multa
	PRIMARY KEY(numero),

	CONSTRAINT FK_multa_automoviles
	FOREIGN KEY (patente) REFERENCES automoviles (patente)
	ON DELETE RESTRICT ON UPDATE CASCADE,

	CONSTRAINT FK_multa_asociado_con
	FOREIGN KEY (id_asociado_con) REFERENCES asociado_con (id_asociado_con)
	ON DELETE RESTRICT ON UPDATE CASCADE
)ENGINE=InnoDB;

#-----------------------------------------------------------------------------------------------

# Creaci�n de vistas 
# estacionados = Datos de todos los autos estacionados

CREATE VIEW estacionados AS
	SELECT calle,altura,patente, fecha_ent, hora_ent
	FROM estacionamientos NATURAL JOIN parquimetros NATURAL JOIN tarjetas
	WHERE fecha_sal IS NULL AND hora_sal IS NULL;

#-----------------------------------------------------------------------------------------------

#creacion del SP conectar

delimiter !
CREATE PROCEDURE conectar(IN id_tarjeta INTEGER, IN id_parq INTEGER)
# Transacción para la apertura o cierre de un estacionamiento.
BEGIN
# Declaro las variables locales que voy a utilizar
	 DECLARE descuento_tarjeta DECIMAL(3,2);
	 DECLARE fecha_ent_automovil DATE;
	 DECLARE hora_ent_automovil TIME;
	 DECLARE tarifa_parquimetro DECIMAL(5,2);
	 DECLARE saldo_actual_tarjeta DECIMAL(5,2);
	 DECLARE tiempo_minutos INTEGER;
	 DECLARE ahora TIMESTAMP;
	 DECLARE fecha_actual DATE;
	 DECLARE hora_actual TIME;

# Declaro variables locales para recuperar los errores
	 DECLARE codigo_SQL  CHAR(5) DEFAULT '00000';
	 DECLARE codigo_MYSQL INT DEFAULT 0;
	 DECLARE mensaje_error TEXT;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN # Si se produce una SQLEXCEPTION, se retrocede la transacción con ROLLBACK
    GET DIAGNOSTICS CONDITION 1  codigo_MYSQL= MYSQL_ERRNO,
                                codigo_SQL= RETURNED_SQLSTATE,
                                mensaje_error= MESSAGE_TEXT;
    SELECT 'SQLEXCEPTION!, transacción abortada' AS resultado, codigo_MySQL, codigo_SQL,  mensaje_error;
    ROLLBACK;
    END;
    SELECT now() INTO ahora;
    SELECT curtime() INTO hora_actual;
    SELECT curdate() INTO fecha_actual;
START TRANSACTION;
IF NOT EXISTS (
        SELECT * FROM tarjetas AS t
        WHERE t.id_tarjeta = id_tarjeta
    )
    THEN
        SELECT 'La tarjeta no existe' AS resultado;
    ELSE
        IF NOT EXISTS (
            SELECT * FROM parquimetros AS p
            WHERE p.id_parq = id_parq
        )
        THEN SELECT 'El parquimetro no existe' AS resultado;
        ELSE
IF EXISTS (
    SELECT * FROM estacionamientos AS e
    WHERE e.id_tarjeta = id_tarjeta
    AND e.id_parq = id_parq
    AND e.hora_sal IS NULL
    AND e.fecha_sal IS NULL
    )
THEN
    # Operacion de CLAUSURA
    SELECT descuento INTO descuento_tarjeta
    FROM tarjetas AS t NATURAL JOIN tipos_tarjeta AS tt WHERE t.id_tarjeta = id_tarjeta;
    SELECT e.fecha_ent INTO fecha_ent_automovil
    FROM estacionamientos AS e WHERE e.id_parq = id_parq
    AND e.hora_sal IS NULL
    AND e.fecha_sal IS NULL;
    SELECT e.hora_ent INTO hora_ent_automovil
    FROM estacionamientos AS e WHERE e.id_parq = id_parq
    AND e.hora_sal IS NULL
    AND e.fecha_sal IS NULL;
    SELECT tarifa INTO tarifa_parquimetro
    FROM parquimetros AS p NATURAL JOIN ubicaciones WHERE p.id_parq = id_parq;
    UPDATE estacionamientos AS e SET fecha_sal = fecha_actual,hora_sal = hora_actual
    WHERE e.id_parq = id_parq
    AND e.id_tarjeta = id_tarjeta
    AND e.hora_sal IS NULL
    AND e.fecha_sal IS NULL;
    SELECT FLOOR(TIME_TO_SEC(TIMEDIFF(ahora, TIMESTAMP(fecha_ent_automovil,hora_ent_automovil))) / 60) INTO tiempo_minutos;
    SELECT t.saldo INTO saldo_actual_tarjeta FROM tarjetas AS t WHERE t.id_tarjeta = id_tarjeta;
    UPDATE tarjetas AS t SET t.saldo = (saldo_actual_tarjeta - (tiempo_minutos * tarifa_parquimetro * (1 - descuento_tarjeta)))
    WHERE t.id_tarjeta = id_tarjeta;
    SELECT 'CIERRE' AS tipo_operacion,
        'La operacion se realizo con exito' AS resultado,
        tiempo_minutos AS minutos_transcurridos,
        t.saldo,
        e.fecha_ent,
        e.fecha_sal,
        e.hora_ent,
        e.hora_sal
    FROM tarjetas AS t NATURAL JOIN estacionamientos AS e
    WHERE t.id_tarjeta = id_tarjeta
    AND e.id_parq = id_parq
    AND e.hora_sal = hora_actual
    AND e.fecha_sal = fecha_actual;
ELSE
    # Operacion de apertura
    SELECT t.saldo INTO saldo_actual_tarjeta
        FROM tarjetas AS t
        WHERE t.id_tarjeta = id_tarjeta;
    IF saldo_actual_tarjeta > 0
    THEN
        SELECT tt.descuento INTO descuento_tarjeta
        FROM tarjetas AS t NATURAL JOIN tipos_tarjeta AS tt WHERE t.id_tarjeta = id_tarjeta;
        SELECT tarifa INTO tarifa_parquimetro
        FROM parquimetros AS p NATURAL JOIN ubicaciones WHERE p.id_parq = id_parq;
        SELECT (saldo_actual_tarjeta / (tarifa_parquimetro * (1 - descuento_tarjeta))) INTO tiempo_minutos;
        INSERT INTO estacionamientos (id_tarjeta, id_parq, fecha_ent, fecha_sal, hora_ent, hora_sal)
        VALUES (id_tarjeta, id_parq, fecha_actual, NULL, hora_actual, NULL);
        SELECT
        'APERTURA' AS tipo_operacion,
        'La operacion se realizo con exito' AS resultado,
        tiempo_minutos AS minutos_disponibles,
        fecha_actual AS fecha_ent,
        hora_actual AS hora_ent;
    ELSE
        SELECT
        'APERTURA' AS tipo_operacion,
        'La tarjeta no tiene saldo suficiente' AS resultado;
    END IF;
END IF;
END IF;
END IF;

COMMIT;
END; !
delimiter ;


#-----------------------------------------------------------------------------------------------
#Creacion de triggers

delimiter !
CREATE TRIGGER saldo_update
AFTER UPDATE ON tarjetas
FOR EACH ROW
BEGIN
IF OLD.saldo < NEW.saldo
THEN
	INSERT INTO recargas (id_tarjeta, fecha, hora, saldo_anterior, saldo_posterior)
	VALUES(OLD.id_tarjeta, curdate(), curtime(),OLD.saldo, NEW.saldo);
END; !
delimiter ;

#-----------------------------------------------------------------------------------------------
#creacion de usuarios y privilegios

#creacion del usuario admin
CREATE USER 'admin'@'localhost' IDENTIFIED BY 'admin';
GRANT ALL PRIVILEGES ON parquimetros.* TO 'admin'@'localhost' WITH GRANT OPTION;

#creacion del usuario venta
CREATE USER 'venta'@'%' IDENTIFIED BY 'venta';
GRANT INSERT ON parquimetros.recargas TO 'venta'@'%';
GRANT SELECT ON parquimetros.tarjetas TO 'venta'@'%';
GRANT INSERT ON parquimetros.tarjetas TO 'venta'@'%';
GRANT UPDATE ON parquimetros.tarjetas TO 'venta'@'%';

#creacion del usuario inspector
CREATE USER 'inspector'@'%' IDENTIFIED BY 'inspector';
GRANT SELECT ON parquimetros.inspectores TO 'inspector'@'%';
GRANT SELECT ON parquimetros.automoviles TO 'inspector'@'%';
GRANT SELECT ON parquimetros.estacionados TO 'inspector'@'%';
GRANT INSERT ON parquimetros.multa TO 'inspector'@'%';
GRANT INSERT ON parquimetros.accede TO 'inspector'@'%';
GRANT SELECT ON parquimetros.asociado_con TO 'inspector'@'%';
GRANT SELECT ON parquimetros.ubicaciones TO 'inspector'@'%';
GRANT SELECT ON parquimetros.parquimetros TO 'inspector'@'%';

#creacion del usuario parquimetro
CREATE USER 'parquimetro'@'%' IDENTIFIED BY 'parq';
GRANT EXECUTE ON PROCEDURE conectar TO 'parquimetro'@'%';
GRANT SELECT ON parquimetros.tarjetas TO 'parquimetro'@'%';
GRANT SELECT ON parquimetros.tipos_tarjeta TO 'parquimetro'@'%';
GRANT SELECT ON parquimetros.automoviles TO 'parquimetro'@'%';
GRANT SELECT ON parquimetros.conductores TO 'parquimetro'@'%';
GRANT SELECT ON parquimetros.ubicaciones TO 'parquimetro'@'%';
GRANT SELECT ON parquimetros.parquimetros TO 'parquimetro'@'%';
