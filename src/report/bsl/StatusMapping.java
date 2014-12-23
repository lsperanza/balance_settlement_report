package report.bsl;

import java.util.HashMap;
import java.util.Map;

public class StatusMapping {
	
	static Map<String,String> statusByPayment;
	static Map<String,String> statusByBalanceHistoryType;
	private static void initialize()
	{
		statusByBalanceHistoryType = new HashMap<String,String>();
		statusByBalanceHistoryType.put("payment","SETTLED");
		statusByBalanceHistoryType.put("coupon","SETTLED");
		statusByBalanceHistoryType.put("refund","REFUNDED");
		statusByBalanceHistoryType.put("mediation","DISPUTE");
		statusByBalanceHistoryType.put("chargeback","CHARGEBACK");
		statusByBalanceHistoryType.put("mediation_cancel","DISPUTE");
		statusByBalanceHistoryType.put("chargeback_cancel","CHARGEBACK");
		statusByPayment = new HashMap<String,String>();
		statusByPayment.put("approved_accredited", "SETTLED");
		statusByPayment.put("approved_partially_refunded", "REFUNDED");
		statusByPayment.put("in_mediation_pending", "SETTLED");
		statusByPayment.put("refunded_refunded", "REFUNDED");
		statusByPayment.put("refunded_by_admin", "REFUNDED");
		statusByPayment.put("refunded_bpp_applied", "DISPUTE");
		statusByPayment.put("refunded_bpp_refunded", "DISPUTE");
		statusByPayment.put("refunded_bpp_covered", "DISPUTE");
		statusByPayment.put("charged_back_in_process", "CHARGEBACK");
		statusByPayment.put("charged_back_reimbursed", "CHARGEBACK");
		statusByPayment.put("charged_back_settled", "CHARGEBACK");
	}
	public static Map<String, String> getStatusMappingBH() {
		if(statusByBalanceHistoryType == null)
		{
			initialize();
		}
		return statusByBalanceHistoryType;
	}
	public static Map<String, String> getStatusMapping() {
		if(statusByPayment == null)
		{
			initialize();
		}
		return statusByPayment;
	}

}
