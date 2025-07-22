// 페이지 로드 시 선택된 카테고리 설정
document.addEventListener("DOMContentLoaded", function () {
  const urlParams = new URLSearchParams(window.location.search);
  const selectedCategory = urlParams.get("category");
  const categorySelect = document.querySelector('select[name="category"]');
  if (selectedCategory && categorySelect) {
    categorySelect.value = selectedCategory;
  }

  const chatBtn = document.getElementById('header-chat-btn');
  if (chatBtn) {
    function checkUnreadMessages() {
      fetch('/notifications/unread-count')
        .then(response => response.json())
        .then(count => {
          if (count > 0) {
            chatBtn.classList.add('new-message');
          } else {
            chatBtn.classList.remove('new-message');
          }
        })
        .catch(error => console.error("Error fetching unread count:", error));
    }
    checkUnreadMessages();
    setInterval(checkUnreadMessages, 30000);
  }
});