package serverClasses;

import serverClasses.requests.*;
import services.*;
import utilities.ArtistsAlbumFetchType;
import utilities.ServerRequest;
import utilities.SongFetchType;

import java.io.*;
import java.net.Socket;

public class HandleClientRequest implements Runnable {

    private Socket socket;
    private ObjectInputStream ois;  //Input Stream of client socket
    private ObjectOutputStream oos; //Output Stream of client socket

    public HandleClientRequest(Socket socket) {
        this.socket = socket;
        try {
            ois = new ObjectInputStream(socket.getInputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println("CLIENT IP ADDRESS: " + socket.getInetAddress().getHostAddress());

        while (true) {
            Object object = null;
            try {
                try {
                    object = ois.readObject();
                } catch (EOFException e) {
                    System.out.println("CLIENT DISCONNECTED");
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }

                assert object != null;
                String request = object.toString();

                if (request.equals(String.valueOf(ServerRequest.SIGNUP_REQUEST))) {
                    SignUpRequest signUpRequest = (SignUpRequest) object;
                    oos.writeObject(AmpifyServices.registerUser(signUpRequest));
                    oos.flush();
                }
                if (request.equals(String.valueOf(ServerRequest.LOGIN_REQUEST))) {
                    LoginRequest log = (LoginRequest) object;
                    oos.writeObject(AmpifyServices.userLogin(log));
                    oos.flush();
                }

                if (request.equals(String.valueOf(ServerRequest.LANGUAGE_SHOW))) {
                    LanguageFetchRequest lang = (LanguageFetchRequest) object;
                    oos.writeObject(AmpifyServices.showAllLanguages(lang));
                    oos.flush();
                }

                if (request.equals(String.valueOf(ServerRequest.GENRES_SHOW))) {
                    GenresFetchRequest obj = (GenresFetchRequest) object;
                    oos.writeObject(AmpifyServices.showAllGenres(obj));
                    oos.flush();
                }
                if (request.equals(String.valueOf(ServerRequest.ARTIST_SHOW))) {
                    ArtistFetchRequest art = (ArtistFetchRequest) object;

                    if(art.getType().equals(String.valueOf(ArtistsAlbumFetchType.ALL))){
                        oos.writeObject(AmpifyServices.showAllArtists(art));
                        oos.flush();
                    }else if(art.getType().equals(String.valueOf(ArtistsAlbumFetchType.TOP))){
                        oos.writeObject(AmpifyServices.showTopArtists(art));
                        oos.flush();
                    }

                }

                if (request.equals(String.valueOf(ServerRequest.ALBUM_SHOW))) {
                    AlbumFetchRequest album = (AlbumFetchRequest) object;

                    if(album.getType().equals(String.valueOf(ArtistsAlbumFetchType.TOP))){
                        oos.writeObject(AmpifyServices.showTopAlbums(album));
                        oos.flush();
                    }

                }



                if (request.equals(String.valueOf(ServerRequest.SUBMIT_CHOICES))) {
                    SubmitChoicesRequest submitChoicesRequest = (SubmitChoicesRequest) object;
                    oos.writeObject(AmpifyServices.saveChoices(submitChoicesRequest));
                    oos.flush();
                }

                if (request.equals(String.valueOf(ServerRequest.GET_CHOICES))) {
                    ChoicesFetchRequest choicesFetchRequest = (ChoicesFetchRequest) object;
                    oos.writeObject(AmpifyServices.getUserChoices(choicesFetchRequest));
                    oos.flush();
                }

                //if request is to fetch songs!!
                if (request.equals(String.valueOf(ServerRequest.SONG_SHOW))) {
                    SongFetchRequest songType = (SongFetchRequest) object;
                    //if request is to display top songs
                    if(songType.getType().equals(String.valueOf(SongFetchType.TOP))){
                        oos.writeObject(AmpifyServices.showTopSongs(songType));
                        oos.flush();
                    }//if request is to display songs of particular artist
                    else if(songType.getType().equals(String.valueOf(SongFetchType.SONGS_OF_PARTICULAR_ARTIST))){
                        oos.writeObject(AmpifyServices.showSongsOfParticularArtist(songType));
                        oos.flush();
                    }

                }

            } catch (StreamCorruptedException e) {
                try {
                    ois = new ObjectInputStream(socket.getInputStream());
                    oos = new ObjectOutputStream(socket.getOutputStream());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
