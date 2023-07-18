package ru.larionov.smarthomeserver.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "name")
    private String name;

    @Column(name = "telegram_id")
    private Long telegramId;

    @Column(name = "chat_id")
    private Long chatId;

    @Column(name = "need_change_password")
    private boolean needChangePassword;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "name")
    private Set<Role> roles;

    @OneToMany(mappedBy = "owner")
    private List<AccessCard> cards;

    public void addRole(Role role) {
        if (this.roles == null) roles = new HashSet<>();

        roles.add(role);
    }

    public void delRole(Role role) {
        roles.remove(role);
    }
}
