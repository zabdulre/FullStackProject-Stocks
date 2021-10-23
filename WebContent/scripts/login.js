/**
 * 
 */
  async function isLoggedIn() {//on page start up
        console.log("number 2");
        var dat = "xyz";
        await fetch("LoginHandler").then(response => response.json()).then(data => dat = data).then(
        function(dat){
        console.log(dat["loggedIn"] + ' is what fetch says');
        updateNavBar((dat["loggedIn"] === 1))});
        console.log(dat["loggedIn"]);
        var filename = location.pathname.substring(location.pathname.lastIndexOf("/") + 1);
        if (((filename === 'portfolio.html') ||  (filename === 'favorites.html')) && (dat["loggedIn"] != 1)){
        window.location.assign("index.html");}
      }
      async function updateNavBar(t) {
      console.log('nav bar says t is ' + t);
        if (t) {
          document.getElementById("loginSignUpList").style.display = "none";
          document.getElementById("portfolioList").style.display =
            "inline-block";
          document.getElementById("favoritesList").style.display =
            "inline-block";
          document.getElementById("logOutList").style.display = "inline-block";
        }
        else{
       	document.getElementById("portfolioList").style.display =
            "none";
          document.getElementById("favoritesList").style.display =
            "none";
          document.getElementById("logOutList").style.display = "none";
        document.getElementById("loginSignUpList").style.display = "inline-block";
        }
      }
      console.log("number 1");
      isLoggedIn();
	  
	
	async function onSignIn(googleUser){//for google button
	googleUser.disconnect();
  var user = await googleUser.getAuthResponse();
  var dat;
  fetch("GoogleHandler", {
    method: 'post',
    body: JSON.stringify(await user)
  }).then(response => response.json()).then(data => dat = data).then(
    function(dat){
    console.log(dat);
      if (dat["status"] === 1){
        goHome();
      }
      else{
       loginFailed();
      }
    }
  )
  isLoggedIn();
}

async function goHome() {
  console.log("user logged in");
  await updateNavBar(true);
  if (history.back() != null) {
    console.log(history.back());
        updateNavBar();
        if ('referrer' in document){
    window.location = document.referrer;
    window.reload();
    }
    else{
    window.location.replace(history.go(-1).href);
    window.reload();
    }
  } else {
    updateNavBar(true);
  }
}

async function loginFailed(){
console.log("user couldn't log in");
}

async function logOut(){
	await fetch("LogoutHandler").then(response => response.json()).then(data => 
	async function(data){
		if(data["loggedIn"] != 1){
			document.cookie = "JSESSIONID= ; expires = Thu, 01 Jan 1970 00:00:00 GMT";
		}
		isLoggedIn();
		window.location.assign("index.html");
		}
	);
	isLoggedIn();
			console.log("logged out");
		window.location.replace("index.html");
}

async function checkLogin(){//for login button
	console.log("sending username and password");
	var usernam = document.getElementById("username").value;
	var passwor = document.getElementById("password").value;
	var payload = ({username: usernam, password: passwor});
	var dat;
	console.log(JSON.stringify(payload));
	await fetch("LoginHandler", {
    method: 'POST', headers: {
         'Accept': 'application/json',
         'Content-Type': 'application/json',
       },
    body: JSON.stringify(payload)
  }).then(response => response.json()).then(data => dat = data).then(function(dat){
  console.log(dat["loggedIn"]);
  	if (dat["loggedIn"] === 2) document.getElementById("loginError").style.display = "";
  	else{
  		goHome();
  	}
  });
}

async function createUser(){//for create user button
	var emai = document.getElementById("email").value;
	var dat;
	var passwor = document.getElementById("passwordInput1").value;
	var usernam = document.getElementById("user").value;
	var confirmPassword = document.getElementById("passwordInput2").value;
	var payload = ({username: usernam, password: passwor, email: emai});
	if ((emai === 'null') || (passwor == 'null') || (usernam == 'null') || (confirmPassword === 'null')){document.getElementById("createUserError").innerHTML = "Fields cannot contain null";
	document.getElementById("createUserError").style.display = "inline-block"; 
	return;
	}
	if (passwor != confirmPassword){document.getElementById("createUserError").innerHTML = "Confirmed Password does not match";  return;}
	console.log("username is " + usernam + " password is " + passwor + "confirmPassword is " + confirmPassword + "email is " + email);
	await fetch("SignUpHandler", {
    method: 'POST', headers: {
         'Accept': 'application/json',
         'Content-Type': 'application/json',
       },
    body: JSON.stringify(payload)
  }).then(response => response.json()).then(function(data){
  if(data["status"] === 2) {
  	document.getElementById("createUserError").innerHTML = data["message"];
  	document.getElementById("createUserError").style.display = "";
  }
  else{
  	console.log("created account successfully" + data["status"]);
  	goHome();
  }
  }
  );
}