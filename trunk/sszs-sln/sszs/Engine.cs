using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using ws.hoyland.util;
using System.Collections;
using System.IO;
using System.Text.RegularExpressions;
using System.Configuration;
using System.Globalization;
using System.Threading;
using System.Windows.Forms;
using System.Diagnostics;
using RedQ;
using System.Management;
using System.Net;

namespace ws.hoyland.sszs
{
    public class Engine : Observable
    {
        private static Engine instance;
        private List<String> accounts;
        private List<String> mails;
        private bool loginx = false;
        private int cptType = 0;
        private bool running = false;
        private int mindex = 0;
        private int mcount = 0;
        private int recc = 0;//reconnect count
        private int frecc = 0;//finished
        //private String cip = null; //current ip

        private int atrecc; //config data

        private int pausec = 0;
        private int fpausec = 0;
        private int atpausec;

        private StreamWriter[] output = new StreamWriter[5]; //成功，失败，未运行
        private String[] fns = new String[] { "成功", "失败", "未运行帐号", "已使用邮箱", "未使用邮箱" };
        private String xpath = AppDomain.CurrentDomain.BaseDirectory;
        private int lastTid = 0;
        private bool pause = false;
        private bool freq = false;
        private String recflag = "False";

        private int pc = 0;//pause count;
        private Configuration cfa = null;

        private Hashtable ips = new Hashtable();
        private HashSet<Task> tasks = new HashSet<Task>();
        private Object data = null;
        private System.Timers.Timer t = null;

        private Engine()
        {
            cfa = ConfigurationManager.OpenExeConfiguration(ConfigurationUserLevel.None);
        }

        public static Engine getInstance()
        {
            if (instance == null)
            {
                instance = new Engine();
            }
            return instance;
        }

