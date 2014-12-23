package report.bsl;


import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

import report.dto.SettlementDataDto;
import report.util.ISO8601Parser;

public class ReportBo {
	private static final Logger log = Logger.getLogger(ReportBo.class);

	BalanceHistoryBo bhbo = new BalanceHistoryBo();
	PaymentsBo pbo = new PaymentsBo();
	
	public void generateSettlementReport(Long userId, Calendar dateFrom, Calendar dateTo, String fileNamePrefix) throws Exception
	{
		List<SettlementDataDto> reportData = getReportData(userId, dateFrom, dateTo);
		log.info("Datos del reporte: ");
		log.info(reportData);
		String path = generateFile(reportData, fileNamePrefix);
		log.info("Archivo Generado: "+path);
	}

	public String generateFile(List<SettlementDataDto> reportData, String fileNamePrefix)
	{
		String dateString = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
		BufferedWriter bufferedWriter = null;
		try {
		    
		    bufferedWriter = new BufferedWriter(new FileWriter(fileNamePrefix+dateString+".csv"));
		    bufferedWriter.write("ORDER_NUMBER,ACQUIRER_TRACE_ID,MERCHANT_ID,PAYMENT_TYPE,PAYMENT_METHOD,SITE,TRANSACTION_TYPE,TRANSACTION_AMOUNT,TRANSACTION_CURRENCY,TRANSACTION_DATE_TIME,SETTLEMENT_NET_AMOUNT,SETTLEMENT_CURRENCY,SETTLEMENT_DATE,REAL_AMOUNT");
		    bufferedWriter.newLine();
		    for(SettlementDataDto dto : reportData)
		    {
		    	bufferedWriter.write(dto.toCSV());
		        // write a new line
		        bufferedWriter.newLine();
		        // flush
		        bufferedWriter.flush();
		    }
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		} finally {
		    // close without throwing exception
		    IOUtils.closeQuietly(bufferedWriter);
		}
		return fileNamePrefix+dateString+".csv";
	}
	
	public List<SettlementDataDto> getReportData(Long userId, Calendar dateFrom, Calendar dateTo) throws Exception
	{
		List<JSONObject> movements = bhbo.getAllUserMovements(userId, ISO8601Parser.toUTCString(dateFrom.getTime()),  ISO8601Parser.toUTCString(dateTo.getTime()));
		HashSet<Long> paymentIds = new HashSet<Long>();
		//Recorro la lista para descartar duplicados
		for(JSONObject json : movements)
		{
			paymentIds.add(json.getJSONObject("source").getLong("id"));
		}

		List<SettlementDataDto> settleData = new ArrayList<SettlementDataDto>();
		//Obtengo la informacion de cada pago
		for(Long pay : paymentIds)
		{
			JSONObject json = pbo.getPayment(pay);
			SettlementDataDto data = new SettlementDataDto();
			
			data.setAcquirerTraceId(json.getLong("id"));
			data.setMerchantId(json.getLong("collector_id"));
			data.setPaymentType(json.getString("payment_type"));
			data.setPaymentMethod(json.getString("payment_method_id"));
			data.setSite(json.getString("site_id"));
			data.setTransactionType(StatusMapping.getStatusMapping().get(json.getString("status")+"_"+json.getString("status_detail")));
			data.setTransactionCurrency(json.getString("currency_id"));
			data.setSettlementCurrency(json.getString("currency_id"));
			data.setSettlementDate(json.getString("last_modified"));
			if(json.getString("status_detail").equals("partially_refunded") || json.getString("status_detail").equals("refunded"))
			{
				@SuppressWarnings("unchecked")
				List<JSONObject> jsonRefunds = (List<JSONObject>) json.get("refunds");
				for(JSONObject refund : jsonRefunds)
				{
					SettlementDataDto copy = data.getCopy();
					Calendar refundDate = Calendar.getInstance();
					refundDate.setTime(ISO8601Parser.parseUTCString(refund.getString("date_created")));
					if((refundDate.after(dateFrom) && refundDate.before(dateTo)) && refund.getJSONObject("metadata") != null)
					{
						copy.setOrderNumber(refund.getJSONObject("metadata").getString("external_reference"));
						copy.setTransactionAmount(BigDecimal.valueOf(refund.getDouble("amount")));
						copy.setSettlementNetAmount(BigDecimal.valueOf(refund.getDouble("amount")));
						copy.setRealAmount(BigDecimal.valueOf(refund.getDouble("amount")));
						copy.setTransactionDateTime(refund.getString("date_created"));
						settleData.add(copy);
					}
				}
			}
			else
			{
				data.setOrderNumber(json.getString("external_reference"));
				data.setTransactionAmount(BigDecimal.valueOf(json.getDouble("transaction_amount")));
				data.setSettlementNetAmount(BigDecimal.valueOf(json.getDouble("net_received_amount")));
				data.setRealAmount(bhbo.getSumMovementsForPayment(userId,pay));
				for(JSONObject jsonMove : movements)
				{
					if(jsonMove.getJSONObject("source").getLong("id") == pay)
					{
						data.setTransactionDateTime(jsonMove.getString("date_created"));
						break;
					}
				}
				settleData.add(data);
			}
		}
		return settleData;
	}
}