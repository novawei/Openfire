package com.nfs.mucextend;

import java.io.File;

import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.interceptor.InterceptorManager;
import org.jivesoftware.openfire.muc.MUCEventDispatcher;

public class MUCExtendPlugin implements Plugin {
	
	private MUCExtendEventListener listener;
	private RoomPresenceInterceptor interceptor;
	private RoomIQHandler roomHandler;
	private UserIQHandler userHandler;

	@Override
	public void initializePlugin(PluginManager manager, File pluginDirectory) {
		listener = new MUCExtendEventListener();
		MUCEventDispatcher.addListener(listener);
		
		interceptor = new RoomPresenceInterceptor();
		InterceptorManager.getInstance().addInterceptor(interceptor);
		
		roomHandler = new RoomIQHandler();
		XMPPServer.getInstance().getIQRouter().addHandler(roomHandler);
		userHandler = new UserIQHandler();
		XMPPServer.getInstance().getIQRouter().addHandler(userHandler);
	}

	@Override
	public void destroyPlugin() {
		MUCEventDispatcher.removeListener(listener);
		listener = null;
		
		InterceptorManager.getInstance().removeInterceptor(interceptor);
		interceptor = null;
		
		XMPPServer.getInstance().getIQRouter().removeHandler(roomHandler);
		roomHandler = null;
		XMPPServer.getInstance().getIQRouter().removeHandler(userHandler);
		userHandler = null;
	}

}
