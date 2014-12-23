package report;

import java.util.Calendar;

import report.bsl.ReportBo;

public class ExecuteAliPayReport {
	public static void main(String[] args) throws Exception 
	{
		try
		{
			ReportBo bo = new ReportBo();
			Calendar dateFrom = Calendar.getInstance();
			dateFrom.add(Calendar.DAY_OF_MONTH, -1);
			dateFrom.set(Calendar.HOUR_OF_DAY, 0);
			dateFrom.set(Calendar.MINUTE, 0);
			dateFrom.set(Calendar.SECOND, 0);
			dateFrom.set(Calendar.MILLISECOND, 0);
			
			Calendar dateTo = Calendar.getInstance();
			dateTo.add(Calendar.DAY_OF_MONTH, -1);
			dateTo.set(Calendar.HOUR_OF_DAY, 23);
			dateTo.set(Calendar.MINUTE, 59);
			dateTo.set(Calendar.SECOND, 59);
			dateTo.set(Calendar.MILLISECOND, 0);
			Long userId = 164884881L;
			bo.generateSettlementReport(userId , dateFrom, dateTo, "ST-MERCADOPAGO-ALIPAY-");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
