CREATE TABLE `follows` (
  `id` integer PRIMARY KEY AUTO_INCREMENT,
  `following_user_id` integer,
  `followed_user_id` integer,
  `created_at` timestamp
);

CREATE TABLE `users` (
  `id` integer PRIMARY KEY,
  `username` varchar(255),
  `password` varchar(255),
  `bio` varchar(255),
  `profile_path` varchar(255),
  `followers` integer,
  `following` integer COMMENT 'Number of following',
  `posts` integer COMMENT 'Number of posts'
);

CREATE TABLE `posts` (
  `post_id` integer PRIMARY KEY,
  `likes` integer,
  `title` varchar(255),
  `bio` text COMMENT 'Description',
  `user_id` integer,
  `created_at` timestamp,
  `backdrop_path` varchar(255) COMMENT 'path of the iamge'
);

CREATE TABLE `notifications` (
  `id` integer PRIMARY KEY AUTO_INCREMENT,
  `post_id` integer,
  `user_id` integer,
  `target_id` integer,
  `created_at` timestamp
);

ALTER TABLE `posts` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

ALTER TABLE `follows` ADD FOREIGN KEY (`following_user_id`) REFERENCES `users` (`id`);

ALTER TABLE `follows` ADD FOREIGN KEY (`followed_user_id`) REFERENCES `users` (`id`);

ALTER TABLE `notifications` ADD FOREIGN KEY (`post_id`) REFERENCES `posts` (`post_id`);

ALTER TABLE `notifications` ADD FOREIGN KEY (`user_id`) REFERENCES `posts` (`user_id`);
