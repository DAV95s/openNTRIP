-- phpMyAdmin SQL Dump
-- version 5.0.4
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Mar 17, 2021 at 07:29 PM
-- Server version: 10.5.6-MariaDB
-- PHP Version: 7.3.6

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `ntrip`
--

-- --------------------------------------------------------

--
-- Table structure for table `casters`
--

CREATE TABLE `casters` (
  `id` int(11) NOT NULL,
  `address` varchar(40) DEFAULT 'localhost',
  `port` int(5) NOT NULL,
  `group_id` int(11) DEFAULT 1,
  `status` int(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `casters`
--

INSERT INTO `casters` (`id`, `address`, `port`, `group_id`, `status`) VALUES
(1, 'localhost', 8500, 1, 1);

-- --------------------------------------------------------

--
-- Table structure for table `clients_log`
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
-- Table structure for table `config`
--

CREATE TABLE `config` (
  `id` int(11) NOT NULL,
  `group` varchar(100) NOT NULL,
  `key` varchar(100) NOT NULL,
  `value` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `config`
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
-- Table structure for table `crs`
--

CREATE TABLE `crs` (
  `id` int(11) NOT NULL,
  `proj4` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `crs`
--

INSERT INTO `crs` (`id`, `proj4`) VALUES
(1, '+proj=tmerc +lat_0=0 +lon_0=30 +k=1 +x_0=95900 +y_0=-6552800 +ellps=WGS84 +towgs84=5.476,2.074,9.338,3.38086,5.93454,-0.49579,-1.676094 +units=m +no_defs');

-- --------------------------------------------------------

--
-- Table structure for table `groups`
--

CREATE TABLE `groups` (
  `id` mediumint(8) UNSIGNED NOT NULL,
  `name` varchar(20) NOT NULL,
  `description` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `groups`
--

INSERT INTO `groups` (`id`, `name`, `description`) VALUES
(1, 'admin', 'Administrator'),
(2, 'members', 'General User');

-- --------------------------------------------------------

--
-- Table structure for table `login_attempts`
--

CREATE TABLE `login_attempts` (
  `id` int(11) UNSIGNED NOT NULL,
  `ip_address` varchar(45) NOT NULL,
  `login` varchar(100) NOT NULL,
  `time` int(11) UNSIGNED DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `mountpoints`
--

CREATE TABLE `mountpoints` (
  `id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `identifier` varchar(255) DEFAULT NULL,
  `format` varchar(255) DEFAULT NULL,
  `format_details` varchar(255) DEFAULT NULL,
  `carrier` int(1) DEFAULT NULL,
  `nav_system` varchar(255) DEFAULT NULL,
  `network` varchar(255) DEFAULT NULL,
  `country` varchar(3) DEFAULT NULL,
  `latitude` double DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  `nmea` tinyint(1) NOT NULL DEFAULT 0,
  `solution` tinyint(1) NOT NULL DEFAULT 0,
  `generator` varchar(255) DEFAULT NULL,
  `compression` varchar(255) DEFAULT NULL,
  `authenticator` varchar(50) DEFAULT NULL,
  `fee` tinyint(1) DEFAULT 0,
  `bitrate` int(11) DEFAULT 0,
  `misc` varchar(255) DEFAULT NULL,
  `caster_id` int(11) NOT NULL,
  `available` tinyint(1) NOT NULL DEFAULT 1,
  `plugin_id` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `mountpoints`
--

INSERT INTO `mountpoints` (`id`, `name`, `identifier`, `format`, `format_details`, `carrier`, `nav_system`, `network`, `country`, `latitude`, `longitude`, `nmea`, `solution`, `generator`, `compression`, `authenticator`, `fee`, `bitrate`, `misc`, `caster_id`, `available`, `plugin_id`) VALUES
(1, 'test1', 'Juneau', 'RTCM 3.1', '1004(1),1005(30),1007(30),1033(30)', 2, 'GPS', NULL, 'USA', 58.416774365884315, -134.5453031026356, 1, 0, '', NULL, 'Basic', 0, 0, '', 1, 1, NULL),
(2, 'test2', 'Juneau', 'RTCM 3.0', '1004(1)', 2, 'GPS', NULL, 'USA', 58.416774365884315, -134.5453031026356, 0, 0, '', NULL, 'Basic', 0, 0, '', 1, 1, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `mountpoints_stations`
--

CREATE TABLE `mountpoints_stations` (
  `mountpoint_id` int(11) NOT NULL,
  `station_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `mountpoints_stations`
--

INSERT INTO `mountpoints_stations` (`mountpoint_id`, `station_id`) VALUES
(1, 1),
(1, 2),
(2, 1);

-- --------------------------------------------------------

--
-- Table structure for table `reference_stations`
--

CREATE TABLE `reference_stations` (
  `id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `identifier` varchar(255) DEFAULT NULL,
  `format` varchar(255) DEFAULT NULL,
  `format_details` varchar(255) DEFAULT NULL,
  `carrier` int(1) DEFAULT NULL,
  `nav_system` varchar(255) DEFAULT NULL,
  `country` varchar(3) DEFAULT NULL,
  `lat` double DEFAULT NULL,
  `lon` double DEFAULT NULL,
  `alt` double DEFAULT NULL,
  `bitrate` int(11) DEFAULT 0,
  `misc` varchar(255) DEFAULT '',
  `is_online` int(1) DEFAULT 0,
  `password` varchar(255) NOT NULL DEFAULT '',
  `hz` int(2) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `reference_stations`
--

INSERT INTO `reference_stations` (`id`, `name`, `identifier`, `format`, `format_details`, `carrier`, `nav_system`, `country`, `lat`, `lon`, `alt`, `bitrate`, `misc`, `is_online`, `password`, `hz`) VALUES
(1, 'AL1', 'Juneau', 'RTCM 3.1', '1004(1),1005(30),1007(30),1033(30)', 2, 'GPS', 'USA', 58.41677474975586, -134.54530334472656, 0, 123, '', 0, '44444', 4),
(2, 'AL2', 'Capital Regional District', 'RTCM 3.2', '1006(10),1008(10),1013(60),1019(1),1020(1),1033(10),1077(1),1087(1),1097(1)', 2, 'GPS+GLO+GAL', 'CAN', 48.3897819519043, -123.48747253417969, 0, 0, '', 0, '44444', 0);

-- --------------------------------------------------------

--
-- Table structure for table `reference_stations_fix_position`
--

CREATE TABLE `reference_stations_fix_position` (
  `id` int(11) NOT NULL,
  `station_id` int(11) NOT NULL,
  `ECEF_X` decimal(12,4) NOT NULL,
  `ECEF_Y` decimal(12,4) NOT NULL,
  `ECEF_Z` decimal(12,4) NOT NULL,
  `antenna_height` decimal(5,4) NOT NULL,
  `status` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) UNSIGNED NOT NULL,
  `ip_address` varchar(45) DEFAULT NULL,
  `username` varchar(100) NOT NULL,
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
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `ip_address`, `username`, `password`, `email`, `activation_selector`, `activation_code`, `forgotten_password_selector`, `forgotten_password_code`, `forgotten_password_time`, `remember_selector`, `remember_code`, `created_on`, `last_login`, `active`, `first_name`, `last_name`, `company`, `phone`) VALUES
(1, '127.0.0.1', 'administrator', '$2y$12$c8pG2YTdvnTPbaxafH1aH.NDvcibJ8HbzRrXyYfiDkHB8pShZBxnq', 'admin@admin.com', NULL, '', NULL, NULL, NULL, NULL, NULL, 1268889823, 1599681037, 1, 'Admin', 'istrator', 'ADMIN', '0');

-- --------------------------------------------------------

--
-- Table structure for table `users_groups`
--

CREATE TABLE `users_groups` (
  `id` int(11) UNSIGNED NOT NULL,
  `user_id` int(11) UNSIGNED NOT NULL,
  `group_id` mediumint(8) UNSIGNED NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `users_groups`
--

INSERT INTO `users_groups` (`id`, `user_id`, `group_id`) VALUES
(1, 1, 1),
(3, 1, 2);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `casters`
--
ALTER TABLE `casters`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `port` (`port`),
  ADD KEY `group_id` (`group_id`);

--
-- Indexes for table `clients_log`
--
ALTER TABLE `clients_log`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `time_mark` (`time_mark`);

--
-- Indexes for table `config`
--
ALTER TABLE `config`
  ADD PRIMARY KEY (`id`),
  ADD KEY `group` (`group`),
  ADD KEY `key` (`key`);

--
-- Indexes for table `crs`
--
ALTER TABLE `crs`
  ADD KEY `id` (`id`);

--
-- Indexes for table `groups`
--
ALTER TABLE `groups`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `login_attempts`
--
ALTER TABLE `login_attempts`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `mountpoints`
--
ALTER TABLE `mountpoints`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `mountpoints_un` (`name`,`caster_id`),
  ADD KEY `mountpoint` (`name`);

--
-- Indexes for table `mountpoints_stations`
--
ALTER TABLE `mountpoints_stations`
  ADD KEY `mountpoint_id` (`mountpoint_id`,`station_id`);

--
-- Indexes for table `reference_stations`
--
ALTER TABLE `reference_stations`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `mountpoint` (`name`);

--
-- Indexes for table `reference_stations_fix_position`
--
ALTER TABLE `reference_stations_fix_position`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `station_id_2` (`station_id`),
  ADD KEY `station_id` (`station_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uc_email` (`email`),
  ADD UNIQUE KEY `uc_activation_selector` (`activation_selector`),
  ADD UNIQUE KEY `uc_forgotten_password_selector` (`forgotten_password_selector`),
  ADD UNIQUE KEY `uc_remember_selector` (`remember_selector`);

--
-- Indexes for table `users_groups`
--
ALTER TABLE `users_groups`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uc_users_groups` (`user_id`,`group_id`),
  ADD KEY `fk_users_groups_users1_idx` (`user_id`),
  ADD KEY `fk_users_groups_groups1_idx` (`group_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `casters`
--
ALTER TABLE `casters`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;

--
-- AUTO_INCREMENT for table `clients_log`
--
ALTER TABLE `clients_log`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `config`
--
ALTER TABLE `config`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `groups`
--
ALTER TABLE `groups`
  MODIFY `id` mediumint(8) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `login_attempts`
--
ALTER TABLE `login_attempts`
  MODIFY `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `mountpoints`
--
ALTER TABLE `mountpoints`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=31;

--
-- AUTO_INCREMENT for table `reference_stations`
--
ALTER TABLE `reference_stations`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=66;

--
-- AUTO_INCREMENT for table `reference_stations_fix_position`
--
ALTER TABLE `reference_stations_fix_position`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `users_groups`
--
ALTER TABLE `users_groups`
  MODIFY `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `users_groups`
--
ALTER TABLE `users_groups`
  ADD CONSTRAINT `fk_users_groups_groups1` FOREIGN KEY (`group_id`) REFERENCES `groups` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_users_groups_users1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
