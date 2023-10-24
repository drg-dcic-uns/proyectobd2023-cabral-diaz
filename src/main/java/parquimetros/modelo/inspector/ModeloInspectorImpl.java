package parquimetros.modelo.inspector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import parquimetros.modelo.ModeloImpl;
import parquimetros.modelo.beans.*;
import parquimetros.modelo.inspector.dao.*;
import parquimetros.modelo.inspector.dto.EstacionamientoPatenteDTO;
import parquimetros.modelo.inspector.dto.EstacionamientoPatenteDTOImpl;
import parquimetros.modelo.inspector.dto.MultaPatenteDTO;
import parquimetros.modelo.inspector.dto.MultaPatenteDTOImpl;
import parquimetros.modelo.inspector.exception.AutomovilNoEncontradoException;
import parquimetros.modelo.inspector.exception.ConexionParquimetroException;
import parquimetros.modelo.inspector.exception.InspectorNoAutenticadoException;
import parquimetros.modelo.inspector.exception.InspectorNoHabilitadoEnUbicacionException;
import parquimetros.utils.Fechas;
import parquimetros.utils.Mensajes;
import parquimetros.utils.Parsing;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

public class ModeloInspectorImpl extends ModeloImpl implements ModeloInspector {

	private static Logger logger = LoggerFactory.getLogger(ModeloInspectorImpl.class);	
	
	public ModeloInspectorImpl() {
		logger.debug(Mensajes.getMessage("ModeloInspectorImpl.constructor.logger"));
	}

	@Override
	public InspectorBean autenticar(String legajo, String password) throws InspectorNoAutenticadoException, Exception {
		logger.info(Mensajes.getMessage("ModeloInspectorImpl.autenticar.logger"), legajo, password);

		if (legajo==null || legajo.isEmpty() || password==null || password.isEmpty()) {
			throw new InspectorNoAutenticadoException(Mensajes.getMessage("ModeloInspectorImpl.autenticar.parametrosVacios"));
		}
		DAOInspector dao = new DAOInspectorImpl(this.conexion);
		return dao.autenticar(legajo, password);		
	}
	
