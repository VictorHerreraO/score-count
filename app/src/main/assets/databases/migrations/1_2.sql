CREATE TABLE IF NOT EXISTS `player_profiles` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL);
CREATE UNIQUE INDEX IF NOT EXISTS `index_player_profiles_name` ON `player_profiles` (`name`);
