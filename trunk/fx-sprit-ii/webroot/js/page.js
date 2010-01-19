//---------------共 20 条记录，当前 3/5 页 首页 上一页 下一页 尾页 GO-------------------
//recordCount = 20;
//show = 20
//pageCount = 5;
//pageNow = 3;
//pageStr = "?page=_page_";
//document.write(showListPage(recordCount, show, pageCount, pageNow, pageStr));
function showListPage0(recordCount, show, pageCount, pageNow, pageStr){
 if(pageCount<1) pageCount =0;
 if(pageNow<1)  pageNow = 0;
 str = '<form name="frmpage">共 <B>'+recordCount+'</B> 条记录，当前 <B>'+pageNow+'/'+pageCount+'</B> 页';
 if(pageNow<=1)
  str += " 首页 ";
 else
  str += " <A href=""+pageStr.replace("_page_",1)+"" mce_href=""+pageStr.replace("_page_",1)+"">首页</A> ";
 if(pageNow<=1)
  str += " 上一页 ";
 else
  str += " <A href=""+pageStr.replace("_page_",(pageNow-1))+"" mce_href=""+pageStr.replace("_page_",(pageNow-1))+"">上一页</A> ";
 if(pageNow>=pageCount)
  str += " 下一页 ";
 else
  str += " <A href=""+pageStr.replace("_page_",(pageNow+1))+"" mce_href=""+pageStr.replace("_page_",(pageNow+1))+"">下一页</A> ";
 if(pageNow>=pageCount)
  str += " 尾页 ";
 else
  str += " <A href=""+pageStr.replace("_page_",pageCount)+"" mce_href=""+pageStr.replace("_page_",pageCount)+"">尾页</A> ";
 str += "跳到<input type=\"text\" name=\"txtpage\" size=\"3\">页";
 str += "<input type=\"button\" value=\"GO\" onclick=\"pagego0(document.frmpage.txtpage.value,"+pageNow+","+pageCount+",'"+pageStr+"')\"></form>";
 return str;
}

function pagego0(pageGo,pageNow,pageCount,pageStr){
 if(pageGo>=1 && pageGo<=pageCount && pageNow!=pageGo)
  window.location = pageStr.replace("_page_", pageGo);
}