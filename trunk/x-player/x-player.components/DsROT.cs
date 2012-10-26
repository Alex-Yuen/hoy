using System;
using System.Collections.Generic;
using System.Text;
using System.Runtime.InteropServices;

namespace xplayer
{
    class DsROT
    {
        public static bool AddGraphToRot(object graph, out int cookie)
        {
            cookie = 0;
            int hr = 0;
            UCOMIRunningObjectTable rot = null;
            UCOMIMoniker mk = null;
            try
            {
                hr = GetRunningObjectTable(0, out rot);
                if (hr < 0)
                    Marshal.ThrowExceptionForHR(hr);

                int id = GetCurrentProcessId();
                IntPtr iuPtr = Marshal.GetIUnknownForObject(graph);
                int iuInt = (int)iuPtr;
                Marshal.Release(iuPtr);
                string item = string.Format("FilterGraph {0} pid {1}", iuInt.ToString("x8"), id.ToString("x8"));
                hr = CreateItemMoniker("!", item, out mk);
                if (hr < 0)
                    Marshal.ThrowExceptionForHR(hr);

                rot.Register(ROTFLAGS_REGISTRATIONKEEPSALIVE, graph, mk, out cookie);
                return true;
            }
            catch (Exception)
            {
                return false;
            }
            finally
            {
                if (mk != null)
                    Marshal.ReleaseComObject(mk); mk = null;
                if (rot != null)
                    Marshal.ReleaseComObject(rot); rot = null;
            }
        }

        public static bool RemoveGraphFromRot(ref int cookie)
        {
            UCOMIRunningObjectTable rot = null;
            try
            {
                int hr = GetRunningObjectTable(0, out rot);
                if (hr < 0)
                    Marshal.ThrowExceptionForHR(hr);

                rot.Revoke(cookie);
                cookie = 0;
                return true;
            }
            catch (Exception)
            {
                return false;
            }
            finally
            {
                if (rot != null)
                    Marshal.ReleaseComObject(rot); rot = null;
            }
        }

        private const int ROTFLAGS_REGISTRATIONKEEPSALIVE = 1;

        [DllImport("ole32.dll", ExactSpelling = true)]
        private static extern int GetRunningObjectTable(int r,
            out UCOMIRunningObjectTable pprot);

        [DllImport("ole32.dll", CharSet = CharSet.Unicode, ExactSpelling = true)]
        private static extern int CreateItemMoniker(string delim,
            string item, out UCOMIMoniker ppmk);

        [DllImport("kernel32.dll", ExactSpelling = true)]
        private static extern int GetCurrentProcessId();
    }
}
