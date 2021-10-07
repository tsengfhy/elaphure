-- create schema if not exists `elaphure` default character set utf8mb4 collate utf8mb4_general_ci;

drop table if exists `user`;
create table `user`
(
    `id`                 bigint not null primary key,
    `name`               varchar(30) default null,
    `sex`                varchar(4)  default null,
    `age`                int         default null,
    `email`              varchar(50) default null,
    `version`            int         default '1',
    `deleted`            varchar(1)  default '0',
    `created_by`         bigint      default null,
    `created_date`       datetime    default null,
    `last_modified_by`   bigint      default null,
    `last_modified_date` datetime    default null
);