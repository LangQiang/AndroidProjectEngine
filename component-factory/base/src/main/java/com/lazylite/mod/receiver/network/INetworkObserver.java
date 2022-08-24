package com.lazylite.mod.receiver.network;

import com.lazylite.mod.messagemgr.EventId;
import com.lazylite.mod.messagemgr.IObserverBase;

public interface INetworkObserver extends IObserverBase {

    EventId EVENT_ID = () -> INetworkObserver.class;

    void onNetworkChanged(boolean isNetworkAvailable, boolean isWifiAvailable);

}
