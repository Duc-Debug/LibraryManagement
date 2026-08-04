package org.example.librarymanagement.infrastructure.persistence.book;


import org.example.librarymanagement.domain.entity.Book;
import org.example.librarymanagement.port.outbound.managebook.FindBookPort;
import org.example.librarymanagement.port.outbound.managebook.SaveBookPort;
import org.springframework.stereotype.Component;
import java.util.List;


@Component
public class BookPersistenceAdapter 
        implements FindBookPort, SaveBookPort {


    private final BookJpaRepository repository;

    private final BookPersistenceMapper mapper;



    public BookPersistenceAdapter(
            BookJpaRepository repository,
            BookPersistenceMapper mapper
    ) {

        this.repository = repository;
        this.mapper = mapper;
    }



   @Override
   public boolean existsByIsbn(String isbn) {

    return repository.existsByIsbnIgnoreCase(
            isbn.trim()
    );

   }



    @Override
    public Book save(Book book) {


        BookJpaEntity entity =
                mapper.toEntity(book);


        BookJpaEntity saved =
                repository.save(entity);


        return mapper.toDomain(saved);
    }
    @Override
    public List<Book> findAll() {

    return repository.findAll()
            .stream()
            .map(mapper::toDomain)
            .toList();

}
}