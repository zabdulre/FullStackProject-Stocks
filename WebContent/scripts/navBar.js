/**
 *
 */

var navContent = document.getElementById("barContent");
var loggedIn =
  '<li style="border: 2px solid rgb(200, 200, 200);">Homepage/Search</li><li><a href = "login.html">Login/Sign Up</a></li>';
if (navContent.innerHTML == null) {
  //not logged in
  var homePageListElement = document.createElement("li");
  var loginListElement = document.createElement("li");
  var homePageElement = document.createElement("a");
  var loginElement = document.createElement("a");
  var filename = location.pathname.substring(
    location.pathname.lastIndexOf("/") + 1
  );
  if (filename == "login.html") {
    homePageElement.href = "index.html";
    homePageElement.innerHTML = "Home/Search";
    homePageListElement.append(homePageElement);
    loginListElement.style = "border: 2px solid rgb(200, 200, 200)";
    loginListElement.innerHTML = "Login/Sign Up";
  } else {
    document.getElementById("SalStocks").href = "#";
    homePageListElement.style = "border: 2px solid rgb(200, 200, 200)";
    homePageListElement.innerHTML = "Home/Search";
    loginElement.href = "login.html";
    loginElement.innerHTML = "Login/Sign Up";
    loginListElement.append(loginElement);
  }
  navContent.append(homePageListElement);
  navContent.append(loginListElement);
} else {
  //logged in
  var filename = location.pathname.substring(
    location.pathname.lastIndexOf("/") + 1
  );
  var homePageListElement = document.createElement("li");
  var favoritesListElement = document.createElement("li");
  var homePageElement = document.createElement("a");
  var favoritesElement = document.createElement("a");
  var portfolioListElement = document.createElement("li");
  var portfolioElement = document.createElement("a");
  var logOutListElement = document.createElement("li");
  var logOutElement = document.createElement("button");
  logOutElement.type = "button";
  homePageElement.innerHTML = "Home/Search";
  homePageElement.href = "index.html";
  favoritesElement.innerHTML = "Favorites";
  favoritesElement.href = "favorites.html";
  portfolioElement.innerHTML = "Portfolio";
  portfolioElement.href = "portfolio.html";
  favoritesElement.innerHTML = "Favorites";
  favoritesElement.href = "favorites.html";
  logOutElement.onclick = "logOut()";
  logOutElement.innerHTML = "Log Out";
  if (filename === "index.html") {
    document.getElementById("SalStocks").href = "#";
    homePageListElement.style = "border: 2px solid rgb(200, 200, 200)";
    homePageListElement.innerHTML = "Home/Search";
    portfolioListElement.append(portfolioElement);
    favoritesListElement.append(favoritesElement);
  } else if (filename === "favorites.html") {
    favoritesListElement.style = "border: 2px solid rgb(200, 200, 200)";
    favoritesListElement.innerHTML = "Favorites";
    portfolioListElement.append(portfolioElement);
    homePageListElement.append(homePageElement);
  } else if (filename === "portfolio.html") {
    portfolioListElement.style = "border: 2px solid rgb(200, 200, 200)";
    portfolioListElement.innerHTML = "Portfolio";
    homePageListElement.append(homePageElement);
    favoritesListElement.append(favoritesElement);
  } else {
    homePageListElement.style = "border: 2px solid rgb(200, 200, 200)";
    homePageListElement.innerHTML = "Home/Search";
    portfolioListElement.append(portfolioElement);
    favoritesListElement.append(favoritesElement);
  }
  logOutListElement.append(logOutElement);
  navContent.append(homePageListElement);
  navContent.append(favoritesListElement);
  navContent.append(portfolioListElement);
  navContent.append(logOutListElement);
}
