package parquimetros.modelo.parquimetro;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import parquimetros.modelo.ModeloImpl;
import parquimetros.modelo.beans.*;
import parquimetros.modelo.parquimetro.dto.EntradaEstacionamientoDTOImpl;
import parquimetros.modelo.parquimetro.dto.EstacionamientoDTO;
import parquimetros.modelo.parquimetro.dto.SalidaEstacionamientoDTOImpl;
import parquimetros.modelo.parquimetro.exception.ParquimetroNoExisteException;
import parquimetros.modelo.parquimetro.exception.SinSaldoSuficienteException;
import parquimetros.modelo.parquimetro.exception.TarjetaNoExisteException;
import parquimetros.utils.Mensajes;
import parquimetros.utils.Parsing;

import java.sql.SQLException;
import java.util.ArrayList;

public class ModeloParquimetroImpl extends ModeloImpl implements ModeloParquimetro {

	private static Logger logger = LoggerFactory.getLogger(ModeloParquimetroImpl.class);
	
	@Override
	public ArrayList<TarjetaBean> recuperarTarjetas() throws Exception {
		logger.info(Mensajes.getMessage("ModeloParquimetroImpl.recuperarTarjetas.logger"));
		/** 
		 * TODO [preguntar] Debe retornar una lista de UbicacionesBean con todas las tarjetas almacenadas en la B.D.
		 *      Deberia propagar una excepción si hay algún error en la consulta.
		 *      
		 *      Importante: Para acceder a la B.D. utilice la propiedad this.conexion (de clase Connection) 
		 *      que se hereda al extender la clase ModeloImpl. 
		 */
		ArrayList<TarjetaBean> tarjetas = new ArrayList<>();

		/* Datos estáticos de prueba. Quitar y reemplazar por código que recupera las ubicaciones de la B.D. en una lista de UbicacionesBean
		DAOTarjetasDatosPrueba.poblar();
		
		for (TarjetaBean ubicacion : DAOTarjetasDatosPrueba.datos.values()) {
			tarjetas.add(ubicacion);	
		}
		// Fin datos estáticos de prueba.
		*/

		String sql = "SELECT * from parquimetros.tarjetas " +
				"NATURAL JOIN parquimetros.tipos_tarjeta " +
				"NATURAL JOIN parquimetros.automoviles " +
				"NATURAL JOIN parquimetros.conductores ";

		try {

			java.sql.ResultSet rs = this.consulta(sql);

			String patente,tipo,marca,modelo,color,nombre,apellido,direccion,telefono;
			int idTarjeta,dni,registro;
			double saldo,descuento;

			while (rs.next()) {

				patente = rs.getString("patente");
				tipo = rs.getString("tipo");
				marca = rs.getString("marca");
				modelo = rs.getString("modelo");
				color = rs.getString("color");
				nombre = rs.getString("nombre");
				apellido = rs.getString("apellido");
				direccion = rs.getString("direccion");
				telefono = rs.getString("telefono");
				idTarjeta = rs.getInt("id_tarjeta");
				dni = rs.getInt("dni");
				registro = rs.getInt("registro");
				saldo = rs.getDouble("saldo");
				descuento = rs.getDouble("descuento");

				ConductorBean conductor = new ConductorBeanImpl();
				conductor.setApellido(apellido);
				conductor.setNombre(nombre);
				conductor.setDireccion(direccion);
				conductor.setRegistro(registro);
				conductor.setNroDocumento(dni);
				conductor.setTelefono(telefono);
				AutomovilBean auto = new AutomovilBeanImpl();
				auto.setModelo(modelo);
				auto.setColor(color);
				auto.setPatente(patente);
				auto.setMarca(marca);
				auto.setConductor(conductor);
				TipoTarjetaBean tipoTarjeta = new TipoTarjetaBeanImpl();
				tipoTarjeta.setTipo(tipo);
				tipoTarjeta.setDescuento(descuento);
				TarjetaBean tarjeta = new TarjetaBeanImpl();
				tarjeta.setId(idTarjeta);
				tarjeta.setSaldo(saldo);
				tarjeta.setAutomovil(auto);
				tarjeta.setTipoTarjeta(tipoTarjeta);
				tarjetas.add(tarjeta);
			}
			rs.close();
			return tarjetas;
		} catch (SQLException ex) {
			logger.error("SQLException: " + ex.getMessage());
			logger.error("SQLState: " + ex.getSQLState());
			logger.error("VendorError: " + ex.getErrorCode());
			ex.printStackTrace();
			throw new Exception("Se produjo un error en la consulta: " + ex.getMessage());
		}
	}
	
