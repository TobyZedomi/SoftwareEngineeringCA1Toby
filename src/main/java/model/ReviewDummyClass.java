package model;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDummyClass extends Review {

    private int review_id;
    private String username;
    private double rating;
    private String comment;
    private String review_type;
}
