/**
 * 
 */
var executingBuy = false;
var isMarketOpen = true;
var executingSale = false;

getCompanies();
getUserInfo();


async function executeSell(ticker, q){
	if (!executingSale){
		executingSale = true;
		
			if (q <= 0){
		executingSale=false;
		alert('Failed: Sale Not Possible, Cannot purchase negative quantity');
		return;}
		
			var data = {
		mode: "sell",
		ticker: ticker,
		quantity: q
		}
		
		await fetch('PortfolioHandler', {
	method: 'POST',
	headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(data)
	}).then(response => response.json()).then(function(data){
		if (data['status'] == 2) alert('Insufficient stock, could not sell ' + q + ' of ' + ticker);
		else{
		alert('Sold ' + q + ' of ' + ticker + ' for $' + data['profit'].toFixed(2));
		getUserInfo();
		//update here
		updateAfterSell(data);
		}
	});
		
		executingSale = false;
	}
}


function updateAfterSell(data){
	if(data['status'] === 1){
	var quantity = document.getElementById(data['ticker'] + 'quantity');
	var change = document.getElementById(data['ticker'] + 'change');
	var average = document.getElementById(data['ticker'] + 'avg');
	var totalCost = document.getElementById(data['ticker'] + 'totalCost');
	var currentPrice = document.getElementById(data['ticker'] + 'currPrice');
	var marketValue = document.getElementById(data['ticker'] + 'marketValue');
	var company = document.getElementById(data['ticker'] + 'wrapper');
	
	if (data['newQuantity'] === 0) {
		company.style.display = "none";
		return;
	}
	
	quantity.innerHTML = data['newQuantity'];
	totalCost.innerHTML = data['newTotalCost'].toFixed(2);
	average.innerHTML = (data['newTotalCost']/data['newQuantity']).toFixed(2);
	
		var holder = (parseInt(currentPrice.innerHTML, 10) - parseInt(average.innerHTML, 10));
	
	if (holder < 0){
		change.innerHTML = '<i class="fas fa-caret-down"></i>' + (holder).toFixed(2);
		change.style = "color:rgb(255, 0,  0)";
	}
	
	else if ((holder) > 0){
		change.innerHTML = '<i class="fas fa-caret-up"></i>' + (holder).toFixed(2);
		change.style = "color:rgb(51, 204, 51)";
	}
	
	else{
		change.innerHTML = (holder).toFixed(2);	
	}
	

	marketValue.innerHTML = (parseInt(data['newQuantity'], 10) * parseInt(currentPrice.innerHTML, 10)).toFixed(2);
	
	
	}
	return;
}


function updateAfterBuy(data){

	if (data['status'] === 1){
	var quantity = document.getElementById(data['ticker'] + 'quantity');
	var change = document.getElementById(data['ticker'] + 'change');
	var average = document.getElementById(data['ticker'] + 'avg');
	var totalCost = document.getElementById(data['ticker'] + 'totalCost');
	var currentPrice = document.getElementById(data['ticker'] + 'currPrice');
	var marketValue = document.getElementById(data['ticker'] + 'marketValue');
	var company = document.getElementById(data['ticker'] + 'wrapper');
	
	quantity.innerHTML = parseInt(quantity.innerHTML,10) + data['quantityBought'];
	totalCost.innerHTML = (parseInt(quantity.innerHTML, 10) * data['price']).toFixed(2);
	average.innerHTML = (parseInt(totalCost.innerHTML, 10)/parseInt(quantity.innerHTML, 10)).toFixed(2);
	
	var holder = (data['price'] - parseInt(average.innerHTML, 10));
		if (holder < 0){
		change.innerHTML = '<i class="fas fa-caret-down"></i>' + (holder).toFixed(2);
		change.style = "color:rgb(255, 0,  0)";
	}
	
	else if ((holder) > 0){
		change.innerHTML = '<i class="fas fa-caret-up"></i>' + (holder).toFixed(2);
		change.style = "color:rgb(51, 204, 51)";
	}
	
	else{
		change.innerHTML = (holder).toFixed(2);	
	}
	
	currentPrice.innerHTML = data['price'].toFixed(2);
	marketValue.innerHTML = (data['price'] * parseInt(quantity.innerHTML, 10)).toFixed(2);
	}
	return;
}