	/*
	 * Atención: Este codigo de recuperarUbicaciones (como el de recuperarParquimetros) es igual en el modeloParquimetro 
	 *           y en modeloInspector. Se podría haber unificado en un DAO compartido. Pero se optó por dejarlo duplicado
	 *           porque tienen diferentes permisos ambos usuarios y quizas uno estaría tentado a seguir agregando metodos
	 *           que van a resultar disponibles para ambos cuando los permisos de la BD no lo permiten.
	 */	
	@Override
	public ArrayList<UbicacionBean> recuperarUbicaciones() throws Exception {
		
		logger.info(Mensajes.getMessage("ModeloParquimetroImpl.recuperarUbicaciones.logger"));
		
		/** 
		 * TODO [preguntar] Debe retornar una lista de UbicacionesBean con todas las ubicaciones almacenadas en la B.D.
		 *      Deberia propagar una excepción si hay algún error en la consulta.
		 *      
		 *      Importante: Para acceder a la B.D. utilice la propiedad this.conexion (de clase Connection) 
		 *      que se hereda al extender la clase ModeloImpl. 
		 */
		ArrayList<UbicacionBean> ubicaciones = new ArrayList<>();

		/* Datos estáticos de prueba. Quitar y reemplazar por código que recupera las ubicaciones de la B.D. en una lista de UbicacionesBean
		DAOUbicacionesDatosPrueba.poblar();
		
		for (UbicacionBean ubicacion : DAOUbicacionesDatosPrueba.datos.values()) {
			ubicaciones.add(ubicacion);	
		}
		// Fin datos estáticos de prueba.
		*/
		String sql = "select * from parquimetros.ubicaciones";

		try {

			java.sql.ResultSet rs = this.consulta(sql);

			String calle;
			int altura;
			double tarifa;

			while (rs.next()) {

				calle = rs.getString("calle");
				altura = Integer.parseInt(rs.getString("altura"));
				tarifa = Parsing.parseMonto(rs.getString("tarifa"));

				UbicacionBean ubi = new UbicacionBeanImpl();
				ubi.setCalle(calle);
				ubi.setAltura(altura);
				ubi.setTarifa(tarifa);

				ubicaciones.add(ubi);
			}
			rs.close();
			return ubicaciones;
		} catch (SQLException ex) {
			logger.error("SQLException: " + ex.getMessage());
			logger.error("SQLState: " + ex.getSQLState());
			logger.error("VendorError: " + ex.getErrorCode());
			ex.printStackTrace();
			throw new Exception("Se produjo un error en la consulta: " + ex.getMessage());
		}
	}

	@Override
	public ArrayList<ParquimetroBean> recuperarParquimetros(UbicacionBean ubicacion) throws Exception {
		logger.info(Mensajes.getMessage("ModeloParquimetroImpl.recuperarParquimetros.logger"));
		
		/** 
		 * TODO [preguntar] Debe retornar una lista de ParquimetroBean con todos los parquimetros que corresponden a una ubicación.
		 * 		 
		 *      Debería propagar una excepción si hay algún error en la consulta.
		 *      
		 *      Importante: Para acceder a la B.D. utilice la propiedad this.conexion (de clase Connection) 
		 *      que se hereda al extender la clase ModeloImpl. 
		 */

		ArrayList<ParquimetroBean> parquimetros = new ArrayList<>();

		/* datos de prueba
		DAOParquimetrosDatosPrueba.poblar(ubicacion);
		
		for (ParquimetroBean parquimetro : DAOParquimetrosDatosPrueba.datos.values()) {
			parquimetros.add(parquimetro);	
		}
		// Fin datos estáticos de prueba.

		 */

		String sql = "select * from parquimetros.parquimetros as p " +
				"where p.calle = '"+ubicacion.getCalle() + "'" +
				"and p.altura ="+ubicacion.getAltura();

		try {

			java.sql.ResultSet rs = this.consulta(sql);

			int id,numero;

			while (rs.next()) {

				id = Integer.parseInt(rs.getString("id_parq"));
				numero = Integer.parseInt(rs.getString("numero"));

				ParquimetroBean p = new ParquimetroBeanImpl();
				p.setUbicacion(ubicacion);
				p.setNumero(numero);
				p.setId(id);

				parquimetros.add(p);

			}
			rs.close();
			return parquimetros;
		} catch (SQLException ex) {
			logger.error("SQLException: " + ex.getMessage());
			logger.error("SQLState: " + ex.getSQLState());
			logger.error("VendorError: " + ex.getErrorCode());
			throw new Exception("Se produjo un error en la consulta: " + ex.getMessage());
		}
	}

