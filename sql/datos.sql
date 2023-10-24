#Projecto 2
#Integrantes: Maria Luz Cabral, Diaz Nahuel Victor.

#Selecciono la bd
USE parquimetros;

#----------valores----------------
INSERT INTO conductores (dni, nombre, apellido, direccion, telefono, registro)
VALUES
    (34567890, 'Carlos', 'López', 'Calle 456', '+54291456789', 765432),
    (45678901, 'Laura', 'Martínez', 'Avenida 789', '+54291567890', 654321),
    (56789012, 'Pedro', 'Rodríguez', 'Calle 1234', '+54291678901', 543210),
	(67890123, 'Sofía', 'García', 'Calle 567', '+54291543210', 432109),
    (78901234, 'Manuel', 'Fernández', 'Avenida 890', '+54291654321', 321098), 
    (89012345, 'Ana', 'Díaz', 'Calle 6789', '+54291567891', 210987),
    (90123456, 'Luis', 'Perez', 'Calle 12345', '+54291543211', 109876),
    (12345678, 'Marcela', 'Sánchez', 'Avenida 23456', '+54291432109', 987654),
    (23456789, 'David', 'López', 'Calle 34567', '+54291456790', 876543),
    (34567891, 'Elena', 'Torres', 'Avenida 45678', '+54291567892', 765433),
    (45678902, 'Carlos', 'Martínez', 'Calle 56789', '+54291678902', 654322),
    (56789013, 'Sara', 'Ramírez', 'Avenida 67890', '+54291432119', 543211),
    (67890124, 'Javier', 'Gómez', 'Calle 78901', '+54291456791', 432110),
    (78901235, 'Paula', 'Rodríguez', 'Avenida 12345', '+54291654322', 321099),
    (89012346, 'Mario', 'Hernández', 'Calle 23456', '+54291567893', 210988),
    (90123457, 'Isabel', 'Fuentes', 'Avenida 34567', '+54291543212', 109877),
    (12345679, 'Cristina', 'Ortega', 'Calle 45678', '+54291432129', 987655),
    (23456790, 'Antonio', 'Silva', 'Avenida 56789', '+54291567894', 876544);

INSERT INTO automoviles (patente, marca, modelo, color, dni)
VALUES
    ('ABC123', 'Toyota', 'Corolla', 'Rojo', 12345678),
    ('XYZ789', 'Ford', 'Fiesta', 'Azul', 23456789),
    ('DEF456','Toyota','Camby','Verde',34567890),
    ('GHI789', 'Honda','Civic','Plata',45678901),
    ('JKL123','Ford','Mustang','Azul' ,56789012),
    ('MNO456','Chevrolet','Silverado','Rojo',67890123),
    ('PQR789','Volkswagen','Jetta','Blanco',78901234),
    ('STU123','BMW', '3 series','Amarillo',89012345),
    ('VWX456','Mercedes-Benz','C-Class','Negro',90123456),
    ('YZA789','Nissan','Antima','Plata',12345678),
    ('BCD123','Subaru','Outback','Blanco',23456789),
    ('EFG456','Hyundai','Elantra','Rojo',34567891),
    ('HIJ789','Audi','A4','Amarillo',45678902),
    ('KLM123','Jeep','Wrangler','Azul',56789013),
    ('NOP456','Tesla','Model 3','Plata',67890124),
    ('QRS789','Kia','Sorento','Negro',78901235),
    ('TUV123','Mazda','CX-5','Plata',89012346),
    ('WXY456','Lexus','RX','Verde',90123457),
    ('ZAB789','Ford','Explorer','Rojo',12345679),
    ('CDE123','Chevrolet','Silverado','Azul',23456790);

INSERT INTO tipos_tarjeta (tipo, descuento)
VALUES
    ('Estudiante', 0.25),
    ('Adulto', 0.10),
    ('Regular', 0.0),
    ('Especial', 1.0);


