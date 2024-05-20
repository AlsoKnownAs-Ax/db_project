DELIMITER $$

CREATE PROCEDURE get_user_stats(IN userId INT, OUT followers_count INT, OUT following_count INT, OUT posts_count INT)
BEGIN
    DECLARE temp_followers_count INT;
    DECLARE temp_following_count INT;
    
    SELECT COUNT(*) INTO temp_followers_count FROM follows WHERE followed_user_id = userID;
    
    SELECT COUNT(*) INTO temp_following_count FROM follows WHERE following_user_id = userID;
    
    SET followers_count = temp_followers_count;
    SET following_count = temp_following_count;
END $$

DELIMITER ;
