package com.example.demo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class UserResource {

    @Autowired
    private UserDaoService service;

    @GetMapping("/allUsers")
    public List<User> retrieveAllUsers(){
        return service.findAll();
    }

    @GetMapping(value = "/user/{id}")
    public Resource<User> retrieveUser(@PathVariable int id){
        User user =  service.findOne(id);
        if (user==null)
            throw new UserNotFoundException("id-" + id);
//            return user;
        Resource <User> resource  = new Resource<User>(user);
        ControllerLinkBuilder linkTo =
                linkTo(methodOn(this.getClass()).retrieveAllUsers());
        resource.add(linkTo.withRel("all-users"));

        return resource;
    }

    @PostMapping("/users")
    public ResponseEntity addUser(@Valid@RequestBody User user){
        User saveUser = service.save(user);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}").buildAndExpand(saveUser.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @DeleteMapping("/users/{id}")
    public void deleteUster(@PathVariable int id){
        User user = service.deleteOne(id);
        if (user==null){
            throw new UserNotFoundException("id-"+id);
        }
    }
}
