const emailInput = document.getElementById('userEmail');
const pwInput = document.getElementById('userPW');
const nameInput = document.getElementById('userName');
const phoneInput = document.getElementById('userPhone');
const joinBtn = document.getElementById('btn_join');

emailInput.addEventListener('keyup', function(event) {
    if (emailInput.value && emailInput.value && nameInput.value && phoneInput.value) {
        joinBtn.disabled = false;
    }
    else {
        joinBtn.disabled = true;
    }
})

pwInput.addEventListener('keyup', function(event) {
    if (emailInput.value && emailInput.value && nameInput.value && phoneInput.value) {
        joinBtn.disabled = false;
    }
    else {
        joinBtn.disabled = true;
    }
})

nameInput.addEventListener('keyup', function(event) {
    if (emailInput.value && emailInput.value && nameInput.value && phoneInput.value) {
        joinBtn.disabled = false;
    }
    else {
        joinBtn.disabled = true;
    }
})

phoneInput.addEventListener('keyup', function(event) {
    if (emailInput.value && emailInput.value && nameInput.value && phoneInput.value) {
        joinBtn.disabled = false;
    }
    else {
        joinBtn.disabled = true;
    }
})

document.addEventListener('keyup', function(event) {
    if (event.keyCode === 13) {
        document.getElementById("btn_join").click();
    }
})