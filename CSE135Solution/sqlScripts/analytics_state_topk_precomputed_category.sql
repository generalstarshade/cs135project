WITH top_state as
(select state_id, sum(amount) as dollar from (
	select state_id, amount from precomputed_2
	UNION ALL
	select id as state_id, 0.0 as amount from state
	) as state_union
 group by state_id order by dollar desc limit 50
),
top_n_state as 
(select row_number() over(order by dollar desc) as state_order, state_id, dollar from top_state
),
top_prod as 
(select product_id, sum(amount) as dollar from (
	select product_id, amount from precomputed_2
	UNION ALL
	select id as product_id, 0.0 as amount from product
	) as product_union
group by product_id order by dollar desc limit 50
),
top_n_prod as 
(select row_number() over(order by dollar desc) as product_order, product_id, dollar from top_prod
)
select ts.state_id, s.state_name as name, tp.product_id as pid, pr.product_name, COALESCE(ot.amount, 0.0) as total, ts.dollar as stotal, tp.dollar as ptotal
	from top_n_prod tp CROSS JOIN top_n_state ts 
	LEFT OUTER JOIN precomputed_2 ot 
	ON ( tp.product_id = ot.product_id and ts.state_id = ot.state_id)
	inner join state s ON ts.state_id = s.id
	inner join product pr ON (tp.product_id = pr.id AND pr.category_id = ?)
	order by ts.state_order, tp.product_order