package com.nfs.mucextend;

import java.io.File;

import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.muc.MUCEventDispatcher;

public class MUCExtendPlugin implements Plugin {
	
	private MUCExtendEventListener listener;
	private MUCExtendIQHandler handler;

	@Override
	public void initializePlugin(PluginManager manager, File pluginDirectory) {
		listener = new MUCExtendEventListener();
		MUCEventDispatcher.addListener(listener);
		handler = new MUCExtendIQHandler();
		XMPPServer.getInstance().getIQRouter().addHandler(handler);
	}

	@Override
	public void destroyPlugin() {
		MUCEventDispatcher.removeListener(listener);
		listener = null;
		XMPPServer.getInstance().getIQRouter().removeHandler(handler);
		handler = null;
	}

}
