using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace ws.hoyland.sszs
{
    public class EngineMessageType
    {
        //incoming message
        public const int IM_CONFIG_UPDATED = 0x1010; //配置更新
        public const int IM_USERLOGIN = 0x1011; //UU用户登录
        public const int IM_UL_STATUS = 0x1012; //登录结果
        public const int IM_LOAD_ACCOUNT = 0x1013; //加载帐号文件
        public const int IM_LOAD_MAIL = 0x1014; //加载邮件列表
        public const int IM_CAPTCHA_TYPE = 0x1015;
        public const int IM_PROCESS = 0x1016;

        public const int IM_IMAGE_DATA = 0x1017;
        public const int IM_REQUIRE_MAIL = 0x1018;
        public const int IM_INFO = 0x1019;

        public const int IM_NO_EMAILS = 0x1020;

        public const int IM_FINISH = 0x1021;
        public const int IM_START = 0x1022;

        public const int IM_EXIT = 0x1023;
        public const int IM_PAUSE = 0x1024;

        public const int IM_FREQ = 0x1025; //申诉频繁

        public const int IM_PAUSE_COUNT = 0x1026;

        //outgoing message
        public const int OM_LOGINING = 0x2010;
        public const int OM_LOGINED = 0x2011;
        public const int OM_CLEAR_ACC_TBL = 0x2012;
        public const int OM_ADD_ACC_TBIT = 0x2013;
        public const int OM_ACCOUNT_LOADED = 0x2014;

        public const int OM_CLEAR_MAIL_TBL = 0x2015;
        public const int OM_ADD_MAIL_TBIT = 0x2016;
        public const int OM_MAIL_LOADED = 0x2017;

        public const int OM_READY = 0x2018;
        public const int OM_UNREADY = 0x2019;
        public const int OM_LOGIN_ERROR = 0x2020;
        public const int OM_RUNNING = 0x2021;
        public const int OM_IMAGE_DATA = 0x2022;
        public const int OM_REQUIRE_MAIL = 0x2023;
        public const int OM_INFO = 0x2024;
        public const int OM_RECONN = 0x2025;
        public const int OM_PAUSE = 0x2026;
        public const int OM_NP = 0x2027; //need pause
    }
}
