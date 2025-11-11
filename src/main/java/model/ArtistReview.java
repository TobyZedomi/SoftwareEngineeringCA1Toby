package model;


import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArtistReview extends Review{

    public int artist_id;

    private int numberOfConcerts;

    public ArtistReview(int review_id, String username, double rating, String comment, String review_type, int artist_id, int numberOfConcerts) {
        super(review_id, username, rating, comment, review_type);
        this.artist_id = artist_id;
        this.numberOfConcerts = numberOfConcerts;
    }

}
