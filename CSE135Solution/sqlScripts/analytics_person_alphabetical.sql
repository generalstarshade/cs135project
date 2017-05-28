WITH proSales AS (WITH pro AS (
   SELECT * 
   FROM product
   ORDER BY product_name
   OFFSET 10 * 1
   FETCH NEXT 10 ROWS ONLY
)
	SELECT p.id AS pid, p.person_name AS person_name, pro.id AS proid, pro.product_name AS product_name, 
    SUM(pic.quantity * pic.price) AS sales
	FROM products_in_cart pic
	JOIN pro
	ON pic.product_id = pro.id, shopping_cart s, person p
	WHERE pic.cart_id = s.id
	AND s.is_purchased = true AND s.person_id = p.id
	GROUP BY p.id, proid, pro.product_name
    ORDER BY p.person_name, pro.product_name
	)

SELECT per.id AS cid, per.person_name, pp.id as pid, pp.product_name, coalesce(proSales.sales, 0) AS total
FROM (SELECT * 
   FROM product
   ORDER BY product_name
   OFFSET 10 * 1
   FETCH NEXT 10 ROWS ONLY) pp cross join person per
LEFT OUTER JOIN proSales
ON (pp.id = proSales.proid AND per.id = proSales.pid) 
WHERE per.role_id = 2
ORDER BY per.person_name, pp.product_name
OFFSET 200 * 1 
FETCH NEXT 200 ROWS ONLY