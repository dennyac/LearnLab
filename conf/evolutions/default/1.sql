# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table event (
  event_id                  bigint not null,
  instructor_id             bigint,
  event_name                varchar(255),
  event_date_time           timestamp,
  start_time                varchar(255),
  end_date_time             timestamp,
  description               TEXT,
  script_phase1             TEXT,
  script_phase2             TEXT,
  script_phase3             TEXT,
  script_phase4             TEXT,
  hashes                    varchar(255),
  phase1duration            integer,
  phase2duration            integer,
  phase3duration            integer,
  active                    integer,
  event_FK                  bigint,
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
  event_id                  bigint,
  no_of_praticipants        integer,
  total_no_of_messages      integer,
  no_of_hash_tag_messgaes   integer,
  no_of_informal_messages   integer,
  percentage_correct_in_phase1 float,
  percentage_correct_in_phase3 float,
  percentage_correct_in_phase4 float,
  positive_collaboration_score float,
  negative_collaboration_score float,
  constraint uq_event_stats_event_id unique (event_id),
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
  bio                       varchar(255),
  constraint uq_users_email unique (email),
  constraint uq_users_fullname unique (fullname),
  constraint pk_users primary key (id))
;

create table user_event_stats (
  user_event_id             bigint not null,
  user_id                   bigint,
  event_event_id            bigint,
  user_stats_id             bigint,
  phase1answer_in_event     boolean,
  phase3answer_in_event     boolean,
  phase4answer_in_event     boolean,
  no_of_individual_informal_messages_in_event integer,
  no_of_individual_hash_tag_messages_in_event integer,
  percentage_contribution_for_discussion_in_event float,
  score_phase1in_event      float,
  score_phase3in_event      float,
  score_phase4in_event      float,
  aggregated_score_for_event float,
  cognitive_ability_score   integer,
  percentage_of_cognitive_ability float,
  collaborative_index_for_event float,
  no_of_up_votes_received_for_event integer,
  constraint pk_user_event_stats primary key (user_event_id))
;

create table user_stats (
  id                        bigint not null,
  user_id                   bigint,
  no_of_events_participated_in integer,
  no_of_individual_informal_messages integer,
  no_of_individual_hash_tag_messages integer,
  no_of_messages_exchanged_by_all_in_all_events_user_participated integer,
  percentage_contribution_for_discussion float,
  aggregate_score           float,
  cognitive_abilities_score integer,
  percentage_of_cognitive_ability float,
  up_votes                  integer,
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

create sequence user_event_stats_seq;

create sequence user_stats_seq;

alter table event add constraint fk_event_instructor_1 foreign key (instructor_id) references users (id);
create index ix_event_instructor_1 on event (instructor_id);
alter table event add constraint fk_event_eventStats_2 foreign key (event_FK) references event_stats (id);
create index ix_event_eventStats_2 on event (event_FK);
alter table event_actions add constraint fk_event_actions_event_3 foreign key (event_event_id) references event (event_id);
create index ix_event_actions_event_3 on event_actions (event_event_id);
alter table event_actions add constraint fk_event_actions_user_4 foreign key (user_id) references users (id);
create index ix_event_actions_user_4 on event_actions (user_id);
alter table user_event_stats add constraint fk_user_event_stats_user_5 foreign key (user_id) references users (id);
create index ix_user_event_stats_user_5 on user_event_stats (user_id);
alter table user_event_stats add constraint fk_user_event_stats_event_6 foreign key (event_event_id) references event (event_id);
create index ix_user_event_stats_event_6 on user_event_stats (event_event_id);
alter table user_event_stats add constraint fk_user_event_stats_userStats_7 foreign key (user_stats_id) references user_stats (id);
create index ix_user_event_stats_userStats_7 on user_event_stats (user_stats_id);
alter table user_stats add constraint fk_user_stats_user_8 foreign key (user_id) references users (id);
create index ix_user_stats_user_8 on user_stats (user_id);



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

drop table if exists user_event_stats cascade;

drop table if exists user_stats cascade;

drop sequence if exists event_seq;

drop sequence if exists event_actions_seq;

drop sequence if exists event_stats_seq;

drop sequence if exists question_seq;

drop sequence if exists token_seq;

drop sequence if exists users_seq;

drop sequence if exists user_event_stats_seq;

drop sequence if exists user_stats_seq;