        /**
         * 消息处理机制
         * @param type
         * @param message
         */
        public void fire(EngineMessage message)
        {
            ConfigurationManager.RefreshSection("appSettings");

            int type = message.getType();
            data = message.getData();
            EngineMessage msg = null;

            switch (type)
            {
                case EngineMessageType.IM_CONFIG_UPDATED:
                    //暂不做处理
                    //
                    break;
                case EngineMessageType.IM_USERLOGIN:
                    t = new System.Timers.Timer(100);
                    //实例化Timer类，设置间隔时间为10000毫秒；   
                    t.Elapsed +=
                    new System.Timers.ElapsedEventHandler(login);

                    //到达时间的时候执行事件；   
                    t.AutoReset = false;
                    //设置是执行一次（false）还是一直执行(true)；   
                    t.Enabled = true;

                    break;
                case EngineMessageType.IM_CHECKEXP:
                    //t = new System.Timers.Timer(100);
                    ////实例化Timer类，设置间隔时间为10000毫秒；   
                    //t.Elapsed +=
                    //new System.Timers.ElapsedEventHandler(checkexp);

                    ////到达时间的时候执行事件；   
                    //t.AutoReset = false;
                    ////设置是执行一次（false）还是一直执行(true)；   
                    //t.Enabled = true;
                    checkexp();
                    break;
                case EngineMessageType.IM_USERCHANGE:
                    loginx = false;
                    msg = new EngineMessage();
                    msg.setType(EngineMessageType.OM_USERCHANGE);
                    this.notifyObservers(msg);

                    ready();

                    break;
                case EngineMessageType.IM_UL_STATUS:
                    msg = new EngineMessage();

                    if (data == null)
                    {
                        msg.setType(EngineMessageType.OM_LOGINING);
                        this.notifyObservers(msg);
                    }
                    else
                    {
                        Object[] objs = (Object[])data;
                        Int32 it = (Int32)objs[0];
                        if (it > 0)
                        {
                            loginx = true;
                            msg.setType(EngineMessageType.OM_LOGINED);
                            msg.setData(data);
                            this.notifyObservers(msg);
                            ready();
                        }
                        else
                        {
                            loginx = false;
                            msg.setType(EngineMessageType.OM_LOGIN_ERROR);
                            msg.setData(data);
                            this.notifyObservers(msg);
                            ready();
                        }
                    }
                    break;
                case EngineMessageType.IM_LOAD_ACCOUNT:
                    String[] paths = Regex.Split((String)message.getData(), "\\|");

                    try
                    {
                        msg = new EngineMessage();
                        msg.setType(EngineMessageType.OM_CLEAR_ACC_TBL);

                        this.notifyObservers(msg);

                        accounts = new List<String>();

                        Encoding ecdtype = EncodingType.GetType(paths[1]);
                        FileStream fs = new FileStream(paths[1], FileMode.Open);
                        StreamReader m_streamReader = new StreamReader(fs, ecdtype);
                        m_streamReader.BaseStream.Seek(0, SeekOrigin.Begin);

                        String line = null;
                        int i = 1;
                        while ((line = m_streamReader.ReadLine()) != null)
                        {
                            if (!line.Equals(""))
                            {
                                line = i + "----" + line;
                                string[] lns = Regex.Split(line, "----");
                                List<string> listArr = new List<string>();
                                listArr.Add(lns[0]);
                                listArr.Add(lns[1]);
                                listArr.Add(lns[2]);
                                listArr.Add("初始化");
                                //listArr.AddRange(lns);
                                //listArr.Insert(3, "初始化");
                                lns = listArr.ToArray();

                                accounts.Add(paths[0] + "----" + line);
                                //							if (lns.size() == 3) {
                                //								lns.add("0");
                                //								lns.add("初始化");
                                //								//line += "----0----初始化";
                                //							} else {
                                //								//line += "----初始化";
                                //								lns.add("初始化");
                                //							}

                                String[] items = lns.ToArray();

                                msg = new EngineMessage();
                                msg.setType(EngineMessageType.OM_ADD_ACC_TBIT);
                                msg.setData(items);

                                this.notifyObservers(msg);
                            }
                            i++;
                        }

                        m_streamReader.Close();
                        m_streamReader.Dispose();
                        fs.Close();
                        fs.Dispose();

                        if (accounts.Count > 0)
                        {
                            List<String> param = new List<String>();
                            param.Add(accounts.Count.ToString());
                            param.Add(paths[1]);

                            msg = new EngineMessage();
                            msg.setType(EngineMessageType.OM_ACCOUNT_LOADED);
                            msg.setData(param);


                            this.notifyObservers(msg);
                        }

                        ready();
                    }
                    catch (Exception ex)
                    {
                        throw ex;
                    }
                    break;
                case EngineMessageType.IM_LOAD_MAIL:
                    String path = (String)message.getData();

                    try
                    {
                        msg = new EngineMessage();
                        msg.setType(EngineMessageType.OM_CLEAR_MAIL_TBL);


                        this.notifyObservers(msg);

                        mails = new List<String>();

                        Encoding ecdtype = EncodingType.GetType(path);
                        FileStream fs = new FileStream(path, FileMode.Open);
                        StreamReader m_streamReader = new StreamReader(fs, ecdtype);
                        m_streamReader.BaseStream.Seek(0, SeekOrigin.Begin);

                        String line = null;
                        int i = 1;
                        while ((line = m_streamReader.ReadLine()) != null)
                        {
                            if (!line.Equals(""))
                            {
                                line = i + "----" + line;
                                mails.Add(line);

                                List<String> lns = new List<String>();
                                lns.AddRange(Regex.Split(line, "----"));
                                lns.Add("0");
                                //							if (lns.size() == 3) {
                                //								lns.add("0");
                                //								lns.add("初始化");
                                //								//line += "----0----初始化";
                                //							} else {
                                //								//line += "----初始化";
                                //								lns.add("初始化");
                                //							}

                                String[] items = lns.ToArray();

                                msg = new EngineMessage();
                                msg.setType(EngineMessageType.OM_ADD_MAIL_TBIT);
                                msg.setData(items);

                                this.notifyObservers(msg);
                            }
                            i++;
                        }

                        m_streamReader.Close();
                        m_streamReader.Dispose();
                        fs.Close();
                        fs.Dispose();

                        if (mails.Count > 0)
                        {
                            List<String> param = new List<String>();
                            param.Add(mails.Count.ToString());
                            param.Add(path);

                            msg = new EngineMessage();
                            msg.setType(EngineMessageType.OM_MAIL_LOADED);
                            msg.setData(param);


                            this.notifyObservers(msg);
                        }

                        ready();
                    }
                    catch (Exception ex)
                    {
                        throw ex;
                    }
                    break;
                case EngineMessageType.IM_CAPTCHA_TYPE:
                    cptType = (Int32)message.getData();
                    ready();
                    break;
                case EngineMessageType.IM_PROCESS:
                    running = !running;

                    msg = new EngineMessage();
                    msg.setType(EngineMessageType.OM_RUNNING);
                    msg.setData(running);

                    this.notifyObservers(msg);

                    if (running)
                    {
                        recflag = cfa.AppSettings.Settings["REC_FLAG"].Value; //每次开始，读一次
                        //创建日志文件
                        //long tm = System.currentTimeMillis();
                        String tm = DateTime.Now.ToString("yyyy年MM月dd日 hh时mm分ss秒", DateTimeFormatInfo.InvariantInfo);
                        for (int i = 0; i < output.Length; i++)
                        {
                            try
                            {
                                output[i] = File.AppendText(xpath + fns[i] + "-" + tm + ".txt");
                            }
                            catch (Exception ex)
                            {
                                throw ex;
                            }
                        }

                        int tc = Int32.Parse(cfa.AppSettings.Settings["THREAD_COUNT"].Value);
                        ThreadPool.SetMinThreads(1, 0);
                        ThreadPool.SetMaxThreads(tc, 0);

                        Int32[] flidx = (Int32[])message.getData();

                        mindex = flidx[2]; //mfirst of SSZS
                        if (mindex == -1)
                        {
                            mindex = 0;
                        }
                        //for (int i = 0; i < accounts.size(); i++) {
                        for (int i = flidx[0]; i <= flidx[1]; i++)
                        {
                            try
                            {
                                Task task = new Task(accounts[i]);
                                Engine.getInstance().addObserver(task);
                                tasks.Add(task);
                                ThreadPool.QueueUserWorkItem(new WaitCallback(task.run));//, task
                            }
                            catch (Exception e)
                            {
                                throw e;
                                //System.out.println(i + ":" + accounts.get(i));
                            }
                        }
                    }
                    else
                    {
                        //停止情况下的处理
                        shutdown();
                    }
                    break;
                case EngineMessageType.IM_IMAGE_DATA:
                    msg = new EngineMessage();
                    msg.setType(EngineMessageType.OM_IMAGE_DATA);
                    msg.setData(message.getData());

                    this.notifyObservers(msg);

                    break;
                case EngineMessageType.IM_REQUIRE_MAIL:

                    String[] ms = null;
                    lock (MailObject.getInstance())
                    {
                        //Random rnd = new Random();
                        //System.err.println("X:A");
                        //System.err.println("X1:"+mcount+"/"+mindex+"/"+mails.size()+"/"+message.getTid());
                        if (mcount == Int32.Parse(cfa.AppSettings.Settings["EMAIL_TIMES"].Value))
                        {
                            //System.err.println("X:B");
                            mcount = 0;
                            mindex++;
                        }

                        //System.err.println("X:C");

                        if (mindex < mails.Count)
                        {
                            //System.err.println("X:D");
                            ms = Regex.Split(mails[mindex], "----");
                            mcount++;
                        }
                        //System.err.println("X:E");
                        //System.err.println("X2:"+mcount+"/"+mindex+"/"+mails.size()+"/"+message.getTid());
                    }
                    msg = new EngineMessage();
                    msg.setTid(message.getTid());
                    msg.setType(EngineMessageType.OM_REQUIRE_MAIL);
                    msg.setData(ms);

                    this.notifyObservers(msg);

                    break;
                case EngineMessageType.IM_INFO:
                    msg = new EngineMessage();
                    msg.setTid(message.getTid());
                    msg.setType(EngineMessageType.OM_INFO);
                    msg.setData(message.getData());


                    this.notifyObservers(msg);
                    break;
                case EngineMessageType.IM_NO_EMAILS:
                    shutdown();
                    break;
                case EngineMessageType.IM_START:
                    //优先处理暂停
                    pausec++;
                    atpausec = Int32.Parse(cfa.AppSettings.Settings["STOP_FLAG_F1"].Value);
                    //暂停通知的触发
                    if ("True".Equals(cfa.AppSettings.Settings["STOP_FLAG"].Value) && pausec == atpausec)
                    {
                        pausec = 0;

                        msg = new EngineMessage();
                        msg.setTid(-1); //所有task
                        msg.setType(EngineMessageType.OM_NP); //need pause
                        //msg.setData(message.getData());


                        this.notifyObservers(msg);
                    }

                    recc++;
                    atrecc = Int32.Parse(cfa.AppSettings.Settings["REC_FLAG_F1"].Value);
                    //System.err.println("通知重拨:"+atrecc+"/"+recc+"/"+frecc);
                    if ("True".Equals(cfa.AppSettings.Settings["REC_FLAG"].Value) && atrecc == recc)//atrecc != 0
                    {//重拨的触发条件
                        recc = 0;

                        msg = new EngineMessage();
                        msg.setTid(-1); //所有task
                        msg.setType(EngineMessageType.OM_RECONN);
                        //msg.setData(message.getData());


                        this.notifyObservers(msg);
                    }

                    break;
                case EngineMessageType.IM_FINISH:
                    //写入日志
                    lastTid = message.getTid();
                    String[] dt = (String[])message.getData();

                    if ("1".Equals(dt[0]))
                    {//成功
                        try
                        {
                            //output[0].write(dt[1]+"----"+dt[2]+"----"+dt[3]+"----"+dt[4]+"----"+dt[5]+"----"+cip + "\r\n");
                            output[0].WriteLine(dt[1] + "----" + dt[2] + "----" + dt[3] + "----" + dt[4] + "----" + dt[5]);
                            output[0].Flush();
                        }
                        catch (Exception e)
                        {
                            throw e;
                        };
                    }
                    else
                    {//失败
                        try
                        {
                            output[1].WriteLine(dt[1] + "----" + dt[2]);
                            output[1].Flush();
                        }
                        catch (Exception e)
                        {
                            throw e;
                        };
                    }

                    //System.err.println("--------------act count:"+pool.getActiveCount()+"/"+pool.getQueue().size());
                    //if(pool.getActiveCount()==1){ //当前是最后一个线程
                    int workerThreads = 0;
                    int completionPortThreads = 0;
                    ThreadPool.GetAvailableThreads(out workerThreads, out completionPortThreads);
                    if (workerThreads == Int32.Parse(cfa.AppSettings.Settings["THREAD_COUNT"].Value) - 1)
                    {
                        //自动关闭
                        //TODO						
                        running = !running;

                        msg = new EngineMessage();
                        msg.setType(EngineMessageType.OM_RUNNING);
                        msg.setData(running);

                        this.notifyObservers(msg);

                        //自动关闭
                        Thread tx = new Thread(shutdown);
                        tx.Start();
                        //shutdown();
                    }

                    fpausec++;
                    atpausec = Int32.Parse(cfa.AppSettings.Settings["STOP_FLAG_F1"].Value);
                    //执行暂停
                    if ("True".Equals(cfa.AppSettings.Settings["STOP_FLAG"].Value) && fpausec == atpausec)
                    {
                        fpausec = 0;
                        try
                        {
                            //System.err.println("自动暂停...");
                            Thread.Sleep(60 * 1000 * Int32.Parse(cfa.AppSettings.Settings["STOP_FLAG_F2"].Value));

                            //所有线程切换状态
                            msg = new EngineMessage();
                            msg.setTid(-1); //所有task
                            msg.setType(EngineMessageType.OM_NP);
                            //msg.setData(message.getData());

                            this.notifyObservers(msg);

                            lock (PauseXObject.getInstance())
                            {
                                try
                                {
                                    Monitor.PulseAll(PauseXObject.getInstance());
                                }
                                catch (Exception e)
                                {
                                    throw e;
                                }
                            }

                            //System.err.println("自动暂停完毕, 继续运行...");
                        }
                        catch (Exception e)
                        {
                            throw e;
                        }
                    }


                    frecc++;

                    atrecc = Int32.Parse(cfa.AppSettings.Settings["REC_FLAG_F1"].Value);
                    //System.err.println(atrecc+"/"+frecc+"/"+freq+"/"+recc);
                    //System.err.println("ACT:"+pool.getActiveCount());
                    //只剩下当前线程，因为START时候发出停止新后之后，可能有遗漏线程已经开始执行了，需等待最后一个线程执行完毕

                    if (("True".Equals(cfa.AppSettings.Settings["REC_FLAG"].Value) && atrecc == frecc) || (freq == true && frecc == recc))//atrecc != 0
                    {//执行重拨 第二个条件不一定可行
                        //System.err.println("Y0");
                        if (freq == true && frecc == recc)
                        {
                            //System.err.println("Y1");
                            freq = false;
                            recc = 0;
                            frecc = 0;
                        }
                        else
                        {
                            //System.err.println("Y2");
                            frecc = 0;
                        }

                        try
                        {
                            //Thread.Sleep(Int32.Parse(cfa.AppSettings.Settings["REC_FLAG_F2"].Value));
                            //重拨
                            System.Timers.Timer tt = new System.Timers.Timer(Int32.Parse(cfa.AppSettings.Settings["REC_FLAG_F2"].Value) * 1000);
                            //实例化Timer类，设置间隔时间为10000毫秒；   
                            tt.Elapsed +=
                            new System.Timers.ElapsedEventHandler(Recon);
                            //到达时间的时候执行事件；   
                            tt.AutoReset = false;
                            //设置是执行一次（false）还是一直执行(true)；   
                            tt.Enabled = true;
                        }
                        catch (Exception e)
                        {
                            throw e;
                        }

                    }
                    break;
                case EngineMessageType.IM_EXIT:
                    //关闭日志文件
                    shutdown();
                    Application.Exit();
                    break;
                case EngineMessageType.IM_PAUSE:
                    pause = !pause;

                    if (pause)
                    {
                        pc = 0; //pause count;
                    }

                    msg = new EngineMessage();
                    msg.setTid(-1); //所有task
                    msg.setType(EngineMessageType.OM_PAUSE);
                    //msg.setData(message.getData());


                    this.notifyObservers(msg);

                    if (!pause)
                    {//继续运行
                        //新建成功文件
                        String tm = DateTime.Now.ToString("yyyy年MM月dd日 hh时mm分ss秒", DateTimeFormatInfo.InvariantInfo);

                        try
                        {
                            output[0] = File.AppendText(xpath + fns[0] + "-" + tm + ".txt");
                        }
                        catch (Exception ex)
                        {
                            throw ex;
                        }
                        //}

                        lock (PauseObject.getInstance())
                        {
                            Monitor.PulseAll(PauseObject.getInstance());
                        }
                    }
                    else
                    {// 停止情况下，关闭output[0]
                        //see EngineMessageType.IM_PAUSE_COUNT
                    }
                    break;
                case EngineMessageType.IM_FREQ:
                    //				recc = 0;
                    //				frecc = 0;
                    freq = true;
                    //通知其他需要重拨
                    msg = new EngineMessage();
                    msg.setTid(-1); //所有task
                    msg.setType(EngineMessageType.OM_RECONN);
                    //msg.setData(message.getData());


                    this.notifyObservers(msg);
                    break;
                case EngineMessageType.IM_PAUSE_COUNT:
                    pc++;
                    if (pc == Int32.Parse(cfa.AppSettings.Settings["THREAD_COUNT"].Value))
                    {
                        //关闭output[0]
                        try
                        {
                            if (output[0] != null)
                            {
                                output[0].Close();
                                output[0] = null;
                            }
                        }
                        catch (Exception e)
                        {
                            throw e;
                        }
                    }
                    break;
                default:
                    break;
            }
        }

