package it.hoyland.me.ddz.main;

import java.util.*;

public class StringParse {
    private int currentPosition;
    private int maxPosition;
    private String str;
    private StringBuffer unstr = null;
    private char delimiter;
    
    //���ڽ����Ĺ��캯��
    public StringParse(String source, char del) {
        currentPosition = 0;
        str = source;
        maxPosition = source.length();
        delimiter = del;
    }
    //������װ�Ĺ��캯��
    public StringParse(char del) {
        unstr = new StringBuffer();
        unstr.append('#');
        delimiter = del; 
    }
    public String getNextToken() {
        if(currentPosition >= maxPosition)
            return " ";
        char c;
        boolean begin = false;
        StringBuffer nextString = new StringBuffer();
       while(currentPosition < maxPosition)
        {
           c = str.charAt(currentPosition);
           if(c != delimiter) 
           {
               nextString.append(c);
               begin = true;
           }
           else//�����������
           {
               if(currentPosition + 1 == maxPosition) break;
               else 
               {
                  c = str.charAt(currentPosition + 1); 
                  if(c == delimiter) 
                  {
                      nextString.append(delimiter);
                      begin = true;
                      currentPosition++;
                  }
                  else if(begin)
                  {
                      break;
                  }
               }
           }
           currentPosition++;
        }
        
       if(!begin) return " ";
       return nextString.toString();
    } 
    //��������ʹ��
     public void setNextToken(String sub) {
         if(sub == null)
         {
              unstr.append("NULL#");
              return;
         }
         int len = sub.length();
         char c;
         for(int i = 0;i < len; i++ )
         {
             c = sub.charAt(i);
             unstr.append(c);
             if(c == '#') unstr.append('#');
         }
         if(len == 0) unstr.append("NULL");
         unstr.append('#');
    } 
    
     public String getString()
     {
          return unstr.toString();
     }
}
    
