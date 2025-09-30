-- 기존 데이터 초기화
DELETE FROM comments;
DELETE FROM article_likes;
DELETE FROM article;
DELETE FROM users;

-- 유저 더미
INSERT INTO users (id, email, password, name, role, profile_image_url)
VALUES (1, 'test@example.com', '1234', 'testUser', 'USER', '/images/default-profile.png');

INSERT INTO users (id, email, password, name, role, profile_image_url)
VALUES (2, '1@1',
        '$2a$10$957G.24RS8dyx0YHQNSC1eHc6PlPYPiSMx/faTYRvgFvcmH3oZyrS',
        '윤동주',
        'USER',
        '/images/default-profile.png');

-- 게시글 더미
INSERT INTO article (id, title, content, author_id, category, created_at, views)
VALUES (1, '테스트 글 1', '첫 번째 내용입니다.', 1, '공지', CURRENT_TIMESTAMP, 0);

INSERT INTO article (id, title, content, author_id, category, created_at, views)
VALUES (2, '테스트 글 2', '두 번째 내용입니다.', 1, '자유', CURRENT_TIMESTAMP, 0);

-- 댓글 더미 (author_id ❌ → user_id ✅)
INSERT INTO comments (id, content, user_id, article_id, created_at)
VALUES (1, '정말 좋은 글이네요!', 2, 1, CURRENT_TIMESTAMP);