        private void shutdown()
        {

            //if(pool!=null){
            //写入未运行帐号日志
            try
            {
                //if(lastTid!=-1){
                if (output[2] != null)
                {
                    for (int i = lastTid; i < accounts.Count; i++)
                    {
                        String[] accl = Regex.Split(accounts[i], "----");
                        output[2].WriteLine(accl[1] + "----" + accl[2]);
                        output[2].Flush();
                    }
                }
            }
            catch (Exception e)
            {
                throw e;
            };
            //写入已使用邮箱日志
            try
            {
                //if(mindex!=-1){
                if (output[3] != null)
                {
                    for (int i = 0; i < mindex + 1; i++)
                    {
                        String[] ml = Regex.Split(mails[i], "----");
                        output[3].WriteLine(ml[1] + "----" + ml[2]);
                        output[3].Flush();
                    }
                }
            }
            catch (Exception e)
            {
                throw e;
            };
            //未使用
            try
            {
                //if(mindex!=-1){
                if (output[4] != null)
                {
                    for (int i = mindex; i < mails.Count; i++)
                    {
                        String[] ml = Regex.Split(mails[i], "----");
                        output[4].WriteLine(ml[1] + "----" + ml[2]);
                        output[4].Flush();
                    }
                }
            }
            catch (Exception e)
            {
                throw e;
            };
            //}

            for (int i = 0; i < output.Length; i++)
            {
                try
                {
                    if (output[i] != null)
                    {
                        output[i].Close();
                        output[i] = null;
                    }
                }
                catch (Exception ex)
                {
                    throw ex;
                }
            }
        }

