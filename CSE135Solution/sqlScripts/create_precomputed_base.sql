select pc.product_id,p.product_name, c.state_id,sum(pc.price*pc.quantity) as amount  
	into precomputed_base
 	from products_in_cart pc  
 	inner join shopping_cart sc on (sc.id = pc.cart_id and sc.is_purchased = true)
 	inner join product p on (pc.product_id = p.id) -- add category filter if any
 	inner join person c on (sc.person_id = c.id)
 	group by pc.product_id,p.product_name, c.state_id