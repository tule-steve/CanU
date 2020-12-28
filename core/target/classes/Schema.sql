
CREATE TABLE `evoucher`.`tu_test_gc` (
  `id` BIGINT(10) NOT NULL,
  `gift_code` VARCHAR(45) NULL,
  `item_id` BIGINT(10) NOT NULL,
  `order_id` VARCHAR(45) NULL,
  `status` VARCHAR(45) NULL,
  PRIMARY KEY (`id`));


  CREATE TABLE `evoucher`.`tu_test_itm` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `description` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`));

  CREATE TABLE `evoucher`.`hibernate_sequence` (
);

