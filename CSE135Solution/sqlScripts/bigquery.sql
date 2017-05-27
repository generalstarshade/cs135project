WITH proSales AS (
	SELECT p.id AS pid, pro.id AS proid, SUM(pic.quantity * pic.price) AS sales
	FROM products_in_cart pic
	JOIN product pro
	ON pic.product_id = pro.id, shopping_cart s, person p
	WHERE pic.cart_id = s.id
	AND s.is_purchased = true AND s.person_id = p.id
	GROUP BY p.id, proid
	)

SELECT per.id AS cid, per.person_name, pp.id as pid, pp.product_name, coalesce(proSales.sales, 0) AS total
FROM product pp cross join person per
LEFT OUTER JOIN proSales
ON (pp.id = proSales.proid AND per.id = proSales.pid) 
WHERE per.role_id = 2
ORDER BY per.person_name, pp.product_name