package com.nfs.mucextend;

import java.io.File;

import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.muc.MUCEventDispatcher;

public class MUCExtendPlugin implements Plugin {
	
	private MUCExtendEventListener listener;

	@Override
	public void initializePlugin(PluginManager manager, File pluginDirectory) {
		listener = new MUCExtendEventListener();
		MUCEventDispatcher.addListener(listener);
	}

	@Override
	public void destroyPlugin() {
		MUCEventDispatcher.removeListener(listener);
		listener = null;
	}

}
