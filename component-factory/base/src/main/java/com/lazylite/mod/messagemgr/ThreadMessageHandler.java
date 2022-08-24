package com.lazylite.mod.messagemgr;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

// by haiping
public final class ThreadMessageHandler {
	
	public Handler getHandler() {
		return handler;
	}
	
	private HandlerThread handlerThread = null;
	
	private Handler handler = null;

	public ThreadMessageHandler() {
		this("mm");
	}

	public ThreadMessageHandler(String name) {
		handlerThread = new HandlerThread("core.ThreadMessageHandler_"+name);
		handlerThread.start();
		handler = new Handler(handlerThread.getLooper());
	}
	
	public ThreadMessageHandler(Looper looper) {
		handler = new Handler(looper);
	}
}
