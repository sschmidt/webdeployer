package org.eclipse.rtp.httpdeployer.client;

import org.apache.commons.httpclient.methods.EntityEnclosingMethod;

public class DeleteMethodRequestEntity extends EntityEnclosingMethod {

	public DeleteMethodRequestEntity(String uri) {
		super(uri);
	}

	public DeleteMethodRequestEntity() {
		super();
	}

	@Override
	public String getName() {
		return "DELETE";
	}

}
