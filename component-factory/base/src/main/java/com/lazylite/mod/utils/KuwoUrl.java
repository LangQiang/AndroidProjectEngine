package com.lazylite.mod.utils;

import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;

import com.lazylite.mod.config.ConfMgr;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;


public class KuwoUrl {

	private static final String TAG = "KuwoUrl";

	private static final String SEC_SAFE_URL = "safeurl";

	private static final String KEY_LOGURL = "safe_log_url";
	private static final String KEY_WELCOME_PIC_URL = "safe_welcome_pic_url";
	private static final String KEY_PUSH_INFO_URL = "safe_push_info_url";
	private static final String KEY_UID_FETCH_URL = "safe_uid_fetch_url";
	private static final String KEY_SUBSCRIBE_URL = "safe_subscribe_url";
	private static final String KEY_HOST_URL = "safe_host_url";
	private static final String KEY_HOST_URL_N = "safe_host_url_n";
	private static final String KEY_HOST_URL_M = "safe_host_url_m";
	private static final String KEY_HOST_URL_LYRIC = "safe_host_url_lyric";
	private static final String KEY_HOST_URL_CHANGENOTIC = "safe_host_url_changenotice";
	private static final String KEY_HOST_URL_NEW_SEARCH = "safe_host_url_new_search";
	private static final String KEY_HOST_URL_SEARCH = "safe_host_url_search";
	private static final String KEY_HOST_URL_SUB_LIST = "safe_host_url_sub_list";
	private static final String KEY_FLOW_URL = "safe_flow_url";
	private static final String KEY_MOBILEAD_URL = "safe_mobilead_url";
	private static final String KEY_VIP_DIALOG_CONFIG_URL = "safe_vip_dialog_config_url";
	private static final String KEY_MVICON_BASE_URL = "safe_mvicon_base_url";
	private static final String KEY_NEWRADIO_URL = "safe_newradio_url";
	private static final String KEY_UPDATE_DC_URL = "safe_update_dc_url";
	private static final String KEY_SEARCE_ARTIST_INFO_URL = "search_artist_info_url";
	private static final String KEY_UNINSTALL_URL = "safe_uninstall_url";
	private static final String KEY_SEARCH_URL = "safe_search_url";
	private static final String KEY_KAIPING_AD_PIC_URL = "safe_kaiping_ad_pic_url";
	private static final String KEY_KAIPING_AD_TODAY_URL = "safe_kaiping_ad_today_url";
	private static final String KEY_ARTISTFEEDS_URL = "safe_artistfeeds_url";
	private static final String KEY_PICFLOW_URL = "safe_picflow_url";
	private static final String KEY_POSTER_ARTIST_URL = "safe_poster_url";
	private static final String KEY_CLOUD_URL = "cloud_url";
	// 弹幕库后台接口
    private static final String KEY_DANMAKU_HOST_URL = "safe_danmaku_url";
	// Vip 歌曲收费权限验证接口
	private static final String KEY_VIP_NEW_VERIFICATION_URL = "safe_vip_new_verification_url";
	private static final String KEY_GET_TINGSHU_VIP_URL = "safe_get_tingshu_vip_url";
	private static final String KEY_VIP_TS_PAY_DIALOG_URL = "safe_pay_dialog_url";
	// Vip 支付接口
	private static final String KEY_VIP_NEW_PAY_URL = "safe_vip_new_pay_url";
	//登录服务器基础接口
	private static final String KEY_LOGIN_BASE_URL = "safe_login_base_url"; //此为基本接口共用地址，不可直接使用
	private static final String KEY_GET_TEMP_USER_URL = "safe_get_temp_user_url";
	private static final String KEY_COMMENT_BASE = "safe_comment_base_url";
	private static final String KEY_COMMENTW_BASE = "safe_comment_base_url";
	// 消息中心
	private static final String KEY_MESSAGE_CENTER = "safe_message_center_url";
	// k歌Host
	private static final String KEY_KSING_HOST = "safe_ksing_host_url";
	// k歌金币相关Host
	private static final String KEY_KSING_PAY_HOST = "safe_ksing_host_pay_url";
	// k歌音乐故事
	private static final String KEY_KSING_STORY_HOST = "story_host_url";
	// k歌 伐木累
	private static final String KEY_KSING_FAMILY_STORY_HOST = "k_family_host_url";// k歌 伐木累
	private static final String KEY_KSING_FAMILY_STORY_HOST_HTTPS = "k_family_host_url_https";// k歌 伐木累(部分family接口要用https)
	// k歌 资产页
	private static final String KEY_KSING_PROPERTY_STORY_HOST = "k_property_host_url";
	private static final String KEY_KSING_STORY_UPLOADHOST = "story_upload_host_url";
	private static final String KEY_RCM_BASE = "safe_rcm_base_url";
	private static final String KEY_MY_SHOW_URL = "safe_key_myshow_url";
	private static final String UGC_HOST_URL = "ugc_host_url";
	private static final String BIBI_HOST_URL = "bibi_host_url";
	private static final String PUSH_SERVER = "push_server";
	private static final String PUSH_SERVER_PORT = "push_server_port";
	private static final String CD_DOWNLOAD_LIMIT_KEY = "cd_download_limit_key";
	private static final String CD_DETAIL_URL_KEY = "cd_download_url_key";
	private static final String UPGRADE_MUSIC_URL_KEY = "upgrade_music_url";
	private static final String LOSSLESS_LIST_URL_KEY = "lossless_list_url";
	private static final String USER_SIGN_URL_KEY = "user_sign_url_key";
	private static final String USER_SIGN_PAGE_KEY = "user_sign_page_key";
	private static final String MUSIC_PACK_TWICE = "music_pack_twice";
	private static final String BABY_SPECIAL_URL_KEY = "baby_special_url_key";
	private static final String SONGLIST_TAG = "songlist_tag";
	private static final String BURN_HOST_URL = "burn_host_url";
	private static final String WX_CACHE_HOST = "wx_cache_host";
	private static final String TS_MAIN_HOST_KEY = "ts_main_host";
	private static final String KEY_WAPI_KUWO_CN = "wapi_kuwo_cn";
	private static final String MOBILE_BASE_HOST_KEY = "mobilebasedata";
	private static final String SEARCH_BASE_HOST_KEY = "search_host";
	private static final String MOBI_WEB_HOST_KEY = "mobi_web_host_key";
	private static final String WAPI_KUWO_KEY = "wapi_kuwo_cn_host";
	private static final String WX_PACK_HOST = "wx_pack_host";


