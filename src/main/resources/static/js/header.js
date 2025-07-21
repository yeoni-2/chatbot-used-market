// 페이지 로드 시 선택된 카테고리 설정
document.addEventListener("DOMContentLoaded", function () {
  const urlParams = new URLSearchParams(window.location.search);
  const selectedCategory = urlParams.get("category");
  const categorySelect = document.querySelector('select[name="category"]');

  if (selectedCategory && categorySelect) {
    categorySelect.value = selectedCategory;
  }
});