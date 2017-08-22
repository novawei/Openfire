package com.nfs.mucextend;

import java.io.File;
import java.util.List;

import org.jivesoftware.openfire.IQRouter;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.handler.IQHandler;
import org.jivesoftware.openfire.muc.MUCEventDispatcher;

import com.nfs.mucextend.iq.IQHandlerUtils;

public class MUCExtendPlugin implements Plugin {
	
	private MUCExtendEventListener listener;
	private List<IQHandler> handlers;

	@Override
	public void initializePlugin(PluginManager manager, File pluginDirectory) {
		listener = new MUCExtendEventListener();
		MUCEventDispatcher.addListener(listener);
		
		IQRouter router = XMPPServer.getInstance().getIQRouter();
		this.handlers = IQHandlerUtils.buildHandlers();
		for (IQHandler handler : this.handlers) {
			router.addHandler(handler);
		}
	}

	@Override
	public void destroyPlugin() {
		MUCEventDispatcher.removeListener(listener);
		listener = null;
		
		IQRouter router = XMPPServer.getInstance().getIQRouter();
		for (IQHandler handler : this.handlers) {
			router.removeHandler(handler);
		}
		this.handlers = null;
	}

}
