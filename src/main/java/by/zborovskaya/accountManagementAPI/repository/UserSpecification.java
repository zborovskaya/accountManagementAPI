package by.zborovskaya.accountManagementAPI.repository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import by.zborovskaya.accountManagementAPI.model.User;

import java.time.LocalDate;


public class UserSpecification implements Specification<User> {
    private final String name;
    private final String email;
    private final String phone;
    private final LocalDate dateOfBirth;

    public UserSpecification(String name, String email, String phone, LocalDate dateOfBirth) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
    }

    @Override
    public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        // Список предикатов, которые будут добавлены к запросу
        Predicate predicate = criteriaBuilder.conjunction(); // Начальная конъюнкция (AND)

        // Добавляем условие для имени
        if (name != null && !name.isEmpty()) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(root.get("name"), "%" + name + "%"));
        }

        // Добавляем условие для email
        if (email != null && !email.isEmpty()) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("emails").get("email"), email));
        }

        // Добавляем условие для телефона
        if (phone != null && !phone.isEmpty()) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("phones").get("phone"), phone));
        }

        // Добавляем условие для даты рождения
        if (dateOfBirth != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThan(root.get("dateOfBirth"), dateOfBirth));
        }

        return predicate;
    }
}

