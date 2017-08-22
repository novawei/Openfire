package com.nfs.mucextend.iq;

import org.jivesoftware.openfire.IQHandlerInfo;
import org.jivesoftware.openfire.auth.UnauthorizedException;
import org.jivesoftware.openfire.handler.IQHandler;
import org.xmpp.packet.IQ;

public class IQProcessor extends IQHandler {

	private IQHandlerInfo info;
	private IQProcess process;

	public IQProcessor(String moduleName, String namespace, IQProcess process) {
		super(moduleName);
		this.info = new IQHandlerInfo(moduleName, namespace);
		this.process = process;
	}

	@Override
	public IQ handleIQ(IQ packet) throws UnauthorizedException {
		return this.process.process(packet);
	}

	@Override
	public IQHandlerInfo getInfo() {
		return this.info;
	}
}
