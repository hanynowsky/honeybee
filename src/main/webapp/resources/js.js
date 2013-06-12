/* 
 * License
 * . HoneyBee
 */


function additem() {
    var i = document.getElementById(document.forms[0].id +':complementBeanComplementPrescriptionsSelect').selectedIndex;
    if (i<1)
    {
        alert('Must select a Prescription');
        return false;
    }
}