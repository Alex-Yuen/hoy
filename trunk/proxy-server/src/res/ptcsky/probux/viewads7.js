var advs = new Array();
var links = new Array();
var tx = 0;

function mkA(a,fieldset){
	tx += 1;
	var t = "";
	var s = 0;
	for(var i=0;i<a.length;i++){
		var b = a[i];
		var adid = b[0];
		var title = unescape(b[1]);
		var desc = unescape(b[2]);
		var url = b[3];
		var uk = b[4];
		var geo = b[5];
		var upg = b[6];
		var adtime = b[7];
		var adpay = b[8];
		var print_class = b[9];
		var clicked = b[10];
		if(!clicked&&uk!=3){
			advs.push("link"+tx+"x"+i);
			links.push(url);
		}
				t += (lastclass && print_class!=lastclass && uk!=3 ? '<div style="clear:left;"></div>' : '')+
                    '<div id="link' + tx + 'x' + i + '" class="anc-style" '+(uk==3 ? 'style="display:none;"' : '')+' onclick="window.open(\'./view.php?a='+url+'\')">'+
                    
                        '<table cellspacing="0" cellpadding="0" class="box-anuncio" onMouseOut="this.style.borderColor=\'#ccc\';this.style.backgroundColor=\'#fff\'" onMouseOver="this.style.borderColor=\'#bbb\';this.style.backgroundColor=\'#ffffcc\'" onClick="mkC('+adid+')">'+
                        '<tr>'+
                          '<td align="left" style="padding:3px 3px 0;"><div id="title-'+adid+'" class="anuncio-'+(clicked ?  'clicked' : print_class)+'-title">'+title+'</div></td>'+
                        '</tr>'+
                        '<tr>'+
                         '<td align="left" style="padding:3px 6px;vertical-align:top;">'+
                          (print_class=="exte" || print_class=="stan" || print_class=="dail" ? '<div class="anuncio-stan-box">'+desc+'</div>' : '')+
							'<div class="anuncio-bottom">'+
                            (geo == 1 ? '<img title="Demographic Filter" src="'+imgurl+'images/anuncio-global.gif" class="anuncio-imgicons">' : '')+
							(upg == 1 ? '<img title="This is a Premium Targeted Ad" src="'+imgurl+'images/anuncio-premium.gif" class="anuncio-imgicons">' : '')+
                            adtime+' Sec'+
							(adpay>0 ? '&middot; $'+adpay : '')+
                            '</div>'+
                        '</td>'+
                        '</tr>'+
                        '</table>'+
                        
                    '</div>';
				
				var lastclass = print_class;
				if(uk!=3){s=1;}
	}
	document.getElementById("all"+fieldset).innerHTML = t;
	if(s==1)
		document.getElementById("f"+fieldset).style.display = 'block';
}
function mkC(a){
document.getElementById("title-"+a).setAttribute("class","anuncio-clicked-title");
}

function ck(name){  
	//alert(name);
	var card = document.getElementById(name);
	if(document.createEvent){
		//alert("E");
		var ev = document.createEvent('HTMLEvents');
		ev.initEvent('click', false, true);
		card.dispatchEvent(ev);
		//alert("F");
	}
}
    
function nt(){
	if(advs.length>0){
		var cid = advs.shift();
		var lk = links.shift();
		//alert(cid);
		//alert(lk);
		//window.open("http://www.baidu.com");
		window.open("./view.php?a="+lk);
	}else{
		window.location.href = "http://www.probux.com/progrid.php";
	}
}


//auto click the first adv
//nt();