async function executeBuyPortfolio(ticke, q, c){
	//if c is 2 send to sell
	if (!isMarketOpen){
		alert('Failed: Transaction Not Possible, Market is Closed');
		return;
	}
	if (c == 2) {
		executeSell(ticke, q);
		return;
	}
	if (!executingBuy){
	executingBuy = true;
	var dat;
	console.log('about to execute buy');
	if (q <= 0){
	executingBuy=false;
	alert('Failed: Purchase Not Possible, Cannot purchase negative quantity');
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
		else{
		alert('Purchased ' + q + ' of ' + ticke + 'for $' + (data['price']*data['quantityBought']).toFixed(2));
		getUserInfo();
		//update here
		updateAfterBuy(data);
		}
	});
	}
	executingBuy = false;
	return;
}





async function getUserInfo(){
	var data = {mode: "user"};
	await fetch('PortfolioHandler', {
	method: 'POST',
	headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(data)
	}).then(response => response.json()).then(function(data){
		setUserInfo(data);
	});
}

function setUserInfo(data){

	document.getElementById('cashBalance').innerHTML = data['cashBalance'].toFixed(2);
	document.getElementById('accountValue').innerHTML = data['accountValue'].toFixed(2);

}


async function getMarketStatus(){

	await fetch('SearchHandler?' + new URLSearchParams({
		ticker: 'AAPL'
	})).then(response => response.json()).then(function(data){
		isMarketOpen = data['isMarketOpen'];			
	});
	
}

async function getCompanies(){
	var dat;
	await getMarketStatus();
	console.log('about to make tables');
	var data = {
		mode: "get",
	}
	await fetch('PortfolioHandler', {
	method: 'POST',
	headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(data)
	}).then(response => response.json()).then(function(data){
		if(data.length === 0) alert('portfolio is empty');
		else{
			makeDivs(data);
		}
	});
	return;
}


function makeDivs(data){
	data.forEach(element => makeDiv(element));
}

function makeDiv(company){
	var z = document.createElement('div');
	z.className = 'company';
	z.id = company['ticker'] + 'wrapper';
	z.appendChild(makeCompanyTitle(company));
	z.appendChild(makeCompanyTableWrapper(company));
	z.appendChild(makeFormWrapper(company));//make sure to put ticker as id for form
	document.getElementById('allCompanies').appendChild(z);
}

function makeCompanyTitle(company){
	var z = document.createElement('div');
	var headingTicker = document.createElement('h2');
	var headingName = document.createElement('h3');
	z .className = 'companyTitle';
	headingTicker.className = 'tickerStyle';//might have to put ticker as id? idk
	headingName.className = 'companyNameStyle';
	headingTicker.innerHTML = company['ticker'];
	headingName.innerHTML = company['name'];
	z.appendChild(headingTicker);
	z.appendChild(headingName);
	return z;
}

function makeCompanyTableWrapper(company){
	var z = document.createElement('div');
	z.className = 'companyTableWrapper';
	z.appendChild(makeCompanyTable(company));
	return z;
}

function makeCompanyTable(company){
	var z = document.createElement('table');
	z.className = 'companyTable';
	z.appendChild(makeRowOne(company));
	z.appendChild(makeRowTwo(company));
	z.appendChild(makeRowThree(company));
	return z;
}

function makeRowOne(company){
	var z = document.createElement('tr');
	var th1 = document.createElement('th');
	var th2 = document.createElement('th');
	var th3 = document.createElement('th');
	var th4 = document.createElement('th');
	var span1 = document.createElement('span');
	var span2 = document.createElement('span');
	var span3 = document.createElement('span');
	var span4 = document.createElement('span');
	
	span1.className = 'left';
	span2.className = 'right';
	span3.className = 'left';
	span4.className = 'right';
	
	span2.id = company['ticker'] + 'quantity';
	span4.id = company['ticker'] + 'change';
	
	span1.innerHTML = 'Quantity:';
	span2.innerHTML = company['quantity'];
	span3.innerHTML = 'Change:';
	
	if (company['currentPrice'] -(getAverageCost(company)) < 0){
		span4.innerHTML = '<i class="fas fa-caret-down"></i>' + ((company['currentPrice']) - (getAverageCost(company))).toFixed(2);
		span4.style = "color:rgb(255, 0,  0)";
	}
	
	else if ((company['currentPrice']) - (getAverageCost(company)) > 0){
		span4.innerHTML = '<i class="fas fa-caret-up"></i>' + ((company['currentPrice']) - (getAverageCost(company))).toFixed(2);
		span4.style = "color:rgb(51, 204, 51)";
	}
	
	else{
		span4.innerHTML = ((company['currentPrice']) - (getAverageCost(company))).toFixed(2);	
	}
	
	th1.appendChild(span1);
	th2.appendChild(span2);
	th3.appendChild(span3);
	th4.appendChild(span4);
	
	z.appendChild(th1);	
	z.appendChild(th2);	
	z.appendChild(th3);		
	z.appendChild(th4);	
	
	return z;
}


