package parquimetros.modelo.inspector.dao;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import parquimetros.modelo.beans.InspectorBean;
import parquimetros.modelo.beans.InspectorBeanImpl;
import parquimetros.modelo.inspector.exception.InspectorNoAutenticadoException;
import parquimetros.utils.Mensajes;

public class DAOInspectorImpl implements DAOInspector {

	private static Logger logger = LoggerFactory.getLogger(DAOInspectorImpl.class);
	
	private Connection conexion;
	
	public DAOInspectorImpl(Connection c) {
		this.conexion = c;
	}

	@Override
	public InspectorBean autenticar(String legajo, String password) throws InspectorNoAutenticadoException, Exception {
		/** 
		 * TODO [DONE]
		 * 		Código que autentica que exista en la B.D. un legajo de inspector y que el password corresponda a ese legajo
		 *      (recuerde que el password guardado en la BD está encriptado con MD5) 
		 *      En caso exitoso deberá retornar el inspectorBean.
		 *      Si la autenticación no es exitosa porque el legajo no es válido o el password es incorrecto
		 *      deberá generar una excepción InspectorNoAutenticadoException 
		 *      y si hubo algún otro error deberá producir y propagar una Exception.
		 *      
		 *      Importante: Para acceder a la B.D. utilice la propiedad this.conexion (de clase Connection) 
		 *      que se inicializa en el constructor.      
		 */
		 

		//Datos estáticos de prueba. Quitar y reemplazar por código que recupera los datos reales.
		//
		// Diseño de datos de prueba: Los legajos que terminan en 
		//  * 1 al 8 retorna exitosamente con el inspector encontrado.
		//  * 9 produce una excepción de InspectorNoAutenticadoException
		//  * 0 propaga la excepción recibida (produce una Exception)
 		// 
		
		/**
		InspectorBean inspector;
		
		int ultimo = Integer.parseInt(legajo.substring(legajo.length()-1));
		
		if (ultimo == 0) {
			throw new Exception(Mensajes.getMessage("DAOInspectorImpl.autenticar.errorConexion"));
		} else if (ultimo == 9) {
			throw new InspectorNoAutenticadoException(Mensajes.getMessage("DAOInspectorImpl.autenticar.inspectorNoAutenticado"));			
		} else {			
			inspector = new InspectorBeanImpl();
			inspector.setLegajo(Integer.parseInt(legajo));
			inspector.setApellido("Apellido"+legajo);
			inspector.setNombre("Nombre"+legajo);
			inspector.setDNI(legajo.hashCode() % 1000000);	
			inspector.setPassword("45c48cce2e2d7fbdea1afc51c7c6ad26"); // md5(9);
		}
		
		return inspector;
		// Fin datos estáticos de prueba.
		 * 
		 */
		
		InspectorBean inspector = new InspectorBeanImpl();
		
		String pws, nombre, apellido;
		int dni;
		
		String sql="select * from parquimetros.inspectores where legajo = " + legajo;
		logger.info("Se intenta realizar la siguiente consulta {}",sql);
		ResultSet rs= null;
		try
		{
			java.sql.Statement stmt = this.conexion.createStatement();
			rs = stmt.executeQuery(sql);
			if(!rs.next()){
				rs.close();
				throw new InspectorNoAutenticadoException(Mensajes.getMessage("DAOInspectorImpl.autenticar.inspectorNoAutenticado"));
			}
			else
			{
				pws = rs.getString("password");
				nombre = rs.getString("nombre");
				apellido = rs.getString("apellido");
				dni = rs.getInt("dni");
				if(pws.equals(getMD5(password)))
				{
					inspector.setApellido(apellido);
					inspector.setDNI(dni);
					inspector.setLegajo(Integer.parseInt(legajo));
					inspector.setNombre(nombre);
					inspector.setPassword(pws);
				}
				else
				{
					rs.close();
					throw new InspectorNoAutenticadoException(Mensajes.getMessage("DAOInspectorImpl.autenticar.inspectorNoAutenticado"));
				}
			}
			rs.close();
		}
		catch (SQLException ex){
			logger.error("SQLException: " + ex.getMessage());
			logger.error("SQLState: " + ex.getSQLState());
			logger.error("VendorError: " + ex.getErrorCode());
			throw new Exception("Se produjo un error en la consulta: " + ex.getMessage());
		}
		return inspector;
	}	

	private static String getMD5(String input) {
		 try {
		 MessageDigest md = MessageDigest.getInstance("MD5");
		 byte[] messageDigest = md.digest(input.getBytes());
		 BigInteger number = new BigInteger(1, messageDigest);
		 String hashtext = number.toString(16);

		 while (hashtext.length() < 32) {
		 hashtext = "0" + hashtext;
		 }
		 return hashtext;
		 }
		 catch (NoSuchAlgorithmException e) {
		 throw new RuntimeException(e);
		 }
		 }
}
