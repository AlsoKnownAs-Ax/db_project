-- 1. List all users who have more than X followers where X can be any integer value.
SET @X = 5;

SET @query = CONCAT('
SELECT u.username, COUNT(f.followed_user_id) AS followers_count
FROM users u
JOIN follows f
ON u.id = f.followed_user_id
GROUP BY u.id
HAVING followers_count >',  @X);

PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 2. Show the total number of posts made by each user.
SELECT u.username, COUNT(p.post_id) AS total_posts
FROM users u
JOIN posts p 
ON u.id = p.user_id
GROUP BY u.id;

-- 3. Find all comments made on a particular user’s post.
SET @user_id = 1;

SET @query = CONCAT('
SELECT c.comment_text, u.username AS commenter
FROM comments c
JOIN posts p
ON c.post_id = p.post_id
JOIN users u
ON c.user_id = u.id
WHERE p.user_id =', @user_id);

PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 4. Display the top X most liked posts.
SET @X = 5;
SET @query = CONCAT('
SELECT p.*, u.username AS author
FROM posts p
JOIN users u
ON p.user_id = u.id
ORDER BY p.likes DESC
LIMIT ', @X);

PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 5. Count the number of posts each user has liked.
SELECT u.username, COUNT(l.post_id) AS liked_posts_count
FROM users u
JOIN likes l ON u.id = l.user_id
GROUP BY u.id;

-- 6. List all users who haven’t made a post yet.
SELECT u.username
FROM users u
LEFT JOIN posts p ON u.id = p.user_id
WHERE p.post_id IS NULL;

-- 7. List users who follow each other.
SELECT u1.username AS user, u2.username AS user_target
FROM follows f1
JOIN follows f2 ON f1.following_user_id = f2.followed_user_id AND f1.followed_user_id = f2.following_user_id
JOIN users u1 ON f1.following_user_id = u1.id
JOIN users u2 ON f1.followed_user_id = u2.id;

-- 8. Show the user with the highest number of posts.
SELECT u.username, COUNT(p.post_id) AS total_posts
FROM users u
JOIN posts p ON u.id = p.user_id
GROUP BY u.id
ORDER BY total_posts DESC
LIMIT 1;

-- 9. List the top X users with the most followers.
SET @X = 5;
SET @query = CONCAT('
SELECT u.username, COUNT(f.followed_user_id) AS followers_count
FROM users u
JOIN follows f ON u.id = f.followed_user_id
GROUP BY u.id
ORDER BY followers_count DESC
LIMIT ', @X);

PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 10. Find posts that have been liked by all users.
SELECT p.*
FROM posts p
JOIN (
    SELECT post_id, COUNT(user_id) AS likes_count
    FROM likes
    GROUP BY post_id
    ) l 
    ON p.post_id = l.post_id
    WHERE l.likes_count = (SELECT COUNT(*) FROM users);

-- 11. Display the most active user (based on posts, comments, and likes).
SELECT u.username, (COUNT(p.post_id) + COUNT(c.comment_id) + COUNT(l.post_id)) AS activity_score
FROM users u
LEFT JOIN posts p ON u.id = p.user_id
LEFT JOIN comments c ON u.id = c.user_id
LEFT JOIN likes l ON u.id = l.user_id
GROUP BY u.id
ORDER BY activity_score DESC
LIMIT 1;

-- 12. Find the average number of likes per post for each user.
SELECT u.username, AVG(p.likes) AS avg_likes_per_post
FROM users u
JOIN posts p ON u.id = p.user_id
GROUP BY u.id;

-- 13. Show posts that have more comments than likes.
SELECT p.*, COUNT(c.comment_id) AS comments_count
FROM posts p
JOIN comments c ON p.post_id = c.post_id
GROUP BY p.post_id
HAVING comments_count > p.likes;

-- 14. List the users who have liked every post of a specific user.
SET @specific_user_id = 1;
SET @query = CONCAT('
SELECT u.username
FROM users u
WHERE NOT EXISTS (
    SELECT 1
    FROM posts p
    LEFT JOIN likes l ON p.post_id = l.post_id AND l.user_id = u.id
    WHERE p.user_id = ', @specific_user_id, ' AND l.post_id IS NULL
    )');

PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;


-- 15. Display the most popular post of each user (based on likes).
SELECT p.*
FROM posts p
JOIN (SELECT user_id, MAX(likes) AS max_likes
      FROM posts
      GROUP BY user_id) mp
      ON p.user_id = mp.user_id
      AND p.likes = mp.max_likes
      ORDER BY likes DESC;

-- 16. Find the user(s) with the highest ratio of followers to following.
SELECT u.username, (COUNT(f1.followed_user_id) / COUNT(f2.following_user_id)) AS ratio
FROM users u
JOIN follows f1 ON u.id = f1.followed_user_id
JOIN follows f2 ON u.id = f2.following_user_id
GROUP BY u.id
ORDER BY ratio DESC
LIMIT 1;

-- 17. Show the month with the highest number of posts made.
SELECT DATE_FORMAT(p.created_at, '%Y-%m') AS month, COUNT(*) AS post_count
FROM posts p
GROUP BY month
ORDER BY post_count DESC
LIMIT 1;

-- 18. Identify users who have not interacted with a specific user’s posts.
SET @specific_user_id = 6;
SET @query = CONCAT('
SELECT u.username
FROM users u
WHERE NOT EXISTS (
    SELECT 1
    FROM posts p
    LEFT JOIN likes l ON p.post_id = l.post_id AND l.user_id = u.id
    LEFT JOIN comments c ON p.post_id = c.post_id AND c.user_id = u.id
    WHERE p.user_id = ',@specific_user_id,');
    ');

PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 19. Display the user with the greatest increase in followers in the last X days.
-- This one needs more testing
SET @X_days = 30;
SET @query = CONCAT('
    SELECT u.username, (COUNT(f.followed_user_id) - COUNT(f_old.followed_user_id)) AS follower_increase
    FROM users u
    JOIN follows f ON u.id = f.followed_user_id
    AND f.created_at > DATE_SUB(NOW(), INTERVAL ',@X_days,' DAY)
    JOIN follows f_old ON u.id = f_old.followed_user_id
    AND f_old.created_at <= DATE_SUB(NOW(), INTERVAL ',@X_days,' DAY)
    GROUP BY u.id
    ORDER BY follower_increase DESC
    LIMIT 1;
');

PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;


-- 20. Find users who are followed by more than X% of the platform users.
SET @X_percent = 50;
SET @query = CONCAT('
    SELECT u.username, COUNT(f.followed_user_id) AS followers_count
    FROM users u
    JOIN follows f ON u.id = f.followed_user_id
    GROUP BY u.id
    HAVING followers_count > (SELECT COUNT(*) FROM users) * (',@X_percent,' / 100);
');


PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;


