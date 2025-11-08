package persistence;

import model.Album;

import java.util.ArrayList;

public interface IAlbumDao {

    public ArrayList<Album> getAllAlbums();

    public ArrayList<Album> searchForAlbumByAlbumName(String albumName);
}