	@Override
	public EstacionamientoDTO conectarParquimetro(ParquimetroBean parquimetro, TarjetaBean tarjeta)
			throws SinSaldoSuficienteException, ParquimetroNoExisteException, TarjetaNoExisteException, Exception {

		logger.info(Mensajes.getMessage("ModeloParquimetroImpl.conectarParquimetro.logger"),parquimetro.getId(),tarjeta.getId());
		
		/**
		 * TODO [preguntar] Invoca al stored procedure conectar(...) que se encarga de realizar en una transacción la apertura o cierre
		 *      de estacionamiento segun corresponda.
		 *      
		 *      Segun la infromacion devuelta por el stored procedure se retorna un objeto EstacionamientoDTO o
		 *      dependiendo del error se produce la excepción correspondiente:
		 *       SinSaldoSuficienteException, ParquimetroNoExisteException, TarjetaNoExisteException     
		 *  
		 */
		
		/*Datos estáticos de prueba. Quitar y reemplazar por código que recupera los datos reales.
		if ((tarjeta.getSaldo() < 0) && (tarjeta.getTipoTarjeta().getDescuento() < 1)) {  // tarjeta k1
			throw new SinSaldoSuficienteException();
		}
		EstacionamientoDTO estacionamiento;

		LocalDateTime currentDateTime = LocalDateTime.now();
        // Definir formatos para la fecha y la hora
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        // Formatear la fecha y la hora como cadenas separadas
        String fechaAhora = currentDateTime.format(dateFormatter);
        String horaAhora = currentDateTime.format(timeFormatter);
		
		if (tarjeta.getId() == 2) { 		//EntradaEstacionamientoDTO(String tiempoDisponible, String fechaEntrada, String horaEntrada)			
			estacionamiento = new EntradaEstacionamientoDTOImpl("01:40:00",
																fechaAhora,
																horaAhora);
		} else if (tarjeta.getId() == 3) {  		//SalidaEstacionamientoDTO(String tiempoTranscurrido, String saldoTarjeta, String fechaEntrada,	String horaEntrada, String fechaSalida, String horaSalida)
			
			LocalDateTime antes = currentDateTime.minusMinutes(45); // hora actual menos 45 minutos
			
			estacionamiento = new SalidaEstacionamientoDTOImpl("00:45:00", // tiempoTranscurrido
																"10.20", // saldoTarjeta
																fechaAhora, // fechaEntrada
																antes.format(timeFormatter), // horaEntrada
																fechaAhora, // fechaSalida
																horaAhora); // horaSalida
		} else if (tarjeta.getId() == 4) { 

			LocalDateTime antes = currentDateTime.minusMinutes(90); // hora actual menos 45 minutos
			
			estacionamiento = new SalidaEstacionamientoDTOImpl("01:30:00", // tiempoTranscurrido
																"-85", // saldoTarjeta
																fechaAhora, // fechaEntrada
																antes.format(timeFormatter), // horaEntrada
																fechaAhora, // fechaSalida
																horaAhora); // horaSalida
			
		} else {
			throw new Exception();
		}
	
		return estacionamiento;
		//Fin datos estáticos de prueba

		 */
		EstacionamientoDTO estacionamiento = null;
		String sql = "call parquimetros.conectar( "+tarjeta.getId()+" , "+parquimetro.getId()+" )";

		try {

			java.sql.ResultSet rs = this.consulta(sql);

			String resultado;

			if(rs.next()){
				resultado = rs.getString("resultado");
				if(resultado.equals("La tarjeta no existe")){
					throw new TarjetaNoExisteException();
				}

				if(resultado.equals("El parquimetro no existe")){
					throw new ParquimetroNoExisteException();
				}

				if(resultado.equals("SQLEXCEPTION!, transaccion abortada")){
					throw new SQLException();
				}

				String tipoOp = rs.getString("tipo_operacion");

				if(tipoOp.equals("CIERRE")){
					estacionamiento = new SalidaEstacionamientoDTOImpl(
							rs.getString("minutos_transcurridos"),
							rs.getString("saldo"),
							rs.getString("fecha_ent"),
							rs.getString("hora_ent"),
							rs.getString("fecha_sal"),
							rs.getString("hora_sal")
					);
				}
				if(tipoOp.equals("APERTURA")){
					resultado= rs.getString("resultado");
					if(resultado.equals("La tarjeta no tiene saldo suficiente")){
						throw new SinSaldoSuficienteException();
					}

					estacionamiento = new EntradaEstacionamientoDTOImpl(
							rs.getString("minutos_disponibles"),
							rs.getString("fecha_ent"),
							rs.getString("hora_ent")
					);
				}
			}
			rs.close();
			return estacionamiento;

		} catch (SQLException ex) {
			logger.error("SQLException: " + ex.getMessage());
			logger.error("SQLState: " + ex.getSQLState());
			logger.error("VendorError: " + ex.getErrorCode());
			throw new Exception("Se produjo un error en la consulta: " + ex.getMessage());
		}
	}

}
