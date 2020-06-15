-- phpMyAdmin SQL Dump
-- version 5.0.2
-- https://www.phpmyadmin.net/
--
-- Хост: 127.0.0.1
-- Время создания: Июн 03 2020 г., 16:31
-- Версия сервера: 10.4.11-MariaDB
-- Версия PHP: 7.2.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- База данных: `ntrip`
--

-- --------------------------------------------------------

--
-- Структура таблицы `base_stations`
--

CREATE TABLE `base_stations` (
  `id` int(11) NOT NULL,
  `mountpoint` varchar(100) NOT NULL,
  `identifier` varchar(255) NOT NULL DEFAULT '',
  `format` varchar(255) NOT NULL DEFAULT '',
  `format-details` varchar(255) NOT NULL DEFAULT '',
  `carrier` int(1) NOT NULL DEFAULT 0,
  `nav-system` varchar(255) NOT NULL DEFAULT '',
  `country` varchar(3) NOT NULL DEFAULT '',
  `lla` point NOT NULL,
  `altitude` decimal(15,10) NOT NULL DEFAULT 0.0000000000,
  `bitrate` int(11) NOT NULL DEFAULT 0,
  `misc` varchar(255) NOT NULL DEFAULT '',
  `is_online` int(1) NOT NULL DEFAULT 0,
  `password` varchar(255) NOT NULL DEFAULT '',
  `ecef` point NOT NULL DEFAULT '',
  `ecef_z` decimal(15,4) NOT NULL DEFAULT 0.0000,
  `hz` int(2) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Дамп данных таблицы `base_stations`
--

INSERT INTO `base_stations` (`id`, `mountpoint`, `identifier`, `format`, `format-details`, `carrier`, `nav-system`, `country`, `lla`, `altitude`, `bitrate`, `misc`, `is_online`, `password`, `ecef`, `ecef_z`, `hz`) VALUES
(1, 'AL1', '1231aa', 'fafaf', '123', 2, 'dddaGFPS', 'RUS', 0x00000000010100000000000000008040400000000000004640, '0.0000000000', 123, '', 1, '44444', 0x0000000001010000000000000000c05e400000000000d07440, '0.0000', 4);

-- --------------------------------------------------------

--
-- Структура таблицы `clients_log`
--

CREATE TABLE `clients_log` (
  `id` int(10) UNSIGNED NOT NULL,
  `user_id` int(10) UNSIGNED NOT NULL,
  `time_mark` datetime NOT NULL DEFAULT current_timestamp(),
  `coordinates` point DEFAULT NULL,
  `ip` varchar(50) DEFAULT NULL,
  `user-agent` varchar(500) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Структура таблицы `config`
--

CREATE TABLE `config` (
  `id` int(11) NOT NULL,
  `group` varchar(100) NOT NULL,
  `key` varchar(100) NOT NULL,
  `value` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Дамп данных таблицы `config`
--

INSERT INTO `config` (`id`, `group`, `key`, `value`) VALUES
(1, 'fail2ban', 'attempts', '10'),
(2, 'fail2ban', 'ban_time_min', '30'),
(3, 'clients', 'authorization', 'none'),
(4, 'stations', 'authorization', 'none'),
(5, 'stations', 'Hz', '1'),
(6, 'system', 'time_out_sec', '10');

-- --------------------------------------------------------

--
-- Структура таблицы `groups`
--

CREATE TABLE `groups` (
  `id` mediumint(8) UNSIGNED NOT NULL,
  `name` varchar(20) NOT NULL,
  `description` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Дамп данных таблицы `groups`
--

INSERT INTO `groups` (`id`, `name`, `description`) VALUES
(1, 'admin', 'Administrator'),
(2, 'members', 'General User');

-- --------------------------------------------------------

--
-- Структура таблицы `login_attempts`
--

CREATE TABLE `login_attempts` (
  `id` int(11) UNSIGNED NOT NULL,
  `ip_address` varchar(45) NOT NULL,
  `login` varchar(100) NOT NULL,
  `time` int(11) UNSIGNED DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Структура таблицы `mountpoint`
--

CREATE TABLE `mountpoint` (
  `id` int(11) NOT NULL,
  `mountpoint` varchar(100) NOT NULL,
  `identifier` varchar(255) DEFAULT NULL,
  `format` varchar(255) DEFAULT NULL,
  `format-details` varchar(255) DEFAULT NULL,
  `carrier` int(1) DEFAULT NULL,
  `nav-system` varchar(255) DEFAULT NULL,
  `network` varchar(255) DEFAULT NULL,
  `country` varchar(3) DEFAULT NULL,
  `latitude` double DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  `nmea` int(1) DEFAULT NULL,
  `solution` int(1) DEFAULT NULL,
  `generator` varchar(255) DEFAULT NULL,
  `compression` varchar(255) DEFAULT NULL,
  `authentication` int(1) DEFAULT NULL,
  `fee` int(1) DEFAULT NULL,
  `bitrate` int(11) DEFAULT NULL,
  `misc` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `caster_id` int(11) DEFAULT 0,
  `bases_id` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`bases_id`))
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Дамп данных таблицы `mountpoint`
--

INSERT INTO `mountpoint` (`id`, `mountpoint`, `identifier`, `format`, `format-details`, `carrier`, `nav-system`, `network`, `country`, `latitude`, `longitude`, `nmea`, `solution`, `generator`, `compression`, `authentication`, `fee`, `bitrate`, `misc`, `password`, `caster_id`, `bases_id`) VALUES
(1, 'test1', 'Juneau', 'RTCM 3.1', '1004(1),1005(30),1007(30),1033(30)', 2, 'GPS', NULL, 'USA', 58.416774365884315, -134.5453031026356, 0, NULL, '', NULL, 0, NULL, 0, NULL, NULL, 0, NULL),
(2, 'test2', 'Juneau', 'RTCM 3.0', '1004(1)', 2, 'GPS', NULL, 'USA', 58.416774365884315, -134.5453031026356, 0, NULL, '', NULL, 1, NULL, 0, NULL, NULL, 0, NULL);

-- --------------------------------------------------------

--
-- Структура таблицы `port_listeners`
--

CREATE TABLE `port_listeners` (
  `id` int(11) NOT NULL,
  `address` varchar(40) NOT NULL,
  `port` int(5) NOT NULL,
  `group_id` int(11) NOT NULL,
  `status` int(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Дамп данных таблицы `port_listeners`
--

INSERT INTO `port_listeners` (`id`, `address`, `port`, `group_id`, `status`) VALUES
(0, 'localhost', 8500, 1, 0);

-- --------------------------------------------------------

--
-- Структура таблицы `users`
--

CREATE TABLE `users` (
  `id` int(11) UNSIGNED NOT NULL,
  `ip_address` varchar(45) NOT NULL,
  `username` varchar(100) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `email` varchar(254) NOT NULL,
  `activation_selector` varchar(255) DEFAULT NULL,
  `activation_code` varchar(255) DEFAULT NULL,
  `forgotten_password_selector` varchar(255) DEFAULT NULL,
  `forgotten_password_code` varchar(255) DEFAULT NULL,
  `forgotten_password_time` int(11) UNSIGNED DEFAULT NULL,
  `remember_selector` varchar(255) DEFAULT NULL,
  `remember_code` varchar(255) DEFAULT NULL,
  `created_on` int(11) UNSIGNED NOT NULL,
  `last_login` int(11) UNSIGNED DEFAULT NULL,
  `active` tinyint(1) UNSIGNED DEFAULT NULL,
  `first_name` varchar(50) DEFAULT NULL,
  `last_name` varchar(50) DEFAULT NULL,
  `company` varchar(100) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Дамп данных таблицы `users`
--

INSERT INTO `users` (`id`, `ip_address`, `username`, `password`, `email`, `activation_selector`, `activation_code`, `forgotten_password_selector`, `forgotten_password_code`, `forgotten_password_time`, `remember_selector`, `remember_code`, `created_on`, `last_login`, `active`, `first_name`, `last_name`, `company`, `phone`) VALUES
(1, '127.0.0.1', 'administrator', '$2y$12$c8pG2YTdvnTPbaxafH1aH.NDvcibJ8HbzRrXyYfiDkHB8pShZBxnq', 'admin@admin.com', NULL, '', NULL, NULL, NULL, NULL, NULL, 1268889823, 1590842478, 1, 'Admin', 'istrator', 'ADMIN', '0');

-- --------------------------------------------------------

--
-- Структура таблицы `users_groups`
--

CREATE TABLE `users_groups` (
  `id` int(11) UNSIGNED NOT NULL,
  `user_id` int(11) UNSIGNED NOT NULL,
  `group_id` mediumint(8) UNSIGNED NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Дамп данных таблицы `users_groups`
--

INSERT INTO `users_groups` (`id`, `user_id`, `group_id`) VALUES
(1, 1, 1),
(2, 1, 2);

--
-- Индексы сохранённых таблиц
--

--
-- Индексы таблицы `base_stations`
--
ALTER TABLE `base_stations`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `mountpoint` (`mountpoint`),
  ADD SPATIAL KEY `lla` (`lla`);

--
-- Индексы таблицы `clients_log`
--
ALTER TABLE `clients_log`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `time_mark` (`time_mark`);

--
-- Индексы таблицы `config`
--
ALTER TABLE `config`
  ADD PRIMARY KEY (`id`),
  ADD KEY `group` (`group`),
  ADD KEY `key` (`key`);

--
-- Индексы таблицы `groups`
--
ALTER TABLE `groups`
  ADD PRIMARY KEY (`id`);

--
-- Индексы таблицы `login_attempts`
--
ALTER TABLE `login_attempts`
  ADD PRIMARY KEY (`id`);

--
-- Индексы таблицы `mountpoint`
--
ALTER TABLE `mountpoint`
  ADD PRIMARY KEY (`id`),
  ADD KEY `mountpoint` (`mountpoint`);

--
-- Индексы таблицы `port_listeners`
--
ALTER TABLE `port_listeners`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `port` (`port`),
  ADD KEY `group_id` (`group_id`);

--
-- Индексы таблицы `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uc_email` (`email`),
  ADD UNIQUE KEY `uc_activation_selector` (`activation_selector`),
  ADD UNIQUE KEY `uc_forgotten_password_selector` (`forgotten_password_selector`),
  ADD UNIQUE KEY `uc_remember_selector` (`remember_selector`);

--
-- Индексы таблицы `users_groups`
--
ALTER TABLE `users_groups`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uc_users_groups` (`user_id`,`group_id`),
  ADD KEY `fk_users_groups_users1_idx` (`user_id`),
  ADD KEY `fk_users_groups_groups1_idx` (`group_id`);

--
-- AUTO_INCREMENT для сохранённых таблиц
--

--
-- AUTO_INCREMENT для таблицы `base_stations`
--
ALTER TABLE `base_stations`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT для таблицы `clients_log`
--
ALTER TABLE `clients_log`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT для таблицы `config`
--
ALTER TABLE `config`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT для таблицы `groups`
--
ALTER TABLE `groups`
  MODIFY `id` mediumint(8) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT для таблицы `login_attempts`
--
ALTER TABLE `login_attempts`
  MODIFY `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT для таблицы `mountpoint`
--
ALTER TABLE `mountpoint`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT для таблицы `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT для таблицы `users_groups`
--
ALTER TABLE `users_groups`
  MODIFY `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- Ограничения внешнего ключа сохраненных таблиц
--

--
-- Ограничения внешнего ключа таблицы `users_groups`
--
ALTER TABLE `users_groups`
  ADD CONSTRAINT `fk_users_groups_groups1` FOREIGN KEY (`group_id`) REFERENCES `groups` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_users_groups_users1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
