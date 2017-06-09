WITH stuff AS(SELECT ROW_NUMBER() OVER() AS theid
FROM product p
WHERE category_id = ?)

SELECT COUNT(*) FROM stuff
WHERE theid > 10 * ?;