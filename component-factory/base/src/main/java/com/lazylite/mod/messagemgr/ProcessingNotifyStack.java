package com.lazylite.mod.messagemgr;

import android.util.Log;

import java.util.ArrayList;


public class ProcessingNotifyStack {

	public static final class ProcessingItem {
		public EventId id;
		public int pos;
		public int total;
	}

	public static ProcessingItem push(final EventId id, final int listSize) {
		ProcessingItem item;
		if (size == items.size()) {
			item = new ProcessingItem();
			items.add(item);
			Log.w("MessageManager", "同步通知嵌套达到" + (size + 1) + "层");
		} else {
			item = items.get(size);
		}
		item.id = id;
		item.pos = 0;
		item.total = listSize;
		++size;
		return item;
	}

	public static void pop() {
		--size;
	}

	public static void doAttach(final EventId notifyID) {
		for (int i = 0; i < size; ++i) {
			ProcessingItem item = items.get(i);
			if (item.id == notifyID) {
				++item.total;
			}
		}
	}

	public static void doDetach(final EventId notifyID, final int detachPos) {
		for (int i = 0; i < size; ++i) {
			ProcessingItem item = items.get(i);
			if (item.id == notifyID) {
				--item.total;
				if (detachPos <= item.pos) {
					--item.pos;
				}
			}
		}
	}

	private static int size = 0;
	private static final int INITIALIZE_CAPACITY = 2;
	private static ArrayList<ProcessingItem> items = new ArrayList<ProcessingItem>(INITIALIZE_CAPACITY);
	static {
		for (int i = 0; i < INITIALIZE_CAPACITY; ++i) {
			items.add(new ProcessingItem());
		}
	}
}
