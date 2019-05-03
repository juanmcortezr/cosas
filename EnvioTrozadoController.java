package pe.gob.sunat.recurso2.planeamiento.siga.sip.web.controller;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.sojo.interchange.json.JsonSerializer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.WebUtils;

import pe.gob.sunat.recurso2.administracion.siga.archivo.service.RegistroArchivosService;
import pe.gob.sunat.recurso2.administracion.siga.firma.service.ConsultaFirmaService;
import pe.gob.sunat.recurso2.administracion.siga.firma.service.ValidarFirmarService;
import pe.gob.sunat.recurso2.administracion.siga.registro.model.bean.MaestroPersonalBean;
import pe.gob.sunat.recurso2.administracion.siga.registro.service.RegistroDependenciasService;
import pe.gob.sunat.recurso2.administracion.siga.registro.service.RegistroPersonalService;
import pe.gob.sunat.recurso2.administracion.siga.util.FechaUtil;
import pe.gob.sunat.recurso2.planeamiento.siga.bean.InformeResultado;
import pe.gob.sunat.recurso2.planeamiento.siga.service.BufirService;
import pe.gob.sunat.recurso2.planeamiento.siga.service.EnvioSipService;
import pe.gob.sunat.recurso2.planeamiento.siga.service.GestorArchivosService;
import pe.gob.sunat.recurso2.planeamiento.siga.service.ParametroService;
import pe.gob.sunat.recurso2.planeamiento.siga.util.Constantes;
import pe.gob.sunat.tecnologia.menu.bean.UsuarioBean;

@Controller
@RequestMapping(value="/sip/enviar")
public class EnvioTrozadoController{
	
	private final Log log = LogFactory.getLog(getClass());
	private final static String ANNIO_ACTUAL = FechaUtil.obtenerAnioActual();
	private final static String MES_ACTUAL = FechaUtil.obtenerMesActual();
	
	JsonSerializer serializador = new JsonSerializer();
	
	@Autowired
	@Qualifier("registro.registroPersonalService")
	private RegistroPersonalService registroPersonalService;
	
	@Autowired
	@Qualifier("gestorArchivosService")
	private GestorArchivosService gestorArchivosService;
	
	@Value("${aplicacion.version}")
	private String version;
	
	@Value("${aplicacion.buildTime}")
	private String buildTime;
	
	@Autowired
	@Qualifier("envioSipService")
	private EnvioSipService envioSipService;
	
	@Autowired
	@Qualifier("bufirService")
	private BufirService bufirService;
	
	//Variable general que guarda el reporte generado a partir del la cadena json retornada por el paquete del negocio respectivo 
	//private ReporteJasperBean reportePrevisualizacion;
	
	@Autowired
	@Qualifier("registro.registroArchivosService")
	private RegistroArchivosService registroArchivosService;
	
	@Autowired
	@Qualifier("validarFirmarService")
	private ValidarFirmarService validarFirmarService;

	@Autowired
	@Qualifier("registro.registroDependenciasService")
	private RegistroDependenciasService registroDependenciasService;
	
	@Autowired
	@Qualifier("consultaFirmaService")
	private ConsultaFirmaService consultaFirmaService;

	@Autowired
	@Qualifier("parametroService")
	private ParametroService parametroService;
	
//	@Autowired
//	private T01ParametroDAO t01ParametroDao;
	
