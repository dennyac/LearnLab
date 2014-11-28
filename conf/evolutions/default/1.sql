# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table event (
  event_id                  bigint auto_increment not null,
  instructor_id             bigint,
  event_name                varchar(255),
  event_date                datetime,
  start_time                varchar(255),
  end_time                  varchar(255),
  description               TEXT,
  script_phase1             TEXT,
  script_phase2             TEXT,
  script_phase3             TEXT,
  script_phase4             TEXT,
  hashes                    varchar(255),
  phase1duration            bigint,
  phase2duration            bigint,
  phase3duration            bigint,
  active                    tinyint(1) default 0,
  event_stats_id            bigint,
  constraint pk_event primary key (event_id))
;

create table event_actions (
  id                        bigint auto_increment not null,
  event_event_id            bigint,
  user_id                   bigint,
  action_type               varchar(255),
  attribute1                varchar(255),
  attribute2                varchar(255),
  time_of_event_action      datetime,
  constraint pk_event_actions primary key (id))
;

create table event_stats (
  id                        bigint auto_increment not null,
  event_id                  bigint,
  no_of_praticipants        integer,
  total_no_of_messages      integer,
  no_of_hash_tag_messgaes   integer,
  no_of_informal_messages   integer,
  percentage_correct_in_phase1 double,
  percentage_correct_in_phase3 double,
  percentage_correct_in_phase4 double,
  positive_collaboration_score double,
  negative_collaboration_score double,
  constraint pk_event_stats primary key (id))
;

create table question (
  question_number           bigint auto_increment not null,
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
  date_creation             datetime,
  email                     varchar(255),
  constraint ck_token_type check (type in ('password','email')),
  constraint pk_token primary key (token))
;

create table users (
  id                        bigint auto_increment not null,
  email                     varchar(255),
  fullname                  varchar(255),
  confirmation_token        varchar(255),
  password_hash             varchar(255),
  date_creation             datetime,
  validated                 tinyint(1) default 0,
  is_instructor             tinyint(1) default 0,
  bio                       varchar(255),
  user_stats_information_id bigint,
  constraint uq_users_email unique (email),
  constraint uq_users_fullname unique (fullname),
  constraint pk_users primary key (id))
;

create table user_event_stats (
  user_event_id             bigint auto_increment not null,
  user_id                   bigint,
  event_event_id            bigint,
  user_stats_id             bigint,
  phase1answer_in_event     tinyint(1) default 0,
  phase3answer_in_event     tinyint(1) default 0,
  phase4answer_in_event     tinyint(1) default 0,
  no_of_individual_informal_messages_in_event integer,
  no_of_individual_hash_tag_messages_in_event integer,
  percentage_contribution_for_discussion_in_event double,
  score_phase1in_event      double,
  score_phase3in_event      double,
  score_phase4in_event      double,
  aggregated_score_for_event double,
  cognitive_ability_score   integer,
  percentage_of_cognitive_ability double,
  collaborative_index_for_event double,
  no_of_up_votes_received_for_event integer,
  constraint pk_user_event_stats primary key (user_event_id))
;

create table user_stats (
  id                        bigint auto_increment not null,
  user_id                   bigint,
  no_of_events_participated_in integer,
  no_of_individual_informal_messages integer,
  no_of_individual_hash_tag_messages integer,
  no_of_messages_exchanged_by_all_in_all_events_user_participated integer,
  percentage_contribution_for_discussion double,
  aggregate_score           double,
  cognitive_abilities_score integer,
  percentage_of_cognitive_ability double,
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
alter table event add constraint fk_event_instructor_1 foreign key (instructor_id) references users (id) on delete restrict on update restrict;
create index ix_event_instructor_1 on event (instructor_id);
alter table event add constraint fk_event_eventStats_2 foreign key (event_stats_id) references event_stats (id) on delete restrict on update restrict;
create index ix_event_eventStats_2 on event (event_stats_id);
alter table event_actions add constraint fk_event_actions_event_3 foreign key (event_event_id) references event (event_id) on delete restrict on update restrict;
create index ix_event_actions_event_3 on event_actions (event_event_id);
alter table event_actions add constraint fk_event_actions_user_4 foreign key (user_id) references users (id) on delete restrict on update restrict;
create index ix_event_actions_user_4 on event_actions (user_id);
alter table users add constraint fk_users_userStatsInformation_5 foreign key (user_stats_information_id) references user_stats (id) on delete restrict on update restrict;
create index ix_users_userStatsInformation_5 on users (user_stats_information_id);
alter table user_event_stats add constraint fk_user_event_stats_user_6 foreign key (user_id) references users (id) on delete restrict on update restrict;
create index ix_user_event_stats_user_6 on user_event_stats (user_id);
alter table user_event_stats add constraint fk_user_event_stats_event_7 foreign key (event_event_id) references event (event_id) on delete restrict on update restrict;
create index ix_user_event_stats_event_7 on user_event_stats (event_event_id);
alter table user_event_stats add constraint fk_user_event_stats_userStats_8 foreign key (user_stats_id) references user_stats (id) on delete restrict on update restrict;
create index ix_user_event_stats_userStats_8 on user_event_stats (user_stats_id);
alter table user_stats add constraint fk_user_stats_user_9 foreign key (user_id) references users (id) on delete restrict on update restrict;
create index ix_user_stats_user_9 on user_stats (user_id);



alter table event_users add constraint fk_event_users_event_01 foreign key (event_event_id) references event (event_id) on delete restrict on update restrict;

alter table event_users add constraint fk_event_users_users_02 foreign key (users_id) references users (id) on delete restrict on update restrict;

alter table event_question add constraint fk_event_question_event_01 foreign key (event_event_id) references event (event_id) on delete restrict on update restrict;

alter table event_question add constraint fk_event_question_question_02 foreign key (question_question_number) references question (question_number) on delete restrict on update restrict;

# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table event;

drop table event_users;

drop table event_question;

drop table event_actions;

drop table event_stats;

drop table question;

drop table token;

drop table users;

drop table user_event_stats;

drop table user_stats;

SET FOREIGN_KEY_CHECKS=1;

