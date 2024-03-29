package ast20201.project.service;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ast20201.project.exception.InsufficientCreditsException;
import ast20201.project.exception.InsufficientStockException;
import ast20201.project.exception.OrderCancelledException;
import ast20201.project.exception.OrderPaidException;
import ast20201.project.model.Order;
import ast20201.project.model.OrderProduct;
import ast20201.project.model.PageData;
import ast20201.project.model.Product;
import ast20201.project.repository.OrderRepository;
import ast20201.project.repository.ProductRepository;
import ast20201.project.repository.UserRepository;

@Service
@Transactional(rollbackFor = Exception.class)
public class OrderService {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    UserRepository userRepository;

    public PageData<Order> getOrders(String query, int page) {
        return orderRepository.getOrders(query, page);
    }

	public Order getOrder(long id) {
		return orderRepository.getOrder(id);
	}

    public Order getOrder(long userId, long orderId) {
        return orderRepository.getOrder(userId, orderId);
    }

    public PageData<Order> getOrders(long userId, int page) {
        return orderRepository.getOrders(userId, page);
    }

    public Order createOrder(long userId, Order order) throws InsufficientStockException {
        if (order.getOrderProducts().size() == 0)
            return null;
        List<OrderProduct> orderProducts = order.getOrderProducts();
        for (OrderProduct orderProduct : orderProducts) {
            long product = orderProduct.getId();
            Product dbproduct = productRepository.getProduct(product);
            if (dbproduct.getQuantity() != null && orderProduct.getAmount() > dbproduct.getQuantity()) {
            	throw new InsufficientStockException(
            			"Insufficient stock! Please adjust the amount below the stock level", dbproduct);
            }
        }
        String message = order.getMessage();
        Order dborder = orderRepository.createOrder(userId, orderProducts, message);
        return dborder;
    }

    public void cancelOrder(long userId, long orderId) {
        orderRepository.cancelOrder(userId, orderId);
    }

	public Order updateOrder(long id, Order order) {
        orderRepository.updateOrder(id, order.getOrderProducts(), order.getOrderStatus(), order.getPaymentStatus(), order.getCreateDate(), order.getMessage(), order.getAdminMessage());
        if (order.getPaymentStatus() == 1) {
        	List<OrderProduct> orderProducts = order.getOrderProducts();
            for (OrderProduct orderProduct : orderProducts) {
                long product = orderProduct.getId();
                int amount = orderProduct.getAmount();
                productRepository.reduceProductQuantity(product, amount);
            }
        }
        return getOrder(id);
	}

	public void confirmOrder(long userId, long orderId) throws InsufficientCreditsException, OrderCancelledException, OrderPaidException, InsufficientStockException {
		List<OrderProduct> orderProducts = orderRepository.getOrderProducts(orderId);
		for (OrderProduct orderProduct : orderProducts) {
            long product = orderProduct.getId();
            Product dbproduct = productRepository.getProduct(product);
            if (dbproduct.getQuantity() != null) {
            	if (orderProduct.getAmount() > dbproduct.getQuantity()) {
            		throw new InsufficientStockException(
            				"Insufficient stock! Please either wait for refill or recreate an order", dbproduct);
            	}
            	else {
            		int amount = orderProduct.getAmount();
            		productRepository.reduceProductQuantity(product, amount);
            	}
            }
        }
		
		BigDecimal totalPrice = orderRepository.getOrderTotalPrice(orderId);
        BigDecimal userCredits = userRepository.getUser(userId).getCredits();
        if (totalPrice.compareTo(userCredits) > 0) {
            throw new InsufficientCreditsException("Insufficient credits. Please recharge the credits.");
        }
        userRepository.reduceCredits(userId, totalPrice);
        
        Order order = getOrder(orderId);
        if (order.getOrderStatus() == -1) {
            throw new OrderCancelledException("Payment has been declined as the order has been cancelled");
        }
        if (order.getPaymentStatus() == 1) {
            throw new OrderPaidException("Payment has been declined as the order has been paid");
        }
        orderRepository.updateOrder(order.getId(), order.getOrderProducts(), order.getOrderStatus(), 1, order.getCreateDate(), order.getMessage(), order.getAdminMessage());
    }
    public BigDecimal getSales(Date start, Date end) {
        return orderRepository.getSales(start, end);
    }

    public int getNumberOfOrders(Date start, Date end) {
        return orderRepository.getNumberOfOrders(start, end);
    }

    public long getTopSellingProduct(Date start, Date end) {
        return orderRepository.getTopSellingProduct(start, end);
    }
}
