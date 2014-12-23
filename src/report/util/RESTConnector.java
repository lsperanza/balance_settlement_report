package report.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

/**
 * 
 * @author hfloresleyes
 * 
 *         <p>
 *         Clase para el consumo de servicios REST. Implementa la conexion
 *         usando <code>org.apache.http.client</code>.
 *         </p>
 *         <p>
 *         Usage:<br/>
 *         <code>
 * 	RESTConnector conn = RESTConnector('pathDeConfig')<br/>
 * 	conn.execPost(contenidoRequest)<br/>
 * 	conn.killConn()
 * </code>
 *         </p>
 */
public class RESTConnector
{

	private HttpClient servClient;
	private Map<String, Object> config;
	private ClientConnectionManager ccm;

	private static RESTConnector INSTANCE = null;

	private static final Logger log = Logger.getLogger(RESTConnector.class);

	/**
	 * Constructor sincronizado para protegerse de posibles problemas multi-hilo
	 * otra prueba para evitar instanciación múltiple
	 */
	private synchronized static void createInstance()
	{
		if (INSTANCE == null)
		{
			INSTANCE = new RESTConnector();
		}
	}

	public static RESTConnector getInstance()
	{
		if (INSTANCE == null)
			createInstance();
		return INSTANCE;
	}

	/**
	 * Constructor por defecto. <b>No recomendado</b> ya que no prepara la
	 * configuracion de conexion. Si se usa este metodo para construir el
	 * Connector, configurar a mano
	 * 
	 * @see setConfig
	 */
	private RESTConnector()
	{
		config = new HashMap<String, Object>();
		SchemeRegistry supportedSchemes = new SchemeRegistry();
		// Register the "http" and "https" protocol schemes, they are
		// required by the default operator to look up socket factories.
		supportedSchemes.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		supportedSchemes.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
		HttpProtocolParams.setUseExpectContinue(params, false);

		Integer connTimeout = 10000;
		Integer soTimeout = 10000;
		Map<String, Integer> timeout = new HashMap<String, Integer>();
		timeout.put("connection", connTimeout);
		timeout.put("socket", soTimeout);

		config.put("timeout", timeout);

		// log.info("initializing restConnector");

		HttpConnectionParams.setConnectionTimeout(params, connTimeout);
		HttpConnectionParams.setSoTimeout(params, soTimeout);

		ccm = new ThreadSafeClientConnManager(params, supportedSchemes);
		servClient = new DefaultHttpClient(ccm, params);

	}

	/**
	 * Retorna la configuracion actual del Connector. Para fines de
	 * control/debugging
	 * 
	 * @see setConfig
	 * @return
	 */
	public Map<String, Object> getConfig()
	{
		return config;
	}

	/**
	 * <b>Obligatorio</b> si se utiliza el constructor sin parametros.
	 * 
	 * @param configObject
	 *            de la forma " <code>
	 * service [ host:'http://www.google.com', proxy = [	enabled:true, host: '10.12.0.251', port: 3128, user: 'guest', password: 'mercadolibre' ]]
	 * </code>
	 *            "
	 */
	public void setConfig(Map<String, Object> conf)
	{
		this.config = conf;
	}

	Map<String, Object> connectWithRequestContent(String requestContent, HttpEntityEnclosingRequest httpMethod, HashMap<String, String> headers)
			throws IOException
	{
		Map<String, Object> ret = new HashMap<String, Object>();

		StringEntity reqEntity = new StringEntity(requestContent, HTTP.UTF_8);
		reqEntity.setContentType("application/json; charset=" + HTTP.UTF_8);

		log.debug("requestContent :" + requestContent);
		if (reqEntity.isRepeatable())
		{
			reqEntity.writeTo(System.out);
		}
		log.debug("reqEntity: " + reqEntity);
		for (Entry<String, String> header : headers.entrySet())
		{
			httpMethod.setHeader(header.getKey(), header.getValue());
		}
		httpMethod.setHeader("Accept", "application/json");
		
		httpMethod.setEntity(reqEntity);
		try
		{

			HttpResponse httpResponse = servClient.execute((HttpUriRequest) httpMethod);
			String responseBody = this.handleResponse(httpResponse);
			// log.info("Response Body: "+responseBody);
			ret.put("status", httpResponse.getStatusLine().getStatusCode());
			ret.put("data", responseBody);
		}
		catch (Exception e)
		{
			ret.put("status", "FAIL");
			ret.put("data", e.getMessage());
		}

		return ret;
	}

