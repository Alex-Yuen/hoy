/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bobble.utils;

/**
 *
 * @author Administrator
 */
public class MyMath {
//�Ƕ���
    //ȡ�ý��󣬰����ս��>>10 (/1024)

    //��int a = 100; b = a * sin(1) >> 10;
    public static int sin(int x)
    {
        int degree = (360 + x % 360) % 360;
        if(degree >= 0 && degree <= 90)
        {
            return sins[degree];
        }
        else if(degree > 90 && degree <= 180)
        {
            return sins[180 - degree];
        }
        else if(degree > 180 && degree <= 270)
        {
            return -sins[degree - 180];
        }
        else
        {
            return -sins[360 - degree];
        }
    }

    public static int cos(int x)
    {
        return sin(90 - x);
    }

    static short[] sins = new short[]
    {
        0,286,571,856,1143,1428,1712,1997,2280,2562,2844,3126,3406,3686,3963,
        4240,4515,4790,5062,5334,5603,5872,6137,6401,6663,6923,7182,7438,7692,
        7942,8192,8437,8681,8922,9161,9397,9630,9859,10087,10310,10531,10749,
        10962,11173,11381,11585,11785,11983,12174,12365,12550,12732,12910,13084,
        13254,13421,13582,13741,13893,14044,14188,14329,14465,14598,14725,14848,
        14966,15081,15191,15296,15396,15491,15582,15668,15749,15825,15897,15964,
        16025,16082,16134,16182,16225,16261,16293,16321,16344,16361,16374,
        16380,16384
    };
}
