package com.driver.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.awt.print.Book;
import java.util.Date;
import java.util.List;

@Entity
@Setter
@Getter
@AllArgsConstructor
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(mappedBy = "card", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("card")
    private Student student;

    @CreationTimestamp
    private Date createdOn;

    @UpdateTimestamp
    private Date updatedOn;

    @Enumerated(value = EnumType.STRING)
    private CardStatus cardStatus;

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("card")
    private List<Book> books;

    public Card(){
        this.cardStatus = CardStatus.ACTIVATED;
    }
}
