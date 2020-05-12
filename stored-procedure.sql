DROP PROCEDURE IF EXISTS add_movie;
DELIMITER $$
CREATE PROCEDURE add_movie (
	IN title varchar(100),
    IN year INT,
    IN director varchar(100),
    IN starName varchar(100),
	IN genreName varchar(32)
    )
BEGIN
    DECLARE movieId varchar(10);
    DECLARE starId varchar(10);
    DECLARE genreId varchar(10);
    
	IF (NOT EXISTS(SELECT * from movies as m where m.title = title and m.year = year and m.director = director)) THEN
        -- new movie id stored in tempId
        select concat("tt", lpad((substring(max(id),3)) + 1, 7, 0)) as id from movies into movieId; -- need this for sm and gm
        -- inserting new movie
        INSERT INTO movies VALUES(movieId, title, year, director);
        
        -- stars
        IF (NOT EXISTS(SELECT * from stars as s where s.name = starName)) THEN
			select concat("nm", lpad((substring(max(id),3)) + 1, 7, 0)) as id from stars into starId; -- generating id
            INSERT INTO stars (id, name) VALUES(starId, starName); -- inserting star that doesn't exist
		ELSE
			SET starId = (SELECT id from stars as s where s.name = starName); -- set starId to existing star with name starName
        END IF;
        
        -- genres
        IF (NOT EXISTS(SELECT * from genres as g where g.name = genreName)) THEN
			select max(id) + 1 from genres into genreId; -- new genreId
            INSERT INTO genres VALUES(genreId, genreName);
		ELSE
			SET genreId = (SELECT id from genres as g where g.name = genreName);
        END IF;
        
         -- add stars in movies
         INSERT INTO stars_in_movies VALUES(starId, movieId);
         INSERT INTO genres_in_movies VALUES(genreId, movieId);
         -- add genres in movies
    END IF;
    
    SELECT movieId, starId, genreId;
    -- return ids
END
$$
-- Change back DELIMITER to ;
DELIMITER ;
