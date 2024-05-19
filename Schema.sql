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
    title VARCHAR(255) NOT NULL,
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



INSERT INTO users (username, password, bio, profile_path)
VALUES
('john_doe', 'password123', 'Just another tech enthusiast.', 'john_doe.jpg'),
('jane_smith', 'password123', 'Lover of nature and photography.', 'jane_smith.jpg'),
('bob_johnson', 'password123', 'Avid reader and writer.', 'bob_johnson.jpg'),
('alice_wonder', 'password123', 'Traveler and foodie.', 'alice_wonder.jpg'),
('Username', 'Password', 'Comic book artist and collector.', 'Username.jpg');

INSERT INTO posts (title, bio, user_id, backdrop_path)
VALUES
('My First Post', 'This is the bio of my first post.', 1, 'post1.jpg'),
('Nature Photography', 'Capturing the beauty of nature.', 2, 'post2.jpg'),
('Book Recommendations', 'My top 5 books to read this year.', 3, 'post3.jpg'),
('Travel Diary', 'My journey through Europe.', 4, 'post4.jpg'),
('Comic Art', 'Latest artwork from my comic series.', 5, 'post5.jpg');

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
