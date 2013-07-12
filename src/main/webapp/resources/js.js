/* 
 * License
 * . HoneyBee
 */

function additem() {
	var i = document.getElementById(document.forms[0].id
			+ ':complementBeanComplementPrescriptionsSelect').selectedIndex;
	if (i < 1) {
		alert('Must select a Prescription');
		return false;
	}
}

function test() {
	alert('hello');
}

/**
 * 
 * @returns {Boolean}
 */
function validateLoginForm() {
	passwordValue = document.getElementById('loginform:j_password').value;
	emailValue = document.getElementById('loginform:j_username').value;
	if (5 > passwordValue.length) {
		alert('Password: Invalid Entry');
		return false;
	}
	if (10 > emailValue.length) {
		alert('Email: Invalid Entry');
		return false;
	}

	if (validateEmail(emailValue) === false) {
		alert("Please enter correct email ID");
		document.getElementById('j_username').className = 'validationerror';
		document.getElementById('j_username').focus();
		return false;
	}
	return (true);
}

/**
 * Checks the validity of an email address
 * 
 * @param email
 * @returns {Boolean}
 */
function validateEmail(email) {
	// email = document.getElementById('j_username').value;
	var splitted = email.match("^(.+)@(.+)$");
	if (splitted == null)
		return false;
	if (splitted[1] != null) {
		var regexp_user = /^\"?[\w-_\.]*\"?$/;
		if (splitted[1].match(regexp_user) == null)
			return false;
	}
	if (splitted[2] != null) {
		var regexp_domain = /^[\w-\.]*\.[A-Za-z]{2,4}$/;
		if (splitted[2].match(regexp_domain) == null) {
			var regexp_ip = /^\[\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}\]$/;
			if (splitted[2].match(regexp_ip) == null)
				return false;
		} // if
		return true;
	}
	return false;
}

/**
 * Validates an email
 * 
 * @param objValue
 * @param strError
 * @returns {Boolean}
 */
function testEmail(objValue) {
	var strError = "Invalid Email";
	var ret = true;
	if (objValue.value.length > 0 && !validateEmail(objValue.value)) {
		if (!strError || strError.length == 0) {
			strError = objValue.name + ": Enter a valid Email address ";
		} // if
		sfm_show_error_msg(strError, objValue);
		ret = false;
	} // if
	return ret;
}

/**
 * Checks the equality of two passwords
 * 
 * @returns {Boolean}
 */
function checkPassConf() {
	var pc = document.getElementById('create:enduserBeanEnduserPassconf').value;
	var p = document.getElementById('create:enduserBeanEnduserPassword').value;
	if (p !== pc) {
		alert('Passwords do not match!');
		return false;
	}
	
	function changeUserLocale(){
		var pass = document.getElementById('j_password').value;
		var mail = document.getElementById('j_username').value;
		return concat(pass+'-'+mail);
	}
	
}