	private static final String LOGIN_SERVER_HOST = "login_server_host";
	private static final String H5_BASE_HOST = "h5_host";


	private static final String SOUND_HOUND_HOST_URL = "sound_hound_host_url";
	public static enum UrlDef {
		LOGURL("http://", "log.kuwo.cn", "/music.yl", KEY_LOGURL),
		WELCOME_PIC_URL("http://", "artistpic.kuwo.cn", "/pic.web?", KEY_WELCOME_PIC_URL),
		COMMENT_BASE_URL("http://", "comment.kuwo.cn","/com.s?f=ar&q=", KEY_COMMENT_BASE),//60.28.210.68:8015 comment.kuwo.cn
		COMMENT_BASE_URL_NEW("http://", "ncomment.kuwo.cn","/com.s?f=ar&q=", KEY_COMMENT_BASE),//60.28.210.68:8015 comment.kuwo.cn
		COMMENTW_BASE_URL_NEW("http://", "ncommentw.kuwo.cn","/com.s?f=ar&q=", KEY_COMMENTW_BASE),//60.28.210.68:8015 comment.kuwo.cn
		PUSH_INFO_URL("http://", "androidpushserver.kuwo.cn", "/apush.s?", KEY_PUSH_INFO_URL),
		UID_FETCH_URL("http://", "mreg.kuwo.cn", "/regsvr.auth?", KEY_UID_FETCH_URL),
		SUBSCRIBE_URL("http://", "subscribe.kuwo.cn", "/res.subscribe?", KEY_SUBSCRIBE_URL),
		WX_CACHE("http://", TsWeexUrlManager.HOST, "/tingshu?", WX_CACHE_HOST),
		HOST_URL("http://", "mobi.kuwo.cn", "/mobi.s?f=kuwo&q=", KEY_HOST_URL),
		HOST_URL2("http://", "mobi.kuwo.cn", "/mobi.s?", KEY_HOST_URL),
		//		HOST_URL("http://", "60.28.220.93:8083", "/mobi.s?f=kuwo&q=", KEY_HOST_URL),
		//部分接口走nmobi.kuwo.cn域名，2016_q1_one added
		HOST_URL_N("http://", "nmobi.kuwo.cn", "/mobi.s?f=kuwo&q=", KEY_HOST_URL_N),
		HOST_URL_M("http://", "mgxhtj.kuwo.cn", "/mgxh.s?", KEY_HOST_URL_M),
//		HOST_URL_N("http://", "60.28.220.93:8083", "/mobi.s?f=kuwo&q=", KEY_HOST_URL_N),
		//Lyric优化,2016-05-26 ADD
		HOST_URL_LYRIC("http://", "mlyric.kuwo.cn", "/mobi.s?f=kuwo&q=", KEY_HOST_URL_LYRIC),
		//type=xh_change_not 换域名
		HOST_URL_CHANGENOTICE("http://", "changenotice.kuwo.cn", "/mobi.s?f=kuwo&q=", KEY_HOST_URL_CHANGENOTIC),
		//New_Search优化,2016-05-26 ADD
		HOST_URL_NEW_SEARCH("http://", "nmsearch.kuwo.cn", "/mobi.s?f=kuwo&q=", KEY_HOST_URL_NEW_SEARCH),
//		HOST_URL_NEW_SEARCH("http://", "60.28.220.93:8083", "/mobi.s?f=kuwo&q=", KEY_HOST_URL_NEW_SEARCH),
		//Search优化,2017-03-03 ADD
		HOST_URL_SEARCH("http://", "search.kuwo.cn", "/r.s?client=kt&all=", KEY_HOST_URL_SEARCH),
//		HOST_URL_SEARCH("http://", "60.29.226.171", "/r.s?client=kt&all=", KEY_HOST_URL_SEARCH),
		//SUB_LIST优化,2016-06-13 ADD
		HOST_URL_SUB_LIST("http://", "nmsublist.kuwo.cn", "/mobi.s?f=kuwo&q=", KEY_HOST_URL_SUB_LIST),
//		HOST_URL_SUB_LIST("http://", "60.28.220.93:8083", "/mobi.s?f=kuwo&q=", KEY_HOST_URL_SUB_LIST),
		FLOW_URL("http://", "dataplan.kuwo.cn", "/UnicomFlow/", KEY_FLOW_URL),
//		FLOW_URL("http://", "60.29.226.168:8080", "/UnicomFlow/", KEY_FLOW_URL),
//		CACHE_MOBILEAD_URL("http://", "60.28.201.6:2503", "/MobileAdServer/GetMobileAd.do?", KEY_MOBILEAD_URL),
		CACHE_VIP_DIALOG_CONFIG_URL("http://", "vip1.kuwo.cn", "/vip/v2/sysinfo?", KEY_VIP_DIALOG_CONFIG_URL),
//		CACHE_VIP_DIALOG_CONFIG_URL("http://", "console.ecom.kuwo.cn", "/vip/v2/sysinfo?", KEY_VIP_DIALOG_CONFIG_URL),
//		CACHE_VIP_DIALOG_CONFIG_URL("http://", "60.28.201.6:9191", "/vip/v2/sysinfo?", KEY_VIP_DIALOG_CONFIG_URL),
		CROWD_FUNDING_INFO_URL("http://", "mobilead.kuwo.cn", "/MobileAdServer/getCrowdFundingInfo.do?", KEY_MOBILEAD_URL),
//		SHIELD_INFO_URL("http://", "60.28.201.6:2503", "/MobileAdServer/getIsHideAd.do?", KEY_MOBILEAD_URL),
		MVICON_BASE_URL("http://", "datacenter.kuwo.cn", "/d.c?", KEY_MVICON_BASE_URL),
		NEWRADIO_URL("http://", "gxh2.kuwo.cn","/newradio.nr?", KEY_NEWRADIO_URL),
		UPDATE_DC_URL("http://", "checkdcserver.kuwo.cn","/u.dc?type=updatedc", KEY_UPDATE_DC_URL),
		SEARCE_ARTIST_INFO_URL("http://", "sartist.kuwo.cn","/qi.s?", KEY_SEARCE_ARTIST_INFO_URL),
		//SEARCE_ARTIST_INFO_URL("http://", "60.29.226.176:8040","/qi.s?", KEY_SEARCE_ARTIST_INFO_URL);
		SEARCH_URL("http://", "dhjss.kuwo.cn","/s.c?all=", KEY_SEARCH_URL),
		KAIPING_AD_PIC_URL("http://", "rich.kuwo.cn", "/EcomResourceServer/kaiping/getcacheListen?", KEY_KAIPING_AD_PIC_URL),
//		KAIPING_AD_PIC_URL("http://", "60.28.201.6:8080", "/AdService/kaiping/getcache?", KEY_KAIPING_AD_PIC_URL),
		KAIPING_AD_TODAY_URL("http://", "rich.kuwo.cn", "/AdService/kaiping/adinfoListen?", KEY_KAIPING_AD_TODAY_URL),
//		KAIPING_AD_TODAY_URL("http://", "60.28.201.6:8080", "/AdService/kaiping/adinfo?", KEY_KAIPING_AD_TODAY_URL),
		VIP_NEW_VERIFICATION_URL("http://", "musicpay.kuwo.cn", "/music.pay", KEY_VIP_NEW_VERIFICATION_URL),
//		VIP_NEW_VERIFICATION_URL("http://", "60.28.195.120", "/music.pay", KEY_VIP_NEW_VERIFICATION_URL),
		// 新版登录和k歌用户信息接口
		LOGIN_BASE_URL("http://","ar.i.kuwo.cn","/",KEY_LOGIN_BASE_URL),// 60.28.198.13:8780
		GET_TEMP_USER_URL("http://","ar.i.kuwo.cn","/US_NEW/kuwo/vuser",KEY_GET_TEMP_USER_URL),
//		GET_TEMP_USER_URL("http://","60.28.220.107:8880","/US_NEW/kuwo/vuser",KEY_GET_TEMP_USER_URL),
		BIND_KW_ACCOUNT_URL("http://","vip1.kuwo.cn","/vip/v2/userbase/vip?",KEY_VIP_NEW_PAY_URL),
//		BIND_KW_ACCOUNT_URL("http://","console.ecom.kuwo.cn","/vip/v2/userbase/vip?",KEY_VIP_NEW_PAY_URL),
//		 消息中心接口
//		MESSAGE_CENTER_URL("http://","60.28.204.142","/message.s?f=ar&q=", KEY_MESSAGE_CENTER), //http://60.28.217.170/message.s?
		MESSAGE_CENTER_URL("http://","message.kuwo.cn","/message.s?f=ar&q=", KEY_MESSAGE_CENTER), //http://60.28.217.170/message.s?
		// k歌接口
		KSING_HOST_URL("http://","kuwosing.kuwo.cn","/",KEY_KSING_HOST),// 60.28.195.121:8180 kuwosing.kuwo.cn
		KSING_PAY_HOST_URL("http://","ksingpaynew.kuwo.cn","/",KEY_KSING_PAY_HOST),// 60.28.195.121:8180 kuwosing.kuwo.cn
		// k歌音乐故事
		KSING_STORY_HOST_URL("http://","ksinghsy.kuwo.cn","/yinyuegushi/",KEY_KSING_STORY_HOST),// 172.17.72.34:8080
		KSING_FAMILY_HOST_URL("http://", "ksinghsy.kuwo.cn", "/family/", KEY_KSING_FAMILY_STORY_HOST),// 172.17.72.34:8080
		KSING_FAMILY_HOST_URL_HTTPS("https://", "ksinghsy.kuwo.cn", "/family/", KEY_KSING_FAMILY_STORY_HOST_HTTPS),// 172.17.72.34:8080
		KSING_PROPERTY_HOST_URL("http://", "ksinghsy.kuwo.cn", "/assets/", KEY_KSING_PROPERTY_STORY_HOST),// 172.17.72.34:8080
		KSING_STORY_UPLOAD_HOST_URL("http://","kstoryupload.kuwo.cn","/kwcom/upload.js",KEY_KSING_STORY_UPLOADHOST),// 172.17.72.34:8081
		//歌手关注接口
		ARTISTFEEDS("http://", "artistfeeds.kuwo.cn","/qz.s?", KEY_ARTISTFEEDS_URL),
		PICFLOW("http://", "lpsm.kuwo.cn","/lsm.s?", KEY_PICFLOW_URL),
//		PICFLOW("http://", "60.28.210.68:8037","/lsm.s?", KEY_PICFLOW_URL),
		RCM_BASE_URL("http://", "rcm.kuwo.cn","/rec.s?", KEY_RCM_BASE),
		POSTER_ARTIST_URL("http://", "arposter.kuwo.cn","/star_poster.pic?", KEY_POSTER_ARTIST_URL),
//		CLOUD_URL("http://", "60.28.195.120","/pl.svc", KEY_CLOUD_URL),
		CLOUD_URL("http://", "nplserver.kuwo.cn","/pl.svc", KEY_CLOUD_URL),
		MY_SHOW_URL("http://", "recall.kuwo.cn", "/PopupRecall/pop/apprecall", KEY_MY_SHOW_URL),
//		MY_SHOW_URL("http://", "60.28.201.6:8080", "/PopupRecall/pop/apprecall", KEY_MY_SHOW_URL),
		VIP_BASE_VIP_URL("http://", "vip1.kuwo.cn", "/vip", KEY_VIP_NEW_PAY_URL),
		VIP_NEW_PAY_URL("http://", "vip1.kuwo.cn", "/vip/added/mobile/v2", KEY_VIP_NEW_PAY_URL),
//		VIP_NEW_PAY_URL("http://", "console.ecom.kuwo.cn", "/vip/added/mobile/v2", KEY_VIP_NEW_PAY_URL),
		VIP_UPDATE_VIP_URL("http://", "vip1.kuwo.cn", "/vip/v2/user/vip", KEY_VIP_NEW_PAY_URL),
//		 VIP_UPDATE_VIP_URL("http://", "console.ecom.kuwo.cn", "/vip/v2/user/vip", KEY_VIP_NEW_PAY_URL),
		VIP_RECALL_DIALOG_URL("http://", "vip1.kuwo.cn", "/vip/recallServlet?", KEY_VIP_NEW_PAY_URL),
//		UGC_HOST("http://", "60.28.220.95:81", "/ugc", UGC_HOST_URL);
		KSING_SOUND_HOUND_URL("http://", "ksingguess.kuwo.cn", "/ksingguess/guess/", SOUND_HOUND_HOST_URL),//你唱我猜新域名
		UGC_HOST("http://", "ugc.kuwo.cn", "/ugc", UGC_HOST_URL),
		//听书的, 叼不叼
//		BIBI_HOST("http://", "tingshu.kuwo.cn", "/yyhapi", BIBI_HOST_URL),
		BIBI_HOST("http://", "newts.kuwo.cn", "/yyhapi", BIBI_HOST_URL),
		PUSH_HOST("", "kwmsg.kuwo.cn", "", PUSH_SERVER),
		PUSH_HOST_PORT("", "7788", "", PUSH_SERVER_PORT),
		CD_DOWNLOAD_LIMIT("http://", "dc.cd.kuwo.cn", "/", CD_DOWNLOAD_LIMIT_KEY),
		CD_DETAIL_URL("http://", "api.cd.kuwo.cn", "/album/detail?", CD_DETAIL_URL_KEY),
		CHILD_SPECIAL_URL("http://", "baby.kuwo.cn", "/", BABY_SPECIAL_URL_KEY),
		LOSSLESS_LIST_URL("http://", "miscservice.kuwo.cn", "/lossless/list", LOSSLESS_LIST_URL_KEY),
		UPGRADE_MUSIC_URL("http://", "newreco.kuwo.cn", "/music.yl", UPGRADE_MUSIC_URL_KEY),
		USER_SIGN_SCORE_PAGE("http://", "m.kuwo.cn", "/newh5/score/index?src=", USER_SIGN_PAGE_KEY),
		USER_SIGN_URL("http://", "www.kuwo.cn", "/kudou/userSign", USER_SIGN_URL_KEY),
		MUSIC_PACK_TWICE_URL("http://", "payrecall.kuwo.cn", "/recall.s?", MUSIC_PACK_TWICE),
		SONGLIST_TAG_URL("http://","st.kuwo.cn", "/mobicase/", SONGLIST_TAG),
		VALIDATE_LOGIN_URL("http://", "loginserver.kuwo.cn", "/u.s?type=validate_ext&", LOGIN_SERVER_HOST),
		BURN_HOST("http://", "m.kuwo.cn", "/newh5/burn", BURN_HOST_URL),
        WAPI_KUWO("http://", "wapi.kuwo.cn","", WAPI_KUWO_KEY),

//		DANMAKU_BASE_URL("http://", "test-mobi.kuwo.cn", "/", KEY_DANMAKU_HOST_URL),
        DANMAKU_BASE_URL("http://", "mobiledanmu.kuwo.cn", "/", KEY_DANMAKU_HOST_URL),
		TS_MAIN_HOST("https://", "tingshu.kuwo.cn","", TS_MAIN_HOST_KEY),
		MOBILE_BASE_URL("http://", "mobilebasedata.kuwo.cn", "/", MOBILE_BASE_HOST_KEY),
		TS_VERIFICATION_URL("http://", "audiobookpaylite.kuwo.cn", "/porn.hub", KEY_GET_TINGSHU_VIP_URL),
		TS_PAY_DIALOG_URL("https://", "kweex.kuwo.cn", "/500006/web/liteVipAdvertDialog.html?transparence=1", KEY_VIP_TS_PAY_DIALOG_URL),
		SEARCH_BASE_URL("http://", "mobi.kuwo.cn", "/r.s?", SEARCH_BASE_HOST_KEY),
        WAPI_KUWO_CN("https://", "wapi.kuwo.cn","/", KEY_WAPI_KUWO_CN),
		WX_PACK_URL("https://", "pack.kuwo.cn","/app/check?", WX_PACK_HOST),
		MOBI_WEB_BASE_URL("http://", "mobi.kuwo.cn", "/mobiweb.s?", MOBI_WEB_HOST_KEY),
		H5_BASE_URL("https://", "tsm.kuwo.cn", "/", H5_BASE_HOST);

