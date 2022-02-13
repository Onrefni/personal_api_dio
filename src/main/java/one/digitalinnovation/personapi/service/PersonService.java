package one.digitalinnovation.personapi.service;

import one.digitalinnovation.personapi.dto.MessageResponseDTO;
import one.digitalinnovation.personapi.dto.requestDTO.PersonDTO;
import one.digitalinnovation.personapi.entity.Person;
import one.digitalinnovation.personapi.exception.PersonNotFoudException;
import one.digitalinnovation.personapi.mapper.PersonMapper;
import one.digitalinnovation.personapi.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonService {

    private PersonRepository personRepository;

    private final PersonMapper personMapper = PersonMapper.INSTANCE;

    @Autowired
    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public MessageResponseDTO createPerson(@RequestBody PersonDTO personDTO) {

        Person person = personRepository.save(personMapper.toModel(personDTO));

        return createMessageResponse("Created Person with ID ", person);

    }

    public List<PersonDTO> listAll() {
        List<Person> listAllPerson = personRepository.findAll();

        return listAllPerson.stream().map(personMapper::toDTO)
                .collect(Collectors.toList());
    }

    public PersonDTO findById(Long id) throws PersonNotFoudException {
        Person person = verifyIfExists(id);
        return personMapper.toDTO(person);
    }

    public void delete(Long id) throws PersonNotFoudException {
        verifyIfExists(id);
        personRepository.deleteById(id);
    }

    public MessageResponseDTO updateById(Long id, PersonDTO personDTO) throws PersonNotFoudException {
        Person personVerifier = verifyIfExists(id);
        personMapper.updatePersonFromDto(personDTO, personVerifier);
        Person person = personRepository.save(personVerifier);

        return createMessageResponse("Updated Person with ID ", person);
    }

    private MessageResponseDTO createMessageResponse(String message, Person person) {
        return MessageResponseDTO.builder()
                .message(message + person.getId()).build();
    }

    private Person verifyIfExists(Long id) throws PersonNotFoudException {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new PersonNotFoudException(id));
        return person;
    }

}
