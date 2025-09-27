-- ✅ 기존 데이터 초기화 (개발환경에서만 사용 권장)
DELETE FROM article;
DELETE FROM users;

-- ✅ id를 강제로 지정해서 항상 일관된 상태 유지
INSERT INTO users (id, email, password, name, role)
VALUES (1, 'test@example.com', '1234', 'testUser', 'USER');

INSERT INTO users (id, email, password, name, role)
VALUES (2, '1@1',
        '$2a$10$957G.24RS8dyx0YHQNSC1eHc6PlPYPiSMx/faTYRvgFvcmH3oZyrS',
        '윤동주',
        'USER');

-- ✅ article은 id 없이 insert (자동 증가)
INSERT INTO article (title, content, author_id, category, created_at, views)
VALUES ('테스트 글 1', '첫 번째 내용입니다.', 1, '공지', CURRENT_TIMESTAMP, 0);

INSERT INTO article (title, content, author_id, category, created_at, views)
VALUES ('테스트 글 2', '두 번째 내용입니다.', 1, '자유', CURRENT_TIMESTAMP, 0);
