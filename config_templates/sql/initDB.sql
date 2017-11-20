drop table if exists user_group;
drop table if exists groups;
drop table if exists projects;
drop table if exists users;
drop table if exists cities;

drop sequence if exists user_seq;

drop type if exists user_flag;
drop type if exists group_type;

create type user_flag as enum ('active', 'deleted', 'superuser');
create type group_type as enum ('REGISTERING', 'CURRENT', 'FINISHED');

create sequence user_seq start 100000;

create table cities (
  id   text primary key,
  name text not null
);

create table users (
  id        integer primary key default nextval('user_seq'),
  full_name text      not null,
  email     text      not null,
  flag      user_flag not null,
  city_id   text references cities (id) on delete set null
);

create unique index email_idx
  on users (email);

create table projects (
  name        text primary key,
  description text not null
);

create table groups (
  name         text primary key,
  gr_type      group_type not null,
  project_name text       not null references projects (name) on delete cascade
);

create table user_group (
  user_id    integer not null references users (id) on delete cascade,
  group_name text    not null references groups (name) on delete cascade,
  constraint user_group_pkey primary key (user_id, group_name)
);
