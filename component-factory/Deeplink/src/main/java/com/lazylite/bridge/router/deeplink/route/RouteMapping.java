package com.lazylite.bridge.router.deeplink.route;

import com.godq.deeplink.route.AbsRouter;
import com.godq.deeplink.route.impl.EmptyRouter;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

public class RouteMapping {

    private static final Map<String, AbsRouter> CACHE_ROUTER_MAP = new HashMap<>();

    private static final Map<String, String> ROUTE_MAP = new HashMap<>();

    public static AbsRouter get(String path) {

        AbsRouter absRouter = null;

        String fullName = ROUTE_MAP.get(path);

        if (fullName != null) {

            if (CACHE_ROUTER_MAP.containsKey(fullName)) {
                absRouter = CACHE_ROUTER_MAP.get(fullName);
            } else {
                try {
                    Class<?> aClass = Class.forName(fullName);
                    Object o = aClass.newInstance();
                    if (o instanceof AbsRouter) {
                        absRouter = (AbsRouter) o;
                        CACHE_ROUTER_MAP.put(fullName, absRouter);
                    }
                } catch (InstantiationException instantiationException) {
                    Timber.e("[" + fullName + "]有无参数构造方法吗?");
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return absRouter == null ? EmptyRouter.EMPTY_ROUTER : absRouter;
    }

}
