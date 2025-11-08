package model;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Artist {

    private int artist_id;
    private String artist_name;
    private String genre;
    private String overview;
    private LocalDate date_of_birth;
}
