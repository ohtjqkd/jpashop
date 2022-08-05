package jpabook.jpashop.repository.query;

import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {
    private final EntityManager em;


    public List<OrderQueryDto> findOrderQueryDto() {
        List<OrderQueryDto> result = findOrders();

        result.forEach(o -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
            o.setOrderItems(orderItems);
        });
        return result;
    }

    private List<OrderQueryDto> findOrders() {
        return em.createQuery(
                        "select new jpabook.jpashop.repository.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address) from Order o" +
                                " join o.member m" +
                                " join o.delivery d", OrderQueryDto.class)
                .getResultList();
    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery(
                "select new jpabook.jpashop.repository.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                        " from OrderItem oi" +
                        " join oi.item i" +
                        " where oi.order.id = :orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    public List<OrderQueryDto> findOrderQueryDtoWithCollection() {
        List<OrderQueryDto> result = findOrders();

        Map<Long, List<OrderItemQueryDto>> orderItemQueryDtoMap = findOrderItemMap(toOrderIds(result));
        result.forEach(o -> o.setOrderItems(orderItemQueryDtoMap.get(o.getOrderId())));
        return result;

    }

    private Map<Long, List<OrderItemQueryDto>> findOrderItemMap(List<Long> orderIds) {
        Map<Long, List<OrderItemQueryDto>> orderItemQueryDtoMap = em.createQuery(
                        "select new jpabook.jpashop.repository.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                                " from OrderItem oi" +
                                " join oi.item i" +
                                " where oi.order.id in :orderIds", OrderItemQueryDto.class)
                .setParameter("orderIds", orderIds)
                .getResultList()
                .stream()
                .collect(Collectors.groupingBy(o -> o.getOrderId()));
        return orderItemQueryDtoMap;
    }

    private static List<Long> toOrderIds(List<OrderQueryDto> result) {
        List<Long> orderIds = result.stream()
                .map(o -> o.getOrderId())
                .collect(Collectors.toList());
        return orderIds;
    }

    public List<OrderFlatDto> findAllByDtoFlat() {
        return em.createQuery("select new jpabook.jpashop.repository.query.OrderFlatDto(o.id, o.member.name, o.orderDate, o.status, o.delivery.address, oi.item.name, oi.orderPrice, oi.count)" +
                " from Order o join o.member m join o.delivery d join o.orderItems oi join oi.item i", OrderFlatDto.class)
                .getResultList();
    }
}