	/**
	 * overwrite de HTTPResponse.handleResponse, para que no tire exception al
	 * recibir un status mayor a 300
	 * 
	 * @param response
	 * @return responseBody
	 */
	String handleResponse(HttpResponse response) throws HttpResponseException, IOException
	{
		HttpEntity entity = response.getEntity();
		return entity == null ? null : EntityUtils.toString(entity);
	}

	Map<String, Object> connectWithURI(HttpRequest httpMethod, HashMap<String, String> headers)
	{

		Map<String, Object> ret = new HashMap<String, Object>();

		try
		{
			for (Entry<String, String> header : headers.entrySet())
			{
				httpMethod.setHeader(header.getKey(), header.getValue());
			}
			httpMethod.setHeader("Accept", "application/json");

			HttpResponse httpResponse = servClient.execute((HttpUriRequest) httpMethod);

			String responseBody = this.handleResponse(httpResponse);

			ret.put("status", httpResponse.getStatusLine().getStatusCode());
			ret.put("data", responseBody);
		}
		catch (Exception e)
		{
			ret.put("status", "FAIL");
			ret.put("data", e.getMessage());
		}

		return ret;
	}

	/**
	 * Conexion por post
	 * 
	 * @param xmlRequest
	 *            contenido del request. Puede ser XML u otro formato
	 * @return un map de la forma
	 *         <code[status:'',data: contenidoDeResponse]></code> (status puede
	 *         ser FAIL u OK)
	 * @throws IOException
	 */
	public Map<String, Object> execPost(String uri,String requestContent, HashMap<String,String> headers) throws IOException
	{
		// config.service.host
		HttpPost httpPost = new HttpPost(getNewUri(uri));

		return connectWithRequestContent(requestContent, httpPost, headers);
	}
	

	public Map<String, Object> execUploadWithPutMethod(String url, String filePath, HashMap<String,String> headers) throws IOException
	{
		Map<String, Object> ret = new HashMap<String, Object>();

		HttpPut request = new HttpPut(url);
	
		for (Entry<String, String> header : headers.entrySet())
		{
			request.setHeader(header.getKey(), header.getValue());
		}
		
		try
		{
			File file = new File(filePath);
			FileEntity fe=new FileEntity(file,"text/csv");
			request.setEntity(fe);
			HttpResponse httpResponse = servClient.execute(request);
			String responseBody = this.handleResponse(httpResponse);
			ret.put("status", httpResponse.getStatusLine().getStatusCode());
			ret.put("data", responseBody);
		}
		catch (Exception e)
		{
			ret.put("status", "FAIL");
			ret.put("data", e.getMessage());
		}

		return ret;
	}
	
	/**
	 * Conexion por post
	 * 
	 * @param xmlRequest
	 *            contenido del request. Puede ser XML u otro formato
	 * @return un map de la forma
	 *         <code[status:'',data: contenidoDeResponse]></code> (status puede
	 *         ser FAIL u OK)
	 * @throws IOException
	 */
	public Map<String, Object> execPostWithUrl(String url,String requestContent, HashMap<String,String> headers) throws IOException
	{
		// config.service.host
		HttpPost httpPost = new HttpPost(url);

		return connectWithRequestContent(requestContent, httpPost, headers);
	}

	private String getNewUri(String uri)
	{
		 return "https://api.mercadolibre.com/"+uri+"&access_token=ADM-601-122309-ed986ae6e75f00b1822e8d11d82799c9__A_K__-testcrm-62867623";
	}

	/**
	 * Conexion por Get
	 * 
	 * @param uri
	 *            - La url del recurso a acceder
	 * @return un map de la forma
	 *         <code[status:'',data: contenidoDeResponse]></code> (status puede
	 *         ser FAIL u OK)
	 */
	public Map<String, Object> execGet(String uri, HashMap<String,String> headers) throws IOException
	{

		HttpGet httpGet = new HttpGet(getNewUri(uri));

		return connectWithURI(httpGet, headers);
	}
	/**
	 * Release de los recursos. <b> Es una buena practica liberar las
	 * conexiones</b>
	 */
	public void killConn()
	{
		servClient.getConnectionManager().shutdown();
	}
}
