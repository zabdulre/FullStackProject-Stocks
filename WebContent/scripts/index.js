/**
 * 
 */
 
 var executingBuy = false;
 var currentTicker;
 
async function setCompany(){
currentTicker = document.getElementById('searchBarTicker').value;
	await fetch('SearchHandler?' + new URLSearchParams({
		ticker: document.getElementById('searchBarTicker').value
	})).then(response => response.json()).then(function(data){
		if(data['loggedIn'] === 1) loggedInView(data);
		else{
			loggedOutView(data);
		}
			
	});
}

async function setCompanyWithTicker(index){
currentTicker = allFavorites[index]['ticker'];
	await loggedInView(allFavorites[index]);
	document.getElementById('lastPrice').innerHTML = allFavorites[index]['last'] + ' <span style="position:relative;margin:0px;display:inline-block;"><button class = "buttons" onclick="hideOverlay();"  style="position:absolute;top:-50px;right:-8px;" ><i class="fas fa-times"></i></button></span>';
}

async function isFavorite(ticker){
	var f;
	await fetch('FavoritesHandler?' + new URLSearchParams({
		ticker: ticker,
		mode: 'z'
	})).then(response => response.json()).then(function(data){
	console.log('data is ' + data);
	if(data['isFavorite'] === true) f = '<i class="fas fa-star" id = "favButton" onclick = "toggleFavorite(' + "'" + ticker + "'" + ')"> </i>';
	else{
	f = f = '<i class="far fa-star" id = "favButton" onclick = "toggleFavorite(' + "'" + ticker + "'" + ')"> </i>';
	}
	});
	console.log('returning f which is ' + f);
	return f;
}

async function toggleFavorite(ticker){
console.log('inside function');
  var filename = location.pathname.substring(
    location.pathname.lastIndexOf("/") + 1
  );
 if ((filename != 'favorites.html') && (filename != 'favorites.html#')){
await fetch('FavoritesHandler?' + new URLSearchParams({
		ticker: ticker,
		mode: 'toggle'
	})).then(response => response.json()).then(function(data){
	if (data['wasFavorite'] === true){
		document.getElementById('stockTickerh').innerHTML = '<i class="far fa-star" id = "favButton" onclick = "toggleFavorite(' + "'" + ticker + "'" + ')"> </i>' + ticker;
	}
	else {
		document.getElementById('stockTickerh').innerHTML = '<i class="fas fa-star" id = "favButton" onclick = "toggleFavorite(' + "'" + ticker + "'" + ')"> </i>' + ticker;
	}
	});
	}
	else{
		removeFromFavorite(ticker);
	}
}

function removeFromFavorite(ticker){//for the favorites page only
	document.getElementById('stockDisplayWrapper').style.display="none";
	xButtonClicked(ticker);
}

async function loggedInView(data){
	console.log('populating tables');
			console.log(data);
			var f = await isFavorite(data['ticker']);
			document.getElementById('stockTickerh').innerHTML = f + data["ticker"];
			document.getElementById('companyNameh').innerHTML = data["name"];
			document.getElementById('stockExchangeh').innerHTML = data["exchangeCode"];
			document.getElementById('table').style.borderSpacing = "82px 12px";
			//document.getElementById('stockDisplay').style.paddingLeft = "37%";
			if ((data['isMarketOpen'] === false)){//make bid ask and mid invisible
			document.getElementById('isMarketOpen').innerHTML = '<span style = "background-color: rgb(255, 0, 0)">Market is closed on ' + data['timestamp'].substring(0, 10) + ' ' + data['timestamp'].substring(12, 19);
				document.getElementById('buyStuff').style.display = "none";
				document.getElementById('t1').style.display = "none";//add favorites icon
				document.getElementById('t2').style.display = "none";
				document.getElementById('t3').style.display = "none";
				document.getElementById('t4').style.display = "none";
				document.getElementById('t5').style.display = "none";
				document.getElementById('askPriceTable').style.display = "none";//add favorites icon
				document.getElementById('bidPriceTable').style.display = "none";
				document.getElementById('bidSizeTable').style.display = "none";
				document.getElementById('dash').style.display = "none";
				document.getElementById('askSizeTable').style.display = "none";
								console.log('done Hiding stuff');
			}
			else{
				document.getElementById('isMarketOpen').innerHTML = '<span style = "background-color: rgb(204, 255, 153)">Market is open</span>';
			}
			//add background color
			document.getElementById('lastPrice').innerHTML = data["last"];
			var change = ((data["last"] - data["prevClose"]) * 100/data["prevClose"]).toFixed(2);
			
			if (change < 0){
				document.getElementById('percentage').innerHTML =  '<i class="fas fa-caret-down"></i>' + (data['last']-data['prevClose']).toFixed(2) + ' (' + change + ')%';
				document.getElementById('percentage').style.color = "rgb(255, 0, 0)";
				document.getElementById('lastPrice').style.color = "rgb(255, 0, 0)";
			}
			else{
				document.getElementById('percentage').innerHTML =  '<i class="fas fa-caret-up"></i>' + (data['last']-data['prevClose']).toFixed(2) + ' (' + change + ')%';
				document.getElementById('percentage').style.color = "rgb(51, 204, 51)";
				document.getElementById('lastPrice').style.color = "rgb(51, 204, 51)";
			}
			//
			var today = new Date();
			var date = today.getFullYear()+'-0'+(today.getMonth()+1)+'-'+today.getDate();
			var time = today.getHours() + ":" + today.getMinutes() + ":" + today.getSeconds();
			var dateTime = date+' '+time;
			//the above code snippet was found available for public use at https://tecadmin.net/get-current-date-time-javascript/ 
			if (data['mid'] != null) document.getElementById('dash').innerHTML = data['mid'];
			document.getElementById('date').innerHTML = dateTime;
			document.getElementById('prevCloseTable').innerHTML = data["prevClose"];
			document.getElementById('highPriceTable').innerHTML = data["high"];
			document.getElementById('lowPriceTable').innerHTML = data["low"];
			document.getElementById('askPriceTable').innerHTML = data["askPrice"];
			document.getElementById('openPriceTable').innerHTML = data["open"];
			document.getElementById('askSizeTable').innerHTML = data["askSize"];
			document.getElementById('bidPriceTable').innerHTML = data["bidPrice"];
			document.getElementById('volumeTable').innerHTML = data["volume"];
			document.getElementById('bidSizeTable').innerHTML = data["bidSize"];
			document.getElementById('companyDescription').innerHTML = data["description"];
			document.getElementById('startDate').innerHTML = 'Start Date: ' + data["startDate"];
}



	var input = document.getElementById('searchBarTicker');
	if (input != null){
	input.addEventListener('keyup', function(event){
	   if(event.which == 13 || event.keyCode == 13) 
   {
     // do stuff
     event.preventDefault();
     search();
   }
	})
	}

