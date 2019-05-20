package com.movies.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.movies.api.model.Comment;
import com.movies.api.model.Movie;
import com.movies.api.model.MovieHub;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.function.BinaryOperator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/movieHub")
public class MovieApiController implements MovieApi{

    public static final String MOVIE_BASE_URI = "/movie/";

    private MovieHub movieHub;

    @Override
    @GetMapping(value = "/allMovies")
    public ResponseEntity<MovieHub> getAllMovies() {

        return populateMovieList();
    }

    @Override
    @GetMapping(value = "/mostComments")
    public ResponseEntity<String> getMostCommentedUser() {

        List<String> users = new ArrayList<>();

        List<Movie> movies = movieHub.getMovies();
        for (Movie movie : movies) {

            List<Comment> comments = movie.getComments();
            for(Comment comment : comments) {

                users.add(comment.getUser());
            }
        }

        String mostCommentedUser = users.stream()
            .reduce(BinaryOperator.maxBy(
                Comparator.comparingInt(o -> Collections.frequency(users, o)))).orElse(null);

        return ResponseEntity.ok().body(mostCommentedUser);
    }

    @Override
    @GetMapping(value = "/mostLikes")
    public ResponseEntity<String> getMostLikedMovie() {

        String mostLikedMovie = "No movie was liked";
        int mostLikes = 0;

        List<Movie> movies = movieHub.getMovies();
        for (Movie movie : movies) {

            if (movie.getLikes() > mostLikes) {
                mostLikedMovie = movie.getTitle();
                mostLikes = movie.getLikes();
            }
        }
        return ResponseEntity.ok().body(mostLikedMovie);
    }

    @Override
    @PostMapping(value = "/populateMovieList")
    public ResponseEntity<MovieHub> populateMovieList() {

        ObjectMapper mapper = new ObjectMapper();
        try {
            File movieFile = new File("./src/main/resources/movies.json");
            MovieHub mapperMovieHub = mapper.readValue(movieFile, MovieHub.class);

            if (mapperMovieHub == null) {
                return ResponseEntity.notFound().build();
            }
            else {
                //Set movie hub for future use,
                setMovieHub(mapperMovieHub);

                //Correct comment date from epoch for display purposes.
                List<Movie> movies = mapperMovieHub.getMovies();
                for (Movie movie : movies) {

                    List<Comment> comments = movie.getComments();
                    for (Comment comment : comments) {

                        Instant instant = Instant.ofEpochSecond(Long.parseLong(comment.getDateCreated()));
                        Date instantDate = Date.from(instant);
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                        String formattedDate = formatter.format(instantDate);

                        comment.setDateCreated(formattedDate);
                    }
                }
                return ResponseEntity.ok().body(mapperMovieHub);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().build();
    }

    public void setMovieHub(final MovieHub movieHub) {
        this.movieHub = movieHub;
    }
}
