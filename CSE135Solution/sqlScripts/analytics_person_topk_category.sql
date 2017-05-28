-- TABLE: pro
SELECT prod.id, prod.product_name, SUM(pic.quantity * pic.price) AS ptotal
INTO TEMP pro
FROM product prod, products_in_cart pic, shopping_cart s
WHERE pic.product_id = prod.id
AND s.is_purchased = true AND pic.cart_id = s.id AND prod.category_id = ?
GROUP BY prod.id, prod.product_name
OFFSET 10 * 0
FETCH NEXT 10 ROWS ONLY;

-- TABLE: proSales
SELECT p.id AS pid, p.person_name AS person_name, pro.id AS proid, pro.product_name AS product_name, 
SUM(pic.quantity * pic.price) AS sales, coalesce(pro.ptotal, 0) AS ptotal
INTO TEMP proSales
FROM products_in_cart pic
JOIN pro
ON pic.product_id = pro.id, shopping_cart s, person p
WHERE pic.cart_id = s.id
AND s.is_purchased = true AND s.person_id = p.id
GROUP BY p.id, proid, pro.product_name, pro.ptotal;

-- Table: allSales
SELECT per.id AS cid, per.person_name, pp.id as pid, pp.product_name, coalesce(proSales.sales, 0) AS total,
pp.ptotal
INTO TEMP allSales
FROM pro pp CROSS JOIN person per
LEFT OUTER JOIN proSales
ON (pp.id = proSales.proid AND per.id = proSales.pid) 
WHERE per.role_id = 2
OFFSET 200 * 0 
FETCH NEXT 200 ROWS ONLY;
    
-- Table: ordered
SELECT cid, SUM(total) AS sumTotal
INTO TEMP ordered
FROM allSales
GROUP BY cid;

-- Final Result
SELECT o.cid, allSales.person_name, allSales.pid, allSales.product_name, allSales.total, allSales.ptotal, o.sumTotal AS ctotal
FROM ordered o
LEFT OUTER JOIN allSales
ON (o.cid = allSales.cid)
ORDER BY ctotal DESC, ptotal DESC;