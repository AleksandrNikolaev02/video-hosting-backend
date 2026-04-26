package com.example.business.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    private Long id;
    @Embedded
    private UserProfile profile;
    @OneToOne(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private Channel channel;
    @OneToMany(mappedBy = "creator")
    private Collection<Video> videos = new ArrayList<>();
    @OneToMany(mappedBy = "creator")
    @Fetch(value = FetchMode.SUBSELECT)
    private List<RequestChannel> requests = new ArrayList<>();
    @OneToMany(mappedBy = "subscriber", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<Subscription> subscriptions = new ArrayList<>();
}
