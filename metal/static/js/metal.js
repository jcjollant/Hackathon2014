function buySellChanged (origin){

	var boxBuySell = document.getElementById('boxBuySell');
	var buttonBuySell = document.getElementById('buttonBuySell');

	if(origin == "chooseBuy"){
		boxBuySell.className = "box boxBuy";
		buttonBuySell.className = "button alternate fit buttonBuy";
		buttonBuySell.innerHTML = '<span id="iconBuySell" class="icon fa-arrow-circle-right"> &nbsp; </span>BUY';
	}
	else{
		boxBuySell.className = "box boxSell";
		buttonBuySell.className = "button alternate fit buttonSell";
		buttonBuySell.innerHTML = '<span id="iconBuySell" class="icon fa-arrow-circle-left"> &nbsp; </span>SELL';
	}

}

function submitform(){
    

	var frm = $(document.OrderForm);
	var array = frm.serializeArray();

	var obj = new Object();

	for (var i = 0; i < array.length; i++)
	{	
		obj[array[i].name] = array[i].value;

		if(array[i].name == "choose") {
			var chooseBuy = document.getElementById('chooseBuy');
			if(chooseBuy.checked){
				obj['Side'] = 1;
			}
			else{
				obj['Side'] = 2;
			}
		}

	}

	obj['ClOrdID'] = "" + new Date().getTime();

	var jsonString = JSON.stringify(obj);

    alert("I am about to POST this:\n\n" + jsonString);

	/*var request = new XMLHttpRequest();
	request.open('POST', 'http://10.25.101.226:81/metal/sendorder/lse', true);
    request.setRequestHeader('Content-Type', 'application/json; charset=UTF-8');
    request.send(jsonString);
	*/

	var boxResult = document.getElementById('boxResult');
	boxResult.className = "box boxResultShow";


}

$(function() {
		    var availablesymbol = [
		    	"F (Ford Motor Co)",
				"FMSA		(FMSA HOLDINGS INC)",
				"FB			(Facebook Inc.)",
				"WFM		(Whole Foods Market Inc.)",
				"WFC		(Wells Fargo & Company)",
				"FNMA		(Federal National Mortgage Association)",
				"^FTSE		(FTSE 100)",
				"FCX		(Freeport-McMoRan Inc.)",
				"FDX		(FedEx Corporation)",
				"VXX		(iPath S&P 500 VIX ST Futures ETN)",
				"BA (BAE Systems plc)"
		    ];
		    $( "#symbol" ).autocomplete({
		      source: availablesymbol
		    });
		  });

