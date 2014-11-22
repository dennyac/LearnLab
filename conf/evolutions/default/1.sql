# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table event (
  event_id                  bigint not null,
  instructor_id             bigint,
  event_name                varchar(255),
  event_start_time          timestamp,
  script                    TEXT,
  script_phase1             TEXT,
  script_phase2             TEXT,
  script_phase3             TEXT,
  active                    boolean,
  event_stats_id            bigint,
  constraint uq_event_event_name unique (event_name),
  constraint pk_event primary key (event_id))
;

create table event_actions (
  id                        bigint not null,
  event_event_id            bigint,
  user_id                   bigint,
  action_type               varchar(255),
  attribute1                varchar(255),
  attribute2                varchar(255),
  time_of_event_action      timestamp,
  constraint pk_event_actions primary key (id))
;

create table event_stats (
  id                        bigint not null,
  total_messages            integer,
  percent_of_success        float,
  constraint pk_event_stats primary key (id))
;

create table question (
  question_number           bigint not null,
  question_string           varchar(255),
  option1                   varchar(255),
  option2                   varchar(255),
  option3                   varchar(255),
  option4                   varchar(255),
  answer                    varchar(255),
  constraint pk_question primary key (question_number))
;

create table token (
  token                     varchar(255) not null,
  user_id                   bigint,
  type                      varchar(8),
  date_creation             timestamp,
  email                     varchar(255),
  constraint ck_token_type check (type in ('password','email')),
  constraint pk_token primary key (token))
;

create table users (
  id                        bigint not null,
  email                     varchar(255),
  fullname                  varchar(255),
  confirmation_token        varchar(255),
  password_hash             varchar(255),
  date_creation             timestamp,
  validated                 boolean,
  is_instructor             boolean,
  no_of_quizes_participated integer,
  aggregated_score          bigint,
  badge                     varchar(255),
  hash_tag_contributions    integer,
  bio                       varchar(255),
  stats_id                  bigint,
  constraint uq_users_email unique (email),
  constraint uq_users_fullname unique (fullname),
  constraint pk_users primary key (id))
;

create table user_stats (
  id                        bigint not null,
  no_of_posts               integer,
  constraint pk_user_stats primary key (id))
;


create table event_users (
  event_event_id                 bigint not null,
  users_id                       bigint not null,
  constraint pk_event_users primary key (event_event_id, users_id))
;

create table event_question (
  event_event_id                 bigint not null,
  question_question_number       bigint not null,
  constraint pk_event_question primary key (event_event_id, question_question_number))
;
create sequence event_seq;

create sequence event_actions_seq;

create sequence event_stats_seq;

create sequence question_seq;

create sequence token_seq;

create sequence users_seq;

create sequence user_stats_seq;

alter table event add constraint fk_event_instructor_1 foreign key (instructor_id) references users (id);
create index ix_event_instructor_1 on event (instructor_id);
alter table event add constraint fk_event_eventStats_2 foreign key (event_stats_id) references event_stats (id);
create index ix_event_eventStats_2 on event (event_stats_id);
alter table event_actions add constraint fk_event_actions_event_3 foreign key (event_event_id) references event (event_id);
create index ix_event_actions_event_3 on event_actions (event_event_id);
alter table event_actions add constraint fk_event_actions_user_4 foreign key (user_id) references users (id);
create index ix_event_actions_user_4 on event_actions (user_id);
alter table users add constraint fk_users_stats_5 foreign key (stats_id) references user_stats (id);
create index ix_users_stats_5 on users (stats_id);



alter table event_users add constraint fk_event_users_event_01 foreign key (event_event_id) references event (event_id);

alter table event_users add constraint fk_event_users_users_02 foreign key (users_id) references users (id);

alter table event_question add constraint fk_event_question_event_01 foreign key (event_event_id) references event (event_id);

alter table event_question add constraint fk_event_question_question_02 foreign key (question_question_number) references question (question_number);

# --- !Downs

drop table if exists event cascade;

drop table if exists event_users cascade;

drop table if exists event_question cascade;

drop table if exists event_actions cascade;

drop table if exists event_stats cascade;

drop table if exists question cascade;

drop table if exists token cascade;

drop table if exists users cascade;

drop table if exists user_stats cascade;

drop sequence if exists event_seq;

drop sequence if exists event_actions_seq;

drop sequence if exists event_stats_seq;

drop sequence if exists question_seq;

drop sequence if exists token_seq;

drop sequence if exists users_seq;

drop sequence if exists user_stats_seq;

