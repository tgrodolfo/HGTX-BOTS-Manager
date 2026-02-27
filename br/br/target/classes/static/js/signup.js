const form = document.getElementById("registerForm");
const password = document.getElementById("password");
const confirmPassword = document.getElementById("confirmPassword");
const errorMsg = document.getElementById("errorMsg");

form.addEventListener("submit", function(e) {
  if (password.value !== confirmPassword.value) {
    e.preventDefault();
    errorMsg.style.display = "block";
  } else {
    errorMsg.style.display = "none";
  }
});