	@Override
	public ArrayList<UbicacionBean> recuperarUbicaciones() throws Exception {
		
		logger.info(Mensajes.getMessage("ModeloInspectorImpl.recuperarUbicaciones.logger"));
		/** 
		 * TODO Debe retornar una lista de UbicacionesBean con todas las ubicaciones almacenadas en la B.D. 
		 *      Debería propagar una excepción si hay algún error en la consulta. 
		 *      
		 *      Importante: Para acceder a la B.D. utilice la propiedad this.conexion (de clase Connection) 
		 *      que se hereda al extender la clase ModeloImpl.       
		 *      
		 */
		ArrayList<UbicacionBean> ubicaciones = new ArrayList<UbicacionBean>();

		/*// Datos estáticos de prueba. Quitar y reemplazar por código que recupera las ubicaciones de la B.D. en una lista de UbicacionesBean
		DAOUbicacionesDatosPrueba.poblar();
		
		for (UbicacionBean ubicacion : DAOUbicacionesDatosPrueba.datos.values()) {
			ubicaciones.add(ubicacion);	
		}
		// Fin datos estáticos de prueba.*/

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
		
		logger.info(Mensajes.getMessage("ModeloInspectorImpl.recuperarParquimetros.logger"),ubicacion.toString());
		
		/** 
		 * TODO Debe retornar una lista de ParquimetroBean con todos los parquimetros que corresponden a una ubicación.
		 * 		Debería propagar una excepción si hay algún error en la consulta.
		 *            
		 *      Importante: Para acceder a la B.D. utilice la propiedad this.conexion (de clase Connection) 
		 *      que se hereda al extender la clase ModeloImpl.      
		 *      
		 */

		ArrayList<ParquimetroBean> parquimetros = new ArrayList<ParquimetroBean>();

		/*// Datos estáticos de prueba. Quitar y reemplazar por código que recupera los parquimetros de la B.D. en una lista de ParquimetroBean
		DAOParquimetrosDatosPrueba.poblar(ubicacion);
		
		for (ParquimetroBean parquimetro : DAOParquimetrosDatosPrueba.datos.values()) {
			parquimetros.add(parquimetro);	
		}
		// Fin datos estáticos de prueba.*/

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
	public void conectarParquimetro(ParquimetroBean parquimetro, InspectorBean inspectorLogueado) throws ConexionParquimetroException, Exception {
		// es llamado desde Controlador.conectarParquimetro
  
		logger.info(Mensajes.getMessage("ModeloInspectorImpl.conectarParquimetro.logger"),parquimetro.toString());
		
		/** TODO Simula la conexión al parquímetro con el inspector que se encuentra logueado en el momento 
		 *       en que se ejecuta la acción. 
		 *       
		 *       Debe verificar si el inspector está habilitado a acceder a la ubicación del parquímetro 
		 *       en el dia y hora actual, segun la tabla asociado_con. 
		 *       Sino puede deberá producir una excepción ConexionParquimetroException.     
		 *       En caso exitoso se registra su acceso en la tabla ACCEDE y retorna exitosamente.		         
		 *     
		 *       Si hay un error no controlado se produce una Exception genérica.
		 *       
		 *       Importante: Para acceder a la B.D. utilice la propiedad this.conexion (de clase Connection) 
		 *       que se hereda al extender la clase ModeloImpl.
		 *  
		 * @param parquimetro
		 * @throws ConexionParquimetroException
		 * @throws Exception
		 */
		UbicacionBean ubicacion = this.recuperarUbicacion(parquimetro);
		String sql = "select dia,turno from parquimetros.asociado_con " +
				"where legajo = " + inspectorLogueado.getLegajo() +
				" and calle = '" + ubicacion.getCalle() + "' " +
				"and altura = "+ ubicacion.getAltura();
		try {

			java.sql.ResultSet rs = this.consulta(sql);

			String dia,turno;
			int hora,minuto,segundo;

			if (rs.next()) {
				dia = rs.getString("dia");
				turno = rs.getString("turno");

                ResultSet rsHora = this.consulta("SELECT NOW()");

                Date fechaHoy = rsHora.getDate(1);
                Time horaHoy = rsHora.getTime(1);

                LocalTime now = horaHoy.toLocalTime();
				hora = now.getHour();
				minuto = now.getMinute();
				segundo = now.getSecond();

				if( turnoValido(turno,dia) ){
					this.actualizacion("INSERT INTO accede (legajo, id_parq, fecha, hora) " +
							"VALUES ( "+
							inspectorLogueado.getLegajo()+" , "+
							parquimetro.getId()+ " , '"+
							Fechas.convertirDateAStringDB(fechaHoy)+"' , "+
							"'"+hora+":"+minuto+":"+segundo+"' )");
				}else{
					throw new ConexionParquimetroException("El inspector no esta habilitado a acceder a la ubicacion del parquimetro en el dia y hora actual.");
				}
			}else{
				throw new ConexionParquimetroException("El inspector no esta habilitado a acceder al parquimetro.");
			}
			rs.close();
		} catch (SQLException ex) {
			logger.error("SQLException: " + ex.getMessage());
			logger.error("SQLState: " + ex.getSQLState());
			logger.error("VendorError: " + ex.getErrorCode());
			throw new Exception("Se produjo un error en la consulta: " + ex.getMessage());
		}
	}

	/** Genera las siglas {"lu","ma","mi","ju","vi","sa","do"}
	 * de acuerdo con el numero del dia de la semana
	 */
	private String diaDeLaSemana(int dia){
		String siglas="";
		switch (dia){
			case 7: siglas = "do"; break;
			case 1: siglas = "lu"; break;
			case 2: siglas = "ma"; break;
			case 3: siglas = "mi"; break;
			case 4: siglas = "ju"; break;
			case 5: siglas = "vi"; break;
			case 6: siglas = "sa"; break;
		}
		return siglas;
	}

	@Override
	public UbicacionBean recuperarUbicacion(ParquimetroBean parquimetro) throws Exception {
		logger.info(Mensajes.getMessage("ModeloInspectorImpl.recuperarUbicacion.logger"),parquimetro.getId());
		UbicacionBean ubicacion = parquimetro.getUbicacion();
		if (Objects.isNull(ubicacion)) {
			DAOParquimetro dao = new DAOParquimetroImpl(this.conexion);
			ubicacion = dao.recuperarUbicacion(parquimetro);
		}			
		return ubicacion; 
	}

	@Override
	public void verificarPatente(String patente) throws AutomovilNoEncontradoException, Exception {
		logger.info(Mensajes.getMessage("ModeloInspectorImpl.verificarPatente.logger"),patente);
		DAOAutomovil dao = new DAOAutomovilImpl(this.conexion);
		dao.verificarPatente(patente); 
	}	
	
	@Override
	public EstacionamientoPatenteDTO recuperarEstacionamiento(String patente, UbicacionBean ubicacion) throws Exception {

		logger.info(Mensajes.getMessage("ModeloInspectorImpl.recuperarEstacionamiento.logger"),patente,ubicacion.getCalle(),ubicacion.getAltura());
		/**
		 * TODO Verifica si existe un estacionamiento abierto registrado la patente en la ubicación, y
		 *	    de ser asi retorna un EstacionamientoPatenteDTO con estado Registrado (EstacionamientoPatenteDTO.ESTADO_REGISTRADO), 
		 * 		y caso contrario sale con estado No Registrado (EstacionamientoPatenteDTO.ESTADO_NO_REGISTRADO).
		 * 
		 *      Importante: Para acceder a la B.D. utilice la propiedad this.conexion (de clase Connection) 
		 *      que se hereda al extender la clase ModeloImpl.
		 */
		//
		// Datos estáticos de prueba. Quitar y reemplazar por código que recupera los datos reale de la BD.
		//
		// Diseño de datos de prueba: Las patentes que terminan en 1 al 8 fueron verificados como existentes en la tabla automovil,
		//                            las terminadas en 9 y 0 produjeron una excepción de AutomovilNoEncontradoException y Exception.
		//                            entonces solo consideramos los casos terminados de 1 a 8
 		// 
		// Utilizaremos el criterio que si es par el último digito de patente entonces está registrado correctamente el estacionamiento.
		//

		String sql = "select * from parquimetros.estacionados " +
				"where patente = '"+patente+"' " +
				"and calle = '"+ubicacion.getCalle()+"' " +
				"and altura = "+ubicacion.getAltura();

		String fechaEntrada, horaEntrada,estado;
		try {

			java.sql.ResultSet rs = this.consulta(sql);


			if(rs.next()){
				fechaEntrada = rs.getString("fecha_ent");
				horaEntrada = rs.getString("hora_ent");
				estado=EstacionamientoPatenteDTO.ESTADO_REGISTRADO;
			}else{
				fechaEntrada = "";
				horaEntrada = "";
				estado=EstacionamientoPatenteDTO.ESTADO_NO_REGISTRADO;
			}

			rs.close();
		} catch (SQLException ex) {
			logger.error("SQLException: " + ex.getMessage());
			logger.error("SQLState: " + ex.getSQLState());
			logger.error("VendorError: " + ex.getErrorCode());
			throw new Exception("Se produjo un error en la consulta: " + ex.getMessage());
		}



		/*if (Integer.parseInt(patente.substring(patente.length()-1)) % 2 == 0) {
			estado = EstacionamientoPatenteDTO.ESTADO_REGISTRADO;

			LocalDateTime currentDateTime = LocalDateTime.now();
	        // Definir formatos para la fecha y la hora
	        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

	        // Formatear la fecha y la hora como cadenas separadas
	        fechaEntrada = currentDateTime.format(dateFormatter);
	        horaEntrada = currentDateTime.format(timeFormatter);

		} else {
			estado = EstacionamientoPatenteDTO.ESTADO_NO_REGISTRADO;
	        fechaEntrada = "";
	        horaEntrada = "";
		}*/
		// Fin de datos de prueba



		return new EstacionamientoPatenteDTOImpl(patente,ubicacion.getCalle(),String.valueOf(ubicacion.getAltura()),fechaEntrada,horaEntrada,estado);
	}

	private boolean turnoValido(String turno,String diaSemana){
		boolean valido = false;
		String diaHoy;
		int hora;
		LocalDateTime now = LocalDateTime.now();

		diaHoy = diaDeLaSemana(now.getDayOfWeek().getValue());

		hora = now.getHour();

		if (turno.equals("m")){
			valido = ((hora >= 8) && (hora <= 13) );
		} else{
			valido = ((hora >= 14) && (hora <= 19) );
		}

		if(valido){
			valido = diaSemana.equals(diaHoy);
		}

		return valido;
	}

	@Override
	public ArrayList<MultaPatenteDTO> generarMultas(ArrayList<String> listaPatentes, 
													UbicacionBean ubicacion, 
													InspectorBean inspectorLogueado) 
									throws InspectorNoHabilitadoEnUbicacionException, Exception {

		logger.info(Mensajes.getMessage("ModeloInspectorImpl.generarMultas.logger"),listaPatentes.size());		
		
		/** 
		 * TODO Primero verificar si el inspector puede realizar una multa en esa ubicacion el dia y hora actual 
		 *      segun la tabla asociado_con. Sino puede deberá producir una excepción de 
		 *      InspectorNoHabilitadoEnUbicacionException. 
		 *            
		 * 		Luego para cada una de las patentes suministradas, si no tiene un estacionamiento abierto en dicha 
		 *      ubicación, se deberá cargar una multa en la B.D. 
		 *      
		 *      Debe retornar una lista de las multas realizadas (lista de objetos MultaPatenteDTO).
		 *      
		 *      Importante: Para acceder a la B.D. utilice la propiedad this.conexion (de clase Connection) 
		 *      que se hereda al extender la clase ModeloImpl.      
		 */

		ArrayList<MultaPatenteDTO> multas = new ArrayList<MultaPatenteDTO>();


		String sql = "select id_asociado_con,dia,turno from parquimetros.asociado_con " +
				"where legajo = " + inspectorLogueado.getLegajo() +
				" and calle = '" + ubicacion.getCalle() + "' " +
				"and altura = "+ ubicacion.getAltura();


		String dia,turno,id_asociado_con;
		try {
			java.sql.ResultSet rs = this.consulta(sql);

			if (rs.next()) {
				id_asociado_con = rs.getString("id_asociado_con");
				dia = rs.getString("dia");
				turno = rs.getString("turno");

				if( !this.turnoValido(turno,dia) ){
					throw new InspectorNoHabilitadoEnUbicacionException();
				}
			}else {
				throw new InspectorNoHabilitadoEnUbicacionException();
			}
			rs.close();
		} catch (SQLException ex) {
			logger.error("SQLException: " + ex.getMessage());
			logger.error("SQLState: " + ex.getSQLState());
			logger.error("VendorError: " + ex.getErrorCode());
			throw new Exception("Se produjo un error en la consulta: " + ex.getMessage());
		}

		int nroMulta = 1;
		
		for (String patente : listaPatentes) {
			
			EstacionamientoPatenteDTO estacionamiento = this.recuperarEstacionamiento(patente,ubicacion);
			if (estacionamiento.getEstado().equals(EstacionamientoPatenteDTO.ESTADO_NO_REGISTRADO)) {
				String sqlMulta="insert into parquimetros.multa(fecha,hora,patente,id_asociado_con) " +
						"VALUES (curdate(),curtime(), ? , ? )";
				PreparedStatement stmMulta = this.conexion.prepareStatement(sqlMulta, Statement.RETURN_GENERATED_KEYS);
				stmMulta.setString(1,patente);
				stmMulta.setString(2,id_asociado_con);
				stmMulta.executeUpdate();

				ResultSet rsMulta = stmMulta.getGeneratedKeys();
				rsMulta.next();
				nroMulta = rsMulta.getInt(1);

				MultaPatenteDTO multa = new MultaPatenteDTOImpl(String.valueOf(nroMulta), 
																patente, 
																ubicacion.getCalle(), 
																String.valueOf(ubicacion.getAltura()), 
																estacionamiento.getFechaEntrada(),
																estacionamiento.getHoraEntrada(),
																String.valueOf(inspectorLogueado.getLegajo()));

				multas.add(multa);
			}
		}
		return multas;		
	}
}
