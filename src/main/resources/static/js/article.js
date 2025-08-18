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
        const body = {
            title: document.getElementById('title').value,
            content: document.getElementById('content').value
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
        location.replace('/articles');
    });
}

//삭제
const deleteButton = document.getElementById('delete-btn');
if (deleteButton) {
    deleteButton.addEventListener('click', async () => {
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
        const id = document.getElementById('article-id')?.value;
        if (!id) return alert('id가 없습니다.');
        const body = {
            title: document.getElementById('title').value,
            content: document.getElementById('content').value
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

