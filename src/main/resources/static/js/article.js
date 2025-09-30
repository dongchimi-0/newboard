function csrf() {
  const token = document.querySelector('meta[name="_csrf"]')?.content;
  const header = document.querySelector('meta[name="_csrf_header"]')?.content || 'X-CSRF-TOKEN';
  if (!token) console.warn('CSRF token not found in meta tags.');
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

// ===== 게시글 이미지 미리보기 ====
document.getElementById('file')?.addEventListener('change', (e) => {
  const file = e.target.files[0];
  if (!file) return;

  const reader = new FileReader();
  reader.onload = (event) => {
    const preview = document.getElementById('image-preview');
    if (preview) preview.src = event.target.result;
  };
  reader.readAsDataURL(file);
});

// ========== 게시글 생성 ==========
const createButton = document.getElementById('create-btn');
if (createButton) {
  createButton.addEventListener('click', async (event) => {
    event.preventDefault();

    const title = document.getElementById('title').value.trim();
    const content = document.getElementById('content').value.trim();
    const category = document.getElementById('category').value.trim();
    const file = document.getElementById('file')?.files[0];

    if (!title) return showToast("제목을 입력하세요.", "error");
    if (!content) return showToast("내용을 입력하세요.", "error");
    if (!category) return showToast("카테고리를 선택하세요.", "error");

    const { header, token } = csrf();
    const formData = new FormData();
    formData.append("title", title);
    formData.append("content", content);
    formData.append("category", category);
    if (file) formData.append("file", file);

    try {
      const res = await fetch('/api/articles', {
        method: 'POST',
        headers: token ? { [header]: token } : undefined,
        body: formData,
        credentials: 'same-origin'
      });

      if (res.status === 400) {
        const errorData = await res.json().catch(() => null);
        const msg = Array.isArray(errorData?.errors)
          ? errorData.errors.map(e => e.defaultMessage).join("\n")
          : (errorData?.message || "입력값을 확인하세요.");
        return showToast(msg, "error");
      }

      if (res.status === 401 || res.status === 403) {
        return showToast('로그인이 필요합니다.', "error");
      }
      if (!res.ok) return showToast(`등록 실패: ${res.status}`, "error");

      showToast('등록 완료되었습니다.');
      const locationUrl = res.headers.get('Location');
      setTimeout(() => location.replace(locationUrl || '/articles'), 2500);

    } catch (error) {
      console.error("게시글 등록 중 오류:", error);
      showToast("서버와 통신 중 문제가 발생했습니다.", "error");
    }
  });
}

// ========== 게시글 수정 ==========
const modifyButton = document.getElementById('modify-btn');
if (modifyButton) {
  modifyButton.addEventListener('click', async (event) => {
    event.preventDefault();
    const id = document.getElementById('article-id')?.value;
    if (!id) return showToast('id가 없습니다.', "error");

    const title = document.getElementById('title').value.trim();
    const content = document.getElementById('content').value.trim();
    const category = document.getElementById('category').value.trim();
    const file = document.getElementById('file')?.files[0];

    if (!title) return showToast("제목을 입력하세요.", "error");
    if (!content) return showToast("내용을 입력하세요.", "error");
    if (!category) return showToast("카테고리를 선택하세요.", "error");

    const { header, token } = csrf();

    const formData = new FormData();
    formData.append("title", title);
    formData.append("content", content);
    formData.append("category", category);
    if (file) formData.append("file", file);

    try {
      const res = await fetch(`/api/articles/${id}`, {
        method: 'PUT',
        headers: token ? { [header]: token } : undefined,
        body: formData,
        credentials: 'same-origin'
      });

      if (res.status === 401 || res.status === 403) {
        return showToast('수정 권한이 없습니다.', "error");
      }
      if (!res.ok) return showToast('수정 실패: ' + res.status, "error");

      showToast('수정이 완료되었습니다.');
      setTimeout(() => location.replace(`/articles/${id}`), 2500);

    } catch (error) {
      console.error("게시글 수정 중 오류:", error);
      showToast("서버와 통신 중 문제가 발생했습니다.", "error");
    }
  });
}

// ========== 게시글 삭제 ==========
const deleteButton = document.getElementById('delete-btn');
if (deleteButton) {
  deleteButton.addEventListener('click', async (event) => {
    event.preventDefault();
    const id = document.getElementById('article-id')?.value;
    if (!id) return showToast('id가 없습니다.', "error");

    const { header, token } = csrf();

    try {
      const res = await fetch(`/api/articles/${id}`, {
        method: 'DELETE',
        headers: token ? { [header]: token } : undefined,
        credentials: 'same-origin'
      });

      if (res.status === 401 || res.status === 403) {
        return showToast('삭제 권한이 없습니다. 로그인 상태를 확인하세요.', "error");
      }
      if (!res.ok) return showToast('삭제 실패: ' + res.status, "error");

      showToast('삭제가 완료되었습니다.');
      setTimeout(() => location.replace('/articles'), 2500);

    } catch (error) {
      console.error("게시글 삭제 중 오류:", error);
      showToast("서버와 통신 중 문제가 발생했습니다.", "error");
    }
  });
}

// ===== 상세 페이지 =====
document.addEventListener('DOMContentLoaded', () => {
  const articleId = document.getElementById('article-id')?.value;
  if (!articleId) return;

  const viewsEl = document.getElementById('views');
  const likesEl = document.getElementById('likes');
  const likeBtn = document.getElementById('like-btn');
  const likeIcon = document.getElementById('like-icon');
  const { header, token } = csrf();

  // ✅ 조회수 갱신
  fetch(`/api/articles/${articleId}`, {
    method: 'GET',
    credentials: 'same-origin'
  })
    .then(res => res.json())
    .then(article => {
      if (viewsEl) viewsEl.textContent = article.views;
      if (likesEl) likesEl.textContent = article.likeCount;
    });

  // ✅ 좋아요 토글
  if (likeBtn) {
    likeBtn.addEventListener('click', async () => {
      const res = await fetch(`/api/articles/${articleId}/like`, {
        method: 'POST',
        headers: {
          ...(token ? { [header]: token } : {}),
          "Content-Type": "application/json"
        },
        credentials: 'same-origin'
      });

      if (res.status === 401) return showToast('로그인이 필요합니다.', 'error');
      if (!res.ok) return showToast('좋아요 처리 실패: ' + res.status, 'error');

      const likeCount = await res.json();
      likesEl.textContent = likeCount;
      likeBtn.classList.toggle("liked");
      if (likeIcon) {
        likeIcon.textContent = likeBtn.classList.contains("liked") ? "❤️" : "🤍";
      }
    });
  }

  // ✅ 댓글 목록 로딩
  async function loadComments() {
    const res = await fetch(`/api/articles/${articleId}/comments`, { credentials: 'same-origin' });
    if (!res.ok) return showToast('댓글 불러오기 실패: ' + res.status, 'error');

    const comments = await res.json();
    const list = document.getElementById('comment-list');
    if (!list) return;

    list.innerHTML = '';
    comments.forEach(c => {
      const li = document.createElement('li');
      li.classList.add('comment-item');
      li.innerHTML = `
        <div class="comment-meta">
          <span><b>${c.authorName}</b></span>
          <span>${new Date(c.createdAt).toLocaleString()}</span>
        </div>
        <div class="comment-content">${c.content}</div>
        <button data-comment-id="${c.id}" class="delete-comment-btn">삭제</button>
      `;
      list.appendChild(li);
    });

    document.querySelectorAll('.delete-comment-btn').forEach(btn => {
      btn.addEventListener('click', async (e) => {
        const commentId = e.target.getAttribute('data-comment-id');
        const delRes = await fetch(`/api/articles/${articleId}/comments/${commentId}`, {
          method: 'DELETE',
          headers: token ? { [header]: token } : undefined,
          credentials: 'same-origin'
        });

        if (delRes.status === 401) return showToast('로그인이 필요합니다.', 'error');
        if (delRes.status === 403) return showToast('삭제 권한이 없습니다.', 'error');
        if (!delRes.ok) return showToast('댓글 삭제 실패: ' + delRes.status, 'error');

        showToast('댓글이 삭제되었습니다.');
        loadComments();
      });
    });
  }

  const commentForm = document.getElementById('comment-form');
  if (commentForm) {
    commentForm.addEventListener('submit', async (e) => {
      e.preventDefault();
      const textarea = document.getElementById('content');
      const content = textarea?.value?.trim();
      if (!content) return showToast('댓글 내용을 입력하세요.', 'error');

      const res = await fetch(`/api/articles/${articleId}/comments`, {
        method: 'POST',
        headers: { "Content-Type": "application/json", ...(token ? { [header]: token } : {}) },
        body: JSON.stringify({ content }),
        credentials: 'same-origin'
      });

      if (res.status === 401) return showToast('로그인이 필요합니다.', 'error');
      if (!res.ok) return showToast('댓글 등록 실패: ' + res.status, 'error');

      textarea.value = '';
      showToast('댓글이 등록되었습니다.');
      loadComments();
    });
  }

  loadComments();
});
