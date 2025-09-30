document.addEventListener("DOMContentLoaded", () => {
  function csrf() {
    const token = document.querySelector('meta[name="_csrf"]')?.content;
    const header = document.querySelector('meta[name="_csrf_header"]')?.content || 'X-CSRF-TOKEN';
    if (!token) console.warn('⚠ CSRF token not found. (로그인 상태를 확인하세요)');
    return { header, token };
  }

  function showToast(message, type = "success") {
    const toast = document.createElement("div");
    toast.className = `toast ${type}`;
    toast.innerText = message;
    document.body.appendChild(toast);

    setTimeout(() => toast.classList.add("show"), 50);
    setTimeout(() => toast.classList.remove("show"), 3000);
    setTimeout(() => toast.remove(), 3500);
  }

  // 🔘 프로필 변경 버튼 → 파일 선택창 열기
  document.getElementById("change-profile-btn")?.addEventListener("click", () => {
    document.getElementById("profile-upload").click();
  });

  // 📤 프로필 업로드
  document.getElementById("profile-upload")?.addEventListener("change", async (e) => {
    const file = e.target.files?.[0];
    if (!file) return;

    // 미리보기
    const img = document.getElementById("profile-image");
    if (img) img.src = URL.createObjectURL(file);

    const { header, token } = csrf();
    const formData = new FormData();
    formData.append("file", file);

    try {
      const res = await fetch("/api/users/profile-image", {
        method: "POST",
        headers: token ? { [header]: token } : undefined,
        body: formData,
        credentials: "same-origin"
      });

      if (!res.ok) {
        showToast(`프로필 업로드 실패 (code: ${res.status})`, "error");
        return;
      }

      const imageUrl = await res.text();
      if (img) img.src = imageUrl;
      showToast("✅ 프로필 이미지가 변경되었습니다!");
    } catch (err) {
      console.error("프로필 업로드 중 오류:", err);
      showToast("업로드 중 오류 발생", "error");
    }
  });
});
