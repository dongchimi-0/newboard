-- User 더미 데이터
INSERT INTO users (email, password, name, role)
VALUES ('test@example.com', '1234', '테스트유저', 'USER');
--
---- Article 더미 데이터 (created_at 추가!)
--INSERT INTO article (title, content, author_id, category, created_date)
--VALUES ('테스트 글 1', '첫 번째 내용입니다.', 1,  'category', CURRENT_TIMESTAMP);
--
--INSERT INTO article (title, content, author_id, category, created_date)
--VALUES ('테스트 글 1', '첫 번째 내용입니다.', 1,  'category', CURRENT_TIMESTAMP);
-- `created_at` 컬럼에 직접 값을 넣어주세요.
INSERT INTO article (title, content, author_id, category, created_at)
VALUES ('테스트 글 1', '첫 번째 내용입니다.', 1,  '공지', CURRENT_TIMESTAMP);

INSERT INTO article (title, content, author_id, category, created_at)
VALUES ('테스트 글 2', '두 번째 내용입니다.', 1,  '자유', CURRENT_TIMESTAMP);