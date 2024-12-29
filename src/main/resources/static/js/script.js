// script.js
document.getElementById("contact-btn").addEventListener("click", function() {
    document.getElementById("contact-popup").classList.add("show");
  });
  
  document.getElementById("close-btn").addEventListener("click", function() {
    document.getElementById("contact-popup").classList.remove("show");
  });
  