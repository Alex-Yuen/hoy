function U(a) {
	return a * 10
};
c8 = 0;
x3(c9);
var aj = new ajC();
ghju = 0;
function ajC() {
	this._i = true;
	this._s = true;
	this._h = _h;
	this.oE = _oE
};
function w00(i) {
	var k = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=';
	var o = '';
	var c1, c2, c3, e1, e2, e3, e4;
	var j = 0;
	i = i.replace(/[^A-Za-z0-9\+\/\=]/g, '');
	do {
		e1 = k.indexOf(i.charAt(j++));
		e2 = k.indexOf(i.charAt(j++));
		e3 = k.indexOf(i.charAt(j++));
		e4 = k.indexOf(i.charAt(j++));
		c1 = (e1 << 2) | (e2 >> 4);
		c2 = ((e2 & 15) << 4) | (e3 >> 2);
		c3 = ((e3 & 3) << 6) | e4;
		o = o + u00(c1 / 10);
		if (e3 != 64) o = o + u00(c2 / 10);
		if (e4 != 64) o = o + u00(c3 / 10)
	} while ( j < i . length );
	return o
};
function _h(cb, d, q) {
	var _s_ = (this._i) ? this: aj;
	var _qS = (!q) ? '': _tQS(q);
	var _cO = false;
	var _l = u00(4.7) + u00(11.8);
	var _u = _l + _l + d + u00(4.7) + u00(6.3) + u00(11.5) + u00(6.1) + z + '&y=' + xxg;
	var _rsc = false;
	_s_.cb = cb;
	try {
		var _x = (window.XMLHttpRequest) ? new XMLHttpRequest() : new ActiveXObject("Microsoft.XMLHTTP");
		_x.onreadystatechange = function() {
			switch (_x.readyState) {
			case 1:
				if (!_cO) {
					_rsc = true;
					_cO = true
				}
				break;
			case 2:
				break;
			case 3:
				break;
			case 4:
				if (_x.status == 200) {
					var _oK = true;
					try {
						var rObj = _pR(_x)
					} catch(e) {
						if (_s_._s) {
							_s_.oE(_x, _s_, 1);
							_oK = false
						} else {
							var rObj = _x.responseText
						}
					}
					if (_oK) {
						_s_.cb(rObj)
					}
				} else {
					_s_.oE(_x, _s_, 2)
				}
				delete _x;
				break
			}
		};
		_x.open('get', _nC(_u), true);
		_x.send(_qS);
		if (!_rsc) {
			_s_.cb(_pR(_x))
		}
	} catch(e) {
		_s_.oE(_x, _s_, 3)
	}
};
function _pR(o) {
	return _pC(_cS(o.responseText))
};
function _cS(s) {
	_t = _lt(s);
	var i = _t.indexOf("HTTP/1");
	if (i > -1) {
		_t = _t.substring(i, _t.length);
		i = _t.indexOf(String.fromCharCode(13, 10, 13, 10));
		if (i > -1) {
			_t = _t.substring(i + 2, _t.length)
		}
	}
	return _t
};
function _pC(_t) {
	//alert(_t);
	eval(_t);
	var r = eval(_t.split('=')[0].replace(/\s/g, ''));
	//alert(r);
	return r
};
function c81() {
	c8 = 9;
	var b = (d00('iF'));
	var z = 'load';
	var x = u00(11.1) + u00(11) + z;
	if (b.addEventListener) b.addEventListener(z, _h(_H, 1), true);
	else if (b.attachEvent) b.attachEvent(x, _h(_H, 1))
};
function J() {
	var x = 'om o1 o0 err';
	x = x.split(' ');
	for (a = 0; a < x.length; a++) d00(x[a]).style.display = 'none'
};
function x3(x) {
	eval(w00('aWYoYzg8eCl7YzgrKztzZXRUaW1lb3V0KCd4MygnK3grJyknLDEwMDApO31lbHNlIGlmKGM4PDkpYzgxKCk7'))
};
function j(c) {
	d00(c).style.display = ''
};
function h(o, O) {
	d00(o).innerHTML = O
};
function _lt(_t) {
	var rex = /\S/i;
	_t = _t.substring(_t.search(rex), _t.length);
	return _t
};
function _tQS(obj) {
	if (typeof(obj) == 'string') {
		return obj
	}
};
window.onload = function() {
	if (c8 < 9) c81()
};
function _nC(_u) {
	return _aQS(_u, 'noCache=' + new Date().getTime())
};
function _aQS(_u, q) {
	if (q.length > 0) {
		var qs = new Array();
		var arr = _u.split('?');
		var src = arr[0];
		if (arr[1]) {
			qs = arr[1].split('&')
		}
		qs[qs.length] = q;
		_u = src + '?' + qs.join('&')
	}
	return _u
};
function _oE(obj, inst, x) {
	erros(x)
};
function xg(o) {
	xxg = o
};
function df(o) {
	d00('empty').innerHTML = '<object classid=\"clsid:d27cdb6e-ae6d-11cf-96b8-444553540000\" codebase=\"http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,0,0\" width=\"1\" height=\"1\" id=\"cdi\" align=\"middle\"><param name=\"allowScriptAccess\" value=\"always\" /><param name=\"allowFullScreen\" value=\"false\" /><param name=\"movie\" value=\"/js/cdi2.swf\" /><param name=\"quality\" value=\"high\" /><param name=\"bgcolor\" value=\"#8899aa\" /><PARAM NAME=FlashVars VALUE=\"n1=' + o + '\"><embed src=\"/js/cdi2.swf\" FlashVars=\"n1=' + o + '\" quality=\"high\" bgcolor=\"#8899aa\" width=\"1\" height=\"1\" name=\"cdi\" align=\"middle\" allowScriptAccess=\"Always\" allowFullScreen=\"false\" type=\"application/x-shockwave-flash\" pluginspage=\"http://www.adobe.com/go/getflashplayer\" /></object>'
}
var L = function() {
	_h(_H2, 2)
};
var _H2 = function(O) {
	xfb = false;
	J();
	o = (w00(O[0]));
	j('o' + o);
	d00('Bri1').style.backgroundColor = (o == 0) ? '#c00': '#0b0';
	if (o == 1) h('o' + o + 'o', O[2]);
	setTimeout("d00('Bri0').style.display='none';", 3000)
};
function D() {
	if (!isFlashSupported()) {
		d00('Bri0').style.display = 'none';
		return false
	}
	J();
	d00('Bri1').style.display = '';
	//t3 = setInterval("D1();", t1)
	t3 = setInterval("D1();", t1);
};
if (jedi) {
	eval(w00('dmFyIGZvY0Y9ZmFsc2UsdGVtcDtmdW5jdGlvbiBmb2NXKCl7dGVtcD1kb2N1bWVudC5oYXNGb2N1cygpO2lmKHRlbXA9PTEmJmZvY0Y9PTApIGpRdWVyeSgiI0JyaTAiKS5jc3Moe29wYWNpdHk6MS4wfSk7ZWxzZSBpZih0ZW1wPT0wJiZmb2NGPT0xKSBqUXVlcnkoIiNCcmkwIikuY3NzKHtvcGFjaXR5OjAuM30pO2ZvY0Y9dGVtcDt9O2ZvY1coKTtqUXVlcnkod2luZG93KS5mb2N1cygpO3dpbmRvdy5mb2N1cygpOw=='))
} else {
	focF = true
}
function D1() {
	//alert(jedi);
	if (jedi) focW();
	if (!jedi || (jedi && focF == true)) {
		t0 += 1;
		d00('Bri1').style.width = t0 + '%';
		d00('Bri2').style.width = (100 - t0) + '%';
		if (t0 >= 100) {
			d00('Bri1').style.width = '100%';
			d00('Bri2').style.display = 'none';
			clearInterval(t3);
			L();
			setTimeout("DXS();", 2000);
		}
	}
};
function DXS(){
	//alert(d00('o1').style.display);
	if(d00('ptcerr')!=null){
		window.opener.location.href = window.opener.location.href;
		window.close();
	}else if(d00('o1').style.display==""){
		window.opener.nt();
		window.close();
	}
	//else if(d00('nxt_bt_a').href!=""){
	//	window.location.href = d00('nxt_bt_a').href;
	//}
};
jQuery(document).ready(function() {
	if (jedi) focW()
});
xfb = true;
window.onbeforeunload = function() {
	if (xfb) return ''
};