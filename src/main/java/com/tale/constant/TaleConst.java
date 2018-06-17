package com.tale.constant;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by BlueT on 2017/3/3.
 */
public class TaleConst {
    public static Map<String, String> initConfig = new HashMap<String, String>();
    public static String LOGIN_SESSION_KEY = "login_user";
    public static final String USER_IN_COOKIE = "S_L_ID";
    public static String APP_VERSION = "v1.0";
    public static Boolean INSTALLED = false;
    public static Boolean ENABLED_CDN = true;

    /**
     * aes加密加盐
     */
    public static String AES_SALT = "0123456789abcdef";

    /**
     * 最大获取文章条数
     */
    public static final int MAX_POSTS = 9999;

    /**
     * 最大页码
     */
    public static final int MAX_PAGE = 100;

    /**
     * 文章最多可以输入的文字数
     */
    public static final int MAX_TEXT_COUNT = 200000;

    /**
     * 文章标题最多可以输入的文字个数
     */
    public static final int MAX_TITLE_COUNT = 200;

    /**
     * 点击次数超过多少更新到数据库
     */
    public static final int HIT_EXCEED = 10;

    /**
     * 上传文件最大1M
     */
    public static Integer MAX_FILE_SIZE = 1048576;

    /**
     * 成功返回
     */
    public static String SUCCESS_RESULT = "SUCCESS";

    /**
     * 要过滤的ip列表
     */
    public static final Set<String> BLOCK_IPS = new HashSet<String>(16);

    public static final String SLUG_HOME = "/";
    public static final String SLUG_ARCHIVES = "archives";
    public static final String SLUG_CATEGRORIES = "categories";
    public static final String SLUG_TAGS = "tags";

    /**
     * 静态资源URI
     */
    public static final String STATIC_URI = "/static";

    /**
     * 安装页面URI
     */
    public static final String INSTALL_URI = "/install";

    /**
     * 后台URI前缀
     */
    public static final String ADMIN_URI = "/admin";

    /**
     * 后台登录地址
     */
    public static final String LOGIN_URI = "/admin/login";

    /**
     * 插件菜单 Attribute Name
     */
    public static final String PLUGINS_MENU_NAME = "plugin_menus";

    public static final String ENV_SUPPORT_163_MUSIC = "app.support_163_music";
    public static final String ENV_SUPPORT_GIST = "app.support_gist";
    public static final String MP3_PREFIX = "[mp3:";
    public static final String MUSIC_IFRAME = "<iframe frameborder=\"no\" border=\"0\" marginwidth=\"0\" marginheight=\"0\" width=350 height=106 src=\"//music.163.com/outchain/player?type=2&id=$1&auto=0&height=88\"></iframe>";
    public static final String MUSIC_REG_PATTERN = "\\[mp3:(\\d+)\\]";
    public static final String GIST_PREFIX_URL = "https://gist.github.com/";
    public static final String GIST_REG_PATTERN = "&lt;script src=\"https://gist.github.com/(\\w+)/(\\w+)\\.js\">&lt;/script>";
    public static final String GIST_REPLATE_PATTERN = "<script src=\"https://gist.github.com/$1/$2\\.js\"></script>";
}
