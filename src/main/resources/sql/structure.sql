create table if not exists player (
    uuid char(36) not null
    , latest_name varchar(20) not null
    , first_seen bigint(20) not null
    , last_seen bigint(20) not null
    , constraint pk_player primary key (uuid)
);

create table if not exists home (
    name varchar(25) not null
    , owner_uuid char(36) not null
    , world varchar(50) not null
    , x double not null
    , y double not null
    , z double not null
    , pitch float not null
    , yaw float not null
    , created bigint(20) not null
    , constraint pk_home primary key (owner_uuid, name)
    , constraint fk_home_player foreign key (owner_uuid)
                                references player(uuid)
);

create table if not exists warp (
    name varchar(25) not null
    , owner_uuid char(36) not null
    , world varchar(50) not null
    , x double not null
    , y double not null
    , z double not null
    , pitch float not null
    , yaw float not null
    , created bigint(20) not null
    , uses int not null default 0
    , constraint pk_warp primary key (name)
    , constraint fk_warp_player foreign key (owner_uuid)
                                references player(uuid)
);

create table if not exists spawn (
    world varchar(50) not null
    , x double not null
    , y double not null
    , z double not null
    , pitch float not null
    , yaw float not null
    , constraint pk_spawn primary key (world)
);
