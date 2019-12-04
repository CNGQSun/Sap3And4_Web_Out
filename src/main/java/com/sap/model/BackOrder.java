package com.sap.model;

public class BackOrder {

	private String documentStatus;
	private String creditAnalyst;
	private String createdOn;
	private String caseId;
	private String documentNumber;
	private String partner;
	private String description;
	private String externalRefer;
	private String totalAmt;
	private String payt;
	private String riskClass;
	private String crExpos;
	private String limit;
	private String days030;
	private String days3160;
	private String days6190;
	private String days91;
	private String utiliztn;
	private String changedBy;
	private String lastChangedOn;

	public String getDocumentStatus() {
		return documentStatus;
	}

	public void setDocumentStatus(String documentStatus) {
		this.documentStatus = documentStatus == null ? null : documentStatus.trim();
	}

	public String getCreditAnalyst() {
		return creditAnalyst;
	}

	public void setCreditAnalyst(String creditAnalyst) {
		this.creditAnalyst = creditAnalyst == null ? null : creditAnalyst.trim();
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn == null ? null : createdOn.trim();
	}

	public String getCaseId() {
		return caseId;
	}

	public void setCaseId(String caseId) {
		this.caseId = caseId == null ? null : caseId.trim();
	}

	public String getDocumentNumber() {
		return documentNumber;
	}

	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber == null ? null : documentNumber.trim();
	}

	public String getPartner() {
		return partner;
	}

	public void setPartner(String partner) {
		this.partner = partner == null ? null : partner.trim();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description == null ? null : description.trim();
	}

	public String getExternalRefer() {
		return externalRefer;
	}

	public void setExternalRefer(String externalRefer) {
		this.externalRefer = externalRefer == null ? null : externalRefer.trim();
	}

	public String getTotalAmt() {
		return totalAmt;
	}

	public void setTotalAmt(String totalAmt) {
		this.totalAmt = totalAmt == null ? null : totalAmt.trim();
	}

	public String getPayt() {
		return payt;
	}

	public void setPayt(String payt) {
		this.payt = payt == null ? null : payt.trim();
	}

	public String getRiskClass() {
		return riskClass;
	}

	public void setRiskClass(String riskClass) {
		this.riskClass = riskClass == null ? null : riskClass.trim();
	}

	public String getCrExpos() {
		return crExpos;
	}

	public void setCrExpos(String crExpos) {
		this.crExpos = crExpos == null ? null : crExpos.trim();
	}

	public String getLimit() {
		return limit;
	}

	public void setLimit(String limit) {
		this.limit = limit == null ? null : limit.trim();
	}

	public String getDays030() {
		return days030;
	}

	public void setDays030(String days030) {
		this.days030 = days030 == null ? null : days030.trim();
	}

	public String getDays3160() {
		return days3160;
	}

	public void setDays3160(String days3160) {
		this.days3160 = days3160 == null ? null : days3160.trim();
	}

	public String getDays6190() {
		return days6190;
	}

	public void setDays6190(String days6190) {
		this.days6190 = days6190 == null ? null : days6190.trim();
	}

	public String getDays91() {
		return days91;
	}

	public void setDays91(String days91) {
		this.days91 = days91 == null ? null : days91.trim();
	}

	public String getUtiliztn() {
		return utiliztn;
	}

	public void setUtiliztn(String utiliztn) {
		this.utiliztn = utiliztn == null ? null : utiliztn.trim();
	}

	public String getChangedBy() {
		return changedBy;
	}

	public void setChangedBy(String changedBy) {
		this.changedBy = changedBy == null ? null : changedBy.trim();
	}

	public String getLastChangedOn() {
		return lastChangedOn;
	}

	public void setLastChangedOn(String lastChangedOn) {
		this.lastChangedOn = lastChangedOn == null ? null : lastChangedOn.trim();
	}

}
