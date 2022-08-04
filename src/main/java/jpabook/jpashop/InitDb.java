package jpabook.jpashop;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.service.MemberService;
import jpabook.jpashop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

@Component
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.dbInit();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {
        private final EntityManager em;

        private Member createMember(String name, String city, String street, String zipcode) {
            Member member = new Member();
            member.setName(name);
            member.setAddress(new Address(city, street, zipcode));
            return member;
        }

        private Book createBook(String name, int price, int stockQuantity) {
            Book book = new Book();
            book.setName(name);
            book.setPrice(price);
            book.setStockQuantity(stockQuantity);
            return book;
        }

        private Order createOrder(Member member, OrderItem... orderItems) {
            Delivery delivery = new Delivery();
            delivery.setAddress(member.getAddress());
            return Order.createOrder(member, delivery, orderItems);
        }
        public void dbInit() {
            Member member1 = createMember("userA", "Busan", "111", "111111");
            em.persist(member1);

            Member member2 = createMember("userB", "Seoul", "112", "1111123123");
            em.persist(member2);

            Book book1 = createBook("JPA1 BOOK", 10000, 100);
            em.persist(book1);

            Book book2 = createBook("JPA2 BOOK", 20000, 100);
            em.persist(book2);

            Book book3 = createBook("SPRING1 BOOK", 15000, 100);
            em.persist(book3);

            Book book4 = createBook("SPRING2 BOOK", 25000, 1000);
            em.persist(book4);


            Order order1 = createOrder(member1,
                    OrderItem.createOrderItem(book1, 10000, 1),
                    OrderItem.createOrderItem(book2, 20000, 1)) ;
            em.persist(order1);

            Order order2 = createOrder(member2,
                    OrderItem.createOrderItem(book1, book1.getPrice(), 1),
                    OrderItem.createOrderItem(book3, book3.getPrice(), 2));
            em.persist(order2);
        }

    }
}

