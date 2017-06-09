-- Table: pro
WITH pro AS(SELECT *
FROM product
WHERE category_id = ?
),

-- Table: proSales
proSales AS (SELECT p.id AS pid, pro.id AS proid
FROM products_in_cart pic
JOIN pro
ON pic.product_id = pro.id, shopping_cart s, person p
WHERE pic.cart_id = s.id
AND s.is_purchased = true AND s.person_id = p.id
),

-- Table: stateSales
stateSales AS (SELECT DISTINCT st.id AS sid
FROM pro pp cross join person per
LEFT OUTER JOIN proSales
ON (pp.id = proSales.proid AND per.id = proSales.pid), state st 
WHERE per.role_id = 2 AND per.state_id = st.id),

therows AS (
SELECT ROW_NUMBER() OVER() AS theid
FROM stateSales
)

SELECT COUNT(*) FROM therows
WHERE theid > 20 * ?