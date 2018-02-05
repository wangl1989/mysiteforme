function navBar(strData){
	var data;
	if(typeof(strData) === "string"){
		var data = JSON.parse(strData); //部分用户解析出来的是字符串，转换一下
	}else{
		data = strData;
	}	
	var ulHtml = '<ul class="layui-nav layui-nav-tree">';
	for(var i=0;i<data.length;i++){
		if(data[i].spread){
			ulHtml += '<li class="layui-nav-item layui-nav-itemed">';
		}else{
			ulHtml += '<li class="layui-nav-item">';
		}
		if(data[i].children !== undefined && data[i].children.length > 0){
			ulHtml += '<a href="javascript:;">';
			if(data[i].icon !== undefined && data[i].icon != ''){
				if(data[i].icon.indexOf("icon-") !== -1){
					ulHtml += '<i class="iconfont '+data[i].icon+'" style="font-size: 20px" data-icon="'+data[i].icon+'"></i>';
				}else{
					ulHtml += '<i class="layui-icon" style="font-size: 20px" data-icon="'+data[i].icon+'">'+data[i].icon+'</i>';
				}
			}
			ulHtml += '<cite>'+data[i].title+'</cite>';
			ulHtml += '<span class="layui-nav-more"></span>';
			ulHtml += '</a>';
			ulHtml += '<dl class="layui-nav-child">';
			for(var j=0;j<data[i].children.length;j++){
				if(data[i].children[j].target === "_blank"){
					ulHtml += '<dd><a href="javascript:;" style="font-size: 20px" data-url="'+data[i].children[j].href+'" target="'+data[i].children[j].target+'">';
				}else{
					ulHtml += '<dd><a href="javascript:;" style="font-size: 20px" data-url="'+data[i].children[j].href+'">';
				}
				if(data[i].children[j].icon !== undefined && data[i].children[j].icon !== ''){
					if(data[i].children[j].icon.indexOf("icon-") !== -1){
						ulHtml += '<i class="iconfont '+data[i].children[j].icon+'" style="font-size: 20px" data-icon="'+data[i].children[j].icon+'"></i>';
					}else{
						ulHtml += '<i class="layui-icon" style="font-size: 20px" data-icon="'+data[i].children[j].icon+'">'+data[i].children[j].icon+'</i>';
					}
				}
				ulHtml += '<cite>'+data[i].children[j].title+'</cite></a></dd>';
			}
			ulHtml += "</dl>";
		}else{
			if(data[i].target === "_blank"){
				ulHtml += '<a href="javascript:;" data-url="'+data[i].href+'" target="'+data[i].target+'">';
			}else{
				ulHtml += '<a href="javascript:;" data-url="'+data[i].href+'">';
			}
			if(data[i].icon !== undefined && data[i].icon !== ''){
				if(data[i].icon.indexOf("icon-") !== -1){
					ulHtml += '<i class="iconfont '+data[i].icon+'" style="font-size: 20px" data-icon="'+data[i].icon+'"></i>';
				}else{
					ulHtml += '<i class="layui-icon" style="font-size: 20px" data-icon="'+data[i].icon+'">'+data[i].icon+'</i>';
				}
			}
			ulHtml += '<cite>'+data[i].title+'</cite></a>';
		}
		ulHtml += '</li>';
	}
	ulHtml += '</ul>';
	return ulHtml;
}
