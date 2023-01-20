package com.driver.services;

import com.driver.models.Student;
import com.driver.models.Card;
import com.driver.models.CardStatus;
import com.driver.repositories.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CardService {


    @Autowired
    CardRepository cardRepository3;

    public Card createAndReturn(Student student){
        //Card card = null;
        Card c = new Card();
        student.setCard(c);
        c.setStudent(student); // yeh nhi kiya toh table pe pharak nhi padega lekin junit me failed test case aaya
        cardRepository3.save(c);
        //link student with a new card
        return c;
    }

    public void deactivateCard(int student_id){
        cardRepository3.deactivateCard(student_id, CardStatus.DEACTIVATED.toString());
    }
}