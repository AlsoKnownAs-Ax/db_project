CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    bio VARCHAR(255),
    profile_path VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE posts (
    post_id INT AUTO_INCREMENT PRIMARY KEY,
    likes INT DEFAULT 0,
    bio TEXT,
    user_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    backdrop_path VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE comments (
    comment_id INT AUTO_INCREMENT PRIMARY KEY,
    post_id INT NOT NULL,
    user_id INT NOT NULL,
    comment_text VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES posts(post_id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE notifications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    post_id INT,
    user_id INT NOT NULL,
    target_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES posts(post_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (target_id) REFERENCES users(id)
);

CREATE TABLE follows (
    id INT AUTO_INCREMENT PRIMARY KEY,
    following_user_id INT NOT NULL,
    followed_user_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (following_user_id) REFERENCES users(id),
    FOREIGN KEY (followed_user_id) REFERENCES users(id),
    UNIQUE (following_user_id, followed_user_id) -- Ensures a user can't follow another user more than once
);

CREATE TABLE likes (
    like_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    post_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (post_id) REFERENCES posts(post_id),
    UNIQUE (user_id, post_id)
);



INSERT INTO users (username, password, bio, profile_path)
VALUES
('john_doe', 'password123', 'Just another tech enthusiast.', 'john_doe.png'),
('jane_smith', 'password123', 'Lover of nature and photography.', 'jane_smith.png'),
('bob_johnson', 'password123', 'Avid reader and writer.', 'bob_johnson.png'),
('alice_wonder', 'password123', 'Traveler and foodie.', 'alice_wonder.png'),
('Username', 'Password', 'Comic book artist and collector.', 'Username.png');

INSERT INTO posts (bio, user_id, backdrop_path)
VALUES
('This is the bio of my first post.', 1, 'post1.png'),
(' the beauty of nature.', 2, 'post2.png'),
('My top 5 books to read this year.', 3, 'post3.png'),
(' journey through Europe.', 4, 'post4.png'),
('Latest artwork from my comic series.', 5, 'post5.png');

INSERT INTO comments (post_id, user_id, comment_text)
VALUES
(1, 2, 'Great post! Really enjoyed it.'),
(2, 1, 'Amazing photography skills!'),
(3, 4, 'Thanks for the recommendations.'),
(4, 3, 'Europe looks beautiful!'),
(5, 1, 'Love your artwork!');

INSERT INTO follows (following_user_id, followed_user_id)
VALUES
(1, 2),
(2, 1),
(1, 3),
(3, 1),
(2, 4),
(4, 2),
(3, 5),
(5, 3),
(4, 1),
(1, 4);

INSERT INTO notifications (post_id, user_id, target_id)
VALUES
(1, 2, 1),
(2, 1, 2),
(3, 4, 3),
(4, 3, 4),
(5, 1, 5);
