
delete Hammer.defaults.cssProps.userSelect;

var items=document.getElementsByTagName("p");

for(var i=0;i<items.length;i++){ 
var mc = new Hammer.Manager(items[i]);

mc.add( new Hammer.Tap() );

mc.add( new Hammer.Tap({ event: 'doubletap', taps: 2 ,posThreshold: 100,interval:500}) );

mc.get('doubletap').recognizeWith('tap');

mc.on("doubletap", function(ev) {
	var param = this.textContent;
	location.href='app://pythe/param?id='+param;
	}.bind(items[i])
);

} 



