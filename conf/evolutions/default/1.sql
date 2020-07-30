CREATE TABLE `cards` (
 `id` int unsigned NOT NULL AUTO_INCREMENT,
 `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
 `description` text CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
 `level` int unsigned DEFAULT NULL,
 `attribute` int unsigned DEFAULT NULL,
 `type` int unsigned DEFAULT NULL,
 `attack` int DEFAULT NULL,
 `defense` int DEFAULT NULL,
 `card_type` int unsigned NOT NULL,
 `rarity` int unsigned NOT NULL,
 `limit_type` int unsigned NOT NULL,
 `image_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
 `version` int unsigned NOT NULL,
 `release_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `card_subtype_map` (
 `id` int unsigned NOT NULL AUTO_INCREMENT,
 `card_id` int unsigned NOT NULL,
 `sub_type_id` int unsigned NOT NULL,
 PRIMARY KEY (`id`),
 UNIQUE KEY `uk_csm_card_sub_type` (`card_id`, `sub_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `my_cards` (
 `id` int unsigned NOT NULL AUTO_INCREMENT,
 `card_id` int unsigned NOT NULL,
 `gloss_type` int unsigned NOT NULL,
 `status` int unsigned NOT NULL,
 `obtained_date` datetime(6) DEFAULT NULL,
 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `sources` (
 `id` int unsigned NOT NULL AUTO_INCREMENT,
 `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
 `type` int unsigned NOT NULL,
 `quantity` int unsigned NOT NULL,
 `expiry` datetime(6) DEFAULT NULL,
 `created_at` datetime(6) DEFAULT NULL,
 PRIMARY KEY (`id`),
 UNIQUE KEY `uk_s_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `source_card_map` (
 `id` int unsigned NOT NULL AUTO_INCREMENT,
 `card_id` int unsigned NOT NULL,
 `source_id` int unsigned NOT NULL,
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

ALTER TABLE card_subtype_map ADD CONSTRAINT fk_card_subtype_map_card FOREIGN KEY (`card_id`) REFERENCES `cards` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE my_cards ADD CONSTRAINT fk_my_cards_card FOREIGN KEY (`card_id`) REFERENCES `cards` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE source_card_map ADD CONSTRAINT fk_source_card_map_card FOREIGN KEY (`card_id`) REFERENCES `cards` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE source_card_map ADD CONSTRAINT fk_source_card_map_source FOREIGN KEY (`source_id`) REFERENCES `sources` (`id`) on DELETE RESTRICT ON UPDATE RESTRICT;