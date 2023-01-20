package com.driver.services;

import com.driver.models.Author;
import com.driver.models.Book;
import com.driver.repositories.AuthorRepository;
import com.driver.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookService {


    @Autowired
    BookRepository bookRepository2;

    @Autowired
    AuthorRepository ar;

    public void createBook(Book book){
//        //get author using author id ,
//        //jab hum book ka json likhenge tab hume hi batana padega ki yeh kis author ki hain
//        //by giving author id in json
//        int authorId = book.getAuthor().getId();
//        boolean checkAuthor = ar.existsById(authorId);
//        //get the author using author id from author table
//        if(checkAuthor == false) return;
//        Author a = ar.findById(authorId).get();
//        //now set this author list of bookswritten
//        List<Book> books = a.getBooksWritten();
//        books.add(book);
//        a.setBooksWritten(books);
//        book.setAuthor(a);
//        ar.save(a); //parent(author) is saved therefore child(book) will automatically save
        ////////////////////////////////////////////////////////////////////
        bookRepository2.save(book); // agar author id doge toh table me aa jaega nhi toh null rahega
        //aur yehi karna tha otherwise error will come in junit
        // author id dena compulsory nhi hain
    }

    public List<Book> getBooks(String genre, boolean available, String author){
        List<Book> books = null; //find the elements of the list by yourself

        books.addAll(bookRepository2.findBooksByAuthor(author,available));
        books.addAll(bookRepository2.findBooksByGenre(genre,available));
        books.addAll(bookRepository2.findBooksByGenreAuthor(genre, author, available));
        books.addAll(bookRepository2.findByAvailability(available));


        return books;
//        if(genre != null && author != null)
//        {
//            books = bookRepository2.findBooksByGenreAuthor(genre , author , available);
//        }
//        else if(genre != null)
//        {
//            books = bookRepository2.findBooksByGenre(genre , available);
//        }
//        else if(author != null)
//        {
//            //it means i want to find book only by author name
//            //i dont care if book is of what genre or is avilable also i dont care
//            books = bookRepository2.findBooksByAuthor(author , available);
//        }
//        else {
//            // just find book by its availability
//            books = bookRepository2.findByAvailability(available);
//        }
//        return books;
    }
}