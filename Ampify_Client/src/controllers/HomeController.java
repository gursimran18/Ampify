package controllers;

import Services.AmpifyServices;
import Services.MediaPlayerService;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Album;
import model.Artist;
import model.Playlist;
import model.Song;
import utilities.HomeScreenDisplays;
import utilities.HomeScreenWidgets;
import utilities.UserApi;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class HomeController implements Initializable {

    public JFXButton logoutButton;
    public Pane displayPane;
    public Pane bottomPane;
    public JFXListView<Song> nowPlayingList;
    public Text userEmailText;
    public JFXTextField searchBar;
    public AnchorPane homePane;
    public ProgressIndicator loadingIndicator;
    UserApi userApi = UserApi.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        HomeScreenWidgets.displayPane = displayPane;
        HomeScreenWidgets.bottomPane = bottomPane;
        HomeScreenWidgets.nowPlayingList = nowPlayingList;
        HomeScreenWidgets.searchBar = searchBar;
        HomeScreenWidgets.loadingIndicator = loadingIndicator;
        HomeScreenWidgets.parentPane = homePane;

        new Thread(() -> {

            loadDataFromServer();

            Platform.runLater(this::displayData);

        }).start();
    }

    private void loadDataFromServer() {
        // Displaying top artists
        try {
            List<Artist> artists = AmpifyServices.getTopArtists();
            userApi.setPopularArtists(artists);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Displaying recently played songs
        try {
            List<Song> songs = AmpifyServices.getUserRecentlyPlayedSong(0, 4);
            userApi.setRecentlyPlayed(songs);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Displaying recently added songs
        try {
            List<Song> songs = AmpifyServices.getRecentAddedSongs(0, 4);
            userApi.setRecentlyAdded(songs);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Displaying recommended songs to the user (Based on choice of Artists, Languages, Genres)
        try {
            List<Song> songs = AmpifyServices.getUserChoiceSongs(0, 4);
            userApi.setRecommendedMusic(songs);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Displaying the top(4) songs to the user
        try {
            List<Song> songs = AmpifyServices.getTopSongs(0, 4);
            userApi.setTopSongs(songs);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Loading user's playlists
        try {
            List<Playlist> playlists = AmpifyServices.getMyPlaylists();
            List<Playlist> personalPlaylists = new ArrayList<>();
            List<Playlist> groupPlaylists = new ArrayList<>();
            for (Playlist playlist : playlists) {
                if (playlist.getCategory().equals("GROUP")) {
                    groupPlaylists.add(playlist);
                } else {
                    personalPlaylists.add(playlist);
                }
            }
            userApi.setPersonalPlaylists(personalPlaylists);
            userApi.setGroupPlaylist(groupPlaylists);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Displaying top albums to the user
        try {
            List<Album> albums = AmpifyServices.getTopAlbums();
            userApi.setTopAlbums(albums);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Displaying most played music to the user
        try {
            List<Song> songs = AmpifyServices.getUserMostPlayedSong();
            userApi.setMostPlayed(songs);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Displaying music played at same time in past
        try {
            List<Song> songs = AmpifyServices.getPreviouslyPlayedSongs();
            userApi.setPreviouslyPlayed(songs);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Loading trending songs from the server
        try {
            List<Song> songs = AmpifyServices.getTrendingSongs();
            userApi.setTrendingSongs(songs);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Loading the last played song from server
        try {
            MediaPlayerService.previousSong = AmpifyServices.getUserLastPlayedSong();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void displayData() {

        userEmailText.setText(userApi.getEmail());

        // Displaying the last played song info in bottom pane
        if (MediaPlayerService.previousSong != null) {
            try {
                Pane mediaController = FXMLLoader.load(getClass().getResource("/resources/fxml/mediaPlayer.fxml"));
                bottomPane.getChildren().add(mediaController);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Displaying home pane
        try {
            Pane newPane = FXMLLoader.load(getClass().getResource("/resources/fxml/homeContentsPane.fxml"));
            displayPane.getChildren().clear();
            displayPane.getChildren().add(newPane);
            HomeScreenWidgets.currentDisplayPage = HomeScreenDisplays.MAIN_PAGE;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void onLogoutClicked(ActionEvent actionEvent) throws IOException {

        MediaPlayerController.stopPlayingMedia();

        // Removing user's info from UserApi class
        UserApi userApi = UserApi.getInstance();
        userApi.setLikedLanguages(null);
        userApi.setLikedArtists(null);
        userApi.setLikedGenres(null);
        userApi.setEmail(null);

        // Removing login info from local storage
        Preferences preferences = Preferences.userNodeForPackage(LoginController.class);
        preferences.put("isLoggedIn", "FALSE");
        preferences.put("email", "");

        goToLoginScreen(actionEvent);
    }

    private void goToLoginScreen(ActionEvent actionEvent) throws IOException {
        // Scene to be displayed
        Parent loginScreenParent = FXMLLoader.load(getClass().getResource("/resources/fxml/login.fxml"));
        Scene loginScreenScene = new Scene(loginScreenParent);

        // Getting the current stage window
        Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

        // Setting the new scene in the window
        window.setScene(loginScreenScene);
        window.show();
    }

    public void historyButtonAction() {

        if (HomeScreenWidgets.currentDisplayPage != HomeScreenDisplays.HISTORY_PAGE) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/fxml/historyScreen.fxml"));
                Pane newLoadedPane = loader.load();
                displayPane.getChildren().clear();
                displayPane.getChildren().add(newLoadedPane);
                HomeScreenWidgets.currentDisplayPage = HomeScreenDisplays.HISTORY_PAGE;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onHomeButtonClicked() {
        if (HomeScreenWidgets.currentDisplayPage != HomeScreenDisplays.MAIN_PAGE) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/fxml/homeContentsPane.fxml"));
                Pane newLoadedPane = loader.load();
                displayPane.getChildren().clear();
                displayPane.getChildren().add(newLoadedPane);
                HomeScreenWidgets.currentDisplayPage = HomeScreenDisplays.MAIN_PAGE;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void downloadsButtonAction() {
        if (HomeScreenWidgets.currentDisplayPage != HomeScreenDisplays.DOWNLOADS_SCREEN) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/fxml/downloadsScreen.fxml"));
                Pane newLoadedPane = loader.load();
                displayPane.getChildren().clear();
                displayPane.getChildren().add(newLoadedPane);
                HomeScreenWidgets.currentDisplayPage = HomeScreenDisplays.DOWNLOADS_SCREEN;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onLocalMusicClicked() {
        if (HomeScreenWidgets.currentDisplayPage != HomeScreenDisplays.LOCAL_MUSIC_SCREEN) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/fxml/localMusicScreen.fxml"));
                Pane newLoadedPane = loader.load();
                displayPane.getChildren().clear();
                displayPane.getChildren().add(newLoadedPane);
                HomeScreenWidgets.currentDisplayPage = HomeScreenDisplays.LOCAL_MUSIC_SCREEN;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onSearchBarClicked() {
        if (HomeScreenWidgets.currentDisplayPage != HomeScreenDisplays.SEARCH_PAGE) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/fxml/searchPage.fxml"));
                Pane newLoadedPane = loader.load();
                displayPane.getChildren().clear();
                displayPane.getChildren().add(newLoadedPane);
                HomeScreenWidgets.currentDisplayPage = HomeScreenDisplays.SEARCH_PAGE;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onVideoPlayerClicked() {
        if(HomeScreenWidgets.currentDisplayPage != HomeScreenDisplays.VIDEO_PLAYER_PAGE){
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/fxml/videoPlayerPage.fxml"));
                Pane newLoadedPane = loader.load();
                displayPane.getChildren().clear();
                displayPane.getChildren().add(newLoadedPane);
                HomeScreenWidgets.currentDisplayPage = HomeScreenDisplays.VIDEO_PLAYER_PAGE;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
