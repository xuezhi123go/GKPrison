package com.gkzxhn.gkprison.utils.CustomUtils;

/**
 * Author: Huang ZN
 * Date: 2016/12/26
 * Email:943852572@qq.com
 * Description:SharedPreferences中的key常量类
 */

public class SPKeyConstants {

    /**
     * 首次登录
     */
    public static final String FIRST_LOGIN = "is_first";

    /**
     * 首次点击 亲情电话
     */
    public static final String FIRST_CLICK = "is_first_click";

    /**
     * 已注册用户登录进主页
     */
    public static final String IS_REGISTERED_USER = "isRegisteredUser";

    /**
     * app加锁
     */
    public static final String APP_LOCK = "isLock";
    /**
     * 用户名
     */
    public static final String USERNAME = "username";
    /**
     * 密码
     */
    public static final String PASSWORD = "password";
    /**
     * 普通用户
     */
    public static final String IS_COMMON_USER = "isCommonUser";

    /**
     * 用户头像
     */
    public static final String AVATAR = "avatar";
    /**
     * 用户姓名
     */
    public static final String NAME = "name";
    /**
     * 用户与囚犯的关系
     */
    public static final String RELATION_SHIP = "relationship";

    /**
     * token 用于https请求验证
     */
    public static final String ACCESS_TOKEN = "token";
    /**
     * 家属id  家属身份唯一id
     */
    public static final String FAMILY_ID = "family_id";
    /**
     * 囚犯所属监狱
     */
    public static final String JAIL = "jail";

    /**
     * 监狱名称
     */
    public static final String PRISON_NAME = "prisonname";

    /**
     * 刑期开始日期
     */
    public static final String PRISON_TERM_STARTED_AT = "prison_term_started_at";

    /**
     * 刑期结束日期
     */
    public static final String PRISON_TERM_ENDED_AT = "prison_term_ended_at";

    /**
     * 性别
     */
    public static final String GENDER = "gender";

    /**
     * 囚犯姓名
     */
    public static final String PRISONER_NAME = "prisoner_name";

    /**
     * 监狱id
     */
    public static final String JAIL_ID = "jail_id";

    /**
     * 服刑人员囚号
     */
    public static final String PRISONER_NUMBER = "prisoner_number";

    /**
     * 上次会见时间
     */
    public static final String LAST_MEETING_TIME = "last_meeting_time";

    /**
     * 提交会见的时间
     */
    public static final String COMMITTED_MEETING_TIME = "committed_meeting_time";

    /**
     * 实地探监提交时间
     */
    public static final String COMMITTED_TIME = "committed_time";

    /**
     * 闹钟提醒sp
     */
    public static final String IS_MSG_REMIND = "isMsgRemind";

    /**
     * app的密码
     */
    public static final String APP_PASSWORD = "app_password";

    /**
     * 有新消息
     */
    public static final String HAS_NEW_NOTIFICATION = "has_new_notification";

    /**
     * 犯罪类型
     */
    public static final String PRISONER_CRIMES = "crimes";
    /**
     * 用户余额
     */
    public static final String USER_BALANCES = "user_balances";
    /**
     * 最近会见时间
     */
    public static final String NEARLEST_MEET_TIME = "nearlest_meet_time";
    /**
     * 家属端会见时间列表
     */
    public static final String MEETINGS_TIME = "meetings_time";
    /**
     * 是否开通电商模块 1 开通
     */
    public static final String SHOPPING = "shopping";
    /**
     * 是否开通视频模块 1 开通
     */
    public static final String MEETING = "meeting";
    /**
     * 是否开通刑期变动模块
     */
    public static final String PRISONTERM = "prisonterm";
    /**
     * 是否开通奖惩模块
     */
    public static final String REWARDS = "rewards";
}
