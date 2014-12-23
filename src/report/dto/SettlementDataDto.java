package report.dto;

import java.math.BigDecimal;

public class SettlementDataDto {

	String orderNumber; //external_reference
	Long acquirerTraceId; //payment_id
	Long merchantId; //collector_id
	String paymentType; //payment_type
	String paymentMethod; //payment_method_id
	String site; //site_id
	String transactionType; //status
	BigDecimal transactionAmount; //transaction_amount o monto del refund
	String transactionCurrency;//currency_id
	String transactionDateTime;//date_created
	BigDecimal settlementNetAmount;//net_received_amount o monto neto del refund
	String settlementCurrency;//currency_id
	String settlementDate;//last_modified
	BigDecimal realAmount;//

	public SettlementDataDto getCopy()
	{
		SettlementDataDto copy = new SettlementDataDto();
		copy.orderNumber = this.orderNumber;
		copy.acquirerTraceId = this.acquirerTraceId;
		copy.merchantId = this.merchantId;
		copy.paymentType = this.paymentType;
		copy.paymentMethod = this.paymentMethod;
		copy.site = this.site;
		copy.transactionType = this.transactionType;
		copy.transactionAmount = this.transactionAmount;
		copy.transactionCurrency = this.transactionCurrency;
		copy.transactionDateTime = this.transactionDateTime;
		copy.settlementNetAmount = this.settlementNetAmount;
		copy.settlementCurrency = this.settlementCurrency;
		copy.settlementDate = this.settlementDate;
		copy.realAmount = this.realAmount;

		return copy;
	}

	public String getOrderNumber()
	{
		return orderNumber;
	}

	public Long getAcquirerTraceId()
	{
		return acquirerTraceId;
	}

	public Long getMerchantId()
	{
		return merchantId;
	}

	public String getPaymentType()
	{
		return paymentType;
	}

	public String getPaymentMethod()
	{
		return paymentMethod;
	}

	public String getSite()
	{
		return site;
	}

	public String getTransactionType()
	{
		return transactionType;
	}

	public BigDecimal getTransactionAmount()
	{
		return transactionAmount;
	}

	public String getTransactionCurrency()
	{
		return transactionCurrency;
	}

	public String getTransactionDateTime()
	{
		return transactionDateTime;
	}

	public BigDecimal getSettlementNetAmount()
	{
		return settlementNetAmount;
	}

	public String getSettlementCurrency()
	{
		return settlementCurrency;
	}

	public String getSettlementDate()
	{
		return settlementDate;
	}

	public BigDecimal getRealAmount()
	{
		return realAmount;
	}

	public void setOrderNumber(String orderNumber)
	{
		this.orderNumber = orderNumber;
	}

	public void setAcquirerTraceId(Long acquirerTraceId)
	{
		this.acquirerTraceId = acquirerTraceId;
	}

	public void setMerchantId(Long merchantId)
	{
		this.merchantId = merchantId;
	}

	public void setPaymentType(String paymentType)
	{
		this.paymentType = paymentType;
	}

	public void setPaymentMethod(String paymentMethod)
	{
		this.paymentMethod = paymentMethod;
	}

	public void setSite(String site)
	{
		this.site = site;
	}

	public void setTransactionType(String transactionType)
	{
		this.transactionType = transactionType;
	}

	public void setTransactionAmount(BigDecimal transactionAmount)
	{
		this.transactionAmount = transactionAmount;
	}

	public void setTransactionCurrency(String transactionCurrency)
	{
		this.transactionCurrency = transactionCurrency;
	}

	public void setTransactionDateTime(String transactionDateTime)
	{
		this.transactionDateTime = transactionDateTime;
	}

	public void setSettlementNetAmount(BigDecimal settlementNetAmount)
	{
		this.settlementNetAmount = settlementNetAmount;
	}

	public void setSettlementCurrency(String settlementCurrency)
	{
		this.settlementCurrency = settlementCurrency;
	}

	public void setSettlementDate(String settlementDate)
	{
		this.settlementDate = settlementDate;
	}

	public void setRealAmount(BigDecimal realAmount)
	{
		this.realAmount = realAmount;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("SettlementDataDto [orderNumber=");
		builder.append(orderNumber);
		builder.append(", acquirerTraceId=");
		builder.append(acquirerTraceId);
		builder.append(", merchantId=");
		builder.append(merchantId);
		builder.append(", paymentType=");
		builder.append(paymentType);
		builder.append(", paymentMethod=");
		builder.append(paymentMethod);
		builder.append(", site=");
		builder.append(site);
		builder.append(", transactionType=");
		builder.append(transactionType);
		builder.append(", transactionAmount=");
		builder.append(transactionAmount);
		builder.append(", transactionCurrency=");
		builder.append(transactionCurrency);
		builder.append(", transactionDateTime=");
		builder.append(transactionDateTime);
		builder.append(", settlementNetAmount=");
		builder.append(settlementNetAmount);
		builder.append(", settlementCurrency=");
		builder.append(settlementCurrency);
		builder.append(", settlementDate=");
		builder.append(settlementDate);
		builder.append(", realAmount=");
		builder.append(realAmount);
		builder.append("]");
		return builder.toString();
	}
	
	public String toCSV()
	{
		StringBuilder builder = new StringBuilder();
		builder.append(orderNumber);
		builder.append(",");
		builder.append(acquirerTraceId);
		builder.append(",");
		builder.append(merchantId);
		builder.append(",");
		builder.append(paymentType);
		builder.append(",");
		builder.append(paymentMethod);
		builder.append(",");
		builder.append(site);
		builder.append(",");
		builder.append(transactionType);
		builder.append(",");
		builder.append(transactionAmount);
		builder.append(",");
		builder.append(transactionCurrency);
		builder.append(",");
		builder.append(transactionDateTime);
		builder.append(",");
		builder.append(settlementNetAmount);
		builder.append(",");
		builder.append(settlementCurrency);
		builder.append(",");
		builder.append(settlementDate);
		builder.append(",");
		builder.append(realAmount);
		return builder.toString();
	}
	
}
