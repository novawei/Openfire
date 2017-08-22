package com.nfs.mucextend.iq;

import org.xmpp.packet.IQ;

public interface IQProcess {
	public IQ process(IQ packet);
}
