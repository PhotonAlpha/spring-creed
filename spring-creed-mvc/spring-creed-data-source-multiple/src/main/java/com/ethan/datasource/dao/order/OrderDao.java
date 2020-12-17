package com.ethan.datasource.dao.order;

import com.ethan.datasource.model.order.OrderDO;
import org.springframework.data.repository.CrudRepository;

public interface OrderDao extends CrudRepository<OrderDO, Long> {
}
