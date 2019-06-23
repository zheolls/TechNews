create database technews;
use technews;
create table keyword
(
  name  varchar(20)     not null,
  fever int default '0' null,
  constraint keyword_name_uindex
  unique (name)
);

alter table keyword
  add primary key (name);

create table user
(
  userid   int auto_increment,
  email    varchar(255) null,
  password varchar(100) not null,
  phone    varchar(20)  null,
  nickname varchar(20)  null,
  constraint user_email_uindex
  unique (email),
  constraint user_phone_uindex
  unique (phone),
  constraint user_userid_uindex
  unique (userid)
);

alter table user
  add primary key (userid);

create table user_click
(
  userid int             not null,
  times  int default '0' null,
  constraint user_click_userid_uindex
  unique (userid),
  constraint user_click_user_userid_fk
  foreign key (userid) references user (userid)
);

alter table user_click
  add primary key (userid);

create table user_keyword
(
  userid  int               not null,
  keyword varchar(20)       not null,
  fever   float default '0' not null,
  primary key (userid, keyword),
  constraint user_keyword_keyword_name_fk
  foreign key (keyword) references keyword (name),
  constraint user_keyword_user_userid_fk
  foreign key (userid) references user (userid)
);

create table web
(
  webid    int auto_increment,
  domain   varchar(255) not null,
  name     varchar(255) null,
  category varchar(20)  null,
  constraint web_domain_uindex
  unique (domain),
  constraint web_webid_uindex
  unique (webid)
);

create table article
(
  articleid int auto_increment,
  url       varchar(255)        not null,
  webid     int                 null,
  source    varchar(32)         null,
  date      datetime            null,
  fever     int default '0'     null,
  valid     tinyint default '1' null,
  imgurl    varchar(255)        null,
  title     varchar(255)        null,
  constraint article_articleid_uindex
  unique (articleid),
  constraint article_title_uindex
  unique (title),
  constraint article_url_uindex
  unique (url),
  constraint article_web_webid_fk
  foreign key (webid) references web (webid)
);

create table article_history
(
  userid    int                 not null,
  articleid int                 not null,
  valid     tinyint default '1' null,
  date      datetime            null,
  primary key (userid, articleid),
  constraint article_history_article_articleid_fk
  foreign key (articleid) references article (articleid),
  constraint article_history_user_userid_fk
  foreign key (userid) references user (userid)
);

create table article_keyword
(
  articleid int         not null,
  keyword   varchar(20) not null,
  primary key (articleid, keyword),
  constraint article_keyword_article_articleid_fk
  foreign key (articleid) references article (articleid),
  constraint article_keyword_keyword_name_fk
  foreign key (keyword) references keyword (name)
);

create table collect_article
(
  userid    int      not null,
  articleid int      not null,
  date      datetime null,
  primary key (userid, articleid),
  constraint collectarticle_article_articleid_fk
  foreign key (articleid) references article (articleid),
  constraint collectarticle_user_userid_fk
  foreign key (userid) references user (userid)
);

create table video
(
  videoid      int auto_increment,
  url          varchar(255)        not null,
  webid        int                 null,
  source       varchar(32)         null,
  date         datetime            null,
  fever        int                 null,
  valid        tinyint default '1' null,
  authorimgurl varchar(255)        null,
  imgurl       varchar(255)        null,
  title        varchar(100)        null,
  constraint video_videoid_uindex
  unique (videoid),
  constraint video_web_webid_fk
  foreign key (webid) references web (webid)
);

alter table video
  add primary key (videoid);

create table collect_video
(
  userid  int not null,
  videoid int not null,
  primary key (userid, videoid),
  constraint collect_video_user_userid_fk
  foreign key (userid) references user (userid),
  constraint collect_video_video_videoid_fk
  foreign key (videoid) references video (videoid)
);

create view article_keyword_view as
  select `technews`.`article_keyword`.`keyword` AS `keyword`
  from `technews`.`article_keyword`
  group by `technews`.`article_keyword`.`articleid`;

create view user_keyword_daily_view as
  select `a2`.`userid`                           AS `userid`,
         `a2`.`keyword`                          AS `keyword`,
         (to_days(now()) - to_days(`a2`.`date`)) AS `date`,
         count(0)                                AS `times`
  from (`technews`.`user_click` join (select `history`.`userid`                     AS `userid`,
                                             `technews`.`article_keyword`.`keyword` AS `keyword`,
                                             `history`.`date`                       AS `date`
                                      from (`technews`.`article_keyword` join (select `technews`.`article_history`.`userid`    AS `userid`,
                                                                                      `technews`.`article_history`.`articleid` AS `articleid`,
                                                                                      `technews`.`article_history`.`valid`     AS `valid`,
                                                                                      `technews`.`article_history`.`date`      AS `date`
                                                                               from `technews`.`article_history`
                                                                               where ((to_days(`technews`.`article_history`.`date`) - to_days(now())) < 60)
                                                                               order by `technews`.`article_history`.`date` desc) `history`)
                                      where (`history`.`articleid` = `technews`.`article_keyword`.`articleid`)
                                      order by `history`.`userid`) `a2`)
  where ((`a2`.`userid` = `technews`.`user_click`.`userid`) and (`technews`.`user_click`.`times` > 0))
  group by `a2`.`userid`, `a2`.`keyword`, `a2`.`date`
  order by `a2`.`userid`;

create view user_keyword_update_instant_view as
  select `a2`.`userid`                           AS `userid`,
         `a2`.`keyword`                          AS `keyword`,
         (to_days(now()) - to_days(`a2`.`date`)) AS `date`,
         count(0)                                AS `times`
  from (`technews`.`user_click` join (select `history`.`userid`                     AS `userid`,
                                             `technews`.`article_keyword`.`keyword` AS `keyword`,
                                             `history`.`date`                       AS `date`
                                      from (`technews`.`article_keyword` join (select `technews`.`article_history`.`userid`    AS `userid`,
                                                                                      `technews`.`article_history`.`articleid` AS `articleid`,
                                                                                      `technews`.`article_history`.`valid`     AS `valid`,
                                                                                      `technews`.`article_history`.`date`      AS `date`
                                                                               from `technews`.`article_history`
                                                                               where ((to_days(`technews`.`article_history`.`date`) - to_days(now())) < 60)
                                                                               order by `technews`.`article_history`.`date` desc) `history`)
                                      where (`history`.`articleid` = `technews`.`article_keyword`.`articleid`)
                                      order by `history`.`userid`) `a2`)
  where ((`a2`.`userid` = `technews`.`user_click`.`userid`) and (`technews`.`user_click`.`times` > 10))
  group by `a2`.`userid`, `a2`.`keyword`, `a2`.`date`
  order by `a2`.`userid`;

