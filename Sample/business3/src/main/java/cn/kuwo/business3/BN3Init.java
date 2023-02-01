package cn.kuwo.business3;

import android.content.Context;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.lazylite.annotationlib.AutoInit;
import com.lazylite.bridge.init.Init;
import com.lazylite.mod.messagemgr.MessageManager;
import com.lazylite.mod.utils.toast.KwToast;

import cn.kuwo.business1_api.IBusiness1Service;
import timber.log.Timber;

/**
 * @author GodQ
 * @date 2023/2/1 10:45 上午
 */
@AutoInit
public class BN3Init extends Init {

    @Override
    public void init(Context context) {
        Timber.tag("ProjectEngine").e("Business2Init init");
        MessageManager.getInstance().attachMessage(IBusiness1Service.IBN1Observer.getEVENT_ID(), (IBusiness1Service.IBN1Observer) event -> KwToast.show("BN3 receive " + event));
    }

    @Override
    public void initAfterAgreeProtocol(Context context) {

    }

    @Override
    public Pair<String, Object> getServicePair() {
        return null;
    }
}