        public String getSafeUrl() {
			return getUrl(null);
		}

		// 仅用于打包检查Url是否合法
		public String getDefaultUrl() {
			return getUrl(defaultHost);
		}

		//仅仅返回host，不带前缀
		public String getHost(){
			String host = SdHost.getInstance().getHostFromSDCardByKey(configKey);
			if (TextUtils.isEmpty(host)) {
				host = defaultHost;
			}
			return host;
		}

		private String startUrl;
		private String endUrl;
		private String defaultHost;
		private String configKey;

		UrlDef(String startUrl, String defaultHost, String endUrl, String configKey) {
			this.startUrl = startUrl;
			this.defaultHost = defaultHost;
			this.endUrl = endUrl;
			this.configKey = configKey;
		}

		private String getUrl(String host) {
			if (TextUtils.isEmpty(host)) {
				// 优先读SD卡后门文件
				host = SdHost.getInstance().getHostFromSDCardByKey(configKey);
				//没读到SD卡继续取后门配置
				if (TextUtils.isEmpty(host)) {
					// 优先读服务器配置，服务器配置没有就用默认的
					host = ConfMgr.getStringValue(SEC_SAFE_URL, configKey, defaultHost);
					Log.i(TAG, "hit from config file:" + configKey);// TODO
				} else {
					Log.v(TAG, "hit from sdcard file:" + configKey);// TODO

					//做个请求协议兼容 如果配置附带协议 去掉默认的startUrl
					if (host.startsWith("http://") || host.startsWith("https://")) {
						return host + endUrl;
					}
				}
			}
			String safeUrl = new StringBuilder(startUrl).append(host).append(endUrl).toString();
			return safeUrl;
		}

	}

