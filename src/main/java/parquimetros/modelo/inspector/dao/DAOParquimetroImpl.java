package parquimetros.modelo.inspector.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import parquimetros.modelo.beans.ParquimetroBean;
import parquimetros.modelo.beans.UbicacionBean;
import parquimetros.modelo.beans.UbicacionBeanImpl;
import parquimetros.utils.Mensajes;

public class DAOParquimetroImpl implements DAOParquimetro {

	private static Logger logger = LoggerFactory.getLogger(DAOParquimetroImpl.class);
	
	private Connection conexion;
	
	public DAOParquimetroImpl(Connection c) {
		this.conexion = c;
	}

	@Override
	public UbicacionBean recuperarUbicacion(ParquimetroBean parquimetro) throws Exception {
		/**
		 * TODO Recuperar  de la B.D. la ubicación de un parquimetro a patir de su ID
		 * 
		 *      Importante: Para acceder a la B.D. utilice la propiedad this.conexion (de clase Connection) 
		 *      que se inicializa en el constructor.   
		 */		

		//Datos estáticos de prueba. Quitar y reemplazar por código que recupera los datos reales.
		
		UbicacionBean ubicacion = new UbicacionBeanImpl();
		
		String sql="select * from parquimetros.parquimetros where id_parq = " + parquimetro.getId();
		logger.info("Se intenta realizar la siguiente consulta {}",sql);
		ResultSet rs= null;
		try
		{
			java.sql.Statement stmt = this.conexion.createStatement();
			rs = stmt.executeQuery(sql);
			if(!rs.next()){
				rs.close();
				throw new Exception(Mensajes.getMessage("DAOParquimetroImpl.recuperarUbicacion"));
			}
			else
			{
				ubicacion.setAltura(rs.getInt("altura"));
				ubicacion.setCalle(rs.getString("calle"));
			}
			rs.close();
		}
		catch (SQLException ex){
			logger.error("SQLException: " + ex.getMessage());
			logger.error("SQLState: " + ex.getSQLState());
			logger.error("VendorError: " + ex.getErrorCode());
			throw new Exception("Se produjo un error en la consulta: " + ex.getMessage());
		}
		
		return ubicacion;
		//Fin datos de prueba 
		
	}



}
