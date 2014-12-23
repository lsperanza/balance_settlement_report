package report.bsl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import report.util.RESTConnector;

public class PaymentsBo {
	
	
	RESTConnector connector = RESTConnector.getInstance();
	
	private static final Logger log = Logger.getLogger(PaymentsBo.class);


	public JSONObject getPayment(Long payId) throws Exception {
		String uri = "collections/"+payId+"?";
		
		JSONObject jsonObject = null;
		//log.info("Intentando conexion con " + host);
		Map<String, Object> resp;
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put("X-New-Coupon-Version","true");

		try {
			resp = connector.execGet(uri,headers);
			log.info("COLLECTIONS API response: " + resp);

			Object status = resp.get("status");
			
			if (!(status instanceof String) && (Integer.valueOf(status.toString()).equals(200))) {
			
				String json = resp.get("data").toString();
				jsonObject = JSONObject.fromObject(json);
			}
			else
			{
				log.error("Respuesta no esperada: "+ resp.toString());
				throw new Exception("Error al conectarse a " + uri);
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		return jsonObject;
	}

}
