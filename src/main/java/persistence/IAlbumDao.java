package persistence;

import model.Album;

import java.util.ArrayList;

public interface IAlbumDao {

    public ArrayList<Album> getAllAlbums();

    public ArrayList<Album> searchForAlbumByAlbumName(String albumName);

    public String getAlbumNameById(int id);

    public Album searchForAlbumWithAlbumId(int albumId);
    public boolean checkIfAlbumExistWithId(int album_id);
}