function makeRowTwo(company){
		var z = document.createElement('tr');
	var th1 = document.createElement('th');
	var th2 = document.createElement('th');
	var th3 = document.createElement('th');
	var th4 = document.createElement('th');
	var span1 = document.createElement('span');
	var span2 = document.createElement('span');
	var span3 = document.createElement('span');
	var span4 = document.createElement('span');
	
	span1.className = 'left';
	span2.className = 'right';
	span3.className = 'left';
	span4.className = 'right';
	
	span2.id = company['ticker'] + 'avg';
	span4.id = company['ticker'] + 'currPrice';
	
	span1.innerHTML = 'Avg. Cost / Share:';
	span2.innerHTML = getAverageCost(company).toFixed(2);
	span3.innerHTML = 'Current Price:';
	span4.innerHTML = company['currentPrice'].toFixed(2);	
	
	th1.appendChild(span1);
	th2.appendChild(span2);
	th3.appendChild(span3);
	th4.appendChild(span4);
	
	z.appendChild(th1);	
	z.appendChild(th2);	
	z.appendChild(th3);		
	z.appendChild(th4);	
	
	return z;
}


function makeRowThree(company){
		var z = document.createElement('tr');
	var th1 = document.createElement('th');
	var th2 = document.createElement('th');
	var th3 = document.createElement('th');
	var th4 = document.createElement('th');
	var span1 = document.createElement('span');
	var span2 = document.createElement('span');
	var span3 = document.createElement('span');
	var span4 = document.createElement('span');
	
	span1.className = 'left';
	span2.className = 'right';
	span3.className = 'left';
	span4.className = 'right';
	
	span2.id = company['ticker'] + 'totalCost';
	span4.id = company['ticker'] + 'marketValue';
	
	span1.innerHTML = 'Total Cost:';
	span2.innerHTML = company['totalCost'].toFixed(2);
	span3.innerHTML = 'Market Value';
	span4.innerHTML = (company['currentPrice'] * company['quantity']).toFixed(2);	
	
	th1.appendChild(span1);
	th2.appendChild(span2);
	th3.appendChild(span3);
	th4.appendChild(span4);
	
	z.appendChild(th1);	
	z.appendChild(th2);	
	z.appendChild(th3);		
	z.appendChild(th4);	
	
	return z;
}


function getAverageCost(company){
	return (company['totalCost']/company['quantity']);
}

function makeFormWrapper(company){
	var z = document.createElement('div');
	z.className = 'formWrapper';
	var form = document.createElement('form');
	form.className = 'form';
	form.id = company['ticker'];
	form.setAttribute('onsubmit', 'executeBuyPortfolio(this.id, this.quantity.value, this.buyOrSell.value); return false;');
	
	
	var q = document.createElement('div');
	q.className = 'quantityInput';
	q.innerHTML = "Quantity: <input name='quantity' type='number'/>";
	
	
	var radio = document.createElement('radio');
	radio.className = 'radio';
	radio.innerHTML = "<input type='radio' name ='buyOrSell' value='1' checked/> <label for='buyOrSell'>BUY</label> <input type ='radio' name ='buyOrSell' value='2'/> <label for='buy'>SELL</label>";
	
	
	var button = document.createElement('div');
	button.className = 'buttonWrapper';
	button.innerHTML = '<input type="submit" value="Submit" style = "width:60px">';

	form.appendChild(q);
	form.appendChild(radio);
	form.appendChild(button);
	
	z.appendChild(form);
	
	
	return z;
}