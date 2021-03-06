package CellFactories;

import controllers.ArtistCellController;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import model.Artist;

public class ArtistCellFactory implements Callback<ListView<Artist>, ListCell<Artist>> {

    @Override
    public ListCell<Artist> call(ListView<Artist> param) {
        return new ArtistCellController();

    }
}



