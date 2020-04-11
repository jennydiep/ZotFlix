CREATE DATABASE IF NOT EXISTS moviedb;
USE moviedb;
CREATE TABLE IF NOT EXISTS stars(
		   id varchar(10) not null,
		   name varchar(100) default '',
		   birthYear integer default null,
           primary key (id)
	   );


CREATE TABLE if not exists movies(
		id VARCHAR(10) DEFAULT '',
		title VARCHAR(100) DEFAULT '',
		year INTEGER not null,
		director VARCHAR(100) DEFAULT '',
		PRIMARY KEY (id)
   );

CREATE TABLE IF NOT EXISTS stars_in_movies(
		starId VARCHAR(10) DEFAULT '',
		movieId VARCHAR(10) DEFAULT '',
		FOREIGN KEY (starId) REFERENCES stars(id),
		FOREIGN KEY (movieId) REFERENCES movies(id)
   );
   
CREATE TABLE IF NOT EXISTS genres(
		id int auto_increment not null,
        name varchar(32) DEFAULT '',
        primary key (id)
);

CREATE TABLE IF NOT EXISTS genres_in_movies(
		genreId int not null,
        movieId varchar(10) default '',
        foreign key (genreId) references genres(id),
        foreign key (movieId) references movies(id)
);

CREATE TABLE IF NOT EXISTS creditcards(
	id varchar(20) not null,
    firstName varchar(50) default '',
    lastName varchar(50) default '',
    expiration date not null,
    primary key (id)
);

CREATE TABLE IF NOT EXISTS customers(
	id int auto_increment not null,
    firstName varchar(50) default '',
    lastName varchar(50) default '',
    ccId varchar(20) default '',
    address varchar(200) default '',
    email varchar(50) default '',
    password varchar(20) default '',
    foreign key (ccId) references creditcards(id),
	primary key (id)
);

CREATE TABLE IF NOT EXISTS sales(
	id int auto_increment not null,
    customerId int not null,
    movieId varchar(10) default '',
    saleDate date not null, 
    foreign key (customerId) references customers(id),
    foreign key (movieId) references movies(id),
    primary key (id)
);


CREATE TABLE IF NOT EXISTS ratings(
	movieId varchar(10) default '',
    rating float not null,
    numVotes int not null,
    foreign key (movieId) references movies(id)
);

-- gets top 20 movies sorted by rating
DROP VIEW IF EXISTS top20movies;
CREATE VIEW top20movies as
SELECT id, title, year, director, rating
FROM movies, ratings
WHERE movies.id = ratings.movieId
ORDER by rating DESC LIMIT 20;

-- lists all genres of top 20 movies
DROP VIEW IF EXISTS top20genres;
CREATE VIEW top20genres as
select top20movies.id, title, group_concat( name ) as "genres"
from top20movies, genres_in_movies, genres
where genres_in_movies.movieId = top20movies.id
and genres.id = genres_in_movies.genreId
group by top20movies.id;


-- lists all stars of top 20 movies
DROP VIEW IF EXISTS top20stars;
CREATE VIEW top20stars as
SELECT top20movies.id, title, group_concat( name ) as "stars",
group_concat( stars.id ) as "starsid"
from top20movies, stars_in_movies, stars
where stars_in_movies.movieId = top20movies.id
and stars.id = stars_in_movies.starId
group by top20movies.id;


-- natural joinning all views together to get top20movie list with
-- list of genres and stars
DROP VIEW IF EXISTS top20;
CREATE VIEW top20 as 
select id, title, year, director, genres, stars, rating, starsid from 
top20movies natural join top20genres
natural join top20stars
order by rating DESC;


-- test
-- select * from top20;

-- select * from movies LIMIT 20;


-- select * 
-- from temp join temp2;

-- -- -- group by m.id

-- select *
-- from movies as m
-- where m.id = 'tt0424773';




-- SELECT * FROM movies;
-- SELECT * FROM stars;
-- SELECT * FROM stars_in_movies;
-- SELECT * FROM genres;
-- SELECT * FROM genres_in_movies;
-- SELECT * FROM customers;
-- SELECT * FROM sales;
-- SELECT * FROM creditcards;
-- SELECT * FROM ratings;