function checkNegative(){
	var q = document.getElementById('quantityh').value;
	if (q <= 0){
	alert('Cannot have a quantity of ' + q);
	return false;}
	}


async function executeBuy(ticke){
	if (ticke === null) ticke = currentTicker;
	if (!executingBuy){
	executingBuy = true;
	var dat;
	console.log('about to execute buy');
	var q = document.getElementById('quantityh').value;
		if (q <= 0){executingBuy=false;
	return;}
	var data = {
		mode: "buy",
		ticker: ticke,
		quantity: q
	}
	await fetch('PortfolioHandler', {
	method: 'POST',
	headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(data)
	}).then(response => response.json()).then(function(data){
		if (data['status'] == 2) alert('Insufficient Balance, could not buy ' + q + ' of ' + ticke);
		else alert('Purchased ' + q + ' of ' + ticke + ' for $' + (data['price']*data['quantityBought']).toFixed(2));
	});
	}
	executingBuy = false;
	return;
}

function loggedOutView(data){
	document.getElementById('percentage').style.display = 'none';
	document.getElementById('lastPrice').style.display = 'none';
	document.getElementById('date').style.display = 'none';
	document.getElementById('buyStuff').style.display = "none";
				document.getElementById('t1').style.display = "none";//add favorites icon
				document.getElementById('t2').style.display = "none";
				document.getElementById('t3').style.display = "none";
				document.getElementById('t4').style.display = "none";
				document.getElementById('t5').style.display = "none";
				document.getElementById('askPriceTable').style.display = "none";//add favorites icon
				document.getElementById('bidPriceTable').style.display = "none";
				document.getElementById('bidSizeTable').style.display = "none";
				document.getElementById('dash').style.display = "none";
				document.getElementById('askSizeTable').style.display = "none";
	document.getElementById('stockTickerh').style = "text-align: center";
	document.getElementById('stockExchange').style = "text-align: center";
	document.getElementById('companyName').style = "text-align: center";
	document.getElementById('table').style = "text-align: center";
	//document.getElementById('table').style.paddingLeft = "20%";
	//document.getElementById('stockTable').style.paddingLeft = "50px";
	//document.getElementById('stockDisplay').style.paddingLeft = "46%";
				document.getElementById('stockTickerh').innerHTML = data["ticker"];
			document.getElementById('companyNameh').innerHTML = data["name"];
			document.getElementById('percentage').innerHTML = data["percentage"];
			document.getElementById('stockExchangeh').innerHTML = data["exchangeCode"];
			document.getElementById('date').innerHTML = data["date"];
						document.getElementById('highPriceTable').innerHTML = data["high"];
			document.getElementById('lowPriceTable').innerHTML = data["low"];
			document.getElementById('askPriceTable').innerHTML = data["askPrice"];
			document.getElementById('openPriceTable').innerHTML = data["open"];
			document.getElementById('askSizeTable').innerHTML = data["askSize"];
			document.getElementById('prevCloseTable').innerHTML = data["close"];
			document.getElementById('bidPriceTable').innerHTML = data["bidPrice"];
			document.getElementById('volumeTable').innerHTML = data["volume"];
			document.getElementById('bidSizeTable').innerHTML = data["bidSize"];
			document.getElementById('companyDescription').innerHTML = data["description"];
			document.getElementById('startDate').innerHTML = 'Start Date: ' + data["startDate"];
}