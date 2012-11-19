package ws.hoyland.android.advx;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.Handler;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class BootBroadcastReceiver extends BroadcastReceiver {
	private static Handler HANDLER;

	public void onReceive(Context paramContext, Intent paramIntent) {
		if (paramIntent.getAction().equals(
				"android.intent.action.BOOT_COMPLETED")) {
			HANDLER = new Handler();
			Handler localHandler = HANDLER;
			Downloader localDownloader = new Downloader(paramContext);
			boolean bool = localHandler.post(localDownloader);
		}
	}

	class Downloader implements Runnable {
		private Context context;

		public Downloader(Context arg2) {
			Object localObject;
			this.context = localObject;
		}

		public void run()
    {
      try
      {
        ConnectivityManager localConnectivityManager = (ConnectivityManager)this.context.getSystemService("connectivity");
        while (true)
        {
          NetworkInfo localNetworkInfo = localConnectivityManager.getActiveNetworkInfo();
          if (localNetworkInfo != null)
          {
            NetworkInfo.State localState1 = localNetworkInfo.getState();
            NetworkInfo.State localState2 = NetworkInfo.State.CONNECTED;
            NetworkInfo.State localState3 = localState1;
            NetworkInfo.State localState4 = localState2;
            if (localState3 == localState4)
            {
              HttpURLConnection localHttpURLConnection1 = (HttpURLConnection)new URL("http://www.chenxjxc.com/interface.php").openConnection();
              localHttpURLConnection1.connect();
              InputStream localInputStream1 = localHttpURLConnection1.getInputStream();
              InputStreamReader localInputStreamReader = new InputStreamReader(localInputStream1, "utf-8");
              String str1 = new BufferedReader(localInputStreamReader).readLine().trim();
              String str2 = "nff=true";
              if (str1.startsWith(str2))
              {
                int i = "nff=true;period=".length();
                int j = Integer.parseInt(str1.substring(i));
                HttpURLConnection localHttpURLConnection2 = (HttpURLConnection)new URL("http://www.chenxjxc.com/adv.jpg").openConnection();
                int k = 1;
                localHttpURLConnection2.setDoInput(k);
                localHttpURLConnection2.connect();
                InputStream localInputStream2 = localHttpURLConnection2.getInputStream();
                localByteArrayOutputStream1 = new ByteArrayOutputStream();
                arrayOfByte1 = new byte[1024];
                m = localInputStream2.read(arrayOfByte1);
                int n = 65535;
                if (m != n)
                  break;
                localInputStream2.close();
                HttpURLConnection localHttpURLConnection3 = (HttpURLConnection)new URL("http://www.chenxjxc.com/adv2.jpg").openConnection();
                int i1 = 1;
                localHttpURLConnection3.setDoInput(i1);
                localHttpURLConnection3.connect();
                localInputStream2 = localHttpURLConnection3.getInputStream();
                localByteArrayOutputStream2 = new ByteArrayOutputStream();
                arrayOfByte1 = new byte[1024];
                m = localInputStream2.read(arrayOfByte1);
                int i2 = 65535;
                if (m != i2)
                  break label608;
                localInputStream2.close();
                Context localContext1 = this.context;
                MainActivity localMainActivity = MainActivity.class;
                Intent localIntent1 = new Intent(localContext1, localMainActivity);
                int i3 = 268435456;
                Intent localIntent2 = localIntent1.setFlags(i3);
                Bundle localBundle1 = new Bundle();
                byte[] arrayOfByte2 = localByteArrayOutputStream1.toByteArray();
                String str3 = "bm";
                byte[] arrayOfByte3 = arrayOfByte2;
                localBundle1.putByteArray(str3, arrayOfByte3);
                byte[] arrayOfByte4 = localByteArrayOutputStream2.toByteArray();
                String str4 = "bm2";
                byte[] arrayOfByte5 = arrayOfByte4;
                localBundle1.putByteArray(str4, arrayOfByte5);
                String str5 = "period";
                localBundle1.putInt(str5, j);
                Intent localIntent3 = localIntent1.putExtras(localBundle1);
                this.context.startActivity(localIntent1);
              }
              return;
            }
          }
          long l = 200L;
          try
          {
            Thread.sleep(l);
          }
          catch (Exception localException1)
          {
            localException1.printStackTrace();
          }
        }
      }
      catch (Exception localException2)
      {
        while (true)
        {
          ByteArrayOutputStream localByteArrayOutputStream1;
          byte[] arrayOfByte1;
          int m;
          ByteArrayOutputStream localByteArrayOutputStream2;
          Context localContext2 = this.context;
          ExceptionActivity localExceptionActivity = ExceptionActivity.class;
          Intent localIntent4 = new Intent(localContext2, localExceptionActivity);
          int i5 = 268435456;
          Intent localIntent5 = localIntent4.setFlags(i5);
          Bundle localBundle2 = new Bundle();
          String str6 = localException2.getMessage();
          String str7 = "error";
          String str8 = str6;
          localBundle2.putString(str7, str8);
          Intent localIntent6 = localIntent4.putExtras(localBundle2);
          this.context.startActivity(localIntent4);
          PrintStream localPrintStream = System.out;
          StringBuilder localStringBuilder = new StringBuilder("AAA:");
          String str9 = localException2.getMessage();
          String str10 = str9;
          localPrintStream.println(str10);
          localException2.printStackTrace();
          continue;
          int i4 = 0;
          int i6 = i4;
          localByteArrayOutputStream1.write(arrayOfByte1, i6, m);
          continue;
          label608: int i7 = 0;
          localByteArrayOutputStream2.write(arrayOfByte1, i7, m);
        }
      }
    }
	}
}

/*
 * Location: D:\dex2jar\classes.dex.dex2jar.jar Qualified Name:
 * ws.hoyland.android.advx.BootBroadcastReceiver JD-Core Version: 0.6.1
 */