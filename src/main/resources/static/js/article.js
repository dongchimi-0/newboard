function csrf() {
  const token = document.querySelector('meta[name="_csrf"]')?.content;
  const header = document.querySelector('meta[name="_csrf_header"]')?.content || 'X-CSRF-TOKEN';
  if (!token) console.warn('CSRF token not found in meta tags.');
  return { header, token };
}


//생성
const createButton = document.getElementById('create-btn');
if (createButton) {
    createButton.addEventListener('click', async () => {
        event.preventDefault();
        const body = {
            title: document.getElementById('title').value,
            content: document.getElementById('content').value,
            category: document.getElementById('category').value   // 이 부분 추가
        };
        const { header, token } = csrf();
            const res = await fetch('/api/articles', {
              method: 'POST',
              headers: {
                "Content-Type": "application/json",
                ...(token ? { [header]: token } : {})
              },
              body: JSON.stringify(body),
              credentials: 'same-origin'
            });
        if(!res.ok) return alert('등록 실패: ' + res.status);
        alert('등록 완료되었습니다.');

        const locationUrl = res.headers.get('Location');
        if (locationUrl) {
            location.replace(locationUrl);
        } else {
            // fallback: 글 목록 페이지로 이동
            location.replace('/articles');
        }
    });
}

//삭제
const deleteButton = document.getElementById('delete-btn');
if (deleteButton) {
    deleteButton.addEventListener('click', async () => {
        event.preventDefault();
        const id = document.getElementById('article-id')?.value;
        if (!id) return alert('id가 없습니다.');
        const { header, token } = csrf();
           const res = await fetch(`/api/articles/${id}`, {
              method: 'DELETE',
              headers: token ? { [header]: token } : undefined,
              credentials: 'same-origin'
            });
        if (!res.ok) return alert('삭제 실패: ' + res.status);
        alert('삭제가 완료되었습니다.');
        location.replace('/articles');
    });
}

//수정
const modifyButton = document.getElementById('modify-btn');
if (modifyButton) {
    modifyButton.addEventListener('click', async () => {
        event.preventDefault();
        const id = document.getElementById('article-id')?.value;
        if (!id) return alert('id가 없습니다.');
        const body = {
            title: document.getElementById('title').value,
            content: document.getElementById('content').value,
            category: document.getElementById('category').value
        };
        const { header, token } = csrf();
            const res = await fetch(`/api/articles/${id}`, {
              method: 'PUT',
              headers: {
                "Content-Type": "application/json",
                ...(token ? { [header]: token } : {})
              },
              body: JSON.stringify(body),
              credentials: 'same-origin'
            });
        if (!res.ok) return alert('수정 실패: ' + res.status);
        alert('수정이 완료되었습니다.');
        location.replace(`/articles/${id}`);
    });
}

// =============================
// comment.js (댓글 전용)
// =============================

// ✅ 댓글 불러오기
async function loadComments(articleId) {
  const res = await fetch(`/api/articles/${articleId}/comments`, {
    credentials: 'same-origin'
  });

  if (!res.ok) {
    alert('댓글을 불러올 수 없습니다. (status: ' + res.status + ')');
    return;
  }

  const comments = await res.json();
  const list = document.getElementById("comment-list");
  if (!list) return;

  list.innerHTML = "";

  comments.forEach(c => {
    const li = document.createElement("li");
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

  // 삭제 버튼 이벤트 등록
  document.querySelectorAll(".delete-comment-btn").forEach(btn => {
    btn.addEventListener("click", async (e) => {
      const commentId = e.target.getAttribute("data-comment-id");
      await deleteComment(articleId, commentId);
    });
  });
}

// ✅ 댓글 작성
const commentForm = document.getElementById("comment-form");
if (commentForm) {
  commentForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    const articleId = document.getElementById("article-id")?.value;
    if (!articleId) return alert("게시글 ID가 없습니다.");

    const body = {
      content: document.getElementById("content").value
    };

    const { header, token } = csrf();
    const res = await fetch(`/api/articles/${articleId}/comments`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        ...(token ? { [header]: token } : {})
      },
      body: JSON.stringify(body),
      credentials: "same-origin"
    });

    if (!res.ok) return alert("댓글 등록 실패: " + res.status);

    document.getElementById("content").value = "";
    loadComments(articleId);
  });
}

// ✅ 댓글 삭제
async function deleteComment(articleId, commentId) {
  const { header, token } = csrf();
  const res = await fetch(`/api/articles/${articleId}/comments/${commentId}`, {
    method: "DELETE",
    headers: token ? { [header]: token } : undefined,
    credentials: "same-origin"
  });

  if (!res.ok) return alert("댓글 삭제 실패: " + res.status);

  loadComments(articleId);
}

// ✅ 페이지 로드시 댓글 불러오기
document.addEventListener("DOMContentLoaded", () => {
  const articleId = document.getElementById("article-id")?.value;
  if (articleId) loadComments(articleId);
});