	public static class SdHost {

		private static final String ATTR_KEY = "key";
		private static final String ATTR_VALUE = "value";
		private static final String TYPE_HOST = "host";

		private static SdHost mInstance = new SdHost();
		private HashMap<String, String> mHosts = new HashMap<String, String>();

		private SdHost() {
		};

		public static SdHost getInstance() {
			return mInstance;
		}

		public void init() {
			long start = System.currentTimeMillis();
			File hostFile = new File(KwDirs.getDir(KwDirs.HOME), "hosts.xml");
			if (hostFile.exists()) {
				InputStream inputStream = null;
				try {
					inputStream = new FileInputStream(hostFile);
					XmlPullParser parser = Xml.newPullParser();
					parser.setInput(inputStream, "UTF-8");
					int event = parser.getEventType();// 产生第一个事件
					while (event != XmlPullParser.END_DOCUMENT) {
						switch (event) {
						case XmlPullParser.START_DOCUMENT:// 判断当前事件是否是文档开始事件

							break;
						case XmlPullParser.START_TAG:// 判断当前事件是否是标签元素开始事件
							if (TYPE_HOST.equals(parser.getName())) {
								String key = parser.getAttributeValue(null, ATTR_KEY);
								String value = parser.getAttributeValue(null, ATTR_VALUE);
								if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
									mHosts.put(key, value);
								}
							}
							break;
						case XmlPullParser.END_TAG:// 判断当前事件是否是标签元素结束事件

							break;
						}
						event = parser.next();// 进入下一个元素并触发相应事件
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (inputStream != null) {
						try {
							inputStream.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
//			Log.d(TAG, App.isMainProgress() + " init cost:" + (System.currentTimeMillis() - start));// TODO

		}

		public String getHostFromSDCardByKey(String key) {
			String value = null;
			if (mHosts.containsKey(key)) {
				value = mHosts.get(key);
			}
			return value;
		}

		public HashMap<String, String> getHosts() {
			return mHosts;
		}
	}

}