        private void checkexp()
        {
            int expire = 0;
            try
            {
                WebClient wc = new WebClient();
                Crypter crypt = new Crypter();
                byte[] mc = Expire.getMC();

                string url = "http://222.186.26.132:8086/ge";
                byte[] key = Util.getKey();
                string content = Util.byteArrayToHexString(key).ToUpper() + Util.byteArrayToHexString(crypt.QQ_Encrypt(mc, key)).ToUpper();
                //Console.WriteLine(byteArrayToHexString(key).ToUpper());
                //Console.WriteLine(content);
                //client.UploadString(url, content);
                //client.UploadString(url, 
                //client.Encoding = Encoding.UTF8;
                wc.Headers[HttpRequestHeader.ContentType] = "text/plain; charset=UTF-8";

                //client.UploadData(url, "POST", Encoding.UTF8.GetBytes(content));
                byte[] bs = null;
                bs = wc.UploadData(url, "POST", Encoding.UTF8.GetBytes(content));
                //byte[] bs = Encoding.GetEncoding("GB2312").GetBytes();

                //bs = crypt.QQ_Decrypt(bs, key);
                string resp = Encoding.UTF8.GetString(bs);
                //Console.WriteLine("1:"+resp);
                bs = crypt.QQ_Decrypt(Util.hexStringToByte(resp), key);
                expire = Int32.Parse(Encoding.UTF8.GetString(bs));
                Console.WriteLine("R:" + expire);
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.Message);
                expire = 0;
            }

