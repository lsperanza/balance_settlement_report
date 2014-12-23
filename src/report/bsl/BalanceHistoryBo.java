package report.bsl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import report.util.RESTConnector;

public class BalanceHistoryBo {
	
	
	RESTConnector connector = RESTConnector.getInstance();
	
	private static final Logger log = Logger.getLogger(BalanceHistoryBo.class);
	
	@SuppressWarnings("unchecked")
	public List<JSONObject> getAllUserMovements(Long custId, String dateFrom, String dateTo) throws Exception {
		
		String params = "limit=100&user_id="+custId+"&source_type=~withdrawal&range=date_created&begin_date="+dateFrom+"&end_date="+dateTo;
		List<JSONObject> op = new ArrayList<JSONObject>();
		Integer offset = 0;
		Integer limit;
		Integer total;
		
		JSONObject resp = getUserMovements(params, offset);
		op.addAll((List<JSONObject>) resp.get("results"));
		
		limit = Integer.valueOf(resp.getJSONObject("paging").get("limit").toString());
		total = Integer.valueOf(resp.getJSONObject("paging").get("total").toString());
		
		while (op.size() != total && offset <= total)
		{
			offset += limit;
			resp = getUserMovements(params, offset);
			op.addAll((List<JSONObject>) resp.get("results"));
		}
		
		return op;
	}

	private JSONObject getUserMovements(String params, Integer offset) throws Exception {
		String uri = "balance/history?" + params + "&offset=" + offset;
		
		
		JSONObject jsonObject = null;
		log.debug("Intentando conexion con " + uri);
		Map<String, Object> resp = connector.execGet(uri,new HashMap<String,String>());
		log.info("BALANCE HISTORY API response: " + resp.toString());

		Object status = resp.get("status");
		String json = resp.get("data").toString();
		
		if (!(status instanceof String) && (Integer.valueOf(status.toString()).equals(200))) {
			jsonObject = JSONObject.fromObject(json);
		}
		else
		{
			log.error("Respuesta no esperada: "+ resp.toString());
			throw new Exception("Error al conectarse a " + uri);
		}
		return jsonObject;
	}

	public BigDecimal getSumMovementsForPayment(Long userId, Long pay) throws Exception
	{
		String uri = "balance/history?source_id="+pay+"&user_id="+userId;
		BigDecimal amount = BigDecimal.ZERO;
		
		log.debug("Intentando conexion con " + uri);
		Map<String, Object> resp = connector.execGet(uri,new HashMap<String,String>());
		log.info("BALANCE HISTORY API response: " + resp);
		
		Object status = resp.get("status");
		String json = resp.get("data").toString();
		
		
		if (!(status instanceof String) && (Integer.valueOf(status.toString()).equals(200))) {
			JSONArray moves = JSONObject.fromObject(json).getJSONArray("results");
			for(Object move : moves)
			{
				amount = amount.add(BigDecimal.valueOf(((JSONObject)move).getDouble("net")));
			}
		}
		else
		{
			log.error("Respuesta no esperada: "+ resp);
			throw new Exception("Error al conectarse a " + uri);
		}
				
		return amount;
	}

}
