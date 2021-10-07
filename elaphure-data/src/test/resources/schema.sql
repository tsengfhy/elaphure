create table `user`
(
    `id`            bigint not null primary key,
    `name`          varchar(30) default null,
    `sex`           varchar(4)  default null,
    `age`           int         default null,
    `email`         varchar(50) default null,
    `version`       int         default '1',
    `deleted`       varchar(1)  default '0',
    `created_by`    bigint      default null,
    `created_date`  datetime    default null,
    `updated_by`    bigint      default null,
    `updated_date`  datetime    default null
);