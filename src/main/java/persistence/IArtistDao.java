package persistence;

import model.Artist;

import java.util.ArrayList;

public interface IArtistDao {

    public ArrayList<Artist> getAllArtist();

    public ArrayList<Artist> searchForArtistByArtistName(String artistName);

    public String getArtistNameById(int id);

}