            EngineMessage msg = new EngineMessage();
            msg.setType(EngineMessageType.OM_CHECKEXP);
            msg.setData(expire);

            this.notifyObservers(msg);
        }

        private void login(object source, System.Timers.ElapsedEventArgs e)
        {
            List<String> msg = (List<String>)data;

            EngineMessage message = new EngineMessage();
            message.setType(EngineMessageType.IM_UL_STATUS);
            message.setData(null);
            Engine.getInstance().fire(message);

            //cptType = Int32.Parse(msg[4]);
            int ret = -1;
            int score = 0;


            {
                int nAppId;         // 软件ＩＤ，开发者分成必要参数。登录开发者后台【我的软件】获得！
                string lpAppKey;    // 软件密钥，开发者分成必要参数。登录开发者后台【我的软件】获得！

                //login
                if (cptType == 0)
                {
                    nAppId = 216;
                    lpAppKey = "25fc5f72b18264ee30ec96d89c5aa1ce";

                    YDMWrapper.YDM_SetAppInfo(nAppId, lpAppKey);
                    ret = YDMWrapper.YDM_Login(msg[0], msg[1]);
                }
                else
                {
                    nAppId = 95435;
                    lpAppKey = "c276a37616f14292929407464d8e3137";
                    ret = UUWrapper.uu_login(msg[0], msg[1]);
                }

                if (ret > 0)
                {
                    loginx = true;

                    if (cptType == 0)
                    {
                        //获取题分
                        score = YDMWrapper.YDM_GetBalance(msg[0], msg[1]);
                    }
                    else
                    {
                        score = UUWrapper.uu_getScore(msg[0], msg[1]);
                    }
                    //toolStripStatusLabel1.Text = "1";

                    //toolStripStatusLabel1.Text = "5";
                    //保存登录参数
                    cfa = ConfigurationManager.OpenExeConfiguration(ConfigurationUserLevel.None);

                    if (cfa == null)
                    {
                        MessageBox.Show("加载配置文件失败!");
                    }
                    ConfigurationManager.RefreshSection("appSettings");

                    cfa.AppSettings.Settings["ACCOUNT"].Value = msg[0];
                    //toolStripStatusLabel1.Text = "2.1";
                    if ("True".Equals(msg[2]))
                    {
                        //toolStripStatusLabel1.Text = "2.2";
                        cfa.AppSettings.Settings["PASSWORD"].Value = msg[1];
                        //toolStripStatusLabel1.Text = "2.3";
                    }
                    else
                    {
                        //toolStripStatusLabel1.Text = "2.4";
                        cfa.AppSettings.Settings["PASSWORD"].Value = "";
                        //toolStripStatusLabel1.Text = "2.5";
                    }
                    //toolStripStatusLabel1.Text = "2.6";
                    cfa.AppSettings.Settings["REM_PASSWORD"].Value = msg[2];
                    //toolStripStatusLabel1.Text = "2.7";
                    cfa.AppSettings.Settings["AUTO_LOGIN"].Value = msg[3];
                    cfa.AppSettings.Settings["CPT_TYPE"].Value = msg[4];
                    //toolStripStatusLabel1.Text = "3";
                    cfa.Save();

                }
                else
                {
                    loginx = false;
                }

            }

            Object[] dt = new Object[3];
            dt[0] = ret;
            dt[1] = score;
            dt[2] = msg[0];

            message = new EngineMessage();
            message.setType(EngineMessageType.IM_UL_STATUS);
            message.setData(dt);
            Engine.getInstance().fire(message);
        }

