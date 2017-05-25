WITH proSales AS (
	SELECT p.id AS pid, pro.id AS proid, SUM(pic.quantity * pic.price) AS sales
	FROM products_in_cart pic
	JOIN product pro
	ON pic.product_id = pro.id, shopping_cart s, person p
	WHERE pic.cart_id = s.id
	AND s.is_purchased = true AND s.person_id = p.id
	GROUP BY p.id, proid
	)
    
SELECT pp.product_name, per.person_name, proSales.sales
FROM product pp
LEFT OUTER JOIN proSales
ON pp.id = proSales.proid, person per
WHERE per.id = proSales.pid
ORDER BY per.person_name ASC;