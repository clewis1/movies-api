package com.movies.api.controller;

import com.movies.api.model.MovieHub;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public interface MovieApi {

    default ResponseEntity<MovieHub> getAllMovies() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    default ResponseEntity<String> getMostCommentedUser() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    default ResponseEntity<String> getMostLikedMovie() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    default ResponseEntity<MovieHub> populateMovieList() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

}
