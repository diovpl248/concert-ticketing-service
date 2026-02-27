-- 1. Concerts (콘서트 정보 삽입)
INSERT INTO concerts (title, category, description, thumbnail_url, venue, status) VALUES
('네온 나이츠 월드 투어 2026', 'CONCERT', '서울 잠실 올림픽 주경기장에서 열리는 화려한 네온 나이츠 월드 투어', 'https://example.com/neon.jpg', '서울 잠실 올림픽 주경기장', 'OPEN'),
('2026 베토벤 넘버 파이브 클래식', 'CLASSIC', '예술의전당에서 만나는 정통 클래식 공연', 'https://example.com/classic.jpg', '예술의전당 콘서트홀', 'OPEN');

-- 2. Concert_Dates (콘서트 일정 삽입)
-- 네온 나이츠 월드 투어(concert_id: 1) 일정
INSERT INTO concert_dates (concert_id, concert_datetime) VALUES
(1, '2026-03-24 19:00:00'),
(1, '2026-03-25 19:00:00');

-- 베토벤 넘버 파이브 클래식(concert_id: 2) 일정
INSERT INTO concert_dates (concert_id, concert_datetime) VALUES
(2, '2026-04-10 20:00:00');

-- 3. Seats (좌석 삽입)
-- 콘서트 일정 1 (네온 나이츠 첫째 날, concert_date_id: 1)에 대한 좌석
INSERT INTO seats (concert_date_id, section, row_no, col_no, price, status, version) VALUES
(1, 'VIP', 'A', '1', 150000, 'AVAILABLE', 0),
(1, 'VIP', 'A', '2', 150000, 'AVAILABLE', 0),
(1, 'VIP', 'A', '3', 150000, 'AVAILABLE', 0),
(1, 'VIP', 'B', '1', 150000, 'AVAILABLE', 0),
(1, 'VIP', 'B', '2', 150000, 'AVAILABLE', 0),
(1, 'S', 'C', '1', 120000, 'AVAILABLE', 0),
(1, 'S', 'C', '2', 120000, 'AVAILABLE', 0),
(1, 'S', 'C', '3', 120000, 'AVAILABLE', 0);

-- 콘서트 일정 2 (네온 나이츠 둘째 날, concert_date_id: 2)에 대한 좌석
INSERT INTO seats (concert_date_id, section, row_no, col_no, price, status, version) VALUES
(2, 'VIP', 'A', '1', 150000, 'AVAILABLE', 0),
(2, 'VIP', 'A', '2', 150000, 'AVAILABLE', 0),
(2, 'S', 'B', '1', 120000, 'AVAILABLE', 0),
(2, 'S', 'B', '2', 120000, 'AVAILABLE', 0);

-- 콘서트 일정 3 (클래식, concert_date_id: 3)에 대한 좌석
INSERT INTO seats (concert_date_id, section, row_no, col_no, price, status, version) VALUES
(3, 'R', 'A', '1', 100000, 'AVAILABLE', 0),
(3, 'R', 'A', '2', 100000, 'AVAILABLE', 0),
(3, 'S', 'B', '1', 80000, 'AVAILABLE', 0),
(3, 'S', 'B', '2', 80000, 'AVAILABLE', 0);

-- 3. Users
-- 사용자 정보
INSERT INTO users (username) VALUES
('user1'),
('user2'),
('user3');