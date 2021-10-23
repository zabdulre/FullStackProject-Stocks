/**
 * 
 */

var xClicked = false;
var allFavorites;
 getAllCompanies();
function toggleCompanyClicks(){
	var companies = document.querySelectorAll('company');
	if (companies.length != 0){
	companies.forEach(element => function(element){
		element.className = 'companyNoClick';
	});
	}
	else{
		companies = document.querySelectorAll('companyNoClick');
		companies.forEach(element => function(element){
		element.className = 'company';
	});
	}
}

async function xButtonClicked(ticker){
	xClicked = true;
	console.log('deleting ' + ticker);
	await fetch('FavoritesHandler?' + new URLSearchParams({
		ticker: ticker,
		mode: 'toggle'
	})).then(function(){document.getElementById(ticker).style.display = 'none'});
}

function tableClicked(ticker){
	if (xClicked){
		console.log('x is clicked so i will do nothing');
		xClicked = false;
	}
	else{
		document.getElementById('stockDisplayWrapper').style.display = "table";
		console.log(ticker + ' company was clicked');
		setCompanyWithTicker(getIndex(ticker));
	}
}

function getIndex(ticker){
	for (var i = 0; i < allFavorites.length; i++){
		if (allFavorites[i]['ticker'] === ticker){return i;}
	}
	console.log('couldnt find ticker for some reason');
}

async function getAllCompanies(){
		await fetch('FavoritesHandler?mode=all').then(response => response.json()).then(async function(data){
				await makeTables(data);
			});
}

async function makeTables(data){
	if (data['error'] === true) alert("no favorites added");
	else{
		allFavorites = data;
		data.forEach( function(currentValue){ populate(currentValue);});
	}
}

 function populate(currentValue){
				var z =  makeCompanyTableRow(currentValue);
				console.log(z);
			document.getElementById('tableBody').appendChild(z);
			}

 function makeCompanyTableRow(currentValue){
	console.log(currentValue['ticker'] + ' made table');
	var z = document.createElement('tr');
	var element =  makeCompanyTableTd(currentValue);
	z.appendChild(element);
	return z;
}

 function makeCompanyTableTd(currentValue){
	var z = document.createElement('td');
	var element =  makeCompanyTable(currentValue);
	z.appendChild(element);
	return z;
}

 function makeCompanyTable(currentValue){
	var z = document.createElement('table');
	z.className = 'company';
	z.setAttribute("onclick", 'tableClicked(this.id);');
	z.id = currentValue['ticker'];
	var element =  makeTBody(currentValue);
	z.appendChild(element); 
	return z;
}

 function makeTBody(currentValue){
	z = document.createElement('tbody');
	var firstRow =  makeFirstRow(currentValue);
	var secondRow =  makeSecondRow(currentValue);
	z.appendChild(firstRow);
	z.appendChild(secondRow);
	return z;
}

 function makeFirstRow(currentValue){
	var z = document.createElement('tr');
	firstHead =  makeFirstRowLeftHead(currentValue);
	secondHead =  makeFirstRowRightHead(currentValue);
	z.appendChild(firstHead);
	z.appendChild(secondHead);
	return z;
}

 function makeFirstRowLeftHead(currentValue){
	var z = document.createElement('th');
	z.className = 'left';
	var heading = document.createElement('h1');
	heading.innerHTML = currentValue['ticker'];
	z.appendChild(heading);
	return z;
}

 function makeFirstRowRightHead(currentValue){
		var z = document.createElement('th');
	z.className = 'right';
	var heading = document.createElement('h1');
	var span = document.createElement('span');
	var button = document.createElement('button');
	var x = document.createElement('i');
	span.className = 'spanSurrounder'; 
	span.style = 'position:relative;margin:0px;display:inline-block;';
	button.className = 'buttons';
	button.name = currentValue['ticker'];
	button.setAttribute("onclick", "xButtonClicked(this.name);");
	button.style = 'position:absolute;top:-72px;right:-15px;';
	x.className = 'fas fa-times';
	button.appendChild(x);
	span.appendChild(button);
	heading.innerHTML = currentValue['last'];
			if ((currentValue['last'] - currentValue['prevClose']) > 0){
			heading.style.color = 'rgb(51, 204, 51)';
		}
		else if ((currentValue['last'] - currentValue['prevClose']) < 0){
			heading.style.color = 'rgb(255, 0, 0)';
		}
	z.appendChild(heading);
	z.appendChild(span);
	return z;
}

 function makeSecondRow(currentValue){
	var z = document.createElement('tr');
	var firstHead =  makeSecondRowLeftHead(currentValue);
	var secondHead =  makeSecondRowRightHead(currentValue);
	z.appendChild(firstHead);
	z.appendChild(secondHead);
	return z;
}

 function makeSecondRowLeftHead(currentValue){
	var z = document.createElement('th');
	var heading = document.createElement('h2');
	heading.innerHTML = currentValue['name'];
	z.className = 'left';
	z.appendChild(heading);
	return z;
}

 function makeSecondRowRightHead(currentValue){
	var z = document.createElement('th');
		var heading = document.createElement('h2');
		if ((currentValue['last'] - currentValue['prevClose']) > 0){
			heading.style.color = 'rgb(51, 204, 51)';
		}
		else if ((currentValue['last'] - currentValue['prevClose']) < 0){
			heading.style.color = 'rgb(255, 0, 0)';
		}
		else{
			
		}
		if ((currentValue['last'] - currentValue['prevClose']) < 0){
			heading.innerHTML = '<i class="fas fa-caret-down"></i>' + (currentValue['last'] - currentValue['prevClose']).toFixed(2) +' ('+ (((currentValue['last'] - currentValue['prevClose'])*100)/currentValue['prevClose']).toFixed(2) + ")%";
			
		}
		else if ((currentValue['last'] - currentValue['prevClose']) > 0){
			heading.innerHTML = '<i class="fas fa-caret-up"></i>' + (currentValue['last'] - currentValue['prevClose']).toFixed(2) +' ('+ (((currentValue['last'] - currentValue['prevClose'])*100)/currentValue['prevClose']).toFixed(2) + ")%";
		}
		else{
	heading.innerHTML = (currentValue['last'] - currentValue['prevClose']).toFixed(2) +' ('+ (((currentValue['last'] - currentValue['prevClose'])*100)/currentValue['prevClose']).toFixed(2) + ")%";
	}
	z.className = 'right';
	z.appendChild(heading);
	return z;
}

function hideOverlay(){
	document.getElementById('stockDisplayWrapper').style.display = 'none';
}