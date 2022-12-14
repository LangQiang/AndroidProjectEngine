package com.lazylite.mod.utils;

import java.util.concurrent.atomic.AtomicInteger;

//by haiping
public final class KwTrigger {
	public interface Listener {
		void trigger();
	}
	
	public KwTrigger(final int targetCount, final Listener l) {
		if (l == null) {
			throw new IllegalArgumentException("listener 不能为null");
		}
		this.targetCount = targetCount;
		listener = l;
	}
	
	public void touch() {
		int num = touchCount.addAndGet(1);
		if (num==targetCount) {
			immediately();
		}
	}
	
	public synchronized void immediately() {
		if (listener!=null) {
			Listener local = listener;
			listener = null;
			local.trigger();
		}
	}
	
	private int targetCount;
	private AtomicInteger touchCount = new AtomicInteger();
	private Listener listener;
}
