package model;


import lombok.*;
import observer.Observer;

import java.util.ArrayList;

@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlbumReview extends Review {
    public int album_id;

    public AlbumReview(int review_id, String username, double rating, String comment, String review_type, int album_id) {
        super(review_id, username, rating, comment, review_type);
        this.album_id = album_id;
    }

}
