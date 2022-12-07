# java-filmorate
Template repository for Filmorate project.
![img.png](img.png)


SELECT u.name,
FROM User AS u


SELECT f.name,
FROM Film AS f


SELECT f.name,
FROM Film AS f
INNER JOIN Likes AS l ON f.id = l.film_id
ORDER BY COUNT(l.user_id) ASC
LIMIT 5; 

