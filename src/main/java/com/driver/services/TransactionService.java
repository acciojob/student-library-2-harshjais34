package com.driver.services;

import com.driver.models.Book;
import com.driver.models.Card;
import com.driver.models.Transaction;
import com.driver.models.TransactionStatus;
import com.driver.repositories.BookRepository;
import com.driver.repositories.CardRepository;
import com.driver.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class TransactionService {

    @Autowired
    BookRepository bookRepository5;

    @Autowired
    CardRepository cardRepository5;

    @Autowired
    TransactionRepository transactionRepository5;

    @Value("${books.max_allowed}")
    public int max_allowed_books;

    @Value("${books.max_allowed_days}")
    public int getMax_allowed_days;

    @Value("${books.fine.per_day}")
    public int fine_per_day;

    public String issueBook(int cardId, int bookId) throws Exception {
        //check whether bookId and cardId already exist
        boolean checkBook = bookRepository5.existsById(bookId);
        boolean checkCard = cardRepository5.existsById(cardId);
        // System.out.println(checkBook);
        Book emptyBook = new Book();
        Book b = bookRepository5.findById(bookId).orElse(emptyBook);
        Card emptyCard = new Card();
        Card c = cardRepository5.findById(cardId).orElse(emptyCard);
        Transaction t = new Transaction();
        t.setTransactionStatus(TransactionStatus.SUCCESSFUL);
        //conditions required for successful transaction of issue book:
        //1. book is present and available

        try{
            if( checkBook == false || b.isAvailable() == false)
            {
                t.setTransactionStatus(TransactionStatus.FAILED);
                throw new Exception("Book is either unavailable or not present");
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
        }

        // If it fails: throw new Exception("Book is either unavailable or not present");
        //2. card is present and activated
        // If it fails: throw new Exception("Card is invalid");

        try{
            if(checkCard == false || c.getCardStatus().toString().equals("DEACTIVATED"))
            {
                t.setTransactionStatus(TransactionStatus.FAILED);
                throw new Exception("Card is invalid");
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }

        //3. number of books issued against the card is strictly less than max_allowed_books
        // If it fails: throw new Exception("Book limit has reached for this card");

        try{
            if(c.getBooks().size() >= max_allowed_books)
            {
                t.setTransactionStatus(TransactionStatus.FAILED);
                throw new Exception("Book limit has reached for this card");
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        //If the transaction is successful, save the transaction to the list of transactions and return the id

        //Note that the error message should match exactly in all cases
        // Transaction t = new Transaction();
        if(checkCard == true) t.setCard(c);
        else t.setCard(null);
        if(checkBook == true) t.setBook(b);
        else t.setBook(null);
        if(t.getTransactionStatus().toString().equals("SUCCESSFUL")) {
            t.setIssueOperation(true);
            //t.setTransactionStatus(TransactionStatus.SUCCESSFUL);

            List<java.awt.print.Book> books = c.getBooks();
            books.add(b);
            c.setBooks(books);
            cardRepository5.save(c);

            List<Transaction> transactionList = b.getTransactions();
            transactionList.add(t);
            b.setTransactions(transactionList);
            b.setAvailable(false);
            b.setCard(c); // ab book table me jab book issue ho jaegi toh cardid show ho jaega in book table
            bookRepository5.save(b);// naya book nhi add hoga sirf update hoga, transaction(child) apne aap
            // save ho jaega kyuki parent(book) is saved
        }
        else {
            transactionRepository5.save(t);
        }
        return t.getTransactionId();
        //return null; //return transactionId instead
    }

    public Transaction returnBook(int cardId, int bookId) throws Exception{

//        List<Transaction> transactions = transactionRepository5.find(cardId, bookId, TransactionStatus.SUCCESSFUL, true);
//        Transaction transaction = transactions.get(transactions.size() - 1);
//
//        if(transactions.size() == 0) return transaction;
//
//        //for the given transaction calculate the fine amount considering the book has been returned exactly when this function is called
//        Date issueDate = transaction.getTransactionDate();
//        long miliSecondsPassed = issueDate.getTime();
//        int daysPassed = (int) ((System.currentTimeMillis() - miliSecondsPassed) / (1000 * 60 * 60 * 24));
//
//        int fine =0;
//        if(daysPassed > getMax_allowed_days)
//        {
//            fine = (daysPassed - getMax_allowed_days) * fine_per_day;
//        }
//        //make the book available for other users
//        Book b = bookRepository5.findById(bookId).get();
//        b.setAvailable(true);
//        b.setCard(null);// link between card and book is removed
//        bookRepository5.save(b);
//        //make a new transaction for return book which contains the fine amount as well
//
//        //Transaction returnBookTransaction  = null;
//        Card c = cardRepository5.findById(cardId).get();
//        Transaction returnBookTransaction = new Transaction();
//        returnBookTransaction.setFineAmount(fine);
//        returnBookTransaction.setCard(c); // transaction table me card id link ho jaegi
//        returnBookTransaction.setBook(b); // book id aa jaegi
//        returnBookTransaction.setTransactionStatus(TransactionStatus.SUCCESSFUL);
//
//        List<Transaction> transactionList = b.getTransactions();
//        transactionList.add(returnBookTransaction);
//        b.setTransactions(transactionList);
//
//        // remove the book from card which is present in a list
//        List<Book> books = c.getBooks();
//        for(int i = 0; i<books.size(); i++)
//        {
//            if(books.get(i).getId() == bookId) books.remove(i);
//        }
//        cardRepository5.save(c);
//
//
//        return returnBookTransaction; //return the transaction after updating all details

        List<Transaction> transactions = transactionRepository5.find(cardId, bookId,TransactionStatus.SUCCESSFUL, true);

        Transaction transaction = transactions.get(transactions.size() - 1);

        Date issueDate = transaction.getTransactionDate();

        long timeIssuetime = Math.abs(System.currentTimeMillis() - issueDate.getTime());

        long no_of_days_passed = TimeUnit.DAYS.convert(timeIssuetime, TimeUnit.MILLISECONDS);

        int fine = 0;
        if(no_of_days_passed > getMax_allowed_days){
            fine = (int)((no_of_days_passed - getMax_allowed_days) * fine_per_day);
        }

        Book book = transaction.getBook();

        book.setAvailable(true);
        book.setCard(null);

        bookRepository5.updateBook(book);

        Transaction tr = new Transaction();
        tr.setBook(transaction.getBook());
        tr.setCard(transaction.getCard());
        tr.setIssueOperation(false);
        tr.setFineAmount(fine);
        tr.setTransactionStatus(TransactionStatus.SUCCESSFUL);

        transactionRepository5.save(tr);

        return tr;
    }
}