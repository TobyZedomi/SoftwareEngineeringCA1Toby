package model;

import java.util.Comparator;

public class ArtistNameComparator implements Comparator<Artist> {


    @Override
    public int compare(Artist o1, Artist o2) {


        return String.CASE_INSENSITIVE_ORDER.compare(o1.getArtist_name(), o2.getArtist_name());
    }
}
