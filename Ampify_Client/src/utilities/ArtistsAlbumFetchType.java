/*
Enum for type of request for fetching albums and artists
*/

package utilities;

public enum ArtistsAlbumFetchType {

    ALL("0"),
    TOP("1"),
    ;

    ArtistsAlbumFetchType(String s) {
        s.toString();
    }
}