        private void ready()
        {
            if (accounts != null && accounts.Count > 0 && mails != null && mails.Count > 0 && (cptType == 2 || (cptType != 2 && loginx)))
            {
                EngineMessage msg = new EngineMessage();
                msg.setType(EngineMessageType.OM_READY);

                this.notifyObservers(msg);
            }
            else
            {
                EngineMessage msg = new EngineMessage();
                msg.setType(EngineMessageType.OM_UNREADY);

                this.notifyObservers(msg);
            }
        }

        public int getCptType()
        {
            return this.cptType;
        }

        private void Recon(object source, System.Timers.ElapsedEventArgs e)
        {
            //dlg = delegate()
            {
                //MessageBox.Show("正在重拨");

                ConfigurationManager.RefreshSection("appSettings");
                string adsl = cfa.AppSettings.Settings["REC_FLAG_F5"].Value;
                if (adsl == null || "".Equals(adsl))
                {
                    MessageBox.Show("请指定宽带连接名称");
                    return;
                }

                List<string> adls = Util.GetAllAdslName();
                bool hasadls = false;
                foreach (string ad in adls)
                {
                    if (ad.Equals(adsl))
                    {
                        hasadls = true;
                        break;
                    }
                }

                if (!hasadls)
                {
                    MessageBox.Show("请指定宽带连接名称");
                    return;
                }

                string account = cfa.AppSettings.Settings["REC_FLAG_F6"].Value;
                string password = cfa.AppSettings.Settings["REC_FLAG_F7"].Value;

                bool st = false;
                string cut = "rasdial \"" + adsl + "\" /disconnect";
                string link = "rasdial \"" + adsl + "\" "
                                        + account
                                        + " "
                                        + password;
                try
                {
                    bool fo = true;
                    bool fi = true;

                    int tfo = 0;
                    int tfi = 0;

                    while (fo && tfo < 4)
                    {
                        string result = Execute(cut);
                        Console.WriteLine("CUT:" + result);
                        if (result.IndexOf("没有连接") == -1)
                        {
                            //System.err.println("CUT1");
                            fo = false; // 断线成功，将跳出外循环
                            fi = true;

                            tfi = 0;
                            //System.err.println("CUT2:" + fi + "/" + configuration.getProperty("AWCONN") + "/" + tfi);
                            while (fi && ("True".Equals(cfa.AppSettings.Settings["REC_FLAG_F3"].Value) || ("False".Equals(cfa.AppSettings.Settings["REC_FLAG_F3"].Value) && tfi < 4)))
                            {
                                //System.err.println("CUT3");
                                result = Execute(link);
                                //System.err.println("LINK:" + result);
                                if (result
                                        .IndexOf("已连接") > 0 || result
                                        .IndexOf("已经连接") > 0)
                                {
                                    //System.err.println("CUT4");

                                    result = Execute("ipconfig");
                                    result = result.Substring(result.IndexOf(adsl));
                                    if (result.IndexOf("IP Address") != -1)
                                    {
                                        result = result.Substring(result.IndexOf("IP Address"));
                                    }
                                    if (result.IndexOf("IPv4 地址") != -1)
                                    {
                                        result = result.Substring(result.IndexOf("IPv4 地址"));
                                    }

                                    result = result.Substring(result.IndexOf(":") + 2);
                                    result = result.Substring(0, result.IndexOf("\n "));
                                    //String ip = result;
                                    string rip = result;
                                    string ip = rip;
                                    /**
                                     * IP段重复的判断
                                    if ("true".equals(recflag))
                                    {
                                        ip = result.substring(0, result.lastIndexOf("."));
                                    }**/

                                    //System.err.println("ip=" + ip);
                                    if (ips.ContainsKey(ip) && "True".Equals(cfa.AppSettings.Settings["REC_FLAG_F4"].Value)) //判断是否重复
                                    {
                                        long time = (long)ips[ip];
                                        if (Util.CurrentTimeMillis() - time >= 1000 * 60 * 60 * Int32.Parse(cfa.AppSettings.Settings["REC_ITV"].Value))
                                        {
                                            //System.err.println("IP重复，但超过configuration.getProperty("REC_ITV")小时，拨号成功:" + ip);
                                            //cip = rip;
                                            ips[ip] = Util.CurrentTimeMillis();
                                            fi = false;//跳出内循环
                                            st = true;
                                            //break;
                                        }
                                        else
                                        {
                                            //System.err.println("IP重复，未超过configuration.getProperty("REC_ITV")小时，重新拨号:" + ip);
                                            fo = true;
                                            fi = false;
                                            tfo = 0;
                                            st = false;
                                            //continue;
                                        }
                                    }
                                    else
                                    {
                                        String[] ipx = Regex.Split(ip, ".");
                                        if ("True".Equals(cfa.AppSettings.Settings["IP3FLAG"].Value))
                                        {
                                            if ((ipx[0].Equals(cfa.AppSettings.Settings["IP3_1"].Value)) && (ipx[1].Equals(cfa.AppSettings.Settings["IP3_2"].Value)) && (ipx[2].Equals(cfa.AppSettings.Settings["IP3_3"].Value)))
                                            {
                                                //System.err.println("前3段IP不符合条件，重新拨号");
                                                fo = true;
                                                fi = false;
                                                tfo = 0;
                                                st = false;
                                            }
                                        }
                                        else
                                        {
                                            //System.err.println("IP符合设定条件，拨号成功:"+ip);
                                            //cip = rip;
                                            ips.Add(ip, Util.CurrentTimeMillis());
                                            fi = false;
                                            st = true;
                                        }
                                    }
                                }
                                else
                                {
                                    //System.err.println("CUT5");
                                    //System.err.println("连接失败(" + tfi + ")");
                                    if (tfi < 3)
                                    {
                                        try
                                        {
                                            Thread.Sleep(1000 * 30);
                                        }
                                        catch (Exception ex)
                                        {
                                            MessageBox.Show(ex.Message);
                                        }
                                    }
                                    tfi++;//允许3次循环
                                    //break;
                                }
                            }//while in
                        }
                        else
                        {
                            //System.err.println("CUT6");
                            Console.WriteLine("没有连接(" + tfo + ")");
                            if (tfo < 3)
                            {
                                try
                                {
                                    Thread.Sleep(1000 * 30);
                                }
                                catch (Exception ex)
                                {
                                    MessageBox.Show(ex.Message);
                                }
                            }
                            tfo++; //允许3次循环
                            //break;
                        }
                    }//while out
                }
                catch (Exception ex)
                {
                    MessageBox.Show(ex.Message);
                }

                if (st)
                {
                    //唤醒
                    foreach (Task task in tasks)
                    {
                        task.Pause();//避免下次继续pause
                    }
                    //notify
                    Monitor.PulseAll(this);

                    //MessageBox.Show("重拨结束");
                }
                else
                {
                    //MessageBox.Show("重拨失败");
                }

                //MessageBox.Show("重拨结束");
            }
            //this.BeginInvoke(dlg);
        }

        private string Execute(string cmd)
        {
            string output = "";
            try
            {
                Process p = new Process();
                p.StartInfo.FileName = "cmd.exe";
                p.StartInfo.Arguments = "/c " + cmd;
                p.StartInfo.UseShellExecute = false;
                p.StartInfo.RedirectStandardInput = true;
                p.StartInfo.RedirectStandardOutput = true;

                p.StartInfo.CreateNoWindow = true;
                p.StartInfo.WindowStyle = ProcessWindowStyle.Hidden;

                p.Start();

                //Console.WriteLine(output);
                p.WaitForExit();
                output = p.StandardOutput.ReadToEnd();
                p.Close();
            }
            catch (Exception e)
            {
                MessageBox.Show(e.Message);
            }
            return output;
        }
    }
}