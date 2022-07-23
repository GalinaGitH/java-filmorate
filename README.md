# java-filmorate
Template repository for Filmorate project.
https://miro.com/welcomeonboard/RDBQZDVPSXVmVFc2Y25lTkY3SXViUGxqa3FhQ3Z5WFRQZ0ZTOUw5UEx1Y0VTQTQ4dTNPVFBZYmxhZDlzdG5HYnwzNDU4NzY0NTI5OTE4MTAwNTE1?share_link_id=255020499630

примеры SQL запросов:

получение всех фильмов
SELECT * FROM films

получение всех пользователей
SELECT * FROM users

топ N наиболее популярных фильмов
SELECT film.name,
COUNT (likes.user_id)
FROM films
LEFT JOIN likes ON films.film_id = likes.film_id
ORDER BY COUNT (likes.user_id) DESC
LIMIT N;

список общих друзей с другим пользователем
Пользователь 1 - user_id = '1'
Пользователь 1 - user_id = '2'

SELECT u.user_name
FROM friends AS fr1
LEFT JOIN friends AS fr2 ON fr1.friend_id=fr2.friend_id
LEFT JOIN users AS u ON fr1.friend_id=u.user_id
WHERE fr1.user_id = '1' AND fr.2user_id = '2';




