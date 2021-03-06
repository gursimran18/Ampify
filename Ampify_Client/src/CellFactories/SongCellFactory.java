package CellFactories;

import controllers.SongCellController;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import model.Song;

public class SongCellFactory implements Callback<ListView<Song>, ListCell<Song>> {

    @Override
    public ListCell<Song> call(ListView<Song> songListView) {

        return new SongCellController();
    }
}
