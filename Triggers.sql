DELIMITER $$

CREATE PROCEDURE notify_new_post(IN post_id INT, IN user_id INT)
BEGIN
    INSERT INTO notifications (post_id, user_id, target_id)
    VALUES (post_id, user_id, user_id);
END $$

DELIMITER ;

DELIMITER $$

CREATE FUNCTION get_follower_count(user_id INT) 
RETURNS INT
BEGIN
    DECLARE follower_count INT;
    SELECT COUNT(*) INTO follower_count FROM follows WHERE followed_user_id = user_id;
    RETURN follower_count;
END $$

DELIMITER ;

DELIMITER $$

CREATE TRIGGER after_post_insert
AFTER INSERT ON posts
FOR EACH ROW
BEGIN
    CALL notify_new_post(NEW.post_id, NEW.user_id);
END $$

DELIMITER ;

DELIMITER $$

CREATE TRIGGER after_follow_insert
AFTER INSERT ON follows
FOR EACH ROW
BEGIN
    DECLARE follower_count INT;
    SET follower_count = get_follower_count(NEW.followed_user_id);
    INSERT INTO notifications (post_id, user_id, target_id)
    VALUES (NULL, NEW.following_user_id, NEW.followed_user_id);
END $$

DELIMITER ;
