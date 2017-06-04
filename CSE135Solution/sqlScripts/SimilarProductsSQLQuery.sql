-- Table: proSales
WITH proSales AS (SELECT p.id AS pid, pro.id AS proid, SUM(pic.quantity * pic.price) AS sales
FROM products_in_cart pic
JOIN product pro
ON pic.product_id = pro.id, shopping_cart s, person p
WHERE pic.cart_id = s.id
AND s.is_purchased = true AND s.person_id = p.id
GROUP BY p.id, proid),

-- Table: prodVecs
prodVecs AS (SELECT ROW_NUMBER() OVER(ORDER BY pp.product_name DESC) as theid,
per.id AS cid, per.person_name, pp.id as pid, pp.product_name, coalesce(proSales.sales, 0) AS total
FROM product pp cross join person per
LEFT OUTER JOIN proSales
ON (pp.id = proSales.proid AND per.id = proSales.pid) 
WHERE per.role_id = 2),

tempProd AS (SELECT pid, SUM(total) AS thesum
 FROM prodVecs
 GROUP BY pid),

-- Table: crossed
crossed AS (SELECT a.person_name AS person_a, a.product_name AS product_a, a.total AS total_a,
b.person_name AS person_b, b.product_name AS product_b, b.total AS total_b, (a.total * b.total) AS toadd,
sales1_total.thesum AS prod_a_sales, sales2_total.thesum AS prod_b_sales
FROM prodVecs a, prodVecs b,
tempProd sales1_total,
tempProd sales2_total
 
WHERE a.cid = b.cid AND a.pid != b.pid
AND a.theid < b.theid AND a.total != 0 AND b.total != 0
AND sales1_total.pid = a.pid AND sales2_total.pid = b.pid)

-- Final Result
SELECT product_a, product_b, (SUM(toadd)/(prod_a_sales * prod_b_sales)) AS cosine, prod_a_sales, prod_b_sales
FROM crossed
GROUP BY product_a, product_b, prod_a_sales, prod_b_sales
ORDER BY cosine DESC
LIMIT 100