INSERT INTO tarjetas (id_tarjeta, saldo, tipo, patente)
VALUES
    (1, 50.00, 'Estudiante', 'ABC123'),
    (2, 75.00, 'Adulto', 'XYZ789'),
    (3, 50.00, 'Estudiante','DEF456'),
    (4, 0.00, 'Adulto','GHI789'),
    (5, 100.00, 'Especial','MNO456'),
    (6, 60.00, 'Regular','PQR789'),
    (7, 10.00, 'Estudiante','STU123'),
    (8, 20.00, 'Especial','VWX456'),
    (9, 40.00, 'Especial','YZA789'),
    (10, 35.00, 'Adulto','BCD123'),
    (11, 39.00, 'Especial','EFG456'),
    (12, 28.00, 'Regular','HIJ789'),
    (13, 0.00, 'Estudiante','KLM123'),
    (14, 12.00, 'Especial','NOP456'),
    (15, 38.00, 'Adulto','QRS789'),
    (16, 62.00, 'Especial','TUV123'),
    (17, 56.00, 'Estudiante','WXY456'),
    (18, 99.00, 'Estudiante','ZAB789'),
    (19, 78.00, 'Regular','CDE123'),
		(20, 79.00, 'Especial', 'CDE123');


INSERT INTO recargas (id_tarjeta, fecha, hora, saldo_anterior, saldo_posterior)
VALUES
    (1, '2023-09-18', '10:00:00', 50.00, 75.00),
    (2, '2023-09-18', '11:00:00', 75.00, 100.00),
    (19, '2023-09-09','12:12:12',27.00,78.00),
    (18,'1998-05-25','23:59:00',00.00,99.00);

INSERT INTO inspectores (legajo, dni, nombre, apellido, password)
VALUES
    (1, 11111111, 'Inspector', 'Uno', md5('1')),
    (2, 22222222, 'Inspector', 'Dos', md5('password2')),
    (3,41099666,'Inspector','Diaz',md5('pass2023')),
    (4,40065733,'Mario','Gomez',md5('bd-2023'));

INSERT INTO ubicaciones (calle, altura, tarifa)
VALUES
    ('Calle 1', 100, 2.50),
    ('Calle 2', 200, 2.00),
    ('Calle 3', 300, 3.00),
    ('Avenida 99',600,2.00);

INSERT INTO parquimetros (id_parq, numero, calle, altura)
VALUES
    (1, 101, 'Calle 1', 100),
    (2, 202, 'Calle 2', 200),
    (3, 303, 'Calle 3', 300),
    (4, 404, 'Avenida 99',600),
    (5, 505, 'Calle 1', 100);

INSERT INTO estacionamientos (id_tarjeta, id_parq, fecha_ent, fecha_sal, hora_ent, hora_sal)
VALUES
    (1, 1, '2023-09-18', '2023-09-18', '10:15:00', '11:30:00'),
    (14,5,'2000-01-31',NULL,'11:24:00',NULL),
    (16,3,'2001-05-17','2001-05-19','11:58:33','10:12:24'),
    (2, 2, '2023-09-18', NULL, '11:45:00', NULL);

INSERT INTO accede (legajo, id_parq, fecha, hora)
VALUES
    (1, 1, '2023-09-18', '10:00:00'),
    (2, 2, '2023-09-18', '11:00:00');

INSERT INTO asociado_con (legajo, calle, altura, dia, turno)
VALUES
    (1, 'Calle 1', 100, 'lu', 't'),
    (1, 'Calle 1', 100, 'ma', 't'),
    (2, 'Calle 2', 200, 'ma', 'm'),
    (3,'Calle 3',300,'mi','t'),
    (4,'Avenida 99',600, 'vi','m');

INSERT INTO multa (numero, fecha, hora, patente, id_asociado_con)
VALUES
    (1, '2023-09-18', '10:30:00', 'ABC123', 1),
    (2, '2023-09-18', '11:15:00', 'XYZ789', 2);
