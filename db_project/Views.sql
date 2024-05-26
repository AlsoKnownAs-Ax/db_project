CREATE VIEW UserEngagement AS
SELECT u.id AS user_id,
    u.username,
    COUNT(DISTINCT p.post_id) AS total_posts,
    COUNT(DISTINCT c.comment_id) AS total_comments
FROM users u
LEFT JOIN posts p ON u.id = p.user_id
LEFT JOIN comments c ON u.id = c.user_id
GROUP BY u.id
HAVING total_posts > 0 OR total_comments > 0;


CREATE VIEW MostLikedPosts AS
SELECT p.post_id,
    p.user_id,
    p.likes
FROM posts p
WHERE p.likes > (SELECT AVG(likes) FROM posts)
ORDER BY p.likes DESC;


CREATE VIEW NotificationsPerPost AS
SELECT p.post_id,
    COUNT(n.id) AS total_notifications
FROM posts p
JOIN notifications n ON p.post_id = n.post_id
GROUP BY p.post_id
HAVING total_notifications > 0;


CREATE INDEX idx_posts_user_id_likes ON posts(user_id, likes);
CREATE INDEX idx_comments_user_id ON comments(user_id);
CREATE INDEX idx_notifications_post_id ON notifications(post_id);
