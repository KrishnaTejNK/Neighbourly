CREATE DATABASE neighbourly;
USE neighbourly;

CREATE TABLE `neighbourhood` (
                                 `neighbourhood_id` int NOT NULL AUTO_INCREMENT,
                                 `name` varchar(255) NOT NULL,
                                 `location` varchar(255) DEFAULT NULL,
                                 PRIMARY KEY (`neighbourhood_id`)
);

CREATE TABLE `user` (
                        `id` int NOT NULL AUTO_INCREMENT,
                        `name` varchar(255) NOT NULL,
                        `email` varchar(255) NOT NULL,
                        `password` varchar(255) NOT NULL,
                        `contact` varchar(255) DEFAULT NULL,
                        `neighbourhood_id` int DEFAULT NULL,
                        `address` varchar(255) DEFAULT NULL,
                        `user_type` enum('USER','RESIDENT','COMMUNITY_MANAGER','ADMIN') DEFAULT 'USER',
                        `is_email_verified` tinyint(1) DEFAULT '0',
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `email` (`email`),
                        KEY `neighbourhood_id` (`neighbourhood_id`),
                        CONSTRAINT `user_ibfk_1` FOREIGN KEY (`neighbourhood_id`) REFERENCES `neighbourhood` (`neighbourhood_id`) ON DELETE SET NULL
);

CREATE TABLE `help_requests` (
                                 `request_id` int NOT NULL AUTO_INCREMENT,
                                 `user_id` int DEFAULT NULL,
                                 `neighbourhood_id` int DEFAULT NULL,
                                 `request_type` enum('JOIN','CREATE') NOT NULL,
                                 `description` varchar(255) DEFAULT NULL,
                                 `status` enum('OPEN','APPROVED','DECLINED') NOT NULL,
                                 `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
                                 `updated_at` datetime DEFAULT NULL,
                                 PRIMARY KEY (`request_id`),
                                 KEY `user_id` (`user_id`),
                                 KEY `neighbourhood_id` (`neighbourhood_id`),
                                 CONSTRAINT `help_requests_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
                                 CONSTRAINT `help_requests_ibfk_2` FOREIGN KEY (`neighbourhood_id`) REFERENCES `neighbourhood` (`neighbourhood_id`) ON DELETE CASCADE
);

CREATE TABLE `amenities` (
                             `amenity_id` int NOT NULL AUTO_INCREMENT,
                             `neighbourhood_id` int DEFAULT NULL,
                             `name` varchar(255) DEFAULT NULL,
                             `available_time_slots` text,
                             PRIMARY KEY (`amenity_id`),
                             KEY `neighbourhood_id` (`neighbourhood_id`),
                             CONSTRAINT `amenities_ibfk_1` FOREIGN KEY (`neighbourhood_id`) REFERENCES `neighbourhood` (`neighbourhood_id`) ON DELETE CASCADE
) ;

CREATE TABLE `parking_rentals` (
                                   `rental_id` int NOT NULL AUTO_INCREMENT,
                                   `neighbourhood_id` int DEFAULT NULL,
                                   `user_id` int DEFAULT NULL,
                                   `spot_number` varchar(255) DEFAULT NULL,
                                   `start_time` datetime DEFAULT NULL,
                                   `end_time` datetime DEFAULT NULL,
                                   `price` decimal(10,2) DEFAULT NULL,
                                   `status` enum('Booked','Available','Cancelled') DEFAULT NULL,
                                   PRIMARY KEY (`rental_id`),
                                   KEY `neighbourhood_id` (`neighbourhood_id`),
                                   KEY `user_id` (`user_id`),
                                   CONSTRAINT `parking_rentals_ibfk_1` FOREIGN KEY (`neighbourhood_id`) REFERENCES `neighbourhood` (`neighbourhood_id`) ON DELETE CASCADE,
                                   CONSTRAINT `parking_rentals_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ;

CREATE TABLE `password_reset_token` (
                                        `id` int NOT NULL AUTO_INCREMENT,
                                        `expiry_date` datetime(6) DEFAULT NULL,
                                        `token` varchar(255) DEFAULT NULL,
                                        `user_id` int NOT NULL,
                                        PRIMARY KEY (`id`),
                                        UNIQUE KEY `UKf90ivichjaokvmovxpnlm5nin` (`user_id`)
) ;

CREATE TABLE `post` (
                        `post_id` int NOT NULL AUTO_INCREMENT,
                        `user_id` int DEFAULT NULL,
                        `neighbourhood_id` int DEFAULT NULL,
                        `post_type` varchar(255) DEFAULT NULL,
                        `post_content` text,
                        `date_time` datetime DEFAULT NULL,
                        `approved` tinyint(1) DEFAULT NULL,
                        PRIMARY KEY (`post_id`),
                        KEY `user_id` (`user_id`),
                        KEY `neighbourhood_id` (`neighbourhood_id`),
                        CONSTRAINT `post_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
                        CONSTRAINT `post_ibfk_2` FOREIGN KEY (`neighbourhood_id`) REFERENCES `neighbourhood` (`neighbourhood_id`) ON DELETE CASCADE
) ;

CREATE TABLE `public_place_bookings` (
                                         `booking_id` int NOT NULL AUTO_INCREMENT,
                                         `neighbourhood_id` int DEFAULT NULL,
                                         `user_id` int DEFAULT NULL,
                                         `place_name` varchar(255) DEFAULT NULL,
                                         `date` date DEFAULT NULL,
                                         `time_slot` varchar(255) DEFAULT NULL,
                                         `status` enum('Pending','Approved','Rejected') DEFAULT NULL,
                                         PRIMARY KEY (`booking_id`),
                                         KEY `neighbourhood_id` (`neighbourhood_id`),
                                         KEY `user_id` (`user_id`),
                                         CONSTRAINT `public_place_bookings_ibfk_1` FOREIGN KEY (`neighbourhood_id`) REFERENCES `neighbourhood` (`neighbourhood_id`) ON DELETE CASCADE,
                                         CONSTRAINT `public_place_bookings_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ;


