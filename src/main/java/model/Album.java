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
public class Album {
    private int album_id;
    private String album_name;
    private int artist_id;
    private String description;
    private LocalDate date_of_release;
}
