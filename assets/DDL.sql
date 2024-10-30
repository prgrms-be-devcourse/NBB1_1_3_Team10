-- auto-generated definition
-- 유저 테이블
create table user_table
(
    user_id       binary(16)             default (unhex(replace(uuid(), _utf8mb3'-', _utf8mb4''))) not null comment '유저 식별자'
        primary key,
    user_email    varchar(50)                                                                      not null comment '유저 이메일, 로그인 ID 로 이용',
    user_pw       varchar(200)                                                                     not null comment '유저 비밀번호, 인코딩 후 저장',
    user_name     varchar(30)                                                                      not null comment '유저 실명',
    role          enum ('ADMIN', 'USER') default 'USER'                                            not null comment '유저 권한 (관리자, 일반 유저)',
    alias         varchar(50)            default (`user_email`)                                    null comment '유저 활동 닉네임, 초기 설정은 이메일과 동일',
    phone_num     varchar(50)                                                                      null comment '전화번호',
    constraint USER_TABLE_pk_2
        unique (user_email)
)
    comment '유저 테이블';


-- auto-generated definition
-- 캐시된 영화 목록 테이블
create table cached_movie_table
(
    movie_id      varchar(50)      not null comment 'KMDB 영화 ID (알파벳-숫자)'
        primary key,
    title         varchar(100)     not null comment '영화 제목',
    poster_url    varchar(200)     not null comment '영화 포스터 이미지 url',
    genre         varchar(50)      not null comment '영화 장르',
    release_date  varchar(30)      not null comment '영화 개봉일',
    plot          text             not null comment '영화 줄거리',
    running_time  varchar(10)      not null comment '영화 상영시간',
    actors        varchar(180)     not null comment '출연 배우 5명',
    director      varchar(30)      not null comment '감독',
    dib_count     bigint default 0 not null comment '해당 영화가 찜으로 지정된 수',
    review_count  bigint default 0 not null comment '해당 영화에 달린 리뷰 포스팅 수',
    comment_count bigint default 0 not null comment '해당 영화에 달린 한줄평 수',
    sum_of_rating bigint default 0 not null comment '해당 영화 한줄평의 평점 총 합'
)
    comment '캐시된 영화 목록들';


-- auto-generated definition
-- 찜 목록 테이블
create table dib_table
(
    dib_id   bigint auto_increment comment '찜 영화 식별자'
        primary key,
    user_id  binary(16)  not null comment '해당 찜을 소유한 유저 ID',
    movie_id varchar(50) not null comment '영화 API 에 따라 달라질 수 있음',
    constraint DIB_TABLE_user_table_user_id_fk
        foreign key (user_id) references user_table (user_id)
            on update cascade on delete cascade,
    constraint dib_table_cached_movie_table_movie_id_fk
        foreign key (movie_id) references cached_movie_table (movie_id)
            on update cascade on delete cascade
)
    comment '찜 목록 테이블';


-- auto-generated definition
-- 한줄평 테이블
create table comment_table
(
    comment_id bigint auto_increment comment '한줄평 리뷰 식별자'
        primary key,
    content    varchar(50)                        not null comment '간단한 리뷰 내용',
    `like`     int      default 0                 not null comment '리뷰에 달린 좋아요 수',
    dislike    int      default 0                 not null comment '리뷰에 달린 싫어요 수',
    rating     int                                not null comment '영화 평점',
    movie_id   varchar(50)                        not null comment '영화 API 에 따라 달라질 수 있음',
    user_id    binary(16)                         not null comment '리뷰 작성자 ID',
    created_at datetime default CURRENT_TIMESTAMP null comment '리뷰 생성일',
    constraint COMMENT_TABLE_user_table_user_id_fk
        foreign key (user_id) references user_table (user_id)
            on update cascade on delete cascade,
    constraint comment_table_cached_movie_table_movie_id_fk
        foreign key (movie_id) references cached_movie_table (movie_id)
            on update cascade on delete cascade
)
    comment '한줄평 테이블';

-- 한줄평 좋아요 로그 테이블
create table comment_like_table
(
    comment_like_id bigint auto_increment primary key,
    comment_id      bigint not null,
    user_id         binary(16),
    constraint foreign key (comment_id) references comment_table (comment_id),
    constraint foreign key (user_id) references user_table (user_id)
);

-- 한줄평 싫어요 로그 테이블
create table comment_dislike_table
(
    comment_dislike_id bigint auto_increment primary key,
    comment_id         bigint not null,
    user_id            binary(16),
    constraint foreign key (comment_id) references comment_table (comment_id),
    constraint foreign key (user_id) references user_table (user_id)
);


-- auto-generated definition
-- 리뷰 테이블
create table review_table
(
    review_id  bigint auto_increment comment '리뷰 식별자'
        primary key,
    title      varchar(50)                        not null comment '포스팅 제목',
    movie_id   varchar(50)                        not null comment '영화 API 에 따라 달라질 수 있음',
    content longtext not null comment '포스팅 리뷰',
    user_id    binary(16)                         not null comment '리뷰 작성자 ID',
    `like`     int      default 0                 not null comment '좋아요 수',
    created_at datetime default CURRENT_TIMESTAMP not null comment '작성 시간',
    updated_at datetime default CURRENT_TIMESTAMP null comment '수정 시간',
    constraint REVIEW_TABLE_user_table_user_id_fk
        foreign key (user_id) references user_table (user_id)
            on update cascade on delete cascade,
    constraint review_table_cached_movie_table_movie_id_fk
        foreign key (movie_id) references cached_movie_table (movie_id)
            on update cascade on delete cascade
)
    comment '리뷰 테이블';


-- auto-generated definition
-- 리뷰 댓글 테이블
create table review_comment_table
(
    review_comment_id bigint auto_increment
        primary key,
    content           varchar(100)                         not null comment '댓글 내용',
    group_id          bigint                               null comment '일반 댓글 -> 자신의 PK, 대댓글 -> 부모 댓글의 PK',
    comment_ref       bigint                               null comment '@멘션',
    user_id           binary(16)                           not null comment '댓글 작성자',
    review_id         bigint                               not null comment '리뷰 PK',
    created_at        datetime   default CURRENT_TIMESTAMP not null comment '작성 시간',
    is_updated        tinyint(1) default 0                 not null comment '수정 여부',
    `like`            int        default 0                 not null comment '댓글의 좋아요 수',
    constraint REVIEW_COMMENT_TABLE_review_table_review_id_fk
        foreign key (review_id) references review_table (review_id)
            on update cascade on delete cascade,
    constraint REVIEW_COMMENT_TABLE_user_table_user_id_fk
        foreign key (user_id) references user_table (user_id)
            on update cascade on delete cascade
)
    comment '리뷰 댓글 테이블';
