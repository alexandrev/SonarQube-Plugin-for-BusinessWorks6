package com.tibco.businessworks6.sonar.plugin.data.model;

import java.util.ArrayList;
import java.util.List;

public class Operation {
	protected String name;
	protected List<BwService> operationReferencedService = new ArrayList<BwService>();

	public List<BwService> getOperationReferencedService() {
		return operationReferencedService;
	}

	public void setOperationReferencedService(List<BwService> operationReferencedService) {
		this.operationReferencedService = operationReferencedService;
	}

	public Operation(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