	@RequestMapping(value = "/recuperarNivelEjecucionInformesAo", method=RequestMethod.POST,produces={MediaType.APPLICATION_JSON_VALUE})
	public @ResponseBody Map<String, Object> recuperarNivelEjecucionInformesAo(	@RequestParam(value="selAnio",	required=false) String selAnio,
																@RequestParam(value="selMes",	required=false) String selMes,
																@RequestParam(value="selUuoo",	required=false) String selUuoo,
																HttpServletRequest request) {
		
		log.info("Iniciando recuperarNivelEjecucionInformesAo(): "+Constantes.ENVIO_SIP_PAGE);
		Map<String, Object> resultado = new HashMap<String, Object>();
		
		DecimalFormat df = new DecimalFormat("00");
	   	String cadJson = "";
		
		try {
			UsuarioBean usuarioBean = (UsuarioBean) WebUtils.getSessionAttribute(request, "usuarioBean");
			MaestroPersonalBean colaborador = registroPersonalService.obtenerPersonaxRegistro(usuarioBean.getNroRegistro());
			
			String codigoEmpleado = colaborador.getCodigoEmpleado();
			String tipoPlan = Constantes.POI;
			String tipoPlanPoi = Constantes.PO2;
			
	    	String tipoDocumento = request.getParameter("tipoDocumento");
	    	String numeroDocumento = request.getParameter("numeroDocumento");
	    	String tipoPrincipal = request.getParameter("tipoPrincipal");
	    	
			//NOTA REVISAR LOS PAREMTROS DE ANNO_POI, ANNO, MES_POI y MES de acuerdo
			//a lo indicado por los usuarios con respecto a periodos
			
			String anioPoi = "";
			String mesPoi = "";
			
			int selMesInt = -1;
			try {
				selMesInt = Integer.parseInt(selMes);
			}
			catch (Exception e) {
				selMesInt = -1;
			}
			
			int selAnioInt = -1;
			try {
				selAnioInt = Integer.parseInt(selAnio);
			}
			catch (Exception e) {
				selAnioInt = -1;
			}
			
			// mes_poi es el del select + 1 (incluyendo anio)
			// mes es el del select (incluyendo anio)
			
			if (selMesInt == 0) {
				mesPoi = "00";
				anioPoi = selAnio;
			}
			else if (selMesInt == 12) {
				mesPoi = "01";
				anioPoi = new Integer(selAnioInt + 1).toString();
			}
			else if (selMesInt >= 1 && selMesInt <= 11) {
				mesPoi = df.format(selMesInt + 1);
				anioPoi = selAnio;
			}
			else if (selMesInt == 13) {
				mesPoi = "13";
				anioPoi = selAnio;
			}
	    	
			InformeResultado informeParam = new InformeResultado();
			informeParam.setAnnInforme(selAnio);
			informeParam.setMesInforme(selMes);
			informeParam.setCodUuooResp(selUuoo);
			informeParam.setCodTipoPlan(tipoPlan);
			informeParam.setCodTipoInforme(Constantes.TIPO_INFORME_ENVIO_SIP);//INFORME ENVIO SIP
			informeParam.setIndDel("0");//FILA ACTIVA
			
			String estado = "0";
			List<InformeResultado> informeResultadoList = envioSipService.recuperarInformeResultado(informeParam);
			if (informeResultadoList != null && informeResultadoList.size() > 0) {
				InformeResultado informeResultado = informeResultadoList.get(0);
				if (informeResultado.getEstado() != null) {
					estado = informeResultado.getIndInforme();
				}
			}
			
			//<parameter property="pFlagrptauto" jdbcType="CHAR" javaType="java.lang.String"
			//	mode="IN" />
			
			Map<String, Object> paramSp = new HashMap<String, Object>();
			paramSp.put("pTipoPlan", Constantes.POI);
			paramSp.put("pAnio", selAnio);
			paramSp.put("pMes", selMes);
			paramSp.put("pAnioPOI", anioPoi);
			paramSp.put("pMesPOI", mesPoi);
			paramSp.put("pUuooresp", selUuoo);
			paramSp.put("pCodtipoinforme", Constantes.TIPO_INFORME_ENVIO_SIP);
			paramSp.put("pFlagrptauto", "1");
			
			if ("0".equals(estado) || "2".equals(estado)) {
				envioSipService.resumirEjecucion(paramSp);//NO AFECTA TX
				envioSipService.procesaAoSipvinculados(paramSp);//NO AFECTA TX
			}
			
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("anio", selAnio);
			param.put("mes", selMes);
			param.put("uuoo", selUuoo);
			param.put("tipoDoc", tipoDocumento);
			param.put("nroDoc", numeroDocumento);
			param.put("cadJson", cadJson);
	    	log.debug("parametros: " + selAnio + " " + selMes + " " + selUuoo + " " + tipoDocumento + " " + numeroDocumento + " " + tipoPrincipal);
			
			//Map<String, Object> reporte = envioSipService.recuperarReporteNivelEjecucion(param);
			//String jsonCadena = (String)reporte.get("reporteJson");
			//String jsonCadena = bufirService.getCadenaJsonDoc(param);
			boolean validarReporte = true;
			boolean validarInforme = true;
			
			//Map<?,?> reporteObject = (Map<?,?>)serializador.deserialize(jsonCadena, Map.class);
			//log.debug("objeto deserializado: " + reporteObject + " parametros: " + reporteObject.get("parametros"));
			//if (reporteObject.get("parametros") == null /*&& reporteObject.get("subreport_01") == null && reporteObject.get("subreport_02") == null*/) {
			//	validarReporte = false;
			//}
			log.debug("validarReporte: " + validarReporte);
			
			InformeResultado informeAoParam = new InformeResultado();
			informeAoParam.setTipoPlanPoi(tipoPlanPoi);
			informeAoParam.setAnnoPoi(anioPoi);
			informeAoParam.setMesPoi(mesPoi);
			informeAoParam.setTipoPlan(tipoPlan);
			informeAoParam.setAnno(selAnio);
			informeAoParam.setMes(selMes);
			informeAoParam.setUoResp(selUuoo);
			
		    /*PARAMETROS DE EJEMPLO
		    --:AS_TIPO_PLAN_POI = 'PO2'
		    --:AS_ANNO_POI = '2018'
		    :AS_MES_POI = '02' (combo, mes de ejecucion)
		    --:AS_TIPO_PLAN = 'POI'
		    --:AS_ANNO = '2018'
		    :AS_MES = '01' (mes de ejecucion menos 1)
		    --:AS_UO_RESP = ''4396 */
			
			List<InformeResultado> listaIndicadoresPoi = envioSipService.recuperarInformeActividadesOperativas(informeAoParam);
			for (InformeResultado ao : listaIndicadoresPoi) {

				String nomInformeAo = ao.getNomInforme();
				String numArchivoAo = ao.getNumArchivo();
				
				try {
					numArchivoAo = numArchivoAo.trim();
				}
				catch (Exception e) {
					numArchivoAo = "";
				}
				
				// se inserta el numArchivo
				if ("".equals(numArchivoAo)) {
					Map<String, Object> archivoParam = new HashMap<String, Object>();
					
					archivoParam.put("aplicacion", Constantes.CODIGO_APLICACION);
					archivoParam.put("modulo", Constantes.CODIGO_MODULO);
					archivoParam.put("tipoDocumento", Constantes.CODIGO_TIPO_DOCUMENTO);//Otros
					archivoParam.put("numeroDocumentoOrigen", nomInformeAo);
					archivoParam.put("registroDescripcion", "INFORME AO ENVIO SIP");
					archivoParam.put("num_archivo", numArchivoAo);
					
					String numeRegiArc = gestorArchivosService.registrarArchivoGeneral(archivoParam);//SI AFECTA TX
					numeRegiArc = numeRegiArc.trim();
					
					informeParam = new InformeResultado();
					informeParam.setAnnInforme(anioPoi);
					informeParam.setMesInforme(mesPoi);
					informeParam.setCodUuooResp(selUuoo);
					informeParam.setCodTipoPlan(tipoPlanPoi);
					informeParam.setCodTipoInforme(Constantes.TIPO_INFORME_ENVIO_AO_SIP);//INFORME ENVIO SIP
					informeParam.setIndDel("0");//FILA ACTIVA
					informeParam.setNumArchivo(numeRegiArc);
					
					envioSipService.actualizarInformeResultado(informeParam);//SI AFECTA TX
				}
			}
			
			informeParam = new InformeResultado();
			informeParam.setAnnInforme(selAnio);
			informeParam.setMesInforme(selMes);
			informeParam.setCodUuooResp(selUuoo);
			informeParam.setCodTipoPlan(tipoPlan);
			informeParam.setCodTipoInforme(Constantes.TIPO_INFORME_ENVIO_SIP);//INFORME ENVIO SIP
			informeParam.setIndDel("0");//FILA ACTIVA
			
			informeResultadoList = envioSipService.recuperarInformeResultado(informeParam);
			if (informeResultadoList != null && informeResultadoList.size() > 0) {
				InformeResultado informeResultado = informeResultadoList.get(0);
				
				String nomInforme = numeroDocumento;//informeResultado.getNomInforme();
				String numArchivo = informeResultado.getNumArchivo();
				
				try {
					numArchivo = numArchivo.trim();
				}
				catch (Exception e) {
					numArchivo = "";
				}
				
				// se inserta el numArchivo
				if ("".equals(numArchivo)) {
					Map<String, Object> archivoParam = new HashMap<String, Object>();
					
					archivoParam.put("aplicacion", Constantes.CODIGO_APLICACION);
					archivoParam.put("modulo", Constantes.CODIGO_MODULO);
					archivoParam.put("tipoDocumento", Constantes.CODIGO_TIPO_DOCUMENTO);//Otros
					archivoParam.put("numeroDocumentoOrigen", nomInforme);
					archivoParam.put("registroDescripcion", "INFORME ENVIO SIP");
					archivoParam.put("num_archivo", numArchivo);
					
					String numeRegiArc = gestorArchivosService.registrarArchivoGeneral(archivoParam);//SI AFECTA TX
					numeRegiArc = numeRegiArc.trim();
					
					informeParam = new InformeResultado();
					informeParam.setAnnInforme(selAnio);
					informeParam.setMesInforme(selMes);
					informeParam.setCodUuooResp(selUuoo);
					informeParam.setCodTipoPlan(tipoPlan);
					informeParam.setCodTipoInforme(Constantes.TIPO_INFORME_ENVIO_SIP);//INFORME ENVIO SIP
					informeParam.setIndDel("0");//FILA ACTIVA
					informeParam.setNumArchivo(numeRegiArc);
					
					envioSipService.actualizarInformeResultado(informeParam);//SI AFECTA TX
				}
				
				informeParam = new InformeResultado();
				informeParam.setAnnInforme(selAnio);
				informeParam.setMesInforme(selMes);
				informeParam.setCodUuooResp(selUuoo);
				informeParam.setCodTipoPlan(tipoPlan);
				informeParam.setCodTipoInforme(Constantes.TIPO_INFORME_ENVIO_SIP);//INFORME ENVIO SIP
				informeParam.setIndDel("0");//FILA ACTIVA
				
				informeResultadoList = envioSipService.recuperarInformeResultado(informeParam);
				informeResultado = informeResultadoList.get(0);
				
				validarInforme = true;
				resultado.put("informeResultado", informeResultado);
			}
			else {
				validarInforme = false;
			}
			
			//WHERE ANN_INFORME = #annInforme:VARCHAR# 
			//AND MES_INFORME = #mesInforme:VARCHAR# 
		    //AND COD_UUOO_RESP = #codUuooResp:VARCHAR# 
		    //AND COD_TIPO_PLAN = #codTipoPlan:VARCHAR# 
		    //AND COD_TIPO_INFORME = #codTipoInforme:VARCHAR# 
		    //AND IND_DEL = #indDel:VARCHAR# 

			resultado.put("validarInforme", validarInforme);
			resultado.put("validarReporte", validarReporte);
			resultado.put("listaIndicadoresPoi", listaIndicadoresPoi);
			resultado.put("error", "0");
		}
		catch (Exception e) {
			log.error("error", e);
			resultado.put("error", "1");
			resultado.put("mensajeError", e.getMessage());
		}
		
		return resultado;
	}
	
	
	@RequestMapping(value = "/enviarNivelEjecucionInformesAo", method=RequestMethod.POST,produces={MediaType.APPLICATION_JSON_VALUE})
	public @ResponseBody Map<String, Object> enviarNivelEjecucionInformesAo(	@RequestParam(value="selAnio",	required=false) String selAnio,
																@RequestParam(value="selMes",	required=false) String selMes,
																@RequestParam(value="selUuoo",	required=false) String selUuoo,
																HttpServletRequest request) {
		
		log.info("Iniciando enviarJustificacion(): "+Constantes.ENVIO_SIP_PAGE);
		Map<String, Object> resultado = new HashMap<String, Object>();
		
		DecimalFormat df = new DecimalFormat("00");
	   	String cadJson = "";
		
		try {
			UsuarioBean usuarioBean = (UsuarioBean) WebUtils.getSessionAttribute(request, "usuarioBean");
			MaestroPersonalBean colaborador = registroPersonalService.obtenerPersonaxRegistro(usuarioBean.getNroRegistro());
			
			String codigoEmpleado = colaborador.getCodigoEmpleado();
			String tipoPlan = Constantes.POI;
			String tipoPlanPoi = Constantes.PO2;
			
	    	String tipoDocumento = request.getParameter("tipoDocumento");
	    	String numeroDocumento = request.getParameter("numeroDocumento");
	    	String tipoPrincipal = request.getParameter("tipoPrincipal");
	    	
	    	List<InformeResultado> listaIndicadoresPoi = new ArrayList<InformeResultado>();
	    	
			String anioPoi = "";
			String mesPoi = "";
			
			int selMesInt = -1;
			try {
				selMesInt = Integer.parseInt(selMes);
			}
			catch (Exception e) {
				selMesInt = -1;
			}
			
			int selAnioInt = -1;
			try {
				selAnioInt = Integer.parseInt(selAnio);
			}
			catch (Exception e) {
				selAnioInt = -1;
			}
			
			// mes_poi es el del select + 1 (incluyendo anio)
			// mes es el del select (incluyendo anio)
			
			if (selMesInt == 0) {
				mesPoi = "00";
				anioPoi = selAnio;
			}
			else if (selMesInt == 12) {
				mesPoi = "01";
				anioPoi = new Integer(selAnioInt + 1).toString();
			}
			else if (selMesInt >= 1 && selMesInt <= 11) {
				mesPoi = df.format(selMesInt + 1);
				anioPoi = selAnio;
			}
			else if (selMesInt == 13) {
				mesPoi = "13";
				anioPoi = selAnio;
			}
	    	
			boolean validarInforme = true;
			
			//SIP-NIV-EJEC-2018-01-1C0000
			//SIP-JUSTI-2018-11-1S0000
			
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("anio", selAnio);
			param.put("mes", selMes);
			param.put("uuoo", selUuoo);
			param.put("tipoDoc", tipoDocumento);
			param.put("nroDoc", numeroDocumento);
			param.put("cadJson", cadJson);
			log.debug("parametros: " + selAnio + " " + selMes + " " + selUuoo + " " + tipoDocumento + " " + numeroDocumento + " " + tipoPrincipal);
			
			InformeResultado informeParam = new InformeResultado();
			informeParam.setAnnInforme(selAnio);
			informeParam.setMesInforme(selMes);
			informeParam.setCodUuooResp(selUuoo);
			informeParam.setCodTipoPlan(tipoPlan);
			informeParam.setCodTipoInforme(Constantes.TIPO_INFORME_ENVIO_SIP);//INFORME ENVIO SIP
			informeParam.setIndDel("0");//FILA ACTIVA
			
			List<InformeResultado> informeResultadoList = envioSipService.recuperarInformeResultado(informeParam);
			if (informeResultadoList != null && informeResultadoList.size() > 0) {
				InformeResultado informeResultado = informeResultadoList.get(0);
				
				String nomInforme = numeroDocumento;//informeResultado.getNomInforme();
				String numExpediente = informeResultado.getNumExpediente();
				
				try {
					numExpediente = numExpediente.trim();
				}
				catch (Exception e) {
					numExpediente = "";
				}
				
				// se inserta el numExpediente
				if ("".equals(numExpediente)) {
					numExpediente = bufirService.crearExpediente(numeroDocumento, codigoEmpleado, "422", Constantes.CODIGO_SEDE_DEFAULT);//SI AFECTA TX
					numExpediente = numExpediente.trim();
					bufirService.crearAccion(numExpediente, "001"/*accion seguimiento autorizar*/, "001"/*estado seguimiento autorizado*/, codigoEmpleado, "422", " ", Constantes.CODIGO_SEDE_DEFAULT);//SI AFECTA TX
					
					
					informeParam = new InformeResultado();
					informeParam.setAnnInforme(selAnio);
					informeParam.setMesInforme(selMes);
					informeParam.setCodUuooResp(selUuoo);
					informeParam.setCodTipoPlan(tipoPlan);
					informeParam.setCodTipoInforme(Constantes.TIPO_INFORME_ENVIO_SIP);//INFORME ENVIO SIP
					informeParam.setIndDel("0");//FILA ACTIVA
					informeParam.setNumExpediente(numExpediente);
					
					envioSipService.actualizarInformeResultado(informeParam);//SI AFECTA TX
				}
				else {
					bufirService.crearAccion(numExpediente, "002"/*accion seguimiento autorizar*/, "001"/*estado seguimiento autorizado*/, codigoEmpleado, "422", " ", Constantes.CODIGO_SEDE_DEFAULT);//SI AFECTA TX
				}
				
				informeParam = new InformeResultado();
				informeParam.setAnnInforme(selAnio);
				informeParam.setMesInforme(selMes);
				informeParam.setCodUuooResp(selUuoo);
				informeParam.setCodTipoPlan(tipoPlan);
				informeParam.setCodTipoInforme(Constantes.TIPO_INFORME_ENVIO_SIP);//INFORME ENVIO SIP
				informeParam.setIndDel("0");//FILA ACTIVA
				informeParam.setIndInforme("3");//enviado a bufir para firmar
				
				envioSipService.actualizarInformeResultado(informeParam);//SI AFECTA TX
								
				InformeResultado informeAoParam = new InformeResultado();
				informeAoParam.setTipoPlanPoi(tipoPlanPoi);
				informeAoParam.setAnnoPoi(anioPoi);
				informeAoParam.setMesPoi(mesPoi);
				informeAoParam.setTipoPlan(tipoPlan);
				informeAoParam.setAnno(selAnio);
				informeAoParam.setMes(selMes);
				informeAoParam.setUoResp(selUuoo);
				
			    /*PARAMETROS DE EJEMPLO
			    --:AS_TIPO_PLAN_POI = 'PO2'
			    --:AS_ANNO_POI = '2018'
			    :AS_MES_POI = '02' (combo, mes de ejecucion)
			    --:AS_TIPO_PLAN = 'POI'
			    --:AS_ANNO = '2018'
			    :AS_MES = '01' (mes de ejecucion menos 1)
			    --:AS_UO_RESP = ''4396 */
				
				listaIndicadoresPoi = envioSipService.recuperarInformeActividadesOperativas(informeAoParam);
				for (InformeResultado ao : listaIndicadoresPoi) {

					String nomInformeAo = ao.getNomInforme();
					String numeroDocumentoAo = nomInformeAo;
					String numExpedienteAo = ao.getNumExpediente();
					
					try {
						numExpedienteAo = numExpedienteAo.trim();
					}
					catch (Exception e) {
						numExpedienteAo = "";
					}

					// se inserta el numExpediente
					if ("".equals(numExpedienteAo)) {
						numExpedienteAo = bufirService.crearExpediente(numeroDocumentoAo, codigoEmpleado, "432", Constantes.CODIGO_SEDE_DEFAULT);//SI AFECTA TX
						numExpedienteAo = numExpedienteAo.trim();
						bufirService.crearAccion(numExpedienteAo, "001"/*accion seguimiento autorizar*/, "001"/*estado seguimiento autorizado*/, codigoEmpleado, "432", " ", Constantes.CODIGO_SEDE_DEFAULT);//SI AFECTA TX
						
						informeParam = new InformeResultado();
						informeParam.setAnnInforme(selAnio);
						informeParam.setMesInforme(selMes);
						informeParam.setCodUuooResp(selUuoo);
						informeParam.setCodTipoPlan(tipoPlan);
						informeParam.setCodTipoInforme(Constantes.TIPO_INFORME_ENVIO_AO_SIP);//INFORME ENVIO SIP
						informeParam.setIndDel("0");//FILA ACTIVA
						informeParam.setNumExpediente(numExpedienteAo);
						
						envioSipService.actualizarInformeResultado(informeParam);//SI AFECTA TX
					}
					else {
						bufirService.crearAccion(numExpedienteAo, "002"/*accion seguimiento autorizar*/, "001"/*estado seguimiento autorizado*/, codigoEmpleado, "432", " ", Constantes.CODIGO_SEDE_DEFAULT);//SI AFECTA TX
					}
					
					informeParam = new InformeResultado();
					informeParam.setAnnInforme(selAnio);
					informeParam.setMesInforme(selMes);
					informeParam.setCodUuooResp(selUuoo);
					informeParam.setCodTipoPlan(tipoPlan);
					informeParam.setCodTipoInforme(Constantes.TIPO_INFORME_ENVIO_AO_SIP);//INFORME ENVIO SIP
					informeParam.setIndDel("0");//FILA ACTIVA
					informeParam.setIndInforme("3");//enviado a bufir para firmar

					envioSipService.actualizarInformeResultado(informeParam);//SI AFECTA TX
				}
				
				
				Map<String, Object> paramSp = new HashMap<String, Object>();
				paramSp.put("numedoc", numeroDocumento);
				paramSp.put("secfirma", 1);
				paramSp.put("usuing", codigoEmpleado);
				paramSp.put("numeexp", numExpediente);
				paramSp.put("secbuf", 0);
				paramSp.put("mensaje", " ");
				
				log.debug("paramaetros pre ejecucion de spDoc943Creadoc: " + paramSp);
				bufirService.spDoc943Creadoc(paramSp);//SI AFECTA TX
				log.debug("paramaetros post ejecucion de spDoc943Creadoc: " + paramSp);
				
				if ("OK".equals(paramSp.get("mensaje"))) {
					paramSp = new HashMap<String, Object>();
					paramSp.put("pTipoPlan", "POI");
					paramSp.put("pAnio", selAnio);
					paramSp.put("pMes", selMes);
					paramSp.put("pProceso", "CARG");
					paramSp.put("ValorProceso", " ");
					paramSp.put("pCodProducto", " ");
					paramSp.put("pUsuario", " ");
					paramSp.put("pEstado", "2");// madar otra vez a 1
					paramSp.put("pFase", "FIRMA");
					paramSp.put("pActualizarEstado", "1");
					
					envioSipService.cerrarIndicadoresEjec(paramSp);//SI AFECTA TX
				}
				else {
					throw new Exception("error en la ejecucion del store procedure de creacion bufe: " + paramSp.get("mensaje"));
				}
				
				informeParam = new InformeResultado();
				informeParam.setAnnInforme(selAnio);
				informeParam.setMesInforme(selMes);
				informeParam.setCodUuooResp(selUuoo);
				informeParam.setCodTipoPlan(tipoPlan);
				informeParam.setCodTipoInforme(Constantes.TIPO_INFORME_ENVIO_SIP);//INFORME ENVIO SIP
				informeParam.setIndDel("0");//FILA ACTIVA
				
				informeResultadoList = envioSipService.recuperarInformeResultado(informeParam);
				informeResultado = informeResultadoList.get(0);
				
				validarInforme = true;
				resultado.put("informeResultado", informeResultado);
			}
			else {
				validarInforme = false;				
			}

			resultado.put("listaIndicadoresPoi", listaIndicadoresPoi);
			resultado.put("error", "0");
		}
		catch (Exception e) {
			log.error("error", e);
			resultado.put("error", "1");
			resultado.put("mensajeError", e.getMessage());
		}
		
		return resultado;
	}
	
	
	@RequestMapping(value = "/recuperarJustificacion", method=RequestMethod.POST,produces={MediaType.APPLICATION_JSON_VALUE})
	public @ResponseBody Map<String, Object> recuperarJustificacion(	@RequestParam(value="selAnio",	required=false) String selAnio,
																@RequestParam(value="selMes",	required=false) String selMes,
																@RequestParam(value="selUuoo",	required=false) String selUuoo,
																HttpServletRequest request) {
		
		log.info("Iniciando recuperarJustificacion(): "+Constantes.JUSTIFICACION_SIP_PAGE);
		Map<String, Object> resultado = new HashMap<String, Object>();
		
		DecimalFormat df = new DecimalFormat("00");
	   	String cadJson = "";
		
		try {
			UsuarioBean usuarioBean = (UsuarioBean) WebUtils.getSessionAttribute(request, "usuarioBean");
			MaestroPersonalBean colaborador = registroPersonalService.obtenerPersonaxRegistro(usuarioBean.getNroRegistro());
			
			String codigoEmpleado = colaborador.getCodigoEmpleado();
			String tipoPlan = Constantes.POI;
			
	    	String tipoDocumento = request.getParameter("tipoDocumento");
	    	String numeroDocumento = request.getParameter("numeroDocumento");
	    	String tipoPrincipal = request.getParameter("tipoPrincipal");
			
			//SIP-NIV-EJEC-2018-01-1C0000
			//SIP-JUSTI-2018-11-1S0000
	    	
	    	
			//NOTA REVISAR LOS PAREMTROS DE ANNO_POI, ANNO, MES_POI y MES de acuerdo
			//a lo indicado por los usuarios con respecto a periodos
			
			String anioPoi = "";
			String mesPoi = "";
			
			int selMesInt = -1;
			try {
				selMesInt = Integer.parseInt(selMes);
			}
			catch (Exception e) {
				selMesInt = -1;
			}
			
			int selAnioInt = -1;
			try {
				selAnioInt = Integer.parseInt(selAnio);
			}
			catch (Exception e) {
				selAnioInt = -1;
			}
			
			// mes_poi es el del select + 1 (incluyendo anio)
			// mes es el del select (incluyendo anio)
			
			if (selMesInt == 0) {
				mesPoi = "00";
				anioPoi = selAnio;
			}
			else if (selMesInt == 12) {
				mesPoi = "01";
				anioPoi = new Integer(selAnioInt + 1).toString();
			}
			else if (selMesInt >= 1 && selMesInt <= 11) {
				mesPoi = df.format(selMesInt + 1);
				anioPoi = selAnio;
			}
			else if (selMesInt == 13) {
				mesPoi = "13";
				anioPoi = selAnio;
			}
	    	
			InformeResultado informeParam = new InformeResultado();
			informeParam.setAnnInforme(selAnio);
			informeParam.setMesInforme(selMes);
			informeParam.setCodUuooResp(selUuoo);
			informeParam.setCodTipoPlan(tipoPlan);
			informeParam.setCodTipoInforme(Constantes.TIPO_INFORME_ENVIO_SIP);//INFORME ENVIO SIP
			informeParam.setIndDel("0");//FILA ACTIVA
			
			String estado = "0";
			List<InformeResultado> informeResultadoList = envioSipService.recuperarInformeResultado(informeParam);
			if (informeResultadoList != null && informeResultadoList.size() > 0) {
				InformeResultado informeResultado = informeResultadoList.get(0);
				if (informeResultado.getEstado() != null) {
					estado = informeResultado.getIndInforme();
				}
			}
	    	
			Map<String, Object> paramSp = new HashMap<String, Object>();
			paramSp.put("pTipoPlan", Constantes.POI);
			paramSp.put("pAnio", selAnio);
			paramSp.put("pMes", selMes);
			paramSp.put("pAnioPOI", anioPoi);
			paramSp.put("pMesPOI", mesPoi);
			paramSp.put("pUuooresp", selUuoo);
			
			if ("0".equals(estado) || "2".equals(estado)) {
				envioSipService.resumirJustificacion(paramSp);//NO AFECTA TX
				envioSipService.procesaAoSipvinculados(paramSp);//NO AFECTA TX
			}
			
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("anio", selAnio);
			param.put("mes", selMes);
			param.put("uuoo", selUuoo);
			param.put("tipoDoc", tipoDocumento);
			param.put("nroDoc", numeroDocumento);
			param.put("cadJson", cadJson);
			log.debug("parametros: " + selAnio + " " + selMes + " " + selUuoo + " " + tipoDocumento + " " + numeroDocumento + " " + tipoPrincipal);
			
			//Map<String, Object> reporte = envioSipService.recuperarReporteJustificacion(param);
			//String jsonCadena = (String)reporte.get("reporteJson");
			String jsonCadena = bufirService.getCadenaJsonDoc(param);
			boolean validarReporte = true;
			boolean validarInforme = true;
			
			Map<?,?> reporteObject = (Map<?,?>)serializador.deserialize(jsonCadena, Map.class);
			log.debug("objeto deserializado: " + reporteObject + " parametros: " + reporteObject.get("parametros"));
			if (reporteObject.get("parametros") == null /*&& reporteObject.get("subreport_01") == null && reporteObject.get("subreport_02") == null*/) {
				validarReporte = false;
			}
			log.debug("validarReporte: " + validarReporte);
			
			
			informeParam = new InformeResultado();
			informeParam.setAnnInforme(selAnio);
			informeParam.setMesInforme(selMes);
			informeParam.setCodUuooResp(selUuoo);
			informeParam.setCodTipoPlan(tipoPlan);
			informeParam.setCodTipoInforme(Constantes.TIPO_INFORME_JUSTIFICACION_SIP);//INFORME ENVIO SIP
			informeParam.setIndDel("0");//FILA ACTIVA
			
			informeResultadoList = envioSipService.recuperarInformeResultado(informeParam);
			if (informeResultadoList != null && informeResultadoList.size() > 0) {
				InformeResultado informeResultado = informeResultadoList.get(0);
				
				String nomInforme = numeroDocumento;//informeResultado.getNomInforme();
				String numArchivo = informeResultado.getNumArchivo();
				
				try {
					numArchivo = numArchivo.trim();
				}
				catch (Exception e) {
					numArchivo = "";
				}
				
				// se inserta el numArchivo
				if ("".equals(numArchivo)) {
					Map<String, Object> archivoParam = new HashMap<String, Object>();
					
					archivoParam.put("aplicacion", Constantes.CODIGO_APLICACION);
					archivoParam.put("modulo", Constantes.CODIGO_MODULO);
					archivoParam.put("tipoDocumento", Constantes.CODIGO_TIPO_DOCUMENTO);//Otros
					archivoParam.put("numeroDocumentoOrigen", nomInforme);
					archivoParam.put("registroDescripcion", "INFORME JUST SIP");
					archivoParam.put("num_archivo", numArchivo);
					
					log.debug("parametros a enviar en el registro de archivo: " + archivoParam);
					
					String numeRegiArc = gestorArchivosService.registrarArchivoGeneral(archivoParam);//SI AFECTA TX
					numeRegiArc = numeRegiArc.trim();
					
					informeParam = new InformeResultado();
					informeParam.setAnnInforme(selAnio);
					informeParam.setMesInforme(selMes);
					informeParam.setCodUuooResp(selUuoo);
					informeParam.setCodTipoPlan(tipoPlan);
					informeParam.setCodTipoInforme(Constantes.TIPO_INFORME_JUSTIFICACION_SIP);//INFORME ENVIO SIP
					informeParam.setIndDel("0");//FILA ACTIVA
					informeParam.setNumArchivo(numeRegiArc);
					
					envioSipService.actualizarInformeResultado(informeParam);//SI AFECTA TX
				}
				
				informeParam = new InformeResultado();
				informeParam.setAnnInforme(selAnio);
				informeParam.setMesInforme(selMes);
				informeParam.setCodUuooResp(selUuoo);
				informeParam.setCodTipoPlan(tipoPlan);
				informeParam.setCodTipoInforme(Constantes.TIPO_INFORME_JUSTIFICACION_SIP);//INFORME ENVIO SIP
				informeParam.setIndDel("0");//FILA ACTIVA
				
				informeResultadoList = envioSipService.recuperarInformeResultado(informeParam);
				informeResultado = informeResultadoList.get(0);
				
				validarInforme = true;
				resultado.put("informeResultado", informeResultado);
			}
			else {
				validarInforme = false;
			}
			//WHERE ANN_INFORME = #annInforme:VARCHAR# 
			//AND MES_INFORME = #mesInforme:VARCHAR# 
		    //AND COD_UUOO_RESP = #codUuooResp:VARCHAR# 
		    //AND COD_TIPO_PLAN = #codTipoPlan:VARCHAR# 
		    //AND COD_TIPO_INFORME = #codTipoInforme:VARCHAR# 
		    //AND IND_DEL = #indDel:VARCHAR# 

			resultado.put("validarInforme", validarInforme);
			resultado.put("validarReporte", validarReporte);
			resultado.put("error", "0");
		}
		catch (Exception e) {
			log.error("error", e);
			resultado.put("error", "1");
			resultado.put("mensajeError", e.getMessage());
		}
		
		return resultado;
	}
	
	
	@RequestMapping(value = "/enviarJustificacion", method=RequestMethod.POST,produces={MediaType.APPLICATION_JSON_VALUE})
	public @ResponseBody Map<String, Object> enviarJustificacion(	@RequestParam(value="selAnio",	required=false) String selAnio,
																@RequestParam(value="selMes",	required=false) String selMes,
																@RequestParam(value="selUuoo",	required=false) String selUuoo,
																HttpServletRequest request) {
		
		log.info("Iniciando enviarJustificacion(): "+Constantes.JUSTIFICACION_SIP_PAGE);
		Map<String, Object> resultado = new HashMap<String, Object>();
		
		DecimalFormat df = new DecimalFormat("00");
	   	String cadJson = "";
		
		try {
			UsuarioBean usuarioBean = (UsuarioBean) WebUtils.getSessionAttribute(request, "usuarioBean");
			MaestroPersonalBean colaborador = registroPersonalService.obtenerPersonaxRegistro(usuarioBean.getNroRegistro());
			
			String codigoEmpleado = colaborador.getCodigoEmpleado();
			String tipoPlan = Constantes.POI;
			
	    	String tipoDocumento = request.getParameter("tipoDocumento");
	    	String numeroDocumento = request.getParameter("numeroDocumento");
	    	String tipoPrincipal = request.getParameter("tipoPrincipal");
	    	
			boolean validarInforme = true;
			
			//SIP-NIV-EJEC-2018-01-1C0000
			//SIP-JUSTI-2018-11-1S0000
			
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("anio", selAnio);
			param.put("mes", selMes);
			param.put("uuoo", selUuoo);
			param.put("tipoDoc", tipoDocumento);
			param.put("nroDoc", numeroDocumento);
			param.put("cadJson", cadJson);
			log.debug("parametros: " + selAnio + " " + selMes + " " + selUuoo + " " + tipoDocumento + " " + numeroDocumento + " " + tipoPrincipal);
			
			InformeResultado informeParam = new InformeResultado();
			informeParam.setAnnInforme(selAnio);
			informeParam.setMesInforme(selMes);
			informeParam.setCodUuooResp(selUuoo);
			informeParam.setCodTipoPlan(tipoPlan);
			informeParam.setCodTipoInforme(Constantes.TIPO_INFORME_JUSTIFICACION_SIP);//INFORME ENVIO SIP
			informeParam.setIndDel("0");//FILA ACTIVA
			
			List<InformeResultado> informeResultadoList = envioSipService.recuperarInformeResultado(informeParam);
			if (informeResultadoList != null && informeResultadoList.size() > 0) {
				InformeResultado informeResultado = informeResultadoList.get(0);
				
				String nomInforme = numeroDocumento;//informeResultado.getNomInforme();
				String numExpediente = informeResultado.getNumExpediente();
				
				try {
					numExpediente = numExpediente.trim();
				}
				catch (Exception e) {
					numExpediente = "";
				}
				
				// se inserta el numExpediente
				if ("".equals(numExpediente)) {
					numExpediente = bufirService.crearExpediente(numeroDocumento, codigoEmpleado, "423", Constantes.CODIGO_SEDE_DEFAULT);//SI AFECTA TX
					numExpediente = numExpediente.trim();
					bufirService.crearAccion(numExpediente, "001"/*accion seguimiento autorizar*/, "001"/*estado seguimiento autorizado*/, codigoEmpleado, "423", " ", Constantes.CODIGO_SEDE_DEFAULT);//SI AFECTA TX
					
					
					informeParam = new InformeResultado();
					informeParam.setAnnInforme(selAnio);
					informeParam.setMesInforme(selMes);
					informeParam.setCodUuooResp(selUuoo);
					informeParam.setCodTipoPlan(tipoPlan);
					informeParam.setCodTipoInforme(Constantes.TIPO_INFORME_JUSTIFICACION_SIP);//INFORME ENVIO SIP
					informeParam.setIndDel("0");//FILA ACTIVA
					informeParam.setNumExpediente(numExpediente);
					
					envioSipService.actualizarInformeResultado(informeParam);//SI AFECTA TX
				}
				else {
					bufirService.crearAccion(numExpediente, "002"/*accion seguimiento autorizar*/, "001"/*estado seguimiento autorizado*/, codigoEmpleado, "423", " ", Constantes.CODIGO_SEDE_DEFAULT);//SI AFECTA TX
				}
				
				informeParam = new InformeResultado();
				informeParam.setAnnInforme(selAnio);
				informeParam.setMesInforme(selMes);
				informeParam.setCodUuooResp(selUuoo);
				informeParam.setCodTipoPlan(tipoPlan);
				informeParam.setCodTipoInforme(Constantes.TIPO_INFORME_JUSTIFICACION_SIP);//INFORME ENVIO SIP
				informeParam.setIndDel("0");//FILA ACTIVA
				informeParam.setIndInforme("3");//prepara envio para bufir
				
				envioSipService.actualizarInformeResultado(informeParam);//SI AFECTA TX
				
				Map<String, Object> paramSp = new HashMap<String, Object>();
				paramSp.put("numedoc", numeroDocumento);
				paramSp.put("secfirma", 1);
				paramSp.put("usuing", codigoEmpleado);
				paramSp.put("numeexp", numExpediente);
				paramSp.put("secbuf", null);
				paramSp.put("mensaje", null);
				
				log.debug("paramaetros pre ejecucion de spDoc944Creadoc: " + paramSp);
				bufirService.spDoc944Creadoc(paramSp);//SI AFECTA TX
				log.debug("paramaetros post ejecucion de spDoc944Creadoc: " + paramSp);
				
				//if ("OK".equals(paramSp.get("mensaje"))) {
				if (paramSp.get("mensaje") == null && paramSp.get("secbuf") != null) {
				paramSp = new HashMap<String, Object>();
					paramSp.put("pTipoPlan", "POI");
					paramSp.put("pAnio", selAnio);
					paramSp.put("pMes", selMes);
					paramSp.put("pProceso", "JUBG");
					paramSp.put("ValorProceso", " ");
					paramSp.put("pCodProducto", " ");
					paramSp.put("pUsuario", " ");
					paramSp.put("pEstado", "2");// madar otra vez a 1
					paramSp.put("pFase", "FIRMA");
					paramSp.put("pActualizarEstado", "1");
					
					envioSipService.cerrarIndicadoresEjec(paramSp);//SI AFECTA TX
				}
				else {
					throw new Exception("error en la ejecucion del store procedure de creacion bufe: " + paramSp.get("mensaje"));
				}
				
				informeParam = new InformeResultado();
				informeParam.setAnnInforme(selAnio);
				informeParam.setMesInforme(selMes);
				informeParam.setCodUuooResp(selUuoo);
				informeParam.setCodTipoPlan(tipoPlan);
				informeParam.setCodTipoInforme(Constantes.TIPO_INFORME_JUSTIFICACION_SIP);//INFORME ENVIO SIP
				informeParam.setIndDel("0");//FILA ACTIVA
				
				informeResultadoList = envioSipService.recuperarInformeResultado(informeParam);
				informeResultado = informeResultadoList.get(0);
				
				validarInforme = true;
				resultado.put("informeResultado", informeResultado);
			}
			else {
				validarInforme = false;
			}
			
			//PARA LAS AO
			/*informeParam = new InformeResultado();
			informeParam.setAnnInforme(selAnio);
			informeParam.setMesInforme(selMes);
			informeParam.setCodUuooResp(selUuoo);
			informeParam.setCodTipoPlan(tipoPlan);
			informeParam.setCodTipoInforme(Constantes.TIPO_INFORME_AO;//"03" INFORME ENVIO SIP
			informeParam.setNomInforme();
			informeParam.setIndDel("0");//FILA ACTIVA*/
			

			resultado.put("error", "0");
		}
		catch (Exception e) {
			log.error("error", e);
			resultado.put("error", "1");
			resultado.put("mensajeError", e.getMessage());
		}
		
		return resultado;
	}
}