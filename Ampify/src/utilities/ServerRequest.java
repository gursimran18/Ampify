package utilities;

public enum ServerRequest {

    SIGNUP_REQUEST("0"),
    LOGIN_REQUEST("1"),
    LANGUAGE_SHOW("3"),
    GENRES_SHOW("4"),
    ARTIST_SHOW("5"),
    SUBMIT_CHOICES("6"),
    ;

    ServerRequest(String s) {
        s.toString();